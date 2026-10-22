package com.phidget22;

public class VoltageInputVoltageChangeEvent {
   VoltageInput Source;
   double Voltage;

   public VoltageInputVoltageChangeEvent(VoltageInput var1, double var2) {
      this.Source = var1;
      this.Voltage = var2;
   }

   public VoltageInput getSource() {
      return this.Source;
   }

   public double getVoltage() {
      return this.Voltage;
   }

   @Override
   public String toString() {
      return "[VoltageInputVoltageChangeEvent](Source: " + this.Source + ", Voltage: " + this.Voltage + ")";
   }
}
