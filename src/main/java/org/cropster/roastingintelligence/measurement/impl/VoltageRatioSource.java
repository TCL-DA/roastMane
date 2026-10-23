package org.cropster.roastingintelligence.measurement.impl;

import com.phidget22.ChannelSubclass;
import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import com.phidget22.VoltageRatioInput;
import com.phidget22.VoltageRatioSensorType;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.sensor.SensorPhidget22;
import org.cropster.roastingintelligence.sensor.SensorValueConverterPhidget22;
import org.cropster.roastingintelligence.sensor.impl.KiloPascalToPascalConverter;
import org.cropster.roastingintelligence.sensor.impl.KiloPascalToPercentConverter;
import org.cropster.roastingintelligence.sensor.impl.VoltageRatioInputSensorPhidget22;

public class VoltageRatioSource extends AbstractAnalogPhidget22Source {
   private Double _minValue = null;
   private Double _maxValue = null;

   public VoltageRatioSource(
      MeasurementSourceFactory factory, double minValue, double maxValue, boolean isHubPortDevice, String host, Integer port, PhidgetDeviceDetector detector
   ) {
      this(factory, isHubPortDevice, host, port, detector);
      this._minValue = minValue;
      this._maxValue = maxValue;
   }

   public VoltageRatioSource(MeasurementSourceFactory factory, boolean isHubPortDevice, String host, Integer port, PhidgetDeviceDetector detector) {
      super(factory, isHubPortDevice, host, port, detector);
      this._detector.addDeviceFilter(this.getFactory().getClass().getSimpleName(), this);
   }

   @Override
   public boolean filter(Phidget phidget) throws PhidgetException {
      int index = this._isHubPortDevice ? phidget.getHubPort() : phidget.getChannel();
      return !this.getChannels().keySet().contains(index)
         ? false
         : ChannelSubclass.VOLTAGE_RATIO_INPUT_SENSOR_PORT.equals(phidget.getChannelSubclass())
            && this._deviceClass.equals(phidget.getDeviceClass())
            && phidget.getIsRemote() == (this._host != null && this._port != null);
   }

   @Override
   protected SensorPhidget22 createSensor(Channel channel) throws PhidgetException {
      VoltageRatioInput phidget = (VoltageRatioInput)this._attachedDevices.get(channel.getIndex());
      VoltageRatioSensorType sensorType = VoltageRatioInputSensorPhidget22.getVoltageRatioSensorType(channel.getProbeType());
      SensorValueConverterPhidget22 converter;
      if (this._minValue != null && this._maxValue != null) {
         converter = new KiloPascalToPercentConverter(this._minValue, this._maxValue);
      } else {
         converter = new KiloPascalToPascalConverter();
      }

      return new VoltageRatioInputSensorPhidget22(phidget, this._isHubPortDevice, sensorType, converter, this._host, this._port);
   }

   @Override
   public boolean isValid(String measureName, Double value) {
      return value > -25000.0 && value < 50000.0;
   }
}
