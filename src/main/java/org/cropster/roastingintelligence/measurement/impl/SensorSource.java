package org.cropster.roastingintelligence.measurement.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.sensor.Sensor;
import org.cropster.roastingintelligence.sensor.SensorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorSource extends AbstractMeasurementSource implements SensorListener {
   private final Logger logger = LoggerFactory.getLogger(SensorSource.class);
   private final Sensor _sensor;
   private final ConcurrentHashMap<String, Double> _values;
   private final Map<Integer, Channel> _channels = new LinkedHashMap<>();

   public SensorSource(MeasurementSourceFactory factory, Sensor sensor) {
      super(factory);
      if (sensor == null) {
         throw new NullPointerException("sensor must not be null");
      } else {
         this._sensor = sensor;
         this._values = new ConcurrentHashMap<>(4);
      }
   }

   @Override
   public synchronized void open() {
      try {
         this._sensor.addSensorListener(this);
         this._sensor.open();
      } catch (IOException var2) {
         this.logger.error("Error opening sensor", var2);
      }
   }

   @Override
   public synchronized void close() throws IOException {
      try {
         this._sensor.removeSensorListener(this);
         this._sensor.close();
         this.logger.info("Sensor closed");
      } catch (IOException var2) {
         this.logger.error("Error closing sensor", var2);
      }
   }

   @Override
   public synchronized boolean isReady() {
      return this._sensor.isAttached();
   }

   @Override
   public Double getValue(String channelName) {
      return channelName.equals("ambientTemperature") ? this._sensor.getAmbientTemperature() : this._values.get(channelName);
   }

   @Override
   public Map<String, Double> getValues() {
      HashMap<String, Double> values = new HashMap<>(this._values);
      values.put("ambientTemperature", this._sensor.getAmbientTemperature());
      return values;
   }

   @Override
   public void sensorAttached() {
      this._sensor.configure(new ArrayList<>(this._channels.values()));
      this.fireStatusChanged();
   }

   @Override
   public void sensorDetached() {
      this._values.clear();
      this.fireStatusChanged();
   }

   @Override
   public void sensorValueChanged(int channelIndex, double value) {
      if (this._channels.containsKey(channelIndex)) {
         Channel channel = this._channels.get(channelIndex);
         if (channel.isActive()) {
            this._values.put(channel.getName(), value);
         }
      } else {
         this.logger.warn("Unexpected event for sensor channel {}", channelIndex);
      }
   }

   @Override
   public void sensorError(Exception exception) {
      this.logger.error("Sensor error", exception);
   }

   @Override
   public void configure(Collection<Channel> channels) {
      this._channels.clear();

      for (Channel channel : channels) {
         this.logger.info("Configuring {}", channel);
         this._channels.put(channel.getIndex(), channel.clone());
      }

      this._sensor.configure(channels);
   }
}
