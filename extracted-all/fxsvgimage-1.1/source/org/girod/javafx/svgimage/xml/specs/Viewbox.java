package org.girod.javafx.svgimage.xml.specs;

import javafx.scene.Node;

public class Viewbox {
   protected double width = 0.0;
   protected double height = 0.0;
   protected boolean hasWidthAndHeight = false;
   protected double viewboxX = 0.0;
   protected double viewboxY = 0.0;
   protected double viewboxWidth = 0.0;
   protected double viewboxHeight = 0.0;
   protected boolean preserveAspectRatio = true;

   public Viewbox(double width, double height) {
      this.width = width;
      this.height = height;
      this.hasWidthAndHeight = true;
   }

   public void setPreserveAspectRatio(boolean preserveAspectRatio) {
      this.preserveAspectRatio = preserveAspectRatio;
   }

   public boolean isPreservingAspectRatio() {
      return this.preserveAspectRatio;
   }

   public void setViewbox(double viewboxX, double viewboxY, double viewboxWidth, double viewboxHeight) {
      this.viewboxX = viewboxX;
      this.viewboxY = viewboxY;
      this.viewboxWidth = viewboxWidth;
      this.viewboxHeight = viewboxHeight;
   }

   public void scaleNode(Node node) {
      if (this.hasWidthAndHeight) {
         if (!this.preserveAspectRatio) {
            if (this.viewboxWidth != 0.0 && this.viewboxHeight != 0.0) {
               node.setScaleX(this.width / this.viewboxWidth);
               node.setScaleY(this.height / this.viewboxHeight);
            }
         } else if (this.viewboxWidth != 0.0) {
            node.setScaleX(this.width / this.viewboxWidth);
            node.setScaleY(this.width / this.viewboxWidth);
         }
      }
   }

   public double scaleValue(boolean isWidth, double value) {
      if (this.preserveAspectRatio) {
         return value * this.width / this.viewboxWidth;
      } else {
         return isWidth ? value : value;
      }
   }

   public double getViewboxX() {
      return this.viewboxX;
   }

   public double getViewboxY() {
      return this.viewboxY;
   }

   public double getViewboxWidth() {
      return this.viewboxWidth;
   }

   public double getViewboxHeight() {
      return this.viewboxHeight;
   }

   public double getWidth() {
      return this.width;
   }

   public double getHeight() {
      return this.height;
   }
}
