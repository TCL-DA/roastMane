package com.phidget22;

public class StepperStoppedEvent {
   Stepper Source;

   public StepperStoppedEvent(Stepper var1) {
      this.Source = var1;
   }

   public Stepper getSource() {
      return this.Source;
   }

   @Override
   public String toString() {
      return "[StepperStoppedEvent](Source: " + this.Source + ")";
   }
}
