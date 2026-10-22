package org.cropster.roastingintelligence.measurement.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.application.Platform;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.listener.SettingsListener;
import org.cropster.roastingintelligence.measurement.ControlStateSource;
import org.cropster.roastingintelligence.measurement.MeasurementMonitor;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.TimeEmitter;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.ControlState;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.util.MathUtil;
import org.cropster.roastingintelligence.util.NanoTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeasurementMonitorImpl implements MeasurementMonitor, SettingsListener {
   private final Logger logger = LoggerFactory.getLogger(MeasurementMonitorImpl.class);
   private static final long RECONNECT_INTERVAL = 5000L;
   private TimerTask _channelTimerTask;
   private final Timer _timer;
   private final TimeEmitter _timeEmitter;
   private final MeasurementSource _source;
   private final CopyOnWriteArrayList<MeasurementModel> _models;
   private long _lastReconnect;

   public MeasurementMonitorImpl(TimeEmitter timeEmitter, MeasurementSource source) {
      this._timer = new Timer(source.getClass().getSimpleName() + "-Timer", true);
      this._timeEmitter = timeEmitter;
      this._source = source;
      this._models = new CopyOnWriteArrayList<>();
   }

   @Override
   public MeasurementSource getSource() {
      return this._source;
   }

   @Override
   public void addModel(MeasurementModel model) {
      this._models.add(model);
   }

   @Override
   public void removeModel(MeasurementModel model) {
      this._models.remove(model);
   }

   synchronized void startChannelTimer() {
      this.stopChannelTimer();
      Date start = new Date(NanoTime.currentTimeMillis() / 1000L * 1000L + 1000L);
      this._channelTimerTask = new MeasurementMonitorImpl.MonitoringTimerTask();
      this._timer.scheduleAtFixedRate(this._channelTimerTask, start, this._timeEmitter.getInterval());
   }

   synchronized void stopChannelTimer() {
      if (this._channelTimerTask != null) {
         this._channelTimerTask.cancel();
         this._timer.purge();
      }
   }

   @Override
   public void start() {
      this.startChannelTimer();
   }

   @Override
   public void stop() throws IOException {
      this.stopChannelTimer();
      this._timer.cancel();
      this._source.close();
   }

   @Override
   public void settingsChanged(AppModel appModel) {
      Collection<Channel> channels = appModel.getChannelsBySource((Class<? extends MeasurementSourceFactory>)this._source.getFactory().getClass());
      this._source.configure(channels);
   }

   Map<String, Double> filter(Map<String, Double> measurements, MeasurementSource source) {
      Iterator<Entry<String, Double>> it = measurements.entrySet().iterator();

      while (it.hasNext()) {
         Entry<String, Double> entry = it.next();
         Double value = entry.getValue();
         String measurement = entry.getKey();
         if (value != null && !source.isValid(measurement, value)) {
            this.logger.warn("Channel {} value out of bounds (-500;+3000): {} C", measurement, value);
            it.remove();
         }
      }

      return measurements;
   }

   private final class MonitoringTimerTask extends TimerTask {
      @Override
      public void run() {
         long currentMillis = MeasurementMonitorImpl.this._timeEmitter.getTime();
         boolean reconnect = currentMillis > MeasurementMonitorImpl.this._lastReconnect + 5000L;
         long roundedMillis = MathUtil.roundLong(currentMillis, 1000L);
         final Date now = new Date(roundedMillis);
         final Map<String, Double> values = new HashMap<>();
         final Map<ControlState, Boolean> controlStateValues = new HashMap<>();
         if (MeasurementMonitorImpl.this._source.isReady()) {
            Map<String, Double> sourceValues = MeasurementMonitorImpl.this.filter(
               MeasurementMonitorImpl.this._source.getValues(), MeasurementMonitorImpl.this._source
            );

            for (MeasurementModel model : MeasurementMonitorImpl.this._models) {
               for (Channel channel : model.getChannels()) {
                  if (MeasurementMonitorImpl.this._source.getFactory().getClass().equals(channel.getSourceFactory())) {
                     Double value = sourceValues.get(channel.getName());
                     if (value != null) {
                        value = MathUtil.roundHalfToEven(value, 1);
                     }

                     values.put(channel.getName(), value);
                  }
               }
            }

            if (MeasurementMonitorImpl.this._source instanceof ControlStateSource controlStateSource) {
               Map<ControlState, Boolean> stateValues = controlStateSource.getStates();

               for (MeasurementModel model : MeasurementMonitorImpl.this._models) {
                  for (ControlState controlState : model.getControlStates()) {
                     if (stateValues.containsKey(controlState)) {
                        controlStateValues.put(controlState, stateValues.get(controlState));
                     }
                  }
               }
            }
         } else if (reconnect) {
            MeasurementMonitorImpl.this._source.open();
         }

         if (reconnect) {
            MeasurementMonitorImpl.this._lastReconnect = currentMillis;
         }

         Platform.runLater(new Runnable() {
            @Override
            public void run() {
               for (MeasurementModel tempModel : MeasurementMonitorImpl.this._models) {
                  for (Channel channel : tempModel.getChannels()) {
                     if (channel.isActive()) {
                        Double value = values.get(channel.getName());
                        if (value != null) {
                           tempModel.put(channel, now, value);
                        }
                     }
                  }
               }

               if (MeasurementMonitorImpl.this._source instanceof ControlStateSource) {
                  for (MeasurementModel tempModel : MeasurementMonitorImpl.this._models) {
                     for (ControlState controlState : tempModel.getControlStates()) {
                        Boolean value = controlStateValues.get(controlState);
                        tempModel.putState(controlState, value);
                     }
                  }
               }
            }
         });
      }
   }
}
