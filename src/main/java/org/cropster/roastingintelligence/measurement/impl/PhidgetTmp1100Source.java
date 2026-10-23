package org.cropster.roastingintelligence.measurement.impl;

import com.phidget22.ChannelClass;
import com.phidget22.DeviceClass;
import com.phidget22.DeviceID;
import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import com.phidget22.TemperatureSensor;
import com.phidget22.ThermocoupleType;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.sensor.SensorPhidget22;
import org.cropster.roastingintelligence.sensor.impl.ThermocoupleTemperatureSensorPhidget22;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhidgetTmp1100Source extends AbstractManagedPhidget22Source {
   private final Logger logger = LoggerFactory.getLogger(PhidgetTmp1100Source.class);

   public PhidgetTmp1100Source(MeasurementSourceFactory factory, PhidgetDeviceDetector deviceDetector) {
      this(factory, null, null, deviceDetector);
   }

   public PhidgetTmp1100Source(MeasurementSourceFactory factory, String host, Integer port, PhidgetDeviceDetector deviceDetector) {
      super(factory, host, port, deviceDetector);
      this._detector.addDeviceFilter(this.getFactory().getClass().getSimpleName(), this);
   }

   @Override
   protected SensorPhidget22 createSensor(Channel channel) throws PhidgetException {
      TemperatureSensor phidget = (TemperatureSensor)this._attachedDevices.get(channel.getIndex());
      return phidget == null
         ? null
         : new ThermocoupleTemperatureSensorPhidget22(
            phidget, DeviceClass.VINT.equals(phidget.getDeviceClass()), ThermocoupleType.valueOf(channel.getProbeType()), this._host, this._port
         );
   }

   @Override
   public boolean filter(Phidget phidget) throws PhidgetException {
      return DeviceID.PN_TMP1100.equals(phidget.getDeviceID())
         && ChannelClass.TEMPERATURE_SENSOR.equals(phidget.getChannelClass())
         && phidget.getChannel() == 0
         && phidget.getIsRemote() == (this._host != null && this._port != null);
   }

   @Override
   public void attachHandler(Phidget phidget) {
      try {
         this._attachedDevices.put(phidget.getHubPort(), (TemperatureSensor)phidget);
         Channel channel = this.getChannels().get(phidget.getHubPort());
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
      } catch (PhidgetException var4) {
         this.logger.error("Error attaching Phidget sensor", var4);
      }
   }

   @Override
   public void detachHandler(Phidget phidget) {
      try {
         this._attachedDevices.remove(phidget.getHubPort());
         SensorPhidget22 sensor = this._sensors.get(phidget.getHubPort());
         if (sensor != null) {
            sensor.close();
            this._sensors.remove(phidget.getHubPort());
         }
      } catch (PhidgetException var3) {
         this.logger.error("Error detaching Phidget sensor", var3);
      }
   }
}
