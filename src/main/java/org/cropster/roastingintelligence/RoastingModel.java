package org.cropster.roastingintelligence;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.Preferences;
import com.cropster.util.model.Unit;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Commentable;
import org.cropster.roastingintelligence.model.ControlState;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.RoastingComment;
import org.cropster.roastingintelligence.model.Scheduled;
import org.cropster.roastingintelligence.model.impl.AbstractCommentable;
import org.cropster.roastingintelligence.model.impl.AbstractCurveModel;
import org.cropster.roastingintelligence.model.impl.ConverterCurveModel;
import org.cropster.roastingintelligence.model.impl.HeadCurveModel;
import org.cropster.roastingintelligence.model.impl.LinearInterpolationCurveModel;
import org.cropster.roastingintelligence.model.impl.MovingAverageCurveModel;
import org.cropster.roastingintelligence.model.impl.RelativeCurveModel;
import org.cropster.roastingintelligence.model.impl.RemoveDuplicatesCurveModel;
import org.cropster.roastingintelligence.model.impl.RorCurveModel;
import org.cropster.roastingintelligence.model.impl.ScalingCurveModel;
import org.cropster.roastingintelligence.model.impl.StandardCurveModel;
import org.cropster.roastingintelligence.model.impl.StartEndPointCurveModel;
import org.cropster.roastingintelligence.model.impl.StepCurveModel;
import org.cropster.roastingintelligence.util.DateUtil;
import org.cropster.roastingintelligence.util.MathUtil;

public class RoastingModel extends AbstractCommentable implements MeasurementModel, Commentable {
   private final Preferences _preferences;
   private final Data _data;
   private final Roast _roast;
   private Scheduled _scheduled;
   private volatile Date _initialDate = null;
   private volatile Date _startDate = null;
   private volatile Date _turningPoint = null;
   private volatile Date _endDate = null;
   private volatile long _startOffset;
   private volatile long _endOffset;
   private volatile Map<String, Long> _eventTimes = new HashMap<>();
   private volatile long _time;
   private final Map<String, Channel> _channels;
   private final Map<String, CurveModel> _curves;
   private final Map<String, CurveModel> _riseRateModels = new HashMap<>();
   private final Map<String, CurveModel> _filteredModels = new HashMap<>();
   private final Map<Lot, Measure> _selectedLots;
   private final Map<ControlState, Boolean> _controlStates;

   public RoastingModel(Preferences preferences, Data data, Collection<Channel> channels, Collection<ControlState> states) {
      this._preferences = preferences;
      this._data = data;
      this._roast = new Roast();
      this._channels = new LinkedHashMap<>();
      this._curves = new HashMap<>();
      this._initialDate = DateUtil.newDateTruncated(3L);
      this._time = this._initialDate.getTime();

      for (Channel channel : channels) {
         MeasurementSourceFactory sourceFactory = RoastingIntelligenceApp.retrieveSingleton(channel.getSourceFactory());
         Unit unit = sourceFactory.getUnit(channel.getName());
         Unit displayUnit = this._preferences.getUnit(unit);
         this._channels.put(channel.getName(), channel);
         CurveModel model = new StandardCurveModel(channel.getName(), new Date(0L), unit);
         if (!unit.equals(displayUnit) || channel.getOffset() != 0.0) {
            model = new ConverterCurveModel(model, displayUnit, channel.getOffset());
         }

         model = new RelativeCurveModel(this, model);
         if (Unit.CELSIUS.isCompatible(model.getUnit()) && !channel.isControlChannel()) {
            Optional.ofNullable(sourceFactory.getFilterCurveModel(channel.getName(), model))
               .ifPresent(
                  filterModel -> this._filteredModels.put(channel.getName(), new LinearInterpolationCurveModel(new StartEndPointCurveModel(filterModel)))
               );
            if (sourceFactory.getSamplingInterval() > 1000) {
               model = new LinearInterpolationCurveModel(new StartEndPointCurveModel(model));
            }
         } else {
            model = new StepCurveModel(model);
         }

         this._curves.put(channel.getName(), model);
      }

      this._selectedLots = new LinkedHashMap<>();
      this._controlStates = new LinkedHashMap<>();

      for (ControlState controlState : states) {
         this._controlStates.put(controlState, null);
      }
   }

