package com.phidget22;

import java.util.Arrays;

public class SpatialAlgorithmDataEvent {
   Spatial Source;
   double[] Quaternion;
   double Timestamp;

   public SpatialAlgorithmDataEvent(Spatial var1, double[] var2, double var3) {
      this.Source = var1;
      this.Quaternion = var2;
      this.Timestamp = var3;
   }

   public Spatial getSource() {
      return this.Source;
   }

   public double[] getQuaternion() {
      return this.Quaternion;
   }

   public double getTimestamp() {
      return this.Timestamp;
   }

   @Override
   public String toString() {
      return "[SpatialAlgorithmDataEvent](Source: "
         + this.Source
         + ", Quaternion: "
         + Arrays.toString(this.Quaternion)
         + ", Timestamp: "
         + this.Timestamp
         + ")";
   }
}
