package com.phidget22;

public class GPSTime {
   public short tm_ms;
   public short tm_sec;
   public short tm_min;
   public short tm_hour;

   @Override
   public String toString() {
      return "[GPSTime](tm_ms: " + this.tm_ms + ", tm_sec: " + this.tm_sec + ", tm_min: " + this.tm_min + ", tm_hour: " + this.tm_hour + ")";
   }
}