   protected RoastingModel(Roast roast) {
      this._roast = roast;
      this._preferences = null;
      this._data = null;
      this._channels = Collections.emptyMap();
      this._curves = Collections.emptyMap();
      this._selectedLots = Collections.emptyMap();
      this._controlStates = Collections.emptyMap();
   }

   public Preferences getPreferences() {
      return this._preferences;
   }

   public Data getData() {
      return this._data;
   }

   @Override
   public Roast getRoast() {
      return this._roast;
   }

   public Scheduled getScheduled() {
      return this._scheduled;
   }

   public void setScheduled(Scheduled scheduled) {
      this._scheduled = scheduled;
   }

   public Date getInitialDate() {
      return this._initialDate;
   }

   @Override
   public Date getStartDate() {
      return this._startDate;
   }

   public void setStartDate(Date startDate) {
      Date previousStartDate = this._startDate;
      this._startDate = startDate;
      if (previousStartDate != null) {
         this.updateStartTimeOfCurveModels();
      }
   }

   public void resetStartDate() {
      this.setStartDate(this._initialDate);
   }

   public Date getTurningPoint() {
      return this._turningPoint;
   }

   public void setTurningPoint(Date turningPoint) {
      this._turningPoint = turningPoint;
   }

   public Date getEndDate() {
      return this._endDate;
   }

   public void setEndDate(Date endDate) {
      this._endDate = endDate;
      this.updateEndTimeOfCurveModels();
   }

   public long getStartOffset() {
      return this._startOffset;
   }

   public void setStartOffset(long startOffset) {
      this._startOffset = startOffset;
      this.updateStartTimeOfCurveModels();
   }

   public long getEndOffset() {
      return this._endOffset;
   }

   public void setEndOffset(long endOffset) {
      this._endOffset = endOffset;
      this.updateEndTimeOfCurveModels();
   }

   public Date getEffectiveStartDate() {
      if (this._startDate != null && this._startOffset != 0L) {
         return new Date(this._startDate.getTime() + this._startOffset);
      } else {
         return this._startDate != null ? this._startDate : this._initialDate;
      }
   }

   public Date getEffectiveEndDate() {
      return this._endDate != null && this._endOffset != 0L ? new Date(this._endDate.getTime() + this._endOffset) : this._endDate;
   }

   public Date getCurrentEndDate() {
      Long maxKey = 0L;

      for (CurveModel curve : this.getCurves().values()) {
         if (!curve.getValues().isEmpty() && curve.getValues().lastKey().compareTo(maxKey) > 0) {
            maxKey = curve.getValues().lastKey();
         }
      }

      return new Date(this.getEffectiveStartDate().getTime() + maxKey);
   }

   public long getEffectiveTime(long time) {
      return time - this._startOffset;
   }

   @Override
   public Long getDuration() {
      Date startDate = this.getEffectiveStartDate();
      if (startDate != null) {
         Date endDate = this.getEffectiveEndDate();
         return endDate != null ? endDate.getTime() - startDate.getTime() : this._time - startDate.getTime();
      } else {
         return null;
      }
   }

   public Long calcDevelopmentTime(long duration, long startOffset) {
      Long eventTime = this.getEventTimes().get("firstCrack");
      if (eventTime != null && duration > 0L) {
         long firstCrackTime = eventTime - startOffset;
         if (duration > firstCrackTime) {
            return duration - firstCrackTime;
         }
      }

      return null;
   }

   public Long getDevelopmentTime() {
      return this.calcDevelopmentTime(this.getDuration(), this._startOffset);
   }

   public Measure calcDevelopmentTimeRatio(long duration, long developmentTime) {
      double ratio = 100.0 / (double)duration * (double)developmentTime;
      return new Measure(MathUtil.roundHalfToEven(ratio, 1), Unit.PERCENT);
   }

   public Measure getDevelopmentTimeRatio() {
      Long developmentTime = this.getDevelopmentTime();
      return developmentTime != null ? this.calcDevelopmentTimeRatio(this.getDuration(), developmentTime) : null;
   }

   public Collection<RoastingComment> getCroppedComments() {
      return this._comments.subMap(this.getStartOffset(), this.getStartOffset() + this.getDuration() + 1L).values();
   }

