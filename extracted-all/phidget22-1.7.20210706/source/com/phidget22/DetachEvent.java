package com.phidget22;

public class DetachEvent {
   Phidget Source;

   public DetachEvent(Phidget var1) {
      this.Source = var1;
   }

   public Phidget getSource() {
      return this.Source;
   }

   @Override
   public String toString() {
      return "[DetachEvent](Source: " + this.Source + ")";
   }
}
