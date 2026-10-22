package org.cropster.roastingintelligence.sensor.impl;

import org.cropster.roastingintelligence.sensor.SensorValueConverter;
import org.cropster.roastingintelligence.util.MathUtil;

public class DiedrichVoltageValueConverter implements SensorValueConverter {
   @Override
   public boolean isRatiometric() {
      return true;
   }

   @Override
   public double convertValue(double sensorValue) {
      double percentage = sensorValue / 950.0 * 100.0;
      return MathUtil.roundHalfToEven(percentage, 0);
   }
}
