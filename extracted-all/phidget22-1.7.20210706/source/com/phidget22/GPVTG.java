package com.phidget22;

public class GPVTG {
   public double trueHeading;
   public double magneticHeading;
   public double speedKnots;
   public double speed;
   public char mode;

   @Override
   public String toString() {
      return "[GPVTG](trueHeading: "
         + this.trueHeading
         + ", magneticHeading: "
         + this.magneticHeading
         + ", speedKnots: "
         + this.speedKnots
         + ", speed: "
         + this.speed
         + ", mode: "
         + this.mode
         + ")";
   }
}
