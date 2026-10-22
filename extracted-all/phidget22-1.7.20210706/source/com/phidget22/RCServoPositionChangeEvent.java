package com.phidget22;

public class RCServoPositionChangeEvent {
   RCServo Source;
   double Position;

   public RCServoPositionChangeEvent(RCServo var1, double var2) {
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
      return "[RCServoPositionChangeEvent](Source: " + this.Source + ", Position: " + this.Position + ")";
   }
}
