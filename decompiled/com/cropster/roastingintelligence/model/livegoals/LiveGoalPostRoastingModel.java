package com.cropster.roastingintelligence.model.livegoals;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.Preferences;
import com.cropster.roastingintelligence.ui.layout.FinishPanel;
import com.cropster.util.model.Unit;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.function.BiConsumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import org.cropster.roastingintelligence.RoastingModel;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.CommentListener;
import org.cropster.roastingintelligence.model.ControlState;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.RoastingComment;
import org.cropster.roastingintelligence.model.Scheduled;

public class LiveGoalPostRoastingModel extends RoastingModel {
   private final RoastingModel delegate;
   private final BiConsumer<Long, Long> timeMarkerUpdater;
   private final ChangeListener<Number> endWeightListener;
   private final ChangeListener<Number> startWeightListener;
   private final ChangeListener<Long> timeListener;
   private final ChangeListener<Number> value1Listener;
   private final ChangeListener<Number> value2Listener;

   public LiveGoalPostRoastingModel(RoastingModel delegate, FinishPanel finishPanel) {
      this(delegate, finishPanel, null);
   }

   public LiveGoalPostRoastingModel(RoastingModel delegate, FinishPanel finishPanel, BiConsumer<Long, Long> timeMarkerUpdater) {
      super(new LiveGoalPostRoastingModel.RoastWrapper(delegate.getRoast()));
      this.delegate = delegate;
      this.timeMarkerUpdater = timeMarkerUpdater;
      this.timeListener = this.installTimeListener(finishPanel);
      this.startWeightListener = this.installStartWeightListener(finishPanel);
      this.endWeightListener = this.installEndWeightListener(finishPanel);
      this.value1Listener = this.installValue1Listener(finishPanel);
      this.value2Listener = this.installValue2Listener(finishPanel);
      this.setEndDate(delegate.getEndDate());
      this.setEndOffset(delegate.getEndOffset());
      this.setStartDate(delegate.getStartDate());
      this.setStartOffset(delegate.getStartOffset());
   }

   private ChangeListener<Long> installTimeListener(FinishPanel finishPanel) {
      ObjectProperty<Long> startTimeProperty = finishPanel.startTimeProperty();
      ObjectProperty<Long> endTimeProperty = finishPanel.endTimeProperty();
      ChangeListener<Long> listener = (s, o, n) -> {
         Long endTime = (Long)endTimeProperty.get();
         Long startTime = (Long)startTimeProperty.get();
         long actualDuration = endTime - startTime;
         Long duration = this.getDuration();
         long effectiveDuration = duration == null ? actualDuration : duration;
         long endOffset = endTime - effectiveDuration;
         this.getRoast().setDuration(actualDuration);
         this.setEndOffset(endOffset);
         this.setStartOffset(startTime);
         if (this.timeMarkerUpdater != null) {
            this.timeMarkerUpdater.accept(startTime, endOffset + effectiveDuration);
         }
      };
      endTimeProperty.addListener(new WeakChangeListener(listener));
      startTimeProperty.addListener(new WeakChangeListener(listener));
      listener.changed(startTimeProperty, null, (Long)startTimeProperty.get());
      return listener;
   }

   private ChangeListener<Number> installStartWeightListener(FinishPanel finishPanel) {
      ObjectProperty<Number> startWeightProperty = finishPanel.startWeightProperty();
      ChangeListener<Number> listener = (s, o, n) -> {
         Unit unit = this.getDelegate().getRoast().getStartWeight().getUnit();
         Measure startWeight = startWeightProperty.get() == null ? null : new Measure(((Number)startWeightProperty.get()).doubleValue(), unit);
         this.getRoast().setStartWeight(startWeight);
      };
      startWeightProperty.addListener(new WeakChangeListener(listener));
      listener.changed(startWeightProperty, null, (Number)startWeightProperty.get());
      return listener;
   }

   private ChangeListener<Number> installEndWeightListener(FinishPanel finishPanel) {
      ObjectProperty<Number> endWeightProperty = finishPanel.endWeightProperty();
      ChangeListener<Number> listener = (s, o, n) -> {
         Unit unit = this.getDelegate().getRoast().getEndWeight().getUnit();
         Measure endWeight = endWeightProperty.get() == null ? null : new Measure(((Number)endWeightProperty.get()).doubleValue(), unit);
         this.getRoast().setEndWeight(endWeight);
      };
      endWeightProperty.addListener(new WeakChangeListener(listener));
      listener.changed(endWeightProperty, null, (Number)endWeightProperty.get());
      return listener;
   }

