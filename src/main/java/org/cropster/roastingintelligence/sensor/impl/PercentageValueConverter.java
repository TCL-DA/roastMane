package org.cropster.roastingintelligence.sensor.impl;

import org.cropster.roastingintelligence.sensor.SensorValueConverter;
import org.cropster.roastingintelligence.util.MathUtil;

public class PercentageValueConverter implements SensorValueConverter {
   @Override
   public boolean isRatiometric() {
      return true;
   }

   @Override
   public double convertValue(double sensorValue) {
      return MathUtil.roundHalfToEven(sensorValue / 10.0, 0);
   }
}
