package com.phidget22;

public class ResistanceInputResistanceChangeEvent {
   ResistanceInput Source;
   double Resistance;

   public ResistanceInputResistanceChangeEvent(ResistanceInput var1, double var2) {
      this.Source = var1;
      this.Resistance = var2;
   }

   public ResistanceInput getSource() {
      return this.Source;
   }

   public double getResistance() {
      return this.Resistance;
   }

   @Override
   public String toString() {
      return "[ResistanceInputResistanceChangeEvent](Source: " + this.Source + ", Resistance: " + this.Resistance + ")";
   }
}
