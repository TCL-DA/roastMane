package org.cropster.roastingintelligence.sensor.impl;

import org.cropster.roastingintelligence.sensor.SensorValueConverter;
import org.cropster.roastingintelligence.util.MathUtil;

public class DiedrichValueConverter implements SensorValueConverter {
   private final double _magicNumber;

   public DiedrichValueConverter(double magicNumber) {
      this._magicNumber = magicNumber;
   }

   @Override
   public boolean isRatiometric() {
      return true;
   }

   @Override
   public double convertValue(double sensorValue) {
      double percent = (sensorValue - 196.0) / this._magicNumber;
      return MathUtil.roundHalfToEven(percent, 0);
   }
}
