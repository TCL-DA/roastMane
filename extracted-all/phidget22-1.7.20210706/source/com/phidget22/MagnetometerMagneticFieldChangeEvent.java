package com.phidget22;

import java.util.Arrays;

public class MagnetometerMagneticFieldChangeEvent {
   Magnetometer Source;
   double[] MagneticField;
   double Timestamp;

   public MagnetometerMagneticFieldChangeEvent(Magnetometer var1, double[] var2, double var3) {
      this.Source = var1;
      this.MagneticField = var2;
      this.Timestamp = var3;
   }

   public Magnetometer getSource() {
      return this.Source;
   }

   public double[] getMagneticField() {
      return this.MagneticField;
   }

   public double getTimestamp() {
      return this.Timestamp;
   }

   @Override
   public String toString() {
      return "[MagnetometerMagneticFieldChangeEvent](Source: "
         + this.Source
         + ", MagneticField: "
         + Arrays.toString(this.MagneticField)
         + ", Timestamp: "
         + this.Timestamp
         + ")";
   }
}
