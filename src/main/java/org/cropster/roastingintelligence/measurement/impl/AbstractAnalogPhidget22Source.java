package org.cropster.roastingintelligence.measurement.impl;

import com.phidget22.DeviceClass;
import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.sensor.SensorPhidget22;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAnalogPhidget22Source extends AbstractManagedPhidget22Source {
   private final Logger logger = LoggerFactory.getLogger(AbstractAnalogPhidget22Source.class);
   protected final boolean _isHubPortDevice;
   protected DeviceClass _deviceClass;

   public AbstractAnalogPhidget22Source(MeasurementSourceFactory factory, boolean isHubPortDevice, String host, Integer port, PhidgetDeviceDetector detector) {
      super(factory, host, port, detector);
      this._deviceClass = isHubPortDevice ? DeviceClass.VINT : DeviceClass.INTERFACE_KIT;
      this._isHubPortDevice = isHubPortDevice;
   }

   @Override
   public void attachHandler(Phidget phidget) {
      try {
         int index = DeviceClass.VINT.equals(phidget.getDeviceClass()) ? phidget.getHubPort() : phidget.getChannel();
         this._attachedDevices.put(index, phidget);
         Channel channel = this.getChannels().get(index);
         if (channel != null) {
            SensorPhidget22 sensor = this.createSensor(channel);
            if (sensor != null) {
               sensor.setListener(this);
               this._sensors.put(channel.getIndex(), sensor);
               sensor.open();
            } else {
               this.logger.error("Error creating sensor {}", channel.getIndex());
            }
         }
      } catch (PhidgetException var5) {
         this.logger.error("Error handling phidget attach", var5);
      }
   }

   @Override
   public void detachHandler(Phidget phidget) {
      try {
         int index = DeviceClass.VINT.equals(phidget.getDeviceClass()) ? phidget.getHubPort() : phidget.getChannel();
         this._attachedDevices.remove(index);
         SensorPhidget22 sensor = this._sensors.get(index);
         if (sensor != null) {
            sensor.close();
            this._sensors.remove(index);
         }
      } catch (PhidgetException var4) {
         this.logger.error("Error handling phidget detach", var4);
      }
   }
}
