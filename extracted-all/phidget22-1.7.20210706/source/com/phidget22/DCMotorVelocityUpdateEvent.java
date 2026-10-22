package com.phidget22;

public class DCMotorVelocityUpdateEvent {
   DCMotor Source;
   double Velocity;

   public DCMotorVelocityUpdateEvent(DCMotor var1, double var2) {
      this.Source = var1;
      this.Velocity = var2;
   }

   public DCMotor getSource() {
      return this.Source;
   }

   public double getVelocity() {
      return this.Velocity;
   }

   @Override
   public String toString() {
      return "[DCMotorVelocityUpdateEvent](Source: " + this.Source + ", Velocity: " + this.Velocity + ")";
   }
}
