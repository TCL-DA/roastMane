package org.cropster.roastingintelligence.sensor.impl;

import com.phidget22.PhidgetException;
import com.phidget22.TemperatureSensor;
import com.phidget22.ThermocoupleType;
import org.cropster.roastingintelligence.model.Channel;

public class ThermocoupleTemperatureSensorPhidget22 extends TemperatureSensorPhidget22 {
   private ThermocoupleType _type;

   public ThermocoupleTemperatureSensorPhidget22(int channel, boolean isVintDevice, Integer hubPort, ThermocoupleType type, String host, Integer port) throws PhidgetException {
      super(channel, isVintDevice, hubPort, host, port);
      if (type == null) {
         throw new IllegalArgumentException("Thermocouple type is null");
      } else {
         this._type = type;
      }
   }

   public ThermocoupleTemperatureSensorPhidget22(int channel, boolean isVintDevice, ThermocoupleType type, String host, Integer port) throws PhidgetException {
      this(channel, isVintDevice, null, type, host, port);
   }

   public ThermocoupleTemperatureSensorPhidget22(TemperatureSensor phidget, boolean isVintDevice, ThermocoupleType type, String host, Integer port) throws PhidgetException {
      super(phidget, isVintDevice, host, port);
      if (type == null) {
         throw new IllegalArgumentException("Thermocouple type is null");
      } else {
         this._type = type;
      }
   }

   public ThermocoupleType getThermocoupleType() {
      return this._type;
   }

   public void setThermocoupleType(ThermocoupleType type) throws PhidgetException {
      this._type = type;
   }

   @Override
   public void configure() throws PhidgetException {
      this._phidget.setThermocoupleType(this._type);
      super.configure();
   }

   @Override
   public void configureChannel(Channel channel) throws PhidgetException {
      ThermocoupleType thermocoupleType = ThermocoupleType.valueOf(channel.getProbeType());
      if (!this._type.equals(thermocoupleType)) {
         this.setThermocoupleType(thermocoupleType);
      }

      if (this._isAttached) {
         this.configure();
      }
   }
}
