package com.phidget22;

public class GPGGA {
   public double latitude;
   public double longitude;
   public short fixQuality;
   public short numSatellites;
   public double horizontalDilution;
   public double altitude;
   public double heightOfGeoid;

   @Override
   public String toString() {
      return "[GPGGA](latitude: "
         + this.latitude
         + ", longitude: "
         + this.longitude
         + ", fixQuality: "
         + this.fixQuality
         + ", numSatellites: "
         + this.numSatellites
         + ", horizontalDilution: "
         + this.horizontalDilution
         + ", altitude: "
         + this.altitude
         + ", heightOfGeoid: "
         + this.heightOfGeoid
         + ")";
   }
}
