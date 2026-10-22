package org.cropster.roastingintelligence;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import org.cropster.roastingintelligence.listener.SettingsListener;
import org.cropster.roastingintelligence.measurement.AutoStartSource;
import org.cropster.roastingintelligence.measurement.ControlStateSource;
import org.cropster.roastingintelligence.measurement.MeasurementListener;
import org.cropster.roastingintelligence.measurement.MeasurementMonitor;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.TimeEmitter;
import org.cropster.roastingintelligence.measurement.impl.DefaultTimeEmitter;
import org.cropster.roastingintelligence.measurement.impl.DemoTimeEmitter;
import org.cropster.roastingintelligence.measurement.impl.KraffePlcSourceFactory;
import org.cropster.roastingintelligence.measurement.impl.MeasurementMonitorImpl;
import org.cropster.roastingintelligence.measurement.impl.PhidgetTmp1101SourceFactory;
import org.cropster.roastingintelligence.measurement.impl.TemperaturePhidgetSourceFactory;
import org.cropster.roastingintelligence.model.AutoStartListener;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.ControlState;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.impl.ConverterCurveModel;
import org.cropster.roastingintelligence.model.impl.MovingAverageCurveModel;
import org.cropster.roastingintelligence.model.impl.RorCurveModel;
import org.cropster.roastingintelligence.model.impl.ScalingCurveModel;
import org.cropster.roastingintelligence.model.impl.StandardCurveModel;
import org.cropster.roastingintelligence.util.NanoTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeasurementHub implements MeasurementModel, SettingsListener {
   private final Logger logger = LoggerFactory.getLogger(MeasurementHub.class);
   private final Map<String, Channel> _channels = new ConcurrentHashMap<>();
   private final Map<String, CurveModel> _curveModels = new LinkedHashMap<>();
   private final Map<String, CurveModel> _rorModels = new LinkedHashMap<>();
   private final Map<MeasurementSource, MeasurementMonitor> _measurementSources;
   private TimeEmitter timeEmitter;
   private final Map<ControlState, Boolean> _controlStates = new LinkedHashMap<>();
   private MeasurementSource _ambientSource;

   public MeasurementHub(
      RoastingManager roastingManager,
      AppModel appModel,
      Set<MeasurementSourceFactory> sourceFactories,
      MeasurementListener listener,
      AutoStartListener autoStartListener
   ) throws IOException {
      if (appModel.isDemoMode() && appModel.getPreferences().isTimeLapseEnabled()) {
         DemoTimeEmitter demoTimeEmitter = new DemoTimeEmitter();
         roastingManager.addRoastingListener(demoTimeEmitter);
         this.timeEmitter = demoTimeEmitter;
      } else {
         this.timeEmitter = new DefaultTimeEmitter();
      }

      this._measurementSources = new LinkedHashMap<>();

      for (MeasurementSourceFactory sourceFactory : sourceFactories) {
         Class<? extends MeasurementSourceFactory> factoryClass = (Class<? extends MeasurementSourceFactory>)sourceFactory.getClass();
         Set<Channel> channels = appModel.getChannelsBySource(factoryClass);
         Set<ControlState> states = appModel.getStatesBySource(factoryClass);
         boolean emptyChannels = channels.isEmpty();
         if (!emptyChannels || !states.isEmpty()) {
            for (MeasurementSource source : sourceFactory.getMeasurementSources(appModel.getActiveChannels())) {
               if (!emptyChannels || source instanceof ControlStateSource) {
                  this.initializeListeners(source, appModel, listener, autoStartListener);
                  if (!emptyChannels) {
                     source.configure(channels);
                     this.logger.info("Configured {} for {}", source.getClass().getSimpleName(), channels);
                  }

                  this.setupMonitor(source, appModel);
                  if (!emptyChannels
                     && (
                        sourceFactory instanceof TemperaturePhidgetSourceFactory
                           || sourceFactory instanceof KraffePlcSourceFactory
                           || sourceFactory instanceof PhidgetTmp1101SourceFactory
                     )) {
                     this._ambientSource = source;
                  }
               }
            }
         }
      }
   }

   private void initializeListeners(MeasurementSource source, AppModel appModel, MeasurementListener listener, AutoStartListener autoStartListener) {
      if (source instanceof SettingsListener settingsListener) {
         appModel.addSettingsListener(settingsListener);
      }

      if (source instanceof AutoStartSource autoStartSource) {
         autoStartSource.addRoastingStartListener(autoStartListener);
      }

      source.addMeasurementListener(listener);
   }

   private void setupMonitor(MeasurementSource source, AppModel appModel) {
      TimeEmitter timeEmitter = (TimeEmitter)(appModel.isDemoMode() && appModel.getPreferences().isTimeLapseEnabled()
         ? this.timeEmitter
         : new DefaultTimeEmitter((long)source.getFactory().getSamplingInterval()));
      MeasurementMonitorImpl monitor = new MeasurementMonitorImpl(timeEmitter, source);
      appModel.addSettingsListener(monitor);
      monitor.addModel(this);
      this._measurementSources.put(source, monitor);
   }

   @Override
   public Date getStartDate() {
      return this.getThresholdDate();
   }

   public long getTime() {
      return this.timeEmitter.getTime();
   }

   private Date getThresholdDate() {
      return new Date(NanoTime.currentTimeMillis() - 1800000L);
   }

   public Set<MeasurementSource> getMeasurementSources() {
      return this._measurementSources.keySet();
   }

   public Collection<MeasurementMonitor> getMeasurementMonitors() {
      return this._measurementSources.values();
   }

   public Measure getAmbientTemperature() {
      if (this._ambientSource != null && this._ambientSource.isReady()) {
         Double ambient = this._ambientSource.getValue("ambientTemperature");
         if (ambient != null) {
            Unit unit = this._ambientSource.getFactory().getUnit("ambientTemperature");
            return new Measure(ambient, unit);
         }
      }

      return null;
   }

   public void start() {
      for (MeasurementMonitor monitor : this.getMeasurementMonitors()) {
         try {
            monitor.start();
         } catch (IOException var4) {
            throw new RuntimeException("Error starting MeasurementMonitor", var4);
         }
      }
   }

   public void stop() {
      for (MeasurementMonitor monitor : this.getMeasurementMonitors()) {
         try {
            monitor.stop();
         } catch (Exception var4) {
            this.logger.error("Error closing MeasurementMonitor for {}", monitor.getSource().getClass().getSimpleName(), var4);
         }
      }
   }

   public void addModel(MeasurementModel model) {
      for (MeasurementMonitor monitor : this.getMeasurementMonitors()) {
         monitor.addModel(model);
      }
   }

   public void removeModel(MeasurementModel model) {
      for (MeasurementMonitor monitor : this.getMeasurementMonitors()) {
         monitor.removeModel(model);
      }
   }

   @Override
   public Collection<Channel> getChannels() {
      return this._channels.values();
   }

   @Override
   public CurveModel getCurveModel(Channel channel) {
      return this._curveModels.get(channel.getName());
   }

   @Override
   public CurveModel getRorModel(Channel channel) {
      return this._rorModels.get(channel.getName());
   }

   @Override
   public void put(Channel channel, Date date, Double value) {
      if (this._curveModels.containsKey(channel.getName())) {
         CurveModel curveModel = this.getCurveModel(channel).getRootModel();
         Long lastKey = curveModel.getValues().isEmpty() ? null : curveModel.getValues().lastKey();
         Long secondLastKey = null;
         if (lastKey != null) {
            SortedMap<Long, Double> headMap = curveModel.getValues().headMap(lastKey);
            if (!headMap.isEmpty()) {
               secondLastKey = headMap.lastKey();
            }
         }

         Long time = date.getTime() - curveModel.getZeroPoint().getTime();
         if (value != null) {
            curveModel.getValues().put(time, value);
         }

         curveModel.fireCurveChanged(time);
         if (!Unit.CELSIUS.isCompatible(curveModel.getUnit())
            && lastKey != null
            && secondLastKey != null
            && value.equals(curveModel.getValues().get(lastKey))
            && value.equals(curveModel.getValues().get(secondLastKey))
            && curveModel.getValues().remove(lastKey) != null) {
            curveModel.fireCurveChanged(lastKey);
         }

         Long thresholdTime = this.getThresholdDate().getTime();
         Iterator<Long> it = curveModel.getValues().headMap(thresholdTime).keySet().iterator();

         while (it.hasNext()) {
            Long t = it.next();
            it.remove();
            curveModel.fireCurveChanged(t);
         }
      }
   }

   @Override
   public Collection<ControlState> getControlStates() {
      return Collections.unmodifiableCollection(this._controlStates.keySet());
   }

   @Override
   public void putState(ControlState controlState, Boolean value) {
      this._controlStates.put(controlState, value);
   }

   @Override
   public Boolean getState(ControlState controlState) {
      return this._controlStates.get(controlState);
   }

   @Override
   public void settingsChanged(AppModel appModel) {
      this._channels.clear();
      Map<String, CurveModel> oldModels = new HashMap<>(this._curveModels);
      this._curveModels.clear();

      for (Channel channel : appModel.getActiveChannels()) {
         MeasurementSourceFactory sourceFactory = RoastingIntelligenceApp.retrieveSingleton(channel.getSourceFactory());
         Unit unit = sourceFactory.getUnit(channel.getName());
         Unit displayUnit = appModel.getPreferences().getUnit(unit);
         this._channels.put(channel.getName(), channel);
         CurveModel model = oldModels.get(channel.getName());
         if (model == null) {
            model = new StandardCurveModel(channel.getName(), new Date(0L), unit);
         }

         ConverterCurveModel converterModel = model.getModel(ConverterCurveModel.class);
         if (converterModel != null || unit.equals(displayUnit) && channel.getOffset() == 0.0) {
            if (converterModel != null) {
               converterModel.setUnit(displayUnit);
               converterModel.setOffset(channel.getOffset());
               converterModel.notifyCurveChanged(model);
            }
         } else {
            model = model.getModel(StandardCurveModel.class);
            model = new ConverterCurveModel(model, displayUnit, channel.getOffset());
         }

         this._curveModels.put(channel.getName(), model);
         if (channel.isRorEnabled() && channel.isRorSupported()) {
            CurveModel rorModel = new RorCurveModel(model, appModel.getPreferences().getRorSmoothing());
            rorModel = new ScalingCurveModel(rorModel, appModel.getPreferences().getRor());
            rorModel = new MovingAverageCurveModel(rorModel, 4);
            this._rorModels.put(channel.getName(), rorModel);
         }
      }

      this._controlStates.clear();

      for (ControlState controlState : appModel.getEnabledStates()) {
         this._controlStates.put(controlState, null);
      }
   }
}
