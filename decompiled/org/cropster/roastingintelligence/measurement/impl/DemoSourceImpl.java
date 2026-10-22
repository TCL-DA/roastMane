package org.cropster.roastingintelligence.measurement.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.cropster.roastingintelligence.RoastingModel;
import org.cropster.roastingintelligence.listener.RoastingListener;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.Roast;

public class DemoSourceImpl extends AbstractMeasurementSource implements RoastingListener {
   private final Random _random = new Random();
   private final Roast _roast;
   private final Map<String, Channel> _channels;
   private final Map<String, Double> _defaultValues;
   private final Map<String, Queue<Double>> _curves;
   private volatile boolean _ready;

   public DemoSourceImpl(MeasurementSourceFactory factory, Roast roast) {
      super(factory);
      this._roast = roast;
      this._channels = new LinkedHashMap<>();
      this._curves = new ConcurrentHashMap<>();
      this._defaultValues = new ConcurrentHashMap<>();

      for (Entry<String, Curve> entry : this._roast.getCurves().entrySet()) {
         this._defaultValues.put(entry.getKey(), entry.getValue().firstEntry().getValue());
      }
   }

   @Override
   public void open() {
      this._ready = true;
      this.fireStatusChanged();
   }

   @Override
   public boolean isReady() {
      return this._ready;
   }

   @Override
   public void configure(Collection<Channel> channels) {
      this._channels.clear();

      for (Channel channel : channels) {
         this._channels.put(channel.getName(), channel);
      }
   }

   @Override
   public Double getValue(String channelName) {
      return this._curves.containsKey(channelName) ? this.getValues().get(channelName) : null;
   }

   @Override
   public Map<String, Double> getValues() {
      Map<String, Double> values = new HashMap<>();
      if (this._curves.isEmpty()) {
         for (Entry<String, Double> entry : this._defaultValues.entrySet()) {
            Double value = entry.getValue() + (double)this._random.nextInt(50) / 10.0;
            values.put(entry.getKey(), value);
         }
      } else {
         for (Entry<String, Queue<Double>> entry : this._curves.entrySet()) {
            values.put(entry.getKey(), entry.getValue().poll());
         }

         Iterator<Queue<Double>> it = this._curves.values().iterator();

         while (it.hasNext()) {
            if (it.next().isEmpty()) {
               it.remove();
            }
         }
      }

      return values;
   }

   @Override
   public void close() throws IOException {
      this._ready = false;
      this.fireStatusChanged();
   }

   @Override
   public void roastingStarted(RoastingModel roastingModel) {
      this._curves.clear();

      for (Entry<String, Curve> entry : this._roast.getCurves().entrySet()) {
         if (!entry.getKey().matches(".+[cC]ontrol")) {
            this._curves.put(entry.getKey(), new LinkedList<>(entry.getValue().values()));
         }
      }

      for (Channel channel : this._channels.values()) {
         if (channel.isActive()) {
            roastingModel.getCurveModel(channel).getRootModel().getValues().clear();
            roastingModel.getCurveModel(channel).getRootModel().fireCurveChanged();
         }
      }
   }

   @Override
   public void roastingForced(RoastingModel roastingModel) {
   }

   @Override
   public void roastingAborted(RoastingModel roastingModel) {
      this._curves.clear();
   }

   @Override
   public void roastingStopped(RoastingModel roastingModel) {
   }

   @Override
   public void roastingFinalized(RoastingModel roastingModel) {
   }
}
