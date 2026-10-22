package org.cropster.roastingintelligence.sensor.impl;

import org.cropster.roastingintelligence.sensor.SensorValueConverter;
import org.cropster.roastingintelligence.util.MathUtil;

public class Phidget1132ValueConverter implements SensorValueConverter {
   private static final double FACTOR = 0.022222222222222223;
   private static final double SCALE_MIN = 0.0;
   private static final double SCALE_MAX = 100.0;
   private static final double SCALE_DIFF = 100.0;
   private static final double SENSOR_MIN = 4.0;
   private static final double SENSOR_MAX = 20.0;
   private static final double SENSOR_DIFF = 16.0;

   @Override
   public boolean isRatiometric() {
      return false;
   }

   @Override
   public double convertValue(double sensorValue) {
      double mA = sensorValue * 0.022222222222222223;
      double percent = (mA - 4.0) * 6.25 + 0.0;
      return MathUtil.roundHalfToEven(percent, 1);
   }
}
