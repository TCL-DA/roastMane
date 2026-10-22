package com.phidget22;

public class CurrentInputCurrentChangeEvent {
   CurrentInput Source;
   double Current;

   public CurrentInputCurrentChangeEvent(CurrentInput var1, double var2) {
      this.Source = var1;
      this.Current = var2;
   }

   public CurrentInput getSource() {
      return this.Source;
   }

   public double getCurrent() {
      return this.Current;
   }

   @Override
   public String toString() {
      return "[CurrentInputCurrentChangeEvent](Source: " + this.Source + ", Current: " + this.Current + ")";
   }
}
