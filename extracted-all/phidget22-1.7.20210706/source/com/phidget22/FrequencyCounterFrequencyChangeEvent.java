package com.phidget22;

public class FrequencyCounterFrequencyChangeEvent {
   FrequencyCounter Source;
   double Frequency;

   public FrequencyCounterFrequencyChangeEvent(FrequencyCounter var1, double var2) {
      this.Source = var1;
      this.Frequency = var2;
   }

   public FrequencyCounter getSource() {
      return this.Source;
   }

   public double getFrequency() {
      return this.Frequency;
   }

   @Override
   public String toString() {
      return "[FrequencyCounterFrequencyChangeEvent](Source: " + this.Source + ", Frequency: " + this.Frequency + ")";
   }
}
