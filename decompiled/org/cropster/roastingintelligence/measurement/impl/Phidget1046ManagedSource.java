package org.cropster.roastingintelligence.measurement.impl;

import com.phidget22.ChannelClass;
import com.phidget22.DeviceID;
import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import com.phidget22.VoltageRatioInput;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.sensor.SensorPhidget22;
import org.cropster.roastingintelligence.sensor.impl.RtdSensorPhidget22;
import org.cropster.roastingintelligence.sensor.impl.VoltageRatioToTemperatureConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Phidget1046ManagedSource extends AbstractManagedPhidget22Source {
   protected final Logger logger = LoggerFactory.getLogger(Phidget1046ManagedSource.class);

   public Phidget1046ManagedSource(MeasurementSourceFactory factory, PhidgetDeviceDetector deviceDetector) {
      super(factory, deviceDetector);
      this._detector.addDeviceFilter(this.getFactory().getClass().getSimpleName(), this);
   }

   private double getResistance(String probeType) {
      return "Pt1000".equalsIgnoreCase(probeType) ? 1000.0 : 100.0;
   }

   @Override
   protected SensorPhidget22 createSensor(Channel channel) throws PhidgetException {
      double resistance = this.getResistance(channel.getProbeType());
      VoltageRatioInput phidget = (VoltageRatioInput)this._attachedDevices.get(channel.getIndex());
      SensorPhidget22 sensor = new RtdSensorPhidget22(phidget, false, new VoltageRatioToTemperatureConverter(resistance));
      return sensor;
   }

   @Override
   public boolean filter(Phidget phidget) throws PhidgetException {
      return DeviceID.PN_1046.equals(phidget.getDeviceID())
         && ChannelClass.VOLTAGE_RATIO_INPUT.equals(phidget.getChannelClass())
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
         this._attachedDevices.put(phidget.getChannel(), (VoltageRatioInput)phidget);
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
         }
      } catch (PhidgetException var4) {
         this.logger.error("Error attaching Phidget sensor", var4);
      }
   }
}
