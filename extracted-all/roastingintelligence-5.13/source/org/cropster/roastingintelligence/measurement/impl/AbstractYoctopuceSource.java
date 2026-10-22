package org.cropster.roastingintelligence.measurement.impl;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YTemperature;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractYoctopuceSource extends AbstractMeasurementSource {
   public static final String TYPE_N = "N";
   public static final String TYPE_S = "S";
   public static final String TYPE_R = "R";
   private final Logger logger = LoggerFactory.getLogger(AbstractYoctopuceSource.class);
   private final Map<Integer, Channel> _channels = new LinkedHashMap<>();
   private YTemperature temperatureSensor;

   protected static int getYoctoSensorType(String type) {
      if ("K".equals(type)) {
         return 1;
      } else if ("S".equals(type)) {
         return 6;
      } else if ("N".equals(type)) {
         return 4;
      } else if ("E".equals(type)) {
         return 2;
      } else if ("J".equals(type)) {
         return 3;
      } else if ("R".equals(type)) {
         return 5;
      } else if ("T".equals(type)) {
         return 7;
      } else {
         throw new IllegalArgumentException("Probe type not supported: " + type);
      }
   }

   public AbstractYoctopuceSource(MeasurementSourceFactory factory) {
      super(factory);
      this.startListener();
   }

   private void startListener() {
      Timeline yoctoDeviceListener = new Timeline(new KeyFrame[]{new KeyFrame(Duration.seconds(0.5), new EventHandler<ActionEvent>() {
         public void handle(ActionEvent event) {
            try {
               YAPI.UpdateDeviceList();
               YAPI.Sleep(100L);
            } catch (YAPI_Exception var3) {
               var3.printStackTrace();
            }
         }
      }, new KeyValue[0])});
      yoctoDeviceListener.setCycleCount(-1);
      yoctoDeviceListener.play();
   }

   @Override
   public void open() {
      this.fireStatusChanged();
   }

   protected Map<Integer, Channel> getChannels() {
      return this._channels;
   }

   protected abstract YTemperature getYoctoSensor(int var1);

   @Override
   public void configure(Collection<Channel> channels) {
      this._channels.clear();

      for (Channel channel : channels) {
         this.logger.info("Configuring {}", channel);
         this._channels.put(channel.getIndex(), channel.clone());
      }
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   @Override
   public Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         try {
            Map<String, Double> values = new LinkedHashMap<>();

            for (Channel channel : this.getChannels().values()) {
               if (channel.isActive()) {
                  this.temperatureSensor = this.getYoctoSensor(channel.getIndex() + 1);
                  this.temperatureSensor.setSensorType(getYoctoSensorType(channel.getProbeType()));
                  double sensorValue = this.temperatureSensor.get_currentValue();
                  values.put(channel.getName(), sensorValue);
               }
            }

            return values;
         } catch (Exception var6) {
            return Collections.emptyMap();
         }
      }
   }

   @Override
   public void close() throws IOException {
      YAPI.FreeAPI();
      this.fireStatusChanged();
   }
}
