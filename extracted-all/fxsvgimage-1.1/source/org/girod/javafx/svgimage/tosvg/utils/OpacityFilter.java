package org.girod.javafx.svgimage.tosvg.utils;

import java.awt.image.RGBImageFilter;

public class OpacityFilter extends RGBImageFilter {
   private double opacity = 0.0;
   private boolean multiply = false;

   public OpacityFilter(double opacity, boolean multiply) {
      this.opacity = opacity;
      this.multiply = multiply;
   }

   @Override
   public int filterRGB(int x, int y, int rgb) {
      int alpha;
      if (this.multiply) {
         alpha = rgb & 0xFF000000;
         alpha = (int)((double)alpha * this.opacity);
      } else {
         alpha = (int)(this.opacity * 255.0);
      }

      if (alpha < 0) {
         alpha = 0;
      } else if (alpha > 255) {
         alpha = 255;
      }

      return (rgb & 16777215) + (alpha << 24);
   }
}
