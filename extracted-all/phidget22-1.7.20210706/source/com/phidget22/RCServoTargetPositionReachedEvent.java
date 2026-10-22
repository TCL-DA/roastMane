package com.phidget22;

public class RCServoTargetPositionReachedEvent {
   RCServo Source;
   double Position;

   public RCServoTargetPositionReachedEvent(RCServo var1, double var2) {
      this.Source = var1;
      this.Position = var2;
   }

   public RCServo getSource() {
      return this.Source;
   }

   public double getPosition() {
      return this.Position;
   }

   @Override
   public String toString() {
      return "[RCServoTargetPositionReachedEvent](Source: " + this.Source + ", Position: " + this.Position + ")";
   }
}
