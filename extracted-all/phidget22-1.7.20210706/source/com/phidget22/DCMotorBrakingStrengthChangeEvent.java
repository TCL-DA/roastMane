package com.phidget22;

public class DCMotorBrakingStrengthChangeEvent {
   DCMotor Source;
   double BrakingStrength;

   public DCMotorBrakingStrengthChangeEvent(DCMotor var1, double var2) {
      this.Source = var1;
      this.BrakingStrength = var2;
   }

   public DCMotor getSource() {
      return this.Source;
   }

   public double getBrakingStrength() {
      return this.BrakingStrength;
   }

   @Override
   public String toString() {
      return "[DCMotorBrakingStrengthChangeEvent](Source: " + this.Source + ", BrakingStrength: " + this.BrakingStrength + ")";
   }
}
