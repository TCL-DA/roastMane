package org.cropster.roastingintelligence.measurement.impl;

import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.sensor.SensorPhidget22;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractManagedPhidget22Source extends AbstractPhidget22Source implements PhidgetDeviceDetector.DeviceFilter {
   private final Logger logger = LoggerFactory.getLogger(AbstractManagedPhidget22Source.class);
   protected final PhidgetDeviceDetector _detector;
   protected final Map<Integer, Phidget> _attachedDevices = new LinkedHashMap<>();

   public AbstractManagedPhidget22Source(MeasurementSourceFactory factory, String host, Integer port, PhidgetDeviceDetector deviceDetector) {
      super(factory, host, port);
      this._detector = deviceDetector;
   }

   public AbstractManagedPhidget22Source(MeasurementSourceFactory factory, PhidgetDeviceDetector deviceDetector) {
      this(factory, null, null, deviceDetector);
   }

   @Override
   public synchronized void configure(Collection<Channel> channels) {
      Set<Integer> unusedChannels = new HashSet<>(this.getChannels().keySet());
      this.getChannels().clear();

      for (Channel channel : channels) {
         this.logger.debug("Channel: {}, {}, {}", new Object[]{channel.getIndex(), channel.getProbeType(), channel.getName()});
         this.getChannels().put(channel.getIndex(), channel.clone());
      }

      unusedChannels.removeAll(this.getChannels().keySet());

      for (Integer channelIndex : unusedChannels) {
         SensorPhidget22 sensor = this._sensors.get(channelIndex);
         if (sensor != null) {
            sensor.close();
            sensor.setListener(null);
            this._sensors.remove(channelIndex);
         }
      }

      for (Channel channel : this.getChannels().values()) {
         if (this._sensors.containsKey(channel.getIndex())) {
            try {
               this._sensors.get(channel.getIndex()).configureChannel(channel);
            } catch (PhidgetException var7) {
               this.logger.error("Error reconfiguring sensor at channel {}", channel.getIndex());
            }
         } else {
            Phidget phidget = this._attachedDevices.get(channel.getIndex());
            if (phidget != null) {
               try {
                  this.logger.info("Creating sensor {}", channel.getIndex());
                  SensorPhidget22 sensor = this.createSensor(channel);
                  if (sensor != null) {
                     sensor.setListener(this);
                     this._sensors.put(channel.getIndex(), sensor);
                     sensor.open();
                  } else {
                     this.logger.error("Error creating sensor {}", channel.getIndex());
                  }
               } catch (PhidgetException var8) {
                  this.logger.error("Error creating sensor at channel {}", channel.getIndex());
               }
            }
         }
      }
   }

   @Override
   public synchronized void open() {
      if (!this._detector.isOpen()) {
         this._detector.open();
      }
   }

   @Override
   public void close() throws IOException {
      super.close();
      this._detector.removeDeviceFilter(this.getFactory().getClass().getSimpleName());
   }
}
