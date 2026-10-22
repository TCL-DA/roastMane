package com.phidget22;

public class NMEAData {
   public GPGGA GGA;
   public GPGSA GSA;
   public GPRMC RMC;
   public GPVTG VTG;

   @Override
   public String toString() {
      return "[NMEAData](GGA: " + this.GGA + ", GSA: " + this.GSA + ", RMC: " + this.RMC + ", VTG: " + this.VTG + ")";
   }
}
