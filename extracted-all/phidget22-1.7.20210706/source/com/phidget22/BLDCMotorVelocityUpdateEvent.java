package com.phidget22;

public class BLDCMotorVelocityUpdateEvent {
   BLDCMotor Source;
   double Velocity;

   public BLDCMotorVelocityUpdateEvent(BLDCMotor var1, double var2) {
      this.Source = var1;
      this.Velocity = var2;
   }

   public BLDCMotor getSource() {
      return this.Source;
   }

   public double getVelocity() {
      return this.Velocity;
   }

   @Override
   public String toString() {
      return "[BLDCMotorVelocityUpdateEvent](Source: " + this.Source + ", Velocity: " + this.Velocity + ")";
   }
}
