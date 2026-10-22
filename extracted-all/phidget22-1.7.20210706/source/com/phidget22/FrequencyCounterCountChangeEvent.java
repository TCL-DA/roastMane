package com.phidget22;

public class FrequencyCounterCountChangeEvent {
   FrequencyCounter Source;
   long Counts;
   double TimeChange;

   public FrequencyCounterCountChangeEvent(FrequencyCounter var1, long var2, double var4) {
      this.Source = var1;
      this.Counts = var2;
      this.TimeChange = var4;
   }

   public FrequencyCounter getSource() {
      return this.Source;
   }

   public long getCounts() {
      return this.Counts;
   }

   public double getTimeChange() {
      return this.TimeChange;
   }

   @Override
   public String toString() {
      return "[FrequencyCounterCountChangeEvent](Source: " + this.Source + ", Counts: " + this.Counts + ", TimeChange: " + this.TimeChange + ")";
   }
}
