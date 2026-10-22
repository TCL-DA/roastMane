package com.phidget22;

public class DCMotorBackEMFChangeEvent {
   DCMotor Source;
   double BackEMF;

   public DCMotorBackEMFChangeEvent(DCMotor var1, double var2) {
      this.Source = var1;
      this.BackEMF = var2;
   }

   public DCMotor getSource() {
      return this.Source;
   }

   public double getBackEMF() {
      return this.BackEMF;
   }

   @Override
   public String toString() {
      return "[DCMotorBackEMFChangeEvent](Source: " + this.Source + ", BackEMF: " + this.BackEMF + ")";
   }
}
