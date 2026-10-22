package com.phidget22;

public class GPSPositionChangeEvent {
   GPS Source;
   double Latitude;
   double Longitude;
   double Altitude;

   public GPSPositionChangeEvent(GPS var1, double var2, double var4, double var6) {
      this.Source = var1;
      this.Latitude = var2;
      this.Longitude = var4;
      this.Altitude = var6;
   }

   public GPS getSource() {
      return this.Source;
   }

   public double getLatitude() {
      return this.Latitude;
   }

   public double getLongitude() {
      return this.Longitude;
   }

   public double getAltitude() {
      return this.Altitude;
   }

   @Override
   public String toString() {
      return "[GPSPositionChangeEvent](Source: "
         + this.Source
         + ", Latitude: "
         + this.Latitude
         + ", Longitude: "
         + this.Longitude
         + ", Altitude: "
         + this.Altitude
         + ")";
   }
}