   public RoastingComment getComment(Long time) {
      return this._comments.get(time);
   }

   @Override
   public RoastingComment addComment(RoastingComment comment) {
      RoastingComment replacedComment = super.addComment(comment);
      this.adjustEventTimes(comment);
      return replacedComment;
   }

   @Override
   public void removeComment(RoastingComment comment) {
      super.removeComment(comment);
      this.adjustEventTimes(comment);
   }

   public Long getEventTime(String event) {
      Long eventTime = this.getEventTimes().get(event);
      return eventTime != null && eventTime <= this.getDuration() ? eventTime : null;
   }

   public Map<String, Long> getEventTimes() {
      return this._eventTimes;
   }

   private void adjustEventTimes(RoastingComment changedComment) {
      if ("firstCrack".equals(changedComment.getEvent()) || "secondCrack".equals(changedComment.getEvent()) || "colorChange".equals(changedComment.getEvent())) {
         for (RoastingComment comment : this._comments.values()) {
            if (changedComment.getEvent().equals(comment.getEvent())) {
               this.getEventTimes().put(changedComment.getEvent(), comment.getTime());
               return;
            }
         }

         this.getEventTimes().remove(changedComment.getEvent());
      }
   }

   public Map<String, ? extends CurveModel> getCurves() {
      return this._curves;
   }

   @Override
   protected CurveModel getPrimaryCurve() {
      return this._curves.get("beanTemperature");
   }

   @Override
   public Collection<Channel> getChannels() {
      return this._channels.values();
   }

   @Override
   public Channel getChannel(String channelName) {
      return this._channels.get(channelName);
   }

   @Override
   public CurveModel getCurveModel(Channel channel) {
      return this._curves.get(channel.getName());
   }

   @Override
   public CurveModel getRorModel(Channel channel) {
      if (channel.isRorEnabled() && channel.isRorSupported()) {
         if (this._riseRateModels.containsKey(channel.getName())) {
            return this._riseRateModels.get(channel.getName());
         } else {
            CurveModel curveModel = this.getCurveModel(channel);
            CurveModel filterModel = this._filteredModels.get(channel.getName());
            CurveModel sourceModel = Optional.ofNullable(filterModel)
               .map(model -> model.getModel(RemoveDuplicatesCurveModel.class))
               .<CurveModel>map(
                  duplicateModel -> new HeadCurveModel(
                        filterModel, () -> Optional.ofNullable(duplicateModel.getLastStepTime()).map(lastStepTime -> lastStepTime + 1L).orElse(null)
                     )
               )
               .orElse(curveModel);
            CurveModel rorModel = new RorCurveModel(sourceModel, this._preferences.getRorSmoothing());
            rorModel = new ScalingCurveModel(rorModel, this._preferences.getRor());
            rorModel = new MovingAverageCurveModel(rorModel, 4);
            this._riseRateModels.put(channel.getName(), rorModel);
            return rorModel;
         }
      } else {
         return null;
      }
   }

   public Map<String, CurveModel> getFilteredModels() {
      return this._filteredModels;
   }

   public Curve getCroppedCurve(Channel channel) {
      CurveModel rootModel = this.getCurveModel(channel).getRootModel();
      long startTime = this.getEffectiveStartDate().getTime();
      long endTime = this.getEffectiveEndDate().getTime();
      return buildCurve(rootModel, channel, this._preferences, startTime, endTime, startTime, endTime);
   }

