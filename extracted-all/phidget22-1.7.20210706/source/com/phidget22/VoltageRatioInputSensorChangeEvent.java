package com.phidget22;

public class VoltageRatioInputSensorChangeEvent {
   VoltageRatioInput Source;
   double SensorValue;
   UnitInfo SensorUnit;

   public VoltageRatioInputSensorChangeEvent(VoltageRatioInput var1, double var2, UnitInfo var4) {
      this.Source = var1;
      this.SensorValue = var2;
      this.SensorUnit = var4;
   }

   public VoltageRatioInput getSource() {
      return this.Source;
   }

   public double getSensorValue() {
      return this.SensorValue;
   }

   public UnitInfo getSensorUnit() {
      return this.SensorUnit;
   }

   @Override
   public String toString() {
      return "[VoltageRatioInputSensorChangeEvent](Source: " + this.Source + ", SensorValue: " + this.SensorValue + ", SensorUnit: " + this.SensorUnit + ")";
   }
}
