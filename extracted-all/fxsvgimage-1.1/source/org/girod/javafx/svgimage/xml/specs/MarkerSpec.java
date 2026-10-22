package org.girod.javafx.svgimage.xml.specs;

import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;

public class MarkerSpec implements SVGTags {
   private Viewbox viewbox = null;
   private final XMLNode node;
   private double refX = 0.0;
   private double refY = 0.0;
   private double width = -1.0;
   private double height = -1.0;

   public MarkerSpec(XMLNode node) {
      this.node = node;
   }

   public void computeRefPosition(Viewport viewport) {
      if (this.node.hasAttribute("refX")) {
         this.refX = -this.node.getLengthValue("refX", viewport);
      }

      if (this.node.hasAttribute("refY")) {
         this.refY = -this.node.getLengthValue("refY", viewport);
      }

      if (this.node.hasAttribute("markerWidth")) {
         this.width = this.node.getLengthValue("markerWidth", viewport);
      }

      if (this.node.hasAttribute("markerHeight")) {
         this.height = this.node.getLengthValue("markerHeight", viewport);
      }
   }

   public double getWidth() {
      return this.width;
   }

   public double getHeight() {
      return this.height;
   }

   public double getRefX() {
      return this.refX;
   }

   public double getRefY() {
      return this.refY;
   }

   public void setViewbox(Viewbox viewbox) {
      this.viewbox = viewbox;
      if (this.node.hasAttribute("preserveAspectRatio")) {
         String value = this.node.getAttributeValue("preserveAspectRatio");
         boolean preserveAspectRatio = ParserUtils.getPreserveAspectRatio(value);
         viewbox.setPreserveAspectRatio(preserveAspectRatio);
      }
   }

   public boolean hasViewbox() {
      return this.viewbox != null;
   }

   public Viewbox getViewbox() {
      return this.viewbox;
   }

   public XMLNode getXMLNode() {
      return this.node;
   }

   public double scaleWidth(double coord) {
      return coord * 1.0;
   }

   public double scaleHeight(double coord) {
      return coord * 1.0;
   }
}
