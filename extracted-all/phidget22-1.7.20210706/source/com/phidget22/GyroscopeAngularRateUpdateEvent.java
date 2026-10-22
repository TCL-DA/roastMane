package com.phidget22;

import java.util.Arrays;

public class GyroscopeAngularRateUpdateEvent {
   Gyroscope Source;
   double[] AngularRate;
   double Timestamp;

   public GyroscopeAngularRateUpdateEvent(Gyroscope var1, double[] var2, double var3) {
      this.Source = var1;
      this.AngularRate = var2;
      this.Timestamp = var3;
   }

   public Gyroscope getSource() {
      return this.Source;
   }

   public double[] getAngularRate() {
      return this.AngularRate;
   }

   public double getTimestamp() {
      return this.Timestamp;
   }

   @Override
   public String toString() {
      return "[GyroscopeAngularRateUpdateEvent](Source: "
         + this.Source
         + ", AngularRate: "
         + Arrays.toString(this.AngularRate)
         + ", Timestamp: "
         + this.Timestamp
         + ")";
   }
}
