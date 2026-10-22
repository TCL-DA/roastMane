package com.phidget22;

public class MotorPositionControllerDutyCycleUpdateEvent {
   MotorPositionController Source;
   double DutyCycle;

   public MotorPositionControllerDutyCycleUpdateEvent(MotorPositionController var1, double var2) {
      this.Source = var1;
      this.DutyCycle = var2;
   }

   public MotorPositionController getSource() {
      return this.Source;
   }

   public double getDutyCycle() {
      return this.DutyCycle;
   }

   @Override
   public String toString() {
      return "[MotorPositionControllerDutyCycleUpdateEvent](Source: " + this.Source + ", DutyCycle: " + this.DutyCycle + ")";
   }
}
