package org.cropster.roastingintelligence.measurement.impl;

import com.phidget22.PhidgetException;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.sensor.SensorPhidget22;
import org.cropster.roastingintelligence.sensor.impl.RtdSensorPhidget22;
import org.cropster.roastingintelligence.sensor.impl.VoltageRatioToTemperatureConverter;

public class Phidget1046Source extends AbstractPhidget22Source {
   public Phidget1046Source(MeasurementSourceFactory factory) {
      super(factory);
   }

   private double getResistance(String probeType) {
      return "Pt1000".equalsIgnoreCase(probeType) ? 1000.0 : 100.0;
   }

   @Override
   protected SensorPhidget22 createSensor(Channel channel) throws PhidgetException {
      double resistance = this.getResistance(channel.getProbeType());
      SensorPhidget22 sensor = new RtdSensorPhidget22(channel.getIndex(), false, new VoltageRatioToTemperatureConverter(resistance));
      return sensor;
   }
}
