package com.phidget22;

public class CapacitiveTouchTouchEndEvent {
   CapacitiveTouch Source;

   public CapacitiveTouchTouchEndEvent(CapacitiveTouch var1) {
      this.Source = var1;
   }

   public CapacitiveTouch getSource() {
      return this.Source;
   }

   @Override
   public String toString() {
      return "[CapacitiveTouchTouchEndEvent](Source: " + this.Source + ")";
   }
}
