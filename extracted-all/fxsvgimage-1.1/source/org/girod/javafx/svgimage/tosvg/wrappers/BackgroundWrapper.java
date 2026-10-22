package org.girod.javafx.svgimage.tosvg.wrappers;

import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;

public class BackgroundWrapper {
   private Paint paint = null;
   private double x = 0.0;
   private double y = 0.0;
   private double width = -1.0;
   private double height = -1.0;
   private CornerRadii radii = null;
   private BackgroundPosition position = null;
   private boolean isTexture = false;

   public BackgroundWrapper(Paint paint) {
      this.paint = paint;
      if (paint instanceof ImagePattern) {
         this.isTexture = true;
      }
   }

   public boolean isTexture() {
      return this.isTexture;
   }

   public void setPosition(double x, double y) {
      this.x = x;
      this.y = y;
   }

   public void setImagePosition(BackgroundPosition position) {
      this.position = position;
   }

   public BackgroundPosition getImagePosition() {
      return this.position;
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

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public void setSize(double width, double height) {
      this.width = width;
      this.height = height;
   }

   public double getWidth() {
      return this.width;
   }

   public double getHeight() {
      return this.height;
   }

   public Paint getPaint() {
      return this.paint;
   }
}
