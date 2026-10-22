package org.girod.javafx.svgimage;

public class LoaderParameters implements Cloneable {
   public String styleSheets = null;
   public double scale = -1.0;
   public boolean scaleLineWidth = true;
   public double width = -1.0;
   public boolean autoStartAnimations = true;
   public boolean centerImage = false;
   public boolean applyViewportPosition = true;

   public LoaderParameters clone() {
      try {
         return (LoaderParameters)super.clone();
      } catch (CloneNotSupportedException var2) {
         return this;
      }
   }

   public static LoaderParameters createWidthParameters(double width) {
      LoaderParameters params = new LoaderParameters();
      params.width = width;
      return params;
   }

   public static LoaderParameters createScaleParameters(double scale) {
      LoaderParameters params = new LoaderParameters();
      params.scale = scale;
      return params;
   }
}
