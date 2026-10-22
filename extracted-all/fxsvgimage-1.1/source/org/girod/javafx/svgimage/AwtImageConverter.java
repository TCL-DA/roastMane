package org.girod.javafx.svgimage;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import org.girod.javafx.svgimage.xml.parsers.SVGLibraryException;

class AwtImageConverter {
   private static Object colorModel = null;
   private static final int[] RGB_MASKS = new int[]{16711680, 65280, 255};

   static boolean snapshot(WritableImage image, SnapshotParameters params, String format, File file) throws SVGLibraryException {
      RenderedImage awtImg = SwingFXUtils.fromFXImage(image, null);

      try {
         if (format.equals("jpg")) {
            PixelGrabber pg = new PixelGrabber((Image)awtImg, 0, 0, -1, -1, true);
            pg.grabPixels();
            int width = pg.getWidth();
            int height = pg.getHeight();
            DataBuffer buffer = new DataBufferInt((int[])pg.getPixels(), pg.getWidth() * pg.getHeight());
            WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, RGB_MASKS, null);
            if (colorModel == null) {
               colorModel = new DirectColorModel(32, RGB_MASKS[0], RGB_MASKS[1], RGB_MASKS[2]);
            }

            awtImg = new BufferedImage((ColorModel)colorModel, raster, false, null);
         }

         ImageIO.write(awtImg, format, file);
         return true;
      } catch (InterruptedException | IOException var10) {
         throw new SVGLibraryException(var10);
      }
   }
}