   private ChangeListener<Number> installValue1Listener(FinishPanel finishPanel) {
      ObjectProperty<Number> value1Property = finishPanel.roastValue1Property();
      ChangeListener<Number> listener = (s, o, n) -> {
         Unit unit = finishPanel.getRoastValue1Unit();
         Double value = value1Property.get() == null ? null : ((Number)value1Property.get()).doubleValue();
         Measure measure = value == null ? null : new Measure(value, unit);
         this.getRoast().getMeasures().put("roastValue", measure);
      };
      value1Property.addListener(new WeakChangeListener(listener));
      listener.changed(value1Property, null, (Number)value1Property.get());
      return listener;
   }

   private ChangeListener<Number> installValue2Listener(FinishPanel finishPanel) {
      ObjectProperty<Number> value2Property = finishPanel.roastValue2Property();
      ChangeListener<Number> listener = (s, o, n) -> {
         Unit unit = finishPanel.getRoastValue2Unit();
         Double value = value2Property.get() == null ? null : ((Number)value2Property.get()).doubleValue();
         Measure measure = value == null ? null : new Measure(value, unit);
         this.getRoast().getMeasures().put("roastValue2", measure);
      };
      value2Property.addListener(new WeakChangeListener(listener));
      listener.changed(value2Property, null, (Number)value2Property.get());
      return listener;
   }

   @Override
   public RoastingComment addComment(RoastingComment comment) {
      return this.delegate.addComment(comment);
   }

   @Override
   public void removeComment(RoastingComment comment) {
      this.delegate.removeComment(comment);
   }

   @Override
   public RoastingComment getComment(Long time) {
      return this.delegate.getComment(time);
   }

   @Override
   public Collection<RoastingComment> getComments() {
      return this.delegate.getComments();
   }

   @Override
   public Collection<RoastingComment> getCroppedComments() {
      return this.delegate.getCroppedComments();
   }

   @Override
   public void addCommentListener(CommentListener listener) {
      this.delegate.addCommentListener(listener);
   }

   @Override
   public void removeCommentListener(CommentListener listener) {
      this.delegate.removeCommentListener(listener);
   }

   @Override
   public void fireCommentUpdated(RoastingComment comment) {
      this.delegate.fireCommentUpdated(comment);
   }

   @Override
   public Measure getTempAtTime(long time) {
      return this.delegate.getTempAtTime(time);
   }

   @Override
   public Long getTimeForTemp(double temp) {
      return this.delegate.getTimeForTemp(temp);
   }

   @Override
   public Long getTimeForTemp(long time, double temp) {
      return this.delegate.getTimeForTemp(time, temp);
   }

   @Override
   public void addSelectedLot(Lot lot, Measure weight) {
      this.delegate.addSelectedLot(lot, weight);
   }

   @Override
   public Map<Lot, Measure> getSelectedLots() {
      return this.delegate.getSelectedLots();
   }

   @Override
   public Collection<LotTransaction> createTransactions() {
      return this.delegate.createTransactions();
   }

   @Override
   public Channel getChannel(String channelName) {
      return this.delegate.getChannel(channelName);
   }

   @Override
   public Collection<Channel> getChannels() {
      return this.delegate.getChannels();
   }

   @Override
   public void put(Channel channel, Date date, Double value) {
      this.delegate.put(channel, date, value);
   }

   @Override
   public Boolean getState(ControlState controlState) {
      return this.delegate.getState(controlState);
   }

   @Override
   public void putState(ControlState controlState, Boolean value) {
      this.delegate.putState(controlState, value);
   }

   @Override
   public Collection<ControlState> getControlStates() {
      return this.delegate.getControlStates();
   }

   @Override
   public Curve getCroppedCurve(Channel channel) {
      return this.delegate.getCroppedCurve(channel);
   }

   @Override
   public CurveModel getCurveModel(Channel channel) {
      return this.delegate.getCurveModel(channel);
   }

   @Override
   public Map<String, ? extends CurveModel> getCurves() {
      return this.delegate.getCurves();
   }

   @Override
   public Roast getCurrentReference() {
      return this.delegate.getCurrentReference();
   }

   @Override
   public Data getData() {
      return this.delegate.getData();
   }

   @Override
   public Date getCurrentEndDate() {
      return this.delegate.getCurrentEndDate();
   }

   @Override
   public long getEffectiveTime(long time) {
      return this.delegate.getEffectiveTime(time);
   }

   @Override
   public Long getEventTime(String event) {
      return this.delegate.getEventTime(event);
   }

   @Override
   public Date getInitialDate() {
      return this.delegate.getInitialDate();
   }

   @Override
   public Machine getMachine() {
      return this.delegate.getMachine();
   }

   @Override
   public Double getMaxStartWeight() {
      return this.delegate.getMaxStartWeight();
   }

   @Override
   public Preferences getPreferences() {
      return this.delegate.getPreferences();
   }

