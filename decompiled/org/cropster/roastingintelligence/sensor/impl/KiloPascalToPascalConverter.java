package org.cropster.roastingintelligence.sensor.impl;

import com.phidget22.PhidgetException;
import org.cropster.roastingintelligence.sensor.SensorValueConverterPhidget22;
import org.cropster.roastingintelligence.util.MathUtil;

public class KiloPascalToPascalConverter implements SensorValueConverterPhidget22 {
   private final double _changeTrigger = 5.0E-4;

   @Override
   public double convertValue(double value) throws PhidgetException {
      double pa = value * 1000.0;
      return MathUtil.roundHalfToEven(pa, 0);
   }

   @Override
   public double getChangeTrigger() {
      return this._changeTrigger;
   }
}
