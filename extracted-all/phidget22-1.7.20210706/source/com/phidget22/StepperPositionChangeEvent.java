package com.phidget22;

public class StepperPositionChangeEvent {
   Stepper Source;
   double Position;

   public StepperPositionChangeEvent(Stepper var1, double var2) {
      this.Source = var1;
      this.Position = var2;
   }

   public Stepper getSource() {
      return this.Source;
   }

   public double getPosition() {
      return this.Position;
   }

   @Override
   public String toString() {
      return "[StepperPositionChangeEvent](Source: " + this.Source + ", Position: " + this.Position + ")";
   }
}
