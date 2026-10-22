package org.cropster.roastingintelligence.measurement.impl;

import com.phidget22.ChannelClass;
import com.phidget22.DeviceID;
import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import com.phidget22.RTDType;
import com.phidget22.RTDWireSetup;
import com.phidget22.TemperatureSensor;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.sensor.SensorPhidget22;
import org.cropster.roastingintelligence.sensor.impl.Tmp1200SensorPhidget22;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhidgetTmp1200Source extends AbstractManagedPhidget22Source {
   private final Logger logger = LoggerFactory.getLogger(PhidgetTmp1200Source.class);
   protected RTDWireSetup _wireSetup;

   public PhidgetTmp1200Source(MeasurementSourceFactory factory, RTDWireSetup wireSetup, PhidgetDeviceDetector deviceDetector) {
      this(factory, wireSetup, null, null, deviceDetector);
   }

   public PhidgetTmp1200Source(MeasurementSourceFactory factory, RTDWireSetup wireSetup, String host, Integer port, PhidgetDeviceDetector deviceDetector) {
      super(factory, host, port, deviceDetector);
      if (wireSetup == null) {
         throw new IllegalArgumentException("RTD wire setup not set");
      } else {
         this._wireSetup = wireSetup;
         this._detector.addDeviceFilter(this.getFactory().getClass().getSimpleName(), this);
      }
   }

   @Override
   protected SensorPhidget22 createSensor(Channel channel) throws PhidgetException {
      RTDType rtdType = RTDType.valueOf(channel.getProbeType());
      TemperatureSensor phidget = (TemperatureSensor)this._attachedDevices.get(channel.getIndex());
      return phidget == null ? null : new Tmp1200SensorPhidget22(phidget, rtdType, this._wireSetup, this._host, this._port);
   }

   @Override
   public boolean filter(Phidget phidget) throws PhidgetException {
      return DeviceID.PN_TMP1200.equals(phidget.getDeviceID())
         && ChannelClass.TEMPERATURE_SENSOR.equals(phidget.getChannelClass())
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
