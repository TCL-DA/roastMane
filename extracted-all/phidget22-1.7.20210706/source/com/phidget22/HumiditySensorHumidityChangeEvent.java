package com.phidget22;

public class HumiditySensorHumidityChangeEvent {
   HumiditySensor Source;
   double Humidity;

   public HumiditySensorHumidityChangeEvent(HumiditySensor var1, double var2) {
      this.Source = var1;
      this.Humidity = var2;
   }

   public HumiditySensor getSource() {
      return this.Source;
   }

   public double getHumidity() {
      return this.Humidity;
   }

   @Override
   public String toString() {
      return "[HumiditySensorHumidityChangeEvent](Source: " + this.Source + ", Humidity: " + this.Humidity + ")";
   }
}
