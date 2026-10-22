package org.girod.javafx.svgimage.xml.specs;

import javafx.scene.shape.Shape;

public class Viewport extends Viewbox {
   private double scale = -1.0;
   private boolean isScaled = false;
   private boolean scaleLineWidth = true;

   public Viewport(double width, double height) {
      super(width, height);
      this.hasWidthAndHeight = true;
   }

   public Viewport() {
      super(0.0, 0.0);
      this.hasWidthAndHeight = false;
   }

   public double getBestWidth() {
      return this.hasWidthAndHeight ? this.width : this.viewboxWidth;
   }

   public double getBestHeight() {
      return this.hasWidthAndHeight ? this.height : this.viewboxHeight;
   }

   public void scaleShape(Shape shape) {
      if (this.isScaled) {
         shape.setScaleX(this.scale);
         shape.setScaleY(this.scale);
      }
   }

   public double getDefaultPosition(double defaultValue, boolean isWidth) {
      return defaultValue;
   }

   public double scalePosition(double value, boolean isWidth) {
      return this.isScaled ? value * this.scale : value;
   }

   public double scaleLength(double length) {
      return this.isScaled ? length * this.scale : length;
   }

   public double scaleLineWidth(double width) {
      return this.scaleLineWidth ? this.scaleLength(width) : width;
   }

   public void setScale(double scale, boolean scaleLineWidth) {
      if (scale > 0.0) {
         this.isScaled = true;
         this.scaleLineWidth = scaleLineWidth;
         this.scale = scale;
      } else {
         this.isScaled = false;
         this.scaleLineWidth = false;
         this.scale = -1.0;
      }
   }

   public double getScale() {
      return this.scale;
   }

   public boolean isScaled() {
      return this.isScaled;
   }

   public boolean isScalingLineWidth() {
      return this.scaleLineWidth;
   }
}
