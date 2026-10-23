package org.cropster.roastingintelligence.measurement.impl;

import com.phidget22.ChannelSubclass;
import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import com.phidget22.VoltageInput;
import com.phidget22.VoltageSensorType;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.sensor.SensorPhidget22;
import org.cropster.roastingintelligence.sensor.impl.MilliAmpereToPercentConverter;
import org.cropster.roastingintelligence.sensor.impl.VoltageInputSensorPhidget22;

public class Phidget1132Source extends AbstractAnalogPhidget22Source {
   private final double _minValue;
   private final double _maxValue;

   public Phidget1132Source(
      MeasurementSourceFactory factory, double minValue, double maxValue, boolean isHubPortDevice, String host, Integer port, PhidgetDeviceDetector detector
   ) throws PhidgetException {
      super(factory, isHubPortDevice, host, port, detector);
      this._minValue = minValue;
      this._maxValue = maxValue;
      this._detector.addDeviceFilter(this.getFactory().getClass().getSimpleName(), this);
   }

   @Override
   protected SensorPhidget22 createSensor(Channel channel) throws PhidgetException {
      VoltageInput phidget = (VoltageInput)this._attachedDevices.get(channel.getIndex());
      return new VoltageInputSensorPhidget22(
         phidget, this._isHubPortDevice, VoltageSensorType.PN_1132, new MilliAmpereToPercentConverter(this._minValue, this._maxValue), this._host, this._port
      );
   }

   @Override
   public boolean filter(Phidget phidget) throws PhidgetException {
      return ChannelSubclass.VOLTAGE_INPUT_SENSOR_PORT.equals(phidget.getChannelSubclass())
         && this._deviceClass.equals(phidget.getDeviceClass())
         && phidget.getIsRemote() == (this._host != null && this._port != null);
   }
}
