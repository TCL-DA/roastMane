package com.phidget22;

public class VoltageRatioInputVoltageRatioChangeEvent {
   VoltageRatioInput Source;
   double VoltageRatio;

   public VoltageRatioInputVoltageRatioChangeEvent(VoltageRatioInput var1, double var2) {
      this.Source = var1;
      this.VoltageRatio = var2;
   }

   public VoltageRatioInput getSource() {
      return this.Source;
   }

   public double getVoltageRatio() {
      return this.VoltageRatio;
   }

   @Override
   public String toString() {
      return "[VoltageRatioInputVoltageRatioChangeEvent](Source: " + this.Source + ", VoltageRatio: " + this.VoltageRatio + ")";
   }
}
