package com.phidget22;

public class LightSensorIlluminanceChangeEvent {
   LightSensor Source;
   double Illuminance;

   public LightSensorIlluminanceChangeEvent(LightSensor var1, double var2) {
      this.Source = var1;
      this.Illuminance = var2;
   }

   public LightSensor getSource() {
      return this.Source;
   }

   public double getIlluminance() {
      return this.Illuminance;
   }

   @Override
   public String toString() {
      return "[LightSensorIlluminanceChangeEvent](Source: " + this.Source + ", Illuminance: " + this.Illuminance + ")";
   }
}
