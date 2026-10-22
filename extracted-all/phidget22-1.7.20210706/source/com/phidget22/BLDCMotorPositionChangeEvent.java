package com.phidget22;

public class BLDCMotorPositionChangeEvent {
   BLDCMotor Source;
   double Position;

   public BLDCMotorPositionChangeEvent(BLDCMotor var1, double var2) {
      this.Source = var1;
      this.Position = var2;
   }

   public BLDCMotor getSource() {
      return this.Source;
   }

   public double getPosition() {
      return this.Position;
   }

   @Override
   public String toString() {
      return "[BLDCMotorPositionChangeEvent](Source: " + this.Source + ", Position: " + this.Position + ")";
   }
}
