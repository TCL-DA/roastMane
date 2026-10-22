package org.girod.javafx.svgimage.tosvg.converters;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.VolatileImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.transform.Transform;
import javax.imageio.ImageIO;
import org.girod.javafx.svgimage.tosvg.utils.AwtImageUtilities;
import org.girod.javafx.svgimage.tosvg.utils.Utilities;
import org.girod.javafx.svgimage.tosvg.xml.SVGConstants;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

public abstract class AbstractImageConverter extends AbstractConverter {
   public AbstractImageConverter(ConverterDelegate delegate, Node node, XMLNode xmlParent) {
      super(delegate, node, xmlParent);
   }

   protected void writeImage(Node theNode, XMLNode xmlNode, Image image, double dstWidth, double dstHeight) {
      BufferedImage awtImage = new BufferedImage((int)dstWidth, (int)dstHeight, 2);
      java.awt.Image awtimage2 = SwingFXUtils.fromFXImage(image, awtImage);
      if (theNode.isDisabled()) {
         awtimage2 = AwtImageUtilities.createDisabledImage(awtimage2);
      }

      awtimage2 = awtimage2.getScaledInstance((int)dstWidth, (int)dstHeight, 4);
      Transform fromAncestorTransform = this.getTransformFromAncestor(theNode);
      AffineTransform awtTransform = AwtImageUtilities.getTransform(fromAncestorTransform);
      double angle = AwtImageUtilities.getRotationAngle(awtTransform);
      if (angle != 0.0) {
         BufferedImage awtImage3 = new BufferedImage((int)dstWidth, (int)dstHeight, 2);
         Graphics2D g2d = awtImage3.createGraphics();
         AffineTransform at = new AffineTransform();
         at.translate(dstWidth / 2.0, dstHeight / 2.0);
         at.rotate(angle);
         at.scale(0.5, 0.5);
         at.translate(-dstWidth / 2.0, -dstHeight / 2.0);
         g2d.drawImage(awtimage2, at, null);
         awtimage2 = awtImage3;
      }

      if (this.allProperties.containsKey("-fx-opacity")) {
         double opacity = (Double)this.allProperties.get("-fx-opacity");
         if (opacity < 1.0) {
            awtimage2 = AwtImageUtilities.createOpacifiedImage(awtimage2, opacity, false);
         }
      }

      BufferedImage bimg = this.toBufferedImage(awtimage2, 2);
      String content = imgToBase64String(bimg);
      List<String> parts = Utilities.splitString(content, 100);
      StringBuilder buf = new StringBuilder();
      Iterator<String> it = parts.iterator();

      while (it.hasNext()) {
         String splitted = it.next();
         buf.append(splitted);
         if (it.hasNext()) {
            buf.append("\n");
         }
      }

      content = "data:image/png;base64," + buf.toString();
      xmlNode.addAttribute("xmlns:xlink", SVGConstants.XLINK);
      xmlNode.addAttribute("xlink:href", content);
   }

   public BufferedImage toBufferedImage(java.awt.Image image, int type) {
      if (image instanceof BufferedImage) {
         return (BufferedImage)image;
      } else if (image instanceof VolatileImage) {
         return ((VolatileImage)image).getSnapshot();
      } else {
         this.loadImage(image);
         BufferedImage buffImg = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
         Graphics2D g2 = buffImg.createGraphics();
         g2.drawImage(image, null, null);
         g2.dispose();
         return buffImg;
      }
   }

   private void loadImage(java.awt.Image image) {
      class StatusObserver implements ImageObserver {
         boolean imageLoaded = false;

         @Override
         public boolean imageUpdate(java.awt.Image img, int infoflags, int x, int y, int width, int height) {
            if (infoflags == 32) {
               synchronized (this) {
                  this.imageLoaded = true;
                  this.notify();
                  return true;
               }
            } else {
               return false;
            }
         }
      }

      StatusObserver imageStatus = new StatusObserver();
      synchronized (imageStatus) {
         if (image.getWidth(imageStatus) == -1 || image.getHeight(imageStatus) == -1) {
            while (!imageStatus.imageLoaded) {
               try {
                  imageStatus.wait();
               } catch (InterruptedException var6) {
               }
            }
         }
      }
   }

   public static String imgToBase64String(RenderedImage img) {
      ByteArrayOutputStream os = new ByteArrayOutputStream();

      try (OutputStream b64os = Base64.getEncoder().wrap(os)) {
         ImageIO.write(img, "png", b64os);
      } catch (IOException var15) {
         return null;
      }

      return os.toString();
   }
}
