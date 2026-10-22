package org.cropster.roastingintelligence.sensor.impl;

import org.cropster.roastingintelligence.measurement.impl.AbstractPhidget22Source;
import org.cropster.roastingintelligence.sensor.SensorValueConverterPhidget22;

public class MilliAmpereToPercentConverter implements SensorValueConverterPhidget22 {
   private final Double _minValue;
   private final Double _maxValue;
   private final double _changeTrigger;

   public MilliAmpereToPercentConverter(Double minValue, Double maxValue) {
      this._minValue = minValue;
      this._maxValue = maxValue;
      this._changeTrigger = (maxValue - minValue) / 100.0 / 2.0;
   }

   @Override
   public double convertValue(double sensorValue) {
      return AbstractPhidget22Source.value2Percentage(sensorValue, this._minValue, this._maxValue);
   }

   @Override
   public double getChangeTrigger() {
      return this._changeTrigger;
   }
}
