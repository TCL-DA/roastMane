package org.cropster.roastingintelligence.sensor.impl;

import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import com.phidget22.VoltageRatioInput;
import com.phidget22.VoltageRatioInputSensorChangeEvent;
import com.phidget22.VoltageRatioInputSensorChangeListener;
import com.phidget22.VoltageRatioInputVoltageRatioChangeEvent;
import com.phidget22.VoltageRatioInputVoltageRatioChangeListener;
import com.phidget22.VoltageRatioSensorType;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.sensor.SensorValueConverterPhidget22;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoltageRatioInputSensorPhidget22
   extends AbstractAnalogPhidget22Sensor
   implements VoltageRatioInputSensorChangeListener,
   VoltageRatioInputVoltageRatioChangeListener {
   protected final Logger logger = LoggerFactory.getLogger(VoltageRatioInputSensorPhidget22.class);
   private final VoltageRatioInput _phidget;
   private final SensorValueConverterPhidget22 _valueConverter;
   private VoltageRatioSensorType _type;
   private Integer _dataInterval = null;

   public VoltageRatioInputSensorPhidget22(
      int channel,
      boolean isHubPortDevice,
      VoltageRatioSensorType type,
      SensorValueConverterPhidget22 valueConverter,
      Integer dataInterval,
      String host,
      Integer port
   ) throws PhidgetException {
      super(channel, isHubPortDevice, host, port);
      this._phidget = new VoltageRatioInput();
      this._type = type;
      this._valueConverter = valueConverter;
      this._dataInterval = dataInterval;
      this.setChannel(this._channel);
      this.addListeners();
      if (VoltageRatioSensorType.VOLTAGE_RATIO.equals(this._type)) {
         this._phidget.addVoltageRatioChangeListener(this);
      } else {
         this._phidget.addSensorChangeListener(this);
      }
   }

   public VoltageRatioInputSensorPhidget22(
      int channel, boolean isHubPortDevice, VoltageRatioSensorType type, SensorValueConverterPhidget22 valueConverter, String host, Integer port
   ) throws PhidgetException {
      this(channel, isHubPortDevice, type, valueConverter, null, host, port);
   }

   public VoltageRatioInputSensorPhidget22(int channel, boolean isHubPortDevice, VoltageRatioSensorType type, int dataInterval, String host, Integer port) throws PhidgetException {
      this(channel, isHubPortDevice, type, x -> x, dataInterval, host, port);
   }

   public VoltageRatioInputSensorPhidget22(int channel, boolean isHubPortDevice, VoltageRatioSensorType type, String host, Integer port) throws PhidgetException {
      this(channel, isHubPortDevice, type, x -> x, null, host, port);
   }

   public VoltageRatioInputSensorPhidget22(
      VoltageRatioInput phidget, boolean isHubPortDevice, VoltageRatioSensorType type, SensorValueConverterPhidget22 valueConverter, String host, Integer port
   ) throws PhidgetException {
      super(phidget.getChannel(), isHubPortDevice, host, port);
      this._phidget = phidget;
      this._type = type;
      this._valueConverter = valueConverter;
      this.addListeners();
      if (VoltageRatioSensorType.VOLTAGE_RATIO.equals(this._type)) {
         this._phidget.addVoltageRatioChangeListener(this);
      } else {
         this._phidget.addSensorChangeListener(this);
      }
   }

   @Override
   public Phidget getPhidget() {
      return this._phidget;
   }

   public void setVoltageRatioType(VoltageRatioSensorType voltageRatioType) throws PhidgetException {
      this._type = voltageRatioType;
   }

   @Override
   public synchronized void configure() throws PhidgetException {
      try {
         this._phidget.setSensorType(this._type);
         if (VoltageRatioSensorType.VOLTAGE_RATIO.equals(this._type)) {
            this._phidget.setVoltageRatioChangeTrigger(this._valueConverter.getChangeTrigger());
            this._phidget.addVoltageRatioChangeListener(this);
         } else {
            this._phidget.setSensorValueChangeTrigger(this._valueConverter.getChangeTrigger());
            this._phidget.removeSensorChangeListener(this);
            this._phidget.addSensorChangeListener(this);
         }

         if (this._dataInterval != null) {
            this._phidget.setDataInterval(this._dataInterval);
         }
      } catch (PhidgetException var2) {
         this.logger.error("Error configuring sensor {}", this.getPhidgetName(), var2);
      }
   }

   @Override
   public void configureChannel(Channel channel) throws PhidgetException {
      VoltageRatioSensorType voltageRatioType = getVoltageRatioSensorType(channel.getProbeType());
      if (!this._type.equals(voltageRatioType)) {
         this.setVoltageRatioType(voltageRatioType);
      }

      if (this._isAttached) {
         this.configure();
      }
   }

   public void onSensorChange(VoltageRatioInputSensorChangeEvent e) {
      try {
         this._value = this._valueConverter.convertValue(e.getSensorValue());
      } catch (PhidgetException var3) {
         this.logger.error("Error reading value from  {}", this.getPhidgetName(), var3);
      }
   }

   public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent e) {
      try {
         this._value = this._valueConverter.convertValue(e.getVoltageRatio());
      } catch (PhidgetException var3) {
         this.logger.error("Error reading value from  {}", this.getPhidgetName(), var3);
      }
   }

   public static VoltageRatioSensorType getVoltageRatioSensorType(String type) {
      return switch (type) {
         case "Default" -> VoltageRatioSensorType.VOLTAGE_RATIO;
         case "Phidget 1136" -> VoltageRatioSensorType.PN_1136;
         case "Phidget 1137" -> VoltageRatioSensorType.PN_1137;
         case "Phidget 1138" -> VoltageRatioSensorType.PN_1138;
         default -> throw new IllegalArgumentException("Invalid type: " + type);
      };
   }

   @Override
   public void close() {
      super.close();
      if (VoltageRatioSensorType.VOLTAGE_RATIO.equals(this._type)) {
         this._phidget.removeVoltageRatioChangeListener(this);
      } else {
         this._phidget.removeSensorChangeListener(this);
      }
   }
}