   public static Curve buildCurve(
      CurveModel sourceRootModel, Channel channel, Preferences preferences, long startTime, long endTime, long firstKey, long lastKey
   ) {
      CurveModel model = new StandardCurveModel(sourceRootModel.getName(), sourceRootModel.getUnit());
      model.getValues().putAll(sourceRootModel.getValues());
      CurveModel rootModel = model;
      Unit displayUnit = preferences.getUnit(model.getUnit());
      if (!model.getUnit().equals(displayUnit) || channel.getOffset() != 0.0) {
         model = new ConverterCurveModel(model, displayUnit, channel.getOffset());
      }

      if (Unit.CELSIUS.isCompatible(model.getUnit()) && !channel.isControlChannel()) {
         MeasurementSourceFactory sourceFactory = RoastingIntelligenceApp.retrieveSingleton(channel.getSourceFactory());
         CurveModel filterModel = sourceFactory.getFilterCurveModel(channel.getName(), model);
         if (filterModel != null) {
            model = filterModel;
         }

         if (filterModel != null || sourceFactory.getSamplingInterval() > 1000) {
            StartEndPointCurveModel startEndPointCurveModel = new StartEndPointCurveModel(model);
            startEndPointCurveModel.setStartTime(startTime);
            startEndPointCurveModel.setEndTime(endTime);
            LinearInterpolationCurveModel interpolationModel = new LinearInterpolationCurveModel(startEndPointCurveModel);
            model = interpolationModel;
         }
      } else {
         model = new StepCurveModel(model);
      }

      rootModel.fireCurveChanged();
      SortedMap<Long, Double> values = model.getValues();
      if (!values.containsKey(Long.valueOf(firstKey))) {
         Optional.ofNullable(AbstractCurveModel.lowerKey(values, firstKey))
            .ifPresent(lowerKey -> Optional.ofNullable(rootModel.getValue(lowerKey)).ifPresent(value -> {
                  rootModel.getValues().put(Long.valueOf(firstKey), value);
                  rootModel.getValues().headMap(firstKey).clear();
                  rootModel.fireCurveChanged();
               }));
      }

      if (!values.containsKey(Long.valueOf(lastKey))) {
         Optional.ofNullable(AbstractCurveModel.lowerKey(values, lastKey))
            .ifPresent(lowerKey -> Optional.ofNullable(rootModel.getValue(lowerKey)).ifPresent(value -> {
                  rootModel.getValues().put(Long.valueOf(lastKey), value);
                  rootModel.getValues().tailMap(lastKey + 1L).clear();
                  rootModel.fireCurveChanged();
               }));
      }

      Curve curve = new Curve(model.getUnit());
      curve.putAll(model.getValues().tailMap(firstKey).headMap(lastKey + 1L));
      return curve;
   }

   @Override
   public void put(Channel channel, Date date, Double value) {
      CurveModel curveModel = this.getCurveModel(channel).getRootModel();
      long time = date.getTime() - curveModel.getZeroPoint().getTime();
      curveModel.getValues().put(Long.valueOf(time), value);
      curveModel.fireCurveChanged(time);
      this._time = time;
   }

   public Map<Lot, Measure> getSelectedLots() {
      return Collections.unmodifiableMap(this._selectedLots);
   }

   public void addSelectedLot(Lot lot, Measure weight) {
      if (lot == null) {
         throw new IllegalArgumentException("lot must not be null!");
      } else if (weight == null) {
         throw new IllegalArgumentException("weight must not be null!");
      } else {
         this._selectedLots.put(lot, weight);
      }
   }

   public Collection<LotTransaction> createTransactions() {
      Set<LotTransaction> transactions = new LinkedHashSet<>();

      for (Entry<Lot, Measure> entry : this._selectedLots.entrySet()) {
         transactions.add(new LotTransaction(entry.getKey().getId(), entry.getValue()));
      }

      return transactions;
   }

   public Machine getMachine() {
      return this._data.getMachines().get(this._roast.getMachine());
   }

   @Override
   public Roast getCurrentReference() {
      return this._data.getReferences().get(this._roast.getProfile());
   }

   public Double getMaxStartWeight() {
      return this.getMachine().getCapacity() != null ? this.getMachine().getCapacity().toUnit(this._preferences.getWeightUnit()).getAmount() : null;
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

   public void updateStartTimeOfCurveModels() {
      this.getCurves()
         .values()
         .stream()
         .map(model -> model.getModel(StartEndPointCurveModel.class))
         .filter(Objects::nonNull)
         .forEach(model -> model.setStartTime(this.getEffectiveStartDate().getTime()));
   }

   public void updateEndTimeOfCurveModels() {
      this.getCurves()
         .values()
         .stream()
         .map(model -> model.getModel(StartEndPointCurveModel.class))
         .filter(Objects::nonNull)
         .forEach(model -> model.setEndTime(this.getEffectiveEndDate().getTime()));
   }
}
