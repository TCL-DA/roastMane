package com.phidget22;

import java.util.Arrays;

public class AccelerometerAccelerationChangeEvent {
   Accelerometer Source;
   double[] Acceleration;
   double Timestamp;

   public AccelerometerAccelerationChangeEvent(Accelerometer var1, double[] var2, double var3) {
      this.Source = var1;
      this.Acceleration = var2;
      this.Timestamp = var3;
   }

   public Accelerometer getSource() {
      return this.Source;
   }

   public double[] getAcceleration() {
      return this.Acceleration;
   }

   public double getTimestamp() {
      return this.Timestamp;
   }

   @Override
   public String toString() {
      return "[AccelerometerAccelerationChangeEvent](Source: "
         + this.Source
         + ", Acceleration: "
         + Arrays.toString(this.Acceleration)
         + ", Timestamp: "
         + this.Timestamp
         + ")";
   }
}
