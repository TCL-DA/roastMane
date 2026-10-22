package com.phidget22;

public class RCServoVelocityChangeEvent {
   RCServo Source;
   double Velocity;

   public RCServoVelocityChangeEvent(RCServo var1, double var2) {
      this.Source = var1;
      this.Velocity = var2;
   }

   public RCServo getSource() {
      return this.Source;
   }

   public double getVelocity() {
      return this.Velocity;
   }

   @Override
   public String toString() {
      return "[RCServoVelocityChangeEvent](Source: " + this.Source + ", Velocity: " + this.Velocity + ")";
   }
}
