package org.cropster.roastingintelligence.sensor.impl;

import com.phidget22.BridgeGain;
import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import com.phidget22.VoltageRatioInput;
import com.phidget22.VoltageRatioInputVoltageRatioChangeEvent;
import com.phidget22.VoltageRatioInputVoltageRatioChangeListener;
import org.cropster.roastingintelligence.sensor.SensorValueConverterPhidget22;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RtdSensorPhidget22 extends AbstractAnalogPhidget22Sensor implements VoltageRatioInputVoltageRatioChangeListener {
   private final Logger logger = LoggerFactory.getLogger(RtdSensorPhidget22.class);
   private static final BridgeGain GAIN = BridgeGain.GAIN_1X;
   private static final int DATA_INTERVAL = 1000;
   private final VoltageRatioInput _phidget;
   private final SensorValueConverterPhidget22 _valueConverter;

   public RtdSensorPhidget22(int channel, boolean isHubPortDevice, SensorValueConverterPhidget22 valueConverter) throws PhidgetException {
      this(channel, isHubPortDevice, valueConverter, null, null);
   }

   public RtdSensorPhidget22(int channel, boolean isHubPortDevice, SensorValueConverterPhidget22 valueConverter, String host, Integer port) throws PhidgetException {
      super(channel, isHubPortDevice, host, port);
      this._phidget = new VoltageRatioInput();
      this._valueConverter = valueConverter;
      this.setChannel(this._channel);
      this.addListeners();
      this._phidget.addVoltageRatioChangeListener(this);
   }

   public RtdSensorPhidget22(VoltageRatioInput phidget, boolean isHubPortDevice, VoltageRatioToTemperatureConverter valueConverter) throws PhidgetException {
      super(phidget.getChannel(), isHubPortDevice, null, null);
      this._phidget = phidget;
      this._valueConverter = valueConverter;
      this.addListeners();
      this._phidget.addVoltageRatioChangeListener(this);
   }

   @Override
   public Phidget getPhidget() {
      return this._phidget;
   }

   @Override
   public void configure() throws PhidgetException {
      try {
         this._phidget.setBridgeEnabled(true);
         this._phidget.setBridgeGain(GAIN);
         this._phidget.setDataInterval(1000);
      } catch (PhidgetException var2) {
         this.logger.error("Error configuring sensor {}", this.getPhidgetName(), var2);
      }
   }

   public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent e) {
      try {
         this._value = this._valueConverter.convertValue(e.getVoltageRatio());
      } catch (PhidgetException var3) {
         this.logger.error("Error reading value from  {}", this.getPhidgetName(), var3);
      }
   }

   @Override
   public void close() {
      super.close();
      this._phidget.removeVoltageRatioChangeListener(this);
   }
}
