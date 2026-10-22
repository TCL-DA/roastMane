package com.phidget22;

public class PHSensorPHChangeEvent {
   PHSensor Source;
   double PH;

   public PHSensorPHChangeEvent(PHSensor var1, double var2) {
      this.Source = var1;
      this.PH = var2;
   }

   public PHSensor getSource() {
      return this.Source;
   }

   public double getPH() {
      return this.PH;
   }

   @Override
   public String toString() {
      return "[PHSensorPHChangeEvent](Source: " + this.Source + ", PH: " + this.PH + ")";
   }
}
