package org.cropster.roastingintelligence.measurement.impl;

import com.phidget22.PhidgetException;
import com.phidget22.ThermocoupleType;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.sensor.SensorPhidget22;
import org.cropster.roastingintelligence.sensor.impl.TemperatureSensorPhidget22;
import org.cropster.roastingintelligence.sensor.impl.ThermocoupleTemperatureSensorPhidget22;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Phidget1048Source extends AbstractPhidget22Source {
   private final Logger logger = LoggerFactory.getLogger(Phidget1048Source.class);
   private static final int AMBIENT_CHANNEL = 4;

   public Phidget1048Source(MeasurementSourceFactory factory) {
      super(factory);
   }

   @Override
   protected SensorPhidget22 createSensor(Channel channel) throws PhidgetException {
      ThermocoupleType thermocoupleType = ThermocoupleType.valueOf(channel.getProbeType());
      return new ThermocoupleTemperatureSensorPhidget22(channel.getIndex(), false, thermocoupleType, this._host, this._port);
   }

   @Override
   public synchronized void configure(Collection<Channel> channels) {
      super.configure(channels);

      try {
         SensorPhidget22 sensor = new TemperatureSensorPhidget22(4, false, this._host, this._port);
         this._sensors.put(4, sensor);
         sensor.setListener(this);
      } catch (PhidgetException var3) {
         this.logger.error("Error configuring ambient channel", var3);
      }
   }

   @Override
   public synchronized Double getValue(String channelName) {
      return channelName.equals("ambientTemperature") ? this.getAmbientTemperature() : this.getValues().get(channelName);
   }

   public Double getAmbientTemperature() {
      return this._sensors.containsKey(4) ? this._sensors.get(4).getValue() : null;
   }
}
