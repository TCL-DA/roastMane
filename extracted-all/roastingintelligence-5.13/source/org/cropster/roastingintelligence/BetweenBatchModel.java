package org.cropster.roastingintelligence;

import com.cropster.roastingintelligence.Preferences;
import com.cropster.util.model.Unit;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.ControlState;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.impl.AbstractCommentable;
import org.cropster.roastingintelligence.model.impl.ConverterCurveModel;
import org.cropster.roastingintelligence.model.impl.LinearInterpolationCurveModel;
import org.cropster.roastingintelligence.model.impl.StandardCurveModel;
import org.cropster.roastingintelligence.model.impl.StartEndPointCurveModel;
import org.cropster.roastingintelligence.model.impl.StepCurveModel;
import org.cropster.roastingintelligence.util.MathUtil;
import org.cropster.roastingintelligence.util.Measures;
import org.cropster.roastingintelligence.util.NanoTime;

public class BetweenBatchModel extends AbstractCommentable implements MeasurementModel {
   private volatile Date _startDate;
   private volatile long _time;
   private volatile long _offset;
   private volatile boolean _stopped = false;
   private final Map<String, Channel> _channels = new LinkedHashMap<>();
   private final Map<String, CurveModel> _curves = new LinkedHashMap<>();
   private final Map<ControlState, Boolean> _controlStates = new LinkedHashMap<>();

   public BetweenBatchModel(Preferences preferences, Date startDate, Collection<Channel> channels, Collection<ControlState> states) {
      this._startDate = startDate;

      for (Channel channel : channels) {
         this._channels.put(channel.getName(), channel);
         MeasurementSourceFactory sourceFactory = RoastingIntelligenceApp.retrieveSingleton(channel.getSourceFactory());
         Unit unit = sourceFactory.getUnit(channel.getName());
         Unit displayUnit = preferences.getUnit(unit);
         String betweenBatchName = Measures.curveNameForBetweenBatch(channel.getName());
         CurveModel model = new StandardCurveModel(betweenBatchName, new Date(0L), unit);
         if (!unit.equals(displayUnit) || channel.getOffset() != 0.0) {
            model = new ConverterCurveModel(model, displayUnit, channel.getOffset());
         }

         if (Unit.CELSIUS.isCompatible(model.getUnit()) && !channel.isControlChannel()) {
            if (sourceFactory.getSamplingInterval() > 1000) {
               model = new LinearInterpolationCurveModel(new StartEndPointCurveModel(model));
            }
         } else {
            model = new StepCurveModel(model);
         }

         this._curves.put(channel.getName(), model);
      }

      for (ControlState controlState : states) {
         this._controlStates.put(controlState, null);
      }
   }

   @Override
   public Date getStartDate() {
      return this._startDate;
   }

   public void setStartDate(Date startDate) {
      this._startDate = startDate;
      this._curves.values().stream().map(model -> model.getModel(StartEndPointCurveModel.class)).filter(Objects::nonNull).forEach(model -> {
         model.setStartTime(startDate.getTime());
         model.getRootModel().fireCurveChanged();
      });
   }

   @Override
   public Long getDuration() {
      return this._time - this._startDate.getTime();
   }

   public long getOffset() {
      return this._offset;
   }

   public void setOffset(long offset) {
      this._offset = offset;
   }

   @Override
   public Collection<Channel> getChannels() {
      return Collections.unmodifiableCollection(this._channels.values());
   }

   @Override
   public Channel getChannel(String channelName) {
      return this._channels.get(channelName);
   }

   @Override
   protected CurveModel getPrimaryCurve() {
      return this._curves.get("beanTemperature");
   }

   @Override
   public CurveModel getCurveModel(Channel channel) {
      return this._curves.get(channel.getName());
   }

   @Override
   public CurveModel getRorModel(Channel channel) {
      return null;
   }

   @Override
   public void put(Channel channel, Date date, Double value) {
      if (!this.isStopped()) {
         CurveModel model = this._curves.get(channel.getName());
         if (model != null) {
            if (date.getTime() > this._time) {
               this._time = date.getTime();
            }

            model.getRootModel().getValues().put(Long.valueOf(date.getTime()), value);
            model.getRootModel().fireCurveChanged(date.getTime());
         }
      }
   }

   @Override
   public Collection<ControlState> getControlStates() {
      return Collections.unmodifiableCollection(this._controlStates.keySet());
   }

   @Override
   public void putState(ControlState controlState, Boolean value) {
      if (!this.isStopped()) {
         this._controlStates.put(controlState, value);
      }
   }

   @Override
   public Boolean getState(ControlState controlState) {
      return this._controlStates.get(controlState);
   }

   public void reset() {
      this._stopped = false;
      this._startDate = new Date(MathUtil.floorLong(NanoTime.currentTimeMillis() + 1000L, 1000L));

      while (!this._comments.isEmpty()) {
         this.removeComment(this._comments.firstEntry().getValue());
      }

      for (CurveModel model : this._curves.values()) {
         model.getRootModel().getValues().clear();
         model.getRootModel().fireCurveChanged();
         Optional.ofNullable(model.getModel(StartEndPointCurveModel.class)).ifPresent(startEndPointCurveModel -> startEndPointCurveModel.setStartTime(null));
      }

      this._controlStates.keySet().forEach(controlState -> this._controlStates.put(controlState, null));
   }

   public boolean isStopped() {
      return this._stopped;
   }

   public void setStopped(boolean stopped) {
      this._stopped = stopped;
   }
}
