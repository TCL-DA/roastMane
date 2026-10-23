package org.cropster.roastingintelligence.sensor.impl;

import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import com.phidget22.VoltageInput;
import com.phidget22.VoltageInputSensorChangeEvent;
import com.phidget22.VoltageInputSensorChangeListener;
import com.phidget22.VoltageSensorType;
import org.cropster.roastingintelligence.sensor.SensorValueConverterPhidget22;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoltageInputSensorPhidget22 extends AbstractAnalogPhidget22Sensor implements VoltageInputSensorChangeListener {
   private final Logger logger = LoggerFactory.getLogger(VoltageInputSensorPhidget22.class);
   private final VoltageInput _phidget;
   private final VoltageSensorType _type;
   private final SensorValueConverterPhidget22 _valueConverter;

   public VoltageInputSensorPhidget22(
      int channel, boolean isHubPortDevice, VoltageSensorType type, SensorValueConverterPhidget22 valueConverter, String host, Integer port
   ) throws PhidgetException {
      super(channel, isHubPortDevice, host, port);
      this._phidget = new VoltageInput();
      this._type = type;
      this._valueConverter = valueConverter;
      this.setChannel(this._channel);
      this.addListeners();
      this._phidget.addSensorChangeListener(this);
   }

   public VoltageInputSensorPhidget22(int channel, boolean isHubPortDevice, VoltageSensorType type, String host, Integer port) throws PhidgetException {
      this(channel, isHubPortDevice, type, x -> x, host, port);
   }

   public VoltageInputSensorPhidget22(
      VoltageInput phidget, boolean isHubPortDevice, VoltageSensorType type, SensorValueConverterPhidget22 valueConverter, String host, Integer port
   ) throws PhidgetException {
      super(phidget.getChannel(), isHubPortDevice, host, port);
      this._phidget = phidget;
      this._type = type;
      this._valueConverter = valueConverter;
      this.addListeners();
      this._phidget.addSensorChangeListener(this);
   }

   @Override
   public Phidget getPhidget() {
      return this._phidget;
   }

   @Override
   public void configure() {
      try {
         this._phidget.setSensorType(this._type);
         this._phidget.setSensorValueChangeTrigger(this._valueConverter.getChangeTrigger());
      } catch (PhidgetException var2) {
         this.logger.error("Error configuring sensor {}", this.getPhidgetName(), var2);
      }
   }

   public void onSensorChange(VoltageInputSensorChangeEvent e) {
      try {
         this._value = this._valueConverter.convertValue(e.getSensorValue());
      } catch (PhidgetException var3) {
         this.logger.error("Error reading value from  {}", this.getPhidgetName(), var3);
      }
   }

   @Override
   public void close() {
      super.close();
      this._phidget.removeSensorChangeListener(this);
   }
}
