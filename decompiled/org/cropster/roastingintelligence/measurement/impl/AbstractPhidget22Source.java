package org.cropster.roastingintelligence.measurement.impl;

import com.phidget22.ErrorCode;
import com.phidget22.Net;
import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.sensor.SensorListenerPhidget22;
import org.cropster.roastingintelligence.sensor.SensorPhidget22;
import org.cropster.roastingintelligence.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPhidget22Source extends AbstractMeasurementSource implements SensorListenerPhidget22 {
   protected static final int DEFAULT_PORT = 5001;
   private final Logger logger = LoggerFactory.getLogger(AbstractPhidget22Source.class);
   private final Map<Integer, Channel> _channels = new LinkedHashMap<>();
   protected final Map<Integer, SensorPhidget22> _sensors = new ConcurrentHashMap<>();
   private volatile boolean _ready = false;
   protected String _host;
   protected Integer _port;

   public AbstractPhidget22Source(MeasurementSourceFactory factory) {
      this(factory, null, null);
   }

   public AbstractPhidget22Source(MeasurementSourceFactory factory, String host, Integer port) {
      super(factory);
      this.setNetworkParams(host, port);
      if (this._host == null) {
         String portProperty = System.getProperty("org.cropster.roastingintelligence.phidgets.port");
         this.setNetworkParams(
            System.getProperty("org.cropster.roastingintelligence.phidgets.ip"), StringUtils.isNotBlank(portProperty) ? Integer.valueOf(portProperty) : null
         );
      }

      if (this._host != null && this._port != null) {
         try {
            Net.addServer("remote", this._host, this._port, "", 0);
            this.logger.debug("Added remote phidget server {}:{}", this._host, this._port);
         } catch (PhidgetException var5) {
            if (var5.getErrorCode().equals(ErrorCode.DUPLICATE)) {
               this.logger.debug("Duplicate remote phidget server {}:{}", this._host, this._port);
            } else {
               this.logger.error("Error adding remote phidget server {}:{}", new Object[]{this._host, this._port, var5});
            }
         }

         this.logger.info("Connecting to server @{}:{}", this._host, this._port);
      }
   }

   private void setNetworkParams(String host, Integer port) {
      this._host = (String)StringUtils.defaultIfBlank(host, null);
      this._port = this._host == null ? null : port != null ? port : 5001;
   }

   protected abstract SensorPhidget22 createSensor(Channel var1) throws PhidgetException;

   protected Collection<? extends Phidget> getPhidgets() {
      ArrayList<Phidget> phidgets = new ArrayList<>();

      for (SensorPhidget22 sensor : this._sensors.values()) {
         phidgets.add(sensor.getPhidget());
      }

      return phidgets;
   }

   protected String getPhidgetName(Phidget phidget) {
      try {
         return phidget.getIsHubPortDevice()
            ? String.format("%s (Hub Port %d)", phidget.getClass().getSimpleName(), phidget.getHubPort())
            : String.format("%s (Channel %d)", phidget.getClass().getSimpleName(), phidget.getChannel());
      } catch (PhidgetException var3) {
         this.logger.error("Error accessing phidget {}", phidget.getClass().getSimpleName(), var3);
         return phidget.getClass().getSimpleName();
      }
   }

   @Override
   public synchronized void open() {
      for (SensorPhidget22 sensor : this._sensors.values()) {
         sensor.open();
      }
   }

   @Override
   public synchronized void close() throws IOException {
      this._ready = false;

      for (SensorPhidget22 sensor : this._sensors.values()) {
         sensor.close();
      }

      this.fireStatusChanged();
   }

   @Override
   public synchronized boolean isReady() {
      this.checkReady();
      return this._ready;
   }

   protected Map<Integer, Channel> getChannels() {
      return this._channels;
   }

   @Override
   public synchronized void configure(Collection<Channel> channels) {
      try {
         this.close();
      } catch (IOException var6) {
         this.logger.error("Error closing {}", this.getClass().getSimpleName(), var6);
      }

      this._channels.clear();

      for (Channel channel : channels) {
         this.logger.info("Configuring {}", channel);
         this._channels.put(channel.getIndex(), channel.clone());
      }

      this._sensors.clear();

      for (Channel channel : this.getChannels().values()) {
         if (channel.isActive()) {
            try {
               SensorPhidget22 sensor = this.createSensor(channel);
               this._sensors.put(channel.getIndex(), sensor);
               sensor.setListener(this);
            } catch (PhidgetException var5) {
               this.logger.error("Error creating Phidget at channel {}", channel.getIndex(), var5);
            }
         }
      }
   }

   @Override
   public synchronized Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   @Override
   public Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         Map<String, Double> values = new LinkedHashMap<>();

         for (Channel channel : this.getChannels().values()) {
            if (channel.isActive()) {
               try {
                  if (this._sensors.containsKey(channel.getIndex())) {
                     SensorPhidget22 sensor = this._sensors.get(channel.getIndex());
                     Double temperature = sensor.getValue();
                     if (temperature != null) {
                        values.put(channel.getName(), temperature);
                     }
                  }
               } catch (Exception var6) {
                  this.logger.error("Error reading from sensor channel {}", channel.getIndex(), var6);
               }
            }
         }

         return values;
      }
   }

   @Override
   public void sensorAttached() {
      this.checkReady();
   }

   @Override
   public void sensorDetached() {
      this.checkReady();
   }

   void checkReady() {
      boolean newReady = false;

      for (SensorPhidget22 sensor : this._sensors.values()) {
         if (!sensor.getAttached()) {
            newReady = false;
            break;
         }

         newReady = true;
      }

      if (this._ready != newReady) {
         this._ready = newReady;
         this.fireStatusChanged();
      }
   }

   public static double value2Percentage(double value, double minValue, double maxValue) {
      return MathUtil.roundHalfToEven((value - minValue) / (maxValue - minValue) * 100.0, 0);
   }
}
