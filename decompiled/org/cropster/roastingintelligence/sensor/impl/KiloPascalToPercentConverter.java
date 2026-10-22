package org.cropster.roastingintelligence.sensor.impl;

import com.phidget22.PhidgetException;
import org.cropster.roastingintelligence.measurement.impl.AbstractPhidget22Source;
import org.cropster.roastingintelligence.sensor.SensorValueConverterPhidget22;

public class KiloPascalToPercentConverter implements SensorValueConverterPhidget22 {
   private final Double _minValue;
   private final Double _maxValue;
   private final double _changeTrigger;

   public KiloPascalToPercentConverter(Double minValue, Double maxValue) {
      this._minValue = minValue;
      this._maxValue = maxValue;
      this._changeTrigger = (maxValue - minValue) / 1000.0 / 100.0 / 2.0;
   }

   @Override
   public double convertValue(double value) throws PhidgetException {
      return AbstractPhidget22Source.value2Percentage(value * 1000.0, this._minValue, this._maxValue);
   }

   @Override
   public double getChangeTrigger() {
      return this._changeTrigger;
   }
}
