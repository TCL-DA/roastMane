package org.girod.javafx.svgimage.tosvg.converters;

import java.io.File;
import java.net.URL;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.girod.javafx.svgimage.tosvg.utils.Utilities;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

public class ImageViewConverter extends AbstractImageConverter {
   private ImageView view = null;

   public ImageViewConverter(ConverterDelegate delegate, ImageView view, XMLNode xmlParent) {
      super(delegate, view, xmlParent);
      this.view = view;
   }

   @Override
   public void applyStyle(XMLNode node, String clipID) {
      if (clipID != null) {
         StringBuilder buf = new StringBuilder();
         String style = buf.toString();
         node.addAttribute("style", style);
      }
   }

   @Override
   public XMLNode convert() {
      URL url = null;
      Image image = this.view.getImage();
      if (this.allProperties.containsKey("-fx-image")) {
         url = (URL)this.allProperties.get("-fx-image");
         if (url != null) {
            image = new Image(url.toString());
         }
      }

      if (url == null && image == null) {
         return null;
      } else {
         XMLNode node = new XMLNode("image");
         node.addAttribute("x", this.view.getX());
         node.addAttribute("y", this.view.getY());
         node.addAttribute("width", this.view.getFitWidth());
         node.addAttribute("height", this.view.getFitHeight());
         if (url != null) {
            File theFile = new File(url.getFile());
            File parentDir = this.delegate.getSVGFile().getParentFile();
            String path = Utilities.getRelativePath(parentDir, theFile, true);
            node.addAttribute("xlink:href", path);
         } else {
            this.writeImage(this.view, node, image, this.view.getFitWidth(), this.view.getFitHeight());
         }

         this.xmlParent.addChild(node);
         return node;
      }
   }
}