   @Override
   public CurveModel getRorModel(Channel channel) {
      return this.delegate.getRorModel(channel);
   }

   @Override
   public Scheduled getScheduled() {
      return this.delegate.getScheduled();
   }

   @Override
   public void setScheduled(Scheduled scheduled) {
      this.delegate.setScheduled(scheduled);
   }

   @Override
   public Date getTurningPoint() {
      return this.delegate.getTurningPoint();
   }

   @Override
   public void setTurningPoint(Date turningPoint) {
      this.delegate.setTurningPoint(turningPoint);
   }

   @Override
   public Map<String, Long> getEventTimes() {
      return this.delegate.getEventTimes();
   }

   private RoastingModel getDelegate() {
      return this.delegate;
   }

   private static class RoastWrapper extends Roast {
      private final Roast delegate;

      RoastWrapper(Roast delegate) {
         this.delegate = delegate;
         this.setDuration(delegate.getDuration());
         this.getMeasures().putAll(delegate.getMeasures());
      }

      @Override
      public RoastingComment addComment(RoastingComment comment) {
         return this.delegate.addComment(comment);
      }

      @Override
      public RoastingComment getComment(Long time) {
         return this.delegate.getComment(time);
      }

      @Override
      public Collection<RoastingComment> getCommentsWithControl() {
         return this.delegate.getCommentsWithControl();
      }

      @Override
      public Collection<RoastingComment> getComments() {
         return this.delegate.getComments();
      }

      @Override
      public void setComments(Collection<RoastingComment> comments) {
         this.delegate.setComments(comments);
      }

      @Override
      public void addCurveValue(String curveName, Long time, Double value, Unit unit) {
         this.delegate.addCurveValue(curveName, time, value, unit);
      }

      @Override
      public Map<String, Curve> getCurves() {
         return this.delegate.getCurves();
      }

      @Override
      public Map<String, Curve> getCurves(Collection<Channel> channels) {
         return this.delegate.getCurves(channels);
      }

      @Override
      public void setCurves(Map<String, Curve> curves) {
         this.delegate.setCurves(curves);
      }

      @Override
      public long getElapsedTime() {
         return this.delegate.getElapsedTime();
      }

      @Override
      public long getElapsedTimeAt(Date date) {
         return this.delegate.getElapsedTimeAt(date);
      }

      @Override
      public Date getEndDate() {
         return this.delegate.getEndDate();
      }

      @Override
      public void setEndDate(Date endDate) {
         this.delegate.setEndDate(endDate);
      }

      @Override
      public Map<String, Long> getEventTimes() {
         return this.delegate.getEventTimes();
      }

      @Override
      public Lot getLot() {
         return this.delegate.getLot();
      }

      @Override
      public void setLot(Lot lot) {
         this.delegate.setLot(lot);
      }

      @Override
      public Long getMachine() {
         return this.delegate.getMachine();
      }

      @Override
      public void setMachine(Long machine) {
         this.delegate.setMachine(machine);
      }

      @Override
      public Date getMarkedAsReferenceDate() {
         return this.delegate.getMarkedAsReferenceDate();
      }

      @Override
      public void setMarkedAsReferenceDate(Date markedAsReferenceDate) {
         this.delegate.setMarkedAsReferenceDate(markedAsReferenceDate);
      }

      @Override
      public Map<String, Long> getModulationPhases() {
         return this.delegate.getModulationPhases();
      }

      @Override
      public String getNotes() {
         return this.delegate.getNotes();
      }

      @Override
      public void setNotes(String notes) {
         this.delegate.setNotes(notes);
      }

      @Override
      public Long getProfile() {
         return this.delegate.getProfile();
      }

      @Override
      public void setProfile(Long profile) {
         this.delegate.setProfile(profile);
      }

      @Override
      public Long getScheduledId() {
         return this.delegate.getScheduledId();
      }

      @Override
      public void setScheduledId(Long scheduledId) {
         this.delegate.setScheduledId(scheduledId);
      }

      @Override
      public Date getStartDate() {
         return this.delegate.getStartDate();
      }

      @Override
      public void setStartDate(Date startDate) {
         this.delegate.setStartDate(startDate);
      }

      @Override
      public Measure getTempAtTime(long time) {
         return this.delegate.getTempAtTime(time);
      }

      @Override
      public String getWorker() {
         return this.delegate.getWorker();
      }

      @Override
      public void setWorker(String worker) {
         this.delegate.setWorker(worker);
      }

      @Override
      public boolean isSynced() {
         return this.delegate.isSynced();
      }

      @Override
      public void setSynced(boolean synced) {
         this.delegate.setSynced(synced);
      }

      @Override
      public String[] toSummary() {
         return this.delegate.toSummary();
      }
   }
}
