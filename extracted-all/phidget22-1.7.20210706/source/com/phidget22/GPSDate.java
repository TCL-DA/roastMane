package com.phidget22;

public class GPSDate {
   public short tm_mday;
   public short tm_mon;
   public short tm_year;

   @Override
   public String toString() {
      return "[GPSDate](tm_mday: " + this.tm_mday + ", tm_mon: " + this.tm_mon + ", tm_year: " + this.tm_year + ")";
   }
}
