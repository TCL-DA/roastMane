package com.phidget22;

public class CapacitiveTouchTouchEvent {
   CapacitiveTouch Source;
   double TouchValue;

   public CapacitiveTouchTouchEvent(CapacitiveTouch var1, double var2) {
      this.Source = var1;
      this.TouchValue = var2;
   }

   public CapacitiveTouch getSource() {
      return this.Source;
   }

   public double getTouchValue() {
      return this.TouchValue;
   }

   @Override
   public String toString() {
      return "[CapacitiveTouchTouchEvent](Source: " + this.Source + ", TouchValue: " + this.TouchValue + ")";
   }
}
