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
import org.cropster.roastingintelligence.sensor.impl.TemperatureSensorPhidget22;
import org.cropster.roastingintelligence.sensor.impl.ThermocoupleTemperatureSensorPhidget22;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Phidget1048ManagedSource extends AbstractManagedPhidget22Source {
   private final Logger logger = LoggerFactory.getLogger(Phidget1048ManagedSource.class);
   protected static final int AMBIENT_CHANNEL = 4;

   public Phidget1048ManagedSource(MeasurementSourceFactory factory, String host, Integer port, PhidgetDeviceDetector deviceDetector) {
      super(factory, host, port, deviceDetector);
      this._detector.addDeviceFilter(this.getFactory().getClass().getSimpleName(), this);
   }

   public Phidget1048ManagedSource(MeasurementSourceFactory factory, PhidgetDeviceDetector deviceDetector) {
      this(factory, null, null, deviceDetector);
   }

   @Override
   public boolean filter(Phidget phidget) throws PhidgetException {
      return (DeviceID.PN_1048.equals(phidget.getDeviceID()) || DeviceID.PN_1051.equals(phidget.getDeviceID()))
         && ChannelClass.TEMPERATURE_SENSOR.equals(phidget.getChannelClass())
         && phidget.getIsRemote() == (this._host != null && this._port != null);
   }

   @Override
   public void detachHandler(Phidget phidget) {
      try {
         this._attachedDevices.remove(phidget.getChannel());
         SensorPhidget22 sensor = this._sensors.get(phidget.getChannel());
         if (sensor != null) {
            sensor.close();
            this._sensors.remove(phidget.getChannel());
         }
      } catch (PhidgetException var3) {
         this.logger.error("Error detaching Phidget sensor", var3);
      }
   }

   @Override
   public void attachHandler(Phidget phidget) {
      try {
         this._attachedDevices.put(phidget.getChannel(), (TemperatureSensor)phidget);
         Channel channel = this.getChannels().get(phidget.getChannel());
         if (channel != null) {
            SensorPhidget22 sensor = this.createSensor(channel);
            if (sensor != null) {
               sensor.setListener(this);
               this._sensors.put(channel.getIndex(), sensor);
               sensor.open();
            } else {
               this.logger.error("Error creating sensor {}", channel.getIndex());
            }
         } else if (phidget.getChannel() == 4) {
            SensorPhidget22 ambientSensor = new TemperatureSensorPhidget22(
               (TemperatureSensor)phidget, DeviceClass.VINT.equals(phidget.getDeviceClass()), this._host, this._port
            );
            ambientSensor.setListener(this);
            this._sensors.put(4, ambientSensor);
            ambientSensor.open();
         }
      } catch (PhidgetException var4) {
         this.logger.error("Error attaching Phidget sensor", var4);
      }
   }

   @Override
   public synchronized Double getValue(String channelName) {
      return channelName.equals("ambientTemperature") ? this.getAmbientTemperature() : this.getValues().get(channelName);
   }

   public Double getAmbientTemperature() {
      SensorPhidget22 sensor = this._sensors.get(4);
      return sensor != null ? this._sensors.get(4).getValue() : null;
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
}
