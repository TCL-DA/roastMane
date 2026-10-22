package org.cropster.roastingintelligence.sensor.impl;

import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import com.phidget22.TemperatureSensor;
import com.phidget22.TemperatureSensorTemperatureChangeEvent;
import com.phidget22.TemperatureSensorTemperatureChangeListener;
import org.cropster.roastingintelligence.sensor.SensorPhidget22;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemperatureSensorPhidget22 extends AbstractPhidget22Sensor implements SensorPhidget22, TemperatureSensorTemperatureChangeListener {
   private final Logger logger = LoggerFactory.getLogger(TemperatureSensorPhidget22.class);
   protected final TemperatureSensor _phidget;
   private final boolean _isVintDevice;
   private final Integer _hubPort;

   public TemperatureSensorPhidget22(int channel, boolean isVintDevice, Integer hubPort, String host, Integer port) throws PhidgetException {
      super(channel, host, port);
      this._phidget = new TemperatureSensor();
      this._isVintDevice = isVintDevice;
      this._hubPort = hubPort;
      this.setChannel(this._channel);
      this.addListeners();
      this._phidget.addTemperatureChangeListener(this);
   }

   public TemperatureSensorPhidget22(int channel, boolean isVintDevice, String host, Integer port) throws PhidgetException {
      this(channel, isVintDevice, null, host, port);
   }

   public TemperatureSensorPhidget22(TemperatureSensor phidget, boolean isVintDevice, String host, Integer port) throws PhidgetException {
      super(phidget.getChannel(), host, port);
      this._phidget = phidget;
      this._isVintDevice = isVintDevice;
      this._hubPort = phidget.getHubPort();
      this.addListeners();
      this._phidget.addTemperatureChangeListener(this);
   }

   @Override
   public Phidget getPhidget() {
      return this._phidget;
   }

   @Override
   public void setChannel(int channel) throws PhidgetException {
      if (this._isVintDevice) {
         if (this._hubPort == null) {
            this._phidget.setHubPort(channel);
         } else {
            this._phidget.setHubPort(this._hubPort);
            this._phidget.setChannel(channel);
         }
      } else {
         this._phidget.setIsHubPortDevice(false);
         this._phidget.setHubPort(0);
         this._phidget.setChannel(channel);
      }
   }

   @Override
   public String getPhidgetName() {
      try {
         if (!this._isVintDevice) {
            return String.format("%s (Channel %d)", this.getPhidget().getClass().getSimpleName(), this.getPhidget().getChannel());
         } else {
            return this._hubPort == null
               ? String.format("%s (Hub Port %d)", this.getPhidget().getClass().getSimpleName(), this.getPhidget().getHubPort())
               : String.format(
                  "%s (Hub Port %d, Channel %d)", this.getPhidget().getClass().getSimpleName(), this.getPhidget().getHubPort(), this.getPhidget().getChannel()
               );
         }
      } catch (PhidgetException var2) {
         this.logger.error("Error accessing phidget {}", this.getPhidget().getClass().getSimpleName(), var2);
         return this.getPhidget().getClass().getSimpleName();
      }
   }

   @Override
   public void configure() throws PhidgetException {
      this._phidget.setTemperatureChangeTrigger(0.1);
   }

   public void onTemperatureChange(TemperatureSensorTemperatureChangeEvent e) {
      this._value = e.getTemperature();
   }

   @Override
   public void close() {
      super.close();
      this._phidget.removeTemperatureChangeListener(this);
   }
}
