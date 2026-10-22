package com.phidget22;

import java.util.Arrays;

public class SpatialSpatialDataEvent {
   Spatial Source;
   double[] Acceleration;
   double[] AngularRate;
   double[] MagneticField;
   double Timestamp;

   public SpatialSpatialDataEvent(Spatial var1, double[] var2, double[] var3, double[] var4, double var5) {
      this.Source = var1;
      this.Acceleration = var2;
      this.AngularRate = var3;
      this.MagneticField = var4;
      this.Timestamp = var5;
   }

   public Spatial getSource() {
      return this.Source;
   }

   public double[] getAcceleration() {
      return this.Acceleration;
   }

   public double[] getAngularRate() {
      return this.AngularRate;
   }

   public double[] getMagneticField() {
      return this.MagneticField;
   }

   public double getTimestamp() {
      return this.Timestamp;
   }

   @Override
   public String toString() {
      return "[SpatialSpatialDataEvent](Source: "
         + this.Source
         + ", Acceleration: "
         + Arrays.toString(this.Acceleration)
         + ", AngularRate: "
         + Arrays.toString(this.AngularRate)
         + ", MagneticField: "
         + Arrays.toString(this.MagneticField)
         + ", Timestamp: "
         + this.Timestamp
         + ")";
   }
}
