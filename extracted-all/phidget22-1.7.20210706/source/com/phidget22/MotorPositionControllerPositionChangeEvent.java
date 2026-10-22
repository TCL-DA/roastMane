package com.phidget22;

public class MotorPositionControllerPositionChangeEvent {
   MotorPositionController Source;
   double Position;

   public MotorPositionControllerPositionChangeEvent(MotorPositionController var1, double var2) {
      this.Source = var1;
      this.Position = var2;
   }

   public MotorPositionController getSource() {
      return this.Source;
   }

   public double getPosition() {
      return this.Position;
   }

   @Override
   public String toString() {
      return "[MotorPositionControllerPositionChangeEvent](Source: " + this.Source + ", Position: " + this.Position + ")";
   }
}
