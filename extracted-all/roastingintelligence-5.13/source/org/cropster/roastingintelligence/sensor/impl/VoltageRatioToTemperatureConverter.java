package org.cropster.roastingintelligence.sensor.impl;

import com.phidget22.PhidgetException;
import org.cropster.roastingintelligence.sensor.SensorValueConverterPhidget22;

public class VoltageRatioToTemperatureConverter implements SensorValueConverterPhidget22 {
   private final double _resistance;

   public VoltageRatioToTemperatureConverter(double resistance) {
      this._resistance = resistance;
   }

   private double round(double value, int precision) {
      double factor = Math.pow(10.0, (double)precision);
      return Math.rint(value * factor) / factor;
   }

   private double convertBridgeValue(double r0, double vb) {
      vb *= 1000.0;
      double a = 4.7503 * Math.pow(10.0, 7.0) / Math.pow(r0, 2.0);
      double b = Math.pow(vb / (1000.0 - vb), 2.0);
      double c = 4.6156 * Math.pow(10.0, 5.0) / r0;
      double d = vb / (1000.0 - vb);
      double result = a * b + c * d - 242.615;
      return this.round(result, 2);
   }

   @Override
   public double convertValue(double value) throws PhidgetException {
      return this.convertBridgeValue(this._resistance, value);
   }

   @Override
   public double getChangeTrigger() {
      return 0.0;
   }
}
