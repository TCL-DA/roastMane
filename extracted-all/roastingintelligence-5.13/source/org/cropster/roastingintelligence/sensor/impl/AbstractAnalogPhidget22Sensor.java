package org.cropster.roastingintelligence.sensor.impl;

import com.phidget22.PhidgetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAnalogPhidget22Sensor extends AbstractPhidget22Sensor {
   private final Logger logger = LoggerFactory.getLogger(AbstractAnalogPhidget22Sensor.class);
   protected final boolean _isHubPortDevice;

   public AbstractAnalogPhidget22Sensor(int channel, boolean isHubPortDevice, String host, Integer port) {
      super(channel, host, port);
      this._isHubPortDevice = isHubPortDevice;
   }

   @Override
   public void setChannel(int channel) throws PhidgetException {
      if (this._isHubPortDevice) {
         this.getPhidget().setIsHubPortDevice(true);
         this.getPhidget().setHubPort(channel);
         this.getPhidget().setChannel(0);
      } else {
         this.getPhidget().setIsHubPortDevice(false);
         this.getPhidget().setChannel(channel);
      }
   }

   @Override
   public String getPhidgetName() {
      try {
         return this._isHubPortDevice
            ? String.format("%s (Hub Port %d)", this.getPhidget().getClass().getSimpleName(), this.getPhidget().getHubPort())
            : String.format("%s (Channel %d)", this.getPhidget().getClass().getSimpleName(), this.getPhidget().getChannel());
      } catch (PhidgetException var2) {
         this.logger.error("Error accessing phidget {}", this.getPhidget().getClass().getSimpleName(), var2);
         return this.getPhidget().getClass().getSimpleName();
      }
   }
}
