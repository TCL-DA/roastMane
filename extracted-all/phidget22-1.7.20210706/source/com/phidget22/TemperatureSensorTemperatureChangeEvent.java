package com.phidget22;

public class TemperatureSensorTemperatureChangeEvent {
   TemperatureSensor Source;
   double Temperature;

   public TemperatureSensorTemperatureChangeEvent(TemperatureSensor var1, double var2) {
      this.Source = var1;
      this.Temperature = var2;
   }

   public TemperatureSensor getSource() {
      return this.Source;
   }

   public double getTemperature() {
      return this.Temperature;
   }

   @Override
   public String toString() {
      return "[TemperatureSensorTemperatureChangeEvent](Source: " + this.Source + ", Temperature: " + this.Temperature + ")";
   }
}
