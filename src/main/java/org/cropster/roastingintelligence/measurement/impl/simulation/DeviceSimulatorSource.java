package org.cropster.roastingintelligence.measurement.impl.simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.cropster.roastingintelligence.measurement.ControlStateSource;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.AbstractMeasurementSource;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.ControlState;

public class DeviceSimulatorSource extends AbstractMeasurementSource implements MeasurementReaderWriter, ControlStateSource {
   private final long readWriteDelayMs = 100L;
   private final Map<String, Double> deviceValueMap = Collections.synchronizedMap(new HashMap<>());
   private final Map<ControlState, Boolean> deviceControlStateValues = Collections.synchronizedMap(new HashMap<>());
   private final ArrayList<Channel> channels = new ArrayList<>();
   private final Map<String, Double> readValueCache = Collections.synchronizedMap(new HashMap<>());
   private final Map<ControlState, Boolean> readControlStateCache = Collections.synchronizedMap(new HashMap<>());
   private boolean autoMode = false;

   public DeviceSimulatorSource(MeasurementSourceFactory factory) {
      super(factory);
      ControlState[] controlStates = factory.getSupportedStates(factory.getParameters());
      this.deviceControlStateValues
         .putAll(Map.of(controlStates[0], false, controlStates[1], false, controlStates[2], false, controlStates[3], false, controlStates[4], false));
   }

   @Override
   public Double getMinValue(String field) {
      switch (field) {
         case "gasControl":
            return 0.0;
         case "airflowControl":
            return 50.0;
         case "drumPressureControl":
            return 80.0;
         case "drumSpeedControl":
            return 35.0;
         case "setTemperatureControl":
            return 0.0;
         default:
            return null;
      }
   }

   @Override
   public Double getMaxValue(String field) {
      switch (field) {
         case "gasControl":
         case "airflowControl":
            return 100.0;
         case "drumPressureControl":
            return 180.0;
         case "drumSpeedControl":
            return 80.0;
         case "setTemperatureControl":
            return 500.0;
         default:
            return null;
      }
   }

   @Override
   public synchronized void setValue(String field, Double value) {
      try {
         Thread.sleep(100L);
      } catch (InterruptedException var4) {
         var4.printStackTrace();
      }

      this.deviceValueMap.put(field, value);
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this.channels.stream().map(c -> c.getName()).collect(Collectors.toSet());
   }

   @Override
   public void open() {
   }

   @Override
   public boolean isReady() {
      return true;
   }

   @Override
   public void configure(Collection<Channel> channels) {
      this.channels.clear();
      this.channels.addAll(channels);
      this.initializeValues(channels);
   }

   private void initializeValues(Collection<Channel> channels) {
      for (Channel channel : channels) {
         this.deviceValueMap.put(channel.getName(), this.getMinValue(channel.getName()));
         this.deviceValueMap.put(channel.getName(), this.getMinValue(channel.getName()));
      }
   }

   @Override
   public synchronized Double getValue(String channelName) {
      try {
         Thread.sleep(100L);
      } catch (InterruptedException var3) {
         var3.printStackTrace();
      }

      return this.deviceValueMap.get(channelName);
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      try {
         Thread.sleep(100L);
      } catch (InterruptedException var2) {
         var2.printStackTrace();
      }

      this.readValueCache.clear();
      this.readValueCache.putAll(this.deviceValueMap);
      this.readControlStateCache.clear();
      this.readControlStateCache.putAll(this.deviceControlStateValues);
      return this.readValueCache;
   }

   @Override
   public void close() throws IOException {
   }

   @Override
   public void setAutoMode(boolean autoMode, String measurement) {
      if ("gasControl".equals(measurement) || "setTemperatureControl".equals(measurement)) {
         this.autoMode = autoMode;
      }
   }

   @Override
   public Boolean getAutoMode(String measurement) {
      return !"gasControl".equals(measurement) && !"setTemperatureControl".equals(measurement) ? null : this.autoMode;
   }

   @Override
   public synchronized Boolean getState(ControlState state) {
      return state == null ? null : this.readControlStateCache.get(state);
   }

   @Override
   public synchronized Map<ControlState, Boolean> getStates() {
      return Collections.unmodifiableMap(this.readControlStateCache);
   }

   @Override
   public synchronized void setState(ControlState state, Boolean value) {
      if (state != null && Arrays.asList(this.getFactory().getSupportedStates(this.getFactory().getParameters())).contains(state)) {
         try {
            Thread.sleep(100L);
         } catch (InterruptedException var4) {
            var4.printStackTrace();
         }

         this.deviceControlStateValues.put(state, value);
      }
   }
}
