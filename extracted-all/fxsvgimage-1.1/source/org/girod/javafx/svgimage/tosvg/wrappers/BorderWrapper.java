package org.girod.javafx.svgimage.tosvg.wrappers;

import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;

public class BorderWrapper {
   private Paint paint = null;
   private double width = 1.0;
   private CornerRadii radii = null;
   private BorderStrokeStyle strokeStyle = null;
   private double x = 0.0;
   private double y = 0.0;

   public BorderWrapper(Paint paint, double width) {
      this.paint = paint;
      this.width = width;
   }

   public void setStrokeStyle(BorderStrokeStyle strokeStyle) {
      this.strokeStyle = strokeStyle;
   }

   public BorderStrokeStyle getStrokeStyle() {
      return this.strokeStyle;
   }

   public void setPosition(double x, double y) {
      this.x = x;
      this.y = y;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public Paint getPaint() {
      return this.paint;
   }

   public void setRadii(CornerRadii radii) {
      this.radii = radii;
   }

   public CornerRadii getRadii() {
      return this.radii;
   }

   public boolean hasRadii() {
      return this.radii != null
         && (
            this.radii.getBottomLeftHorizontalRadius() != 0.0
               || this.radii.getBottomLeftVerticalRadius() != 0.0
               || this.radii.getBottomRightHorizontalRadius() != 0.0
               || this.radii.getBottomRightVerticalRadius() != 0.0
               || this.radii.getTopLeftHorizontalRadius() != 0.0
               || this.radii.getTopLeftVerticalRadius() != 0.0
               || this.radii.getTopRightHorizontalRadius() != 0.0
               || this.radii.getTopRightVerticalRadius() != 0.0
         );
   }

   public double getWidth() {
      return this.width;
   }

   public int getMeanRadiiWidth() {
      if (this.radii == null) {
         return 0;
      } else {
         double mean = (
               this.radii.getBottomLeftHorizontalRadius()
                  + this.radii.getBottomRightHorizontalRadius()
                  + this.radii.getTopLeftHorizontalRadius()
                  + this.radii.getTopRightHorizontalRadius()
            )
            / 8.0;
         return (int)mean;
      }
   }

   public int getMeanRadiiHeight() {
      if (this.radii == null) {
         return 0;
      } else {
         double mean = (
               this.radii.getBottomLeftVerticalRadius()
                  + this.radii.getBottomRightVerticalRadius()
                  + this.radii.getTopLeftVerticalRadius()
                  + this.radii.getTopRightVerticalRadius()
            )
            / 8.0;
         return (int)mean;
      }
   }
}
