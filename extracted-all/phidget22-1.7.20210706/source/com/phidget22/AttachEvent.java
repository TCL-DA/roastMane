package com.phidget22;

public class AttachEvent {
   Phidget Source;

   public AttachEvent(Phidget var1) {
      this.Source = var1;
   }

   public Phidget getSource() {
      return this.Source;
   }

   @Override
   public String toString() {
      return "[AttachEvent](Source: " + this.Source + ")";
   }
}
