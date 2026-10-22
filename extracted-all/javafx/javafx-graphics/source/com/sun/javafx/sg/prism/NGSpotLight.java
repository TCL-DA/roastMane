package com.sun.javafx.sg.prism;

import com.sun.javafx.geom.Vec3d;
import javafx.geometry.Point3D;

public class NGSpotLight extends NGPointLight {
   private static final Point3D DEFAULT_DIRECTION = new Point3D(0.0, 0.0, 1.0);
   private static final float DEFAULT_INNER_ANGLE = 0.0F;
   private static final float DEFAULT_OUTER_ANGLE = 30.0F;
   private static final float DEFAULT_FALLOFF = 1.0F;
   private Point3D direction = DEFAULT_DIRECTION;
   private final Vec3d effectiveDir = new Vec3d();
   private float innerAngle = 0.0F;
   private float outerAngle = 30.0F;
   private float falloff = 1.0F;

   public static Point3D getDefaultDirection() {
      return DEFAULT_DIRECTION;
   }

   public static float getDefaultInnerAngle() {
      return 0.0F;
   }

   public static float getDefaultOuterAngle() {
      return 30.0F;
   }

   public static float getDefaultFalloff() {
      return 1.0F;
   }

   @Override
   public Point3D getDirection() {
      Vec3d var1 = new Vec3d(this.direction.getX(), this.direction.getY(), this.direction.getZ());
      this.getWorldTransform().deltaTransform(var1, this.effectiveDir);
      return new Point3D(this.effectiveDir.x, this.effectiveDir.y, this.effectiveDir.z);
   }

   public void setDirection(Point3D var1) {
      if (!this.direction.equals(var1)) {
         this.direction = var1;
         this.visualsChanged();
      }
   }

   @Override
   public float getInnerAngle() {
      return this.innerAngle;
   }

   public void setInnerAngle(float var1) {
      if (this.innerAngle != var1) {
         this.innerAngle = var1;
         this.visualsChanged();
      }
   }

   @Override
   public float getOuterAngle() {
      return this.outerAngle;
   }

   public void setOuterAngle(float var1) {
      if (this.outerAngle != var1) {
         this.outerAngle = var1;
         this.visualsChanged();
      }
   }

   @Override
   public float getFalloff() {
      return this.falloff;
   }

   public void setFalloff(float var1) {
      if (this.falloff != var1) {
         this.falloff = var1;
         this.visualsChanged();
      }
   }
}
