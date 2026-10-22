package com.phidget22;

public class GPSHeadingChangeEvent {
   GPS Source;
   double Heading;
   double Velocity;

   public GPSHeadingChangeEvent(GPS var1, double var2, double var4) {
      this.Source = var1;
      this.Heading = var2;
      this.Velocity = var4;
   }

   public GPS getSource() {
      return this.Source;
   }

   public double getHeading() {
      return this.Heading;
   }

   public double getVelocity() {
      return this.Velocity;
   }

   @Override
   public String toString() {
      return "[GPSHeadingChangeEvent](Source: " + this.Source + ", Heading: " + this.Heading + ", Velocity: " + this.Velocity + ")";
   }
}
