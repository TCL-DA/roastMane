package com.sun.javafx.sg.prism;

import javafx.geometry.Point3D;

public class NGPointLight extends NGLightBase {
   private static final float DEFAULT_CA = 1.0F;
   private static final float DEFAULT_LA = 0.0F;
   private static final float DEFAULT_QA = 0.0F;
   private static final float DEFAULT_MAX_RANGE = Float.POSITIVE_INFINITY;
   private static final Point3D SIMULATED_DIRECTION = new Point3D(0.0, 0.0, 1.0);
   private static final float SIMULATED_INNER_ANGLE = 0.0F;
   private static final float SIMULATED_OUTER_ANGLE = 180.0F;
   private static final float SIMULATED_FALLOFF = 0.0F;
   private float ca = 1.0F;
   private float la = 0.0F;
   private float qa = 0.0F;
   private float maxRange = Float.POSITIVE_INFINITY;

   public static float getDefaultCa() {
      return 1.0F;
   }

   public static float getDefaultLa() {
      return 0.0F;
   }

   public static float getDefaultQa() {
      return 0.0F;
   }

   public static float getDefaultMaxRange() {
      return Float.POSITIVE_INFINITY;
   }

   public static Point3D getSimulatedDirection() {
      return SIMULATED_DIRECTION;
   }

   public static float getSimulatedInnerAngle() {
      return 0.0F;
   }

   public static float getSimulatedOuterAngle() {
      return 180.0F;
   }

   public static float getSimulatedFalloff() {
      return 0.0F;
   }

   public Point3D getDirection() {
      return SIMULATED_DIRECTION;
   }

   public float getInnerAngle() {
      return 0.0F;
   }

   public float getOuterAngle() {
      return 180.0F;
   }

   public float getFalloff() {
      return 0.0F;
   }

   public float getCa() {
      return this.ca;
   }

   public void setCa(float var1) {
      if (this.ca != var1) {
         this.ca = var1;
         this.visualsChanged();
      }
   }

   public float getLa() {
      return this.la;
   }

   public void setLa(float var1) {
      if (this.la != var1) {
         this.la = var1;
         this.visualsChanged();
      }
   }

   public float getQa() {
      return this.qa;
   }

   public void setQa(float var1) {
      if (this.qa != var1) {
         this.qa = var1;
         this.visualsChanged();
      }
   }

   public float getMaxRange() {
      return this.maxRange;
   }

   public void setMaxRange(float var1) {
      var1 = var1 < 0.0F ? 0.0F : var1;
      if (this.maxRange != var1) {
         this.maxRange = var1;
         this.visualsChanged();
      }
   }
}
