package org.cropster.roastingintelligence.measurement.impl;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YModule;
import com.yoctopuce.YoctoAPI.YTemperature;
import com.yoctopuce.YoctoAPI.YAPI.DeviceArrivalCallback;
import com.yoctopuce.YoctoAPI.YAPI.DeviceRemovalCallback;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YoctopuceSource extends AbstractMeasurementSource implements Runnable, DeviceRemovalCallback, DeviceArrivalCallback {
   private final Logger logger = LoggerFactory.getLogger(YoctopuceSource.class);
   private final Map<String, YTemperature> sensors = new HashMap<>();
   private final Map<Integer, Channel> _channels = new LinkedHashMap<>();
   private volatile Thread deviceUpdateThread;

   public YoctopuceSource(MeasurementSourceFactory factory) {
      super(factory);
   }

   @Override
   public void configure(Collection<Channel> channels) {
      this._channels.clear();

      for (Channel channel : channels) {
         this.logger.info("Configuring {}", channel);
         this._channels.put(channel.getIndex(), channel.clone());
      }
   }

   @Override
   public void open() {
      try {
         YAPI.RegisterDeviceArrivalCallback(this);
         YAPI.RegisterDeviceRemovalCallback(this);
         YAPI.RegisterHub("localhost:4444");
         if (this.deviceUpdateThread == null) {
            this.deviceUpdateThread = new Thread(this);
            this.deviceUpdateThread.setName("YoctopuceDeviceUpdater");
            this.deviceUpdateThread.setDaemon(true);
            this.deviceUpdateThread.start();
         }

         for (YTemperature sensor = YTemperature.FirstTemperature(); sensor != null; sensor = sensor.nextTemperature()) {
            this.logger.info("Sensor detected: {}{}", sensor, StringUtils.isNotBlank(sensor.getLogicalName()) ? "/" + sensor.getLogicalName() : "");
            Iterator var2 = this.getChannels().values().iterator();

            while (true) {
               if (var2.hasNext()) {
                  Channel channel = (Channel)var2.next();
                  if (!sensor.getFunctionId().equals("temperature" + (channel.getIndex() + 1))) {
                     continue;
                  }

                  this.sensors.put(channel.getName(), sensor);
               }

               for (Channel channel : this.getChannels().values()) {
                  if (channel.getName().equals(sensor.getLogicalName())) {
                     this.sensors.put(channel.getName(), sensor);
                     break;
                  }
               }
               break;
            }
         }

         for (Channel channelx : this._channels.values()) {
            if (this.sensors.containsKey(channelx.getName())) {
               YTemperature var5 = this.sensors.get(channelx.getName());
               if (var5.isOnline()) {
                  var5.setSensorType(AbstractYoctopuceSource.getYoctoSensorType(channelx.getProbeType()));
               }
            }
         }

         this.fireStatusChanged();
      } catch (Exception var4) {
         this.logger.error("Error detecting sensors", var4);
      }
   }

   @Override
   public boolean isReady() {
      for (YTemperature sensor : this.sensors.values()) {
         if (sensor.isOnline()) {
            return true;
         }
      }

      return false;
   }

   protected Map<Integer, Channel> getChannels() {
      return this._channels;
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
            YAPI.HandleEvents();
            Map<String, Double> values = new LinkedHashMap<>();

            for (Channel channel : this.getChannels().values()) {
               if (channel.isActive()) {
                  YTemperature sensor = this.sensors.get(channel.getName());
                  if (sensor != null) {
                     values.put(channel.getName(), sensor.getCurrentValue());
                  } else {
                     this.logger.warn("Yoctopuce sensor not found for {}", channel.getName());
                  }
               }
            }

            return values;
         } catch (Exception var5) {
            this.logger.error("Error reading from Yoctopuce sensor", var5);
            return Collections.emptyMap();
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.deviceUpdateThread = null;
      this.fireStatusChanged();
   }

   @Override
   public void run() {
      while (this.deviceUpdateThread == Thread.currentThread()) {
         try {
            YAPI.UpdateDeviceList();
            Thread.sleep(5000L);
         } catch (Exception var2) {
            this.logger.error("Error updating device list", var2);
         }
      }
   }

   public void yDeviceArrival(YModule module) {
      try {
         this.logger.info("Attached {}", module.getFriendlyName());
      } catch (YAPI_Exception var3) {
         this.logger.error("Error on device arrival", var3);
      }

      this.fireStatusChanged();
   }

   public void yDeviceRemoval(YModule module) {
      try {
         this.logger.info("Detached {}", module.getFriendlyName());
      } catch (YAPI_Exception var3) {
         this.logger.error("Error on device removal", var3);
      }

      this.fireStatusChanged();
   }
}
