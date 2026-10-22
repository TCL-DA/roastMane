package org.girod.javafx.svgimage.tosvg.converters;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape3D;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

public class Shape3DConverter extends AbstractImageConverter {
   private Shape3D shape = null;

   public Shape3DConverter(ConverterDelegate delegate, Shape3D shape, XMLNode xmlParent) {
      super(delegate, shape, xmlParent);
      this.shape = shape;
   }

   public Shape3D getShape() {
      return this.shape;
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
      SnapshotParameters params = new SnapshotParameters();
      Color transparent = new Color(0.0, 0.0, 0.0, 0.0);
      params.setFill(transparent);
      params.setDepthBuffer(true);
      params.setCamera(this.shape.getScene().getCamera());
      XMLNode node = new XMLNode("image");
      node.addAttribute("x", this.shape.getLayoutX());
      node.addAttribute("y", this.shape.getLayoutY());
      double width = this.shape.getBoundsInLocal().getWidth();
      double height = this.shape.getBoundsInLocal().getHeight();
      node.addAttribute("width", width);
      node.addAttribute("height", height);
      WritableImage image = this.shape.snapshot(params, null);
      this.writeImage(this.shape, node, image, width, height);
      this.xmlParent.addChild(node);
      return node;
   }
}
