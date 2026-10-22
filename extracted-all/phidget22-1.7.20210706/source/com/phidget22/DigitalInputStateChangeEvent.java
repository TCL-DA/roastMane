package com.phidget22;

public class DigitalInputStateChangeEvent {
   DigitalInput Source;
   boolean State;

   public DigitalInputStateChangeEvent(DigitalInput var1, boolean var2) {
      this.Source = var1;
      this.State = var2;
   }

   public DigitalInput getSource() {
      return this.Source;
   }

   public boolean getState() {
      return this.State;
   }

   @Override
   public String toString() {
      return "[DigitalInputStateChangeEvent](Source: " + this.Source + ", State: " + this.State + ")";
   }
}
