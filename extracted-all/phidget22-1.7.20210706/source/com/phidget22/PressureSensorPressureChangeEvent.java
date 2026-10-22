package com.phidget22;

public class PressureSensorPressureChangeEvent {
   PressureSensor Source;
   double Pressure;

   public PressureSensorPressureChangeEvent(PressureSensor var1, double var2) {
      this.Source = var1;
      this.Pressure = var2;
   }

   public PressureSensor getSource() {
      return this.Source;
   }

   public double getPressure() {
      return this.Pressure;
   }

   @Override
   public String toString() {
      return "[PressureSensorPressureChangeEvent](Source: " + this.Source + ", Pressure: " + this.Pressure + ")";
   }
}
