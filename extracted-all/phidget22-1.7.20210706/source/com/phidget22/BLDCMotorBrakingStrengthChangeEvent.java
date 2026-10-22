package com.phidget22;

public class BLDCMotorBrakingStrengthChangeEvent {
   BLDCMotor Source;
   double BrakingStrength;

   public BLDCMotorBrakingStrengthChangeEvent(BLDCMotor var1, double var2) {
      this.Source = var1;
      this.BrakingStrength = var2;
   }

   public BLDCMotor getSource() {
      return this.Source;
   }

   public double getBrakingStrength() {
      return this.BrakingStrength;
   }

   @Override
   public String toString() {
      return "[BLDCMotorBrakingStrengthChangeEvent](Source: " + this.Source + ", BrakingStrength: " + this.BrakingStrength + ")";
   }
}
