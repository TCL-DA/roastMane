package com.cropster.roastingintelligence.service.impl;

import com.cropster.roastingintelligence.listener.AlarmsChangeEvent;
import com.cropster.roastingintelligence.listener.AlarmsChangeListener;
import com.cropster.roastingintelligence.listener.TriggeredAlarmEvent;
import com.cropster.roastingintelligence.listener.TriggeredAlarmListener;
import com.cropster.roastingintelligence.model.RoastingPhase;
import com.cropster.roastingintelligence.model.alarms.Alarm;
import com.cropster.roastingintelligence.model.alarms.AlarmTargetDirection;
import com.cropster.roastingintelligence.model.alarms.AlarmType;
import com.cropster.roastingintelligence.service.AlarmService;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Commentable;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.RoastingComment;

public class AlarmServiceImpl implements AlarmService {
   private final List<Alarm> alarms = new ArrayList<>();
   private final Set<AlarmServiceImpl.MeasureBasedAlarmHolder> measureBasedAlarmsSet = new HashSet<>();
   private final Queue<AlarmServiceImpl.EventBasedAlarmHolder> eventBasedAlarmsQueue = new LinkedList<>();
   private final EventListenerSupport<AlarmsChangeListener> alarmsChangeListeners = EventListenerSupport.create(AlarmsChangeListener.class);
   private final EventListenerSupport<TriggeredAlarmListener> triggeredAlarmListeners = EventListenerSupport.create(TriggeredAlarmListener.class);
   private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
   private final AtomicReference<RoastingPhase> currentTriggeringPhase = new AtomicReference<>();
   private final AtomicBoolean dirty = new AtomicBoolean(false);
   private final AtomicBoolean dirtyNotificationEnabled = new AtomicBoolean(true);
   private final AtomicBoolean suspended = new AtomicBoolean(true);
   private final AtomicReference<MeasurementModel> currentModel = new AtomicReference<>();
   private final CurveModelListener eventBasedListener = new CurveModelListener() {
      @Override
      public void notifyCurveChanged(CurveModel source) {
      }

      @Override
      public void notifyCurveChanged(CurveModel source, long time) {
         synchronized (AlarmServiceImpl.this) {
            if (!AlarmServiceImpl.this.eventBasedAlarmsQueue.isEmpty()) {
               for (AlarmServiceImpl.EventBasedAlarmHolder h = AlarmServiceImpl.this.eventBasedAlarmsQueue.peek();
                  h != null && h.getTriggerTime() <= time;
                  h = AlarmServiceImpl.this.eventBasedAlarmsQueue.peek()
               ) {
                  AlarmServiceImpl.this.triggerAlarm(
                     AlarmServiceImpl.this.eventBasedAlarmsQueue.poll().getAlarm(), time, AlarmServiceImpl.this.currentTriggeringPhase.get()
                  );
               }
            }
         }
      }
   };
   private final CurveModelListener timeBasedListener = new CurveModelListener() {
      @Override
      public void notifyCurveChanged(CurveModel source) {
      }

      @Override
      public void notifyCurveChanged(CurveModel source, long time) {
         synchronized (AlarmServiceImpl.this) {
            if (!AlarmServiceImpl.this.eventBasedAlarmsQueue.isEmpty()) {
               time -= AlarmServiceImpl.this.currentModel.get().getStartDate().getTime();

               for (AlarmServiceImpl.EventBasedAlarmHolder h = AlarmServiceImpl.this.eventBasedAlarmsQueue.peek();
                  h != null && h.getTriggerTime() <= time;
                  h = AlarmServiceImpl.this.eventBasedAlarmsQueue.peek()
               ) {
                  AlarmServiceImpl.this.triggerAlarm(
                     AlarmServiceImpl.this.eventBasedAlarmsQueue.poll().getAlarm(), time, AlarmServiceImpl.this.currentTriggeringPhase.get()
                  );
               }
            }
         }
      }
   };

   public AlarmServiceImpl() {
      this.addAlarmsChangeListener(e -> {
         if (this.dirtyNotificationEnabled.get()) {
            synchronized (this) {
               this.setDirty(true);
            }
         }
      });
   }

   @Override
   public void addAlarms(Collection<Alarm> alarms) {
      if (alarms != null && !alarms.isEmpty()) {
         AlarmsChangeEvent changeEvent;
         synchronized (this) {
            this.alarms.addAll(alarms);
            changeEvent = new AlarmsChangeEvent(this.alarms, alarms, AlarmsChangeEvent.ChangeType.ADD);
            this.refresh();
         }

         ((AlarmsChangeListener)this.alarmsChangeListeners.fire()).alarmsChanged(changeEvent);
      }
   }

   @Override
   public boolean removeAlarms(Collection<Alarm> alarms) {
      boolean removed = false;
      if (alarms != null && !alarms.isEmpty()) {
         List<Alarm> filteredAlarms = alarms.stream().filter(this.alarms::contains).toList();
         AlarmsChangeEvent changeEvent;
         synchronized (this) {
            changeEvent = new AlarmsChangeEvent(this.alarms, filteredAlarms, AlarmsChangeEvent.ChangeType.REMOVE);
            removed = this.alarms.removeAll(filteredAlarms);
            if (removed) {
               this.refresh();
            }
         }

         if (removed) {
            ((AlarmsChangeListener)this.alarmsChangeListeners.fire()).alarmsChanged(changeEvent);
         }
      }

      return removed;
   }

   @Override
   public void clearAlarms() {
      AlarmsChangeEvent changeEvent;
      synchronized (this) {
         changeEvent = new AlarmsChangeEvent(this.alarms, null, AlarmsChangeEvent.ChangeType.CLEAR);
         this.alarms.clear();
         this.refresh();
      }

      ((AlarmsChangeListener)this.alarmsChangeListeners.fire()).alarmsChanged(changeEvent);
   }

   @Override
   public List<Alarm> getAlarms() {
      return Collections.unmodifiableList(this.alarms);
   }

   @Override
   public void setAlarms(Collection<Alarm> alarms) {
      this.clearAlarms();
      this.addAlarms(alarms);
   }

   @Override
   public boolean replaceAlarms(Collection<Alarm> alarms) {
      boolean replaced = false;
      if (alarms != null && !alarms.isEmpty()) {
         AlarmsChangeEvent addEvent;
         AlarmsChangeEvent removeEvent;
         synchronized (this) {
            Map<Alarm, Integer> idToIndexMap = new HashMap<>();
            alarms.forEach(
               replacement -> this.alarms
                     .stream()
                     .filter(a -> a.getId().equals(replacement.getId()))
                     .mapToInt(this.alarms::indexOf)
                     .findFirst()
                     .ifPresent(i -> idToIndexMap.put(replacement, i))
            );
            removeEvent = new AlarmsChangeEvent(this.alarms, idToIndexMap.values().stream().map(this.alarms::get).toList(), AlarmsChangeEvent.ChangeType.REMOVE);
            idToIndexMap.forEach((alarm, index) -> this.alarms.set(index, alarm));
            addEvent = new AlarmsChangeEvent(this.alarms, idToIndexMap.values().stream().map(this.alarms::get).toList(), AlarmsChangeEvent.ChangeType.ADD);
            replaced = !idToIndexMap.isEmpty();
            if (replaced) {
               this.refresh();
            }
         }

         if (replaced) {
            this.setDirtyNotificationEnabled(false);
            ((AlarmsChangeListener)this.alarmsChangeListeners.fire()).alarmsChanged(removeEvent);
            ((AlarmsChangeListener)this.alarmsChangeListeners.fire()).alarmsChanged(addEvent);
            this.setDirtyNotificationEnabled(true, true);
         }
      }

      return replaced;
   }

   @Override
   public boolean containsAlarm(String alarmId) {
      return this.alarms.stream().anyMatch(a -> a.getId().equals(alarmId));
   }

   @Override
   public boolean isDirty() {
      return this.dirty.get();
   }

   private void setDirty(boolean dirty) {
      boolean wasDirty = this.isDirty();
      this.dirty.set(dirty);
      this.propertyChangeSupport.firePropertyChange("dirty", wasDirty, dirty);
   }

   @Override
   public boolean isDirtyNotificationEnabled() {
      return this.dirtyNotificationEnabled.get();
   }

   @Override
   public void setDirtyNotificationEnabled(boolean dirtyNotificationEnabled) {
      boolean wasDirtyNotificationEnabled = this.isDirtyNotificationEnabled();
      this.dirtyNotificationEnabled.set(dirtyNotificationEnabled);
      this.propertyChangeSupport.firePropertyChange("dirtyNotificationEnabled", wasDirtyNotificationEnabled, dirtyNotificationEnabled);
   }

   @Override
   public void setDirtyNotificationEnabled(boolean dirtyNotificationEnabled, boolean newDirtyValue) {
      this.setDirtyNotificationEnabled(dirtyNotificationEnabled);
      this.setDirty(newDirtyValue);
   }

   @Override
   public void importFrom(Collection<Alarm> alarms) {
      synchronized (this) {
         this.setAlarms(alarms);
         this.setDirty(false);
      }
   }

   @Override
   public List<Alarm> exportTo() {
      synchronized (this) {
         List<Alarm> exports = new ArrayList<>(this.alarms);
         this.setDirty(false);
         return exports;
      }
   }

   @Override
   public void attach(MeasurementModel model, RoastingPhase phase) {
      Objects.requireNonNull(phase, "Null 'phase' parameter.");
      if (phase == RoastingPhase.ALWAYS) {
         throw new IllegalArgumentException("Phase ALWAYS is not allowed.");
      } else {
         synchronized (this) {
            if (this.currentModel.get() != null) {
               this.detach();
            }

            this.currentTriggeringPhase.set(phase);
            this.currentModel.set(model);
            if (model != null) {
               if (!this.suspended.get()) {
                  this.performRefresh();
                  this.attachInnerListeners();
               }
            }
         }
      }
   }

   @Override
   public void detach() {
      if (this.currentModel.get() != null) {
         synchronized (this) {
            this.detachInnerListeners();
            this.currentModel.set(null);
            this.currentTriggeringPhase.set(null);
            if (!this.suspended.get()) {
               this.performRefresh();
            }
         }
      }
   }

   @Override
   public void refresh() {
      synchronized (this) {
         if (!this.suspended.get()) {
            this.detachInnerListeners();
            this.performRefresh();
            this.attachInnerListeners();
         }
      }
   }

   @Override
   public void suspend() {
      synchronized (this) {
         if (this.suspended.compareAndSet(false, true)) {
            this.detachInnerListeners();
            this.performRefresh();
            this.propertyChangeSupport.firePropertyChange("suspended", false, true);
         }
      }
   }

   @Override
   public void resume() {
      synchronized (this) {
         if (this.suspended.compareAndSet(true, false)) {
            this.performRefresh();
            this.attachInnerListeners();
            this.propertyChangeSupport.firePropertyChange("suspended", true, false);
         }
      }
   }

   @Override
   public boolean isSuspended() {
      synchronized (this) {
         return this.suspended.get();
      }
   }

   @Override
   public void addAlarmsChangeListener(AlarmsChangeListener listener) {
      synchronized (this) {
         this.alarmsChangeListeners.addListener(listener, false);
      }
   }

   @Override
   public void removeAlarmsChangeListener(AlarmsChangeListener listener) {
      synchronized (this) {
         this.alarmsChangeListeners.removeListener(listener);
      }
   }

   @Override
   public void addTriggeredAlarmListener(TriggeredAlarmListener listener) {
      synchronized (this) {
         this.triggeredAlarmListeners.addListener(listener, false);
      }
   }

   @Override
   public void removeTriggeredAlarmListener(TriggeredAlarmListener listener) {
      synchronized (this) {
         this.triggeredAlarmListeners.removeListener(listener);
      }
   }

   @Override
   public void addPropertyChangeListener(PropertyChangeListener listener) {
      this.propertyChangeSupport.addPropertyChangeListener(listener);
   }

   @Override
   public void removePropertyChangeListener(PropertyChangeListener listener) {
      this.propertyChangeSupport.removePropertyChangeListener(listener);
   }

   @Override
   public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
      this.propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
   }

   @Override
   public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
      this.propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
   }

   private void triggerAlarm(Alarm alarm, long triggerTime, RoastingPhase triggerPhase) {
      if (alarm != null) {
         TriggeredAlarmEvent event = new TriggeredAlarmEvent(alarm, triggerTime, triggerPhase);
         synchronized (this) {
            ((TriggeredAlarmListener)this.triggeredAlarmListeners.fire()).alarmTriggered(event);
         }
      }
   }

   private void attachInnerListeners() {
      MeasurementModel cm = this.currentModel.get();
      if (this.isEventBasedEnabled()) {
         cm.getCurveModel(cm.getChannel("beanTemperature")).addListener(this.eventBasedListener);
      } else if (this.isTimeBasedEnabled()) {
         cm.getCurveModel(cm.getChannel("beanTemperature")).addListener(this.timeBasedListener);
      }
   }

   private void detachInnerListeners() {
      MeasurementModel cm = this.currentModel.get();
      if (cm != null) {
         cm.getCurveModel(cm.getChannel("beanTemperature")).removeListener(this.eventBasedListener);
         cm.getCurveModel(cm.getChannel("beanTemperature")).removeListener(this.timeBasedListener);
      }

      this.measureBasedAlarmsSet.forEach(holder -> holder.getCurveModel().removeListener(holder));
   }

   private boolean isEventBasedEnabled() {
      return this.isEventBasedEnabled(this.alarms.stream().filter(this::matchesCurrentTriggeringPhase));
   }

   private boolean isEventBasedEnabled(Stream<Alarm> alarmsStream) {
      MeasurementModel cm = this.currentModel.get();
      return cm != null
         && cm.getCurrentReference() != null
         && this.currentTriggeringPhase.get() != null
         && alarmsStream.anyMatch(a -> a.getType() == AlarmType.EVENT_BASED);
   }

   private boolean isMeasureBasedEnabled() {
      return this.isMeasureBasedEnabled(this.alarms.stream().filter(this::matchesCurrentTriggeringPhase));
   }

   private boolean isMeasureBasedEnabled(Stream<Alarm> alarmsStream) {
      return this.currentModel.get() != null && this.currentTriggeringPhase.get() != null && alarmsStream.anyMatch(a -> a.getType() == AlarmType.MEASURE_BASED);
   }

   private boolean isTimeBasedEnabled() {
      return this.isTimeBasedEnabled(this.alarms.stream().filter(this::matchesCurrentTriggeringPhase));
   }

   private boolean isTimeBasedEnabled(Stream<Alarm> alarmsStream) {
      MeasurementModel cm = this.currentModel.get();
      return cm != null
         && this.currentTriggeringPhase.get() != null
         && alarmsStream.anyMatch(a -> a.getType() == AlarmType.EVENT_BASED && "time".equals(a.getTrigger()));
   }

   private boolean matchesCurrentTriggeringPhase(Alarm alarm) {
      RoastingPhase triggeringPhase = this.currentTriggeringPhase.get();
      return alarm.getTriggeringPhase() == triggeringPhase || alarm.getTriggeringPhase() == RoastingPhase.ALWAYS;
   }

   private void performRefresh() {
      this.eventBasedAlarmsQueue.clear();
      this.measureBasedAlarmsSet.clear();
      if (!this.suspended.get()) {
         boolean eventBasedEnabled = this.isEventBasedEnabled();
         if (eventBasedEnabled || this.isTimeBasedEnabled()) {
            MeasurementModel cm = this.currentModel.get();
            long currentTime = cm instanceof Commentable cmm ? cmm.getDuration() : 0L;
            List<AlarmServiceImpl.EventBasedAlarmHolder> alarmHolders = new ArrayList<>();
            List<Alarm> enabledEventBasedAlarms = this.alarms
               .stream()
               .filter(this::matchesCurrentTriggeringPhase)
               .filter(alarm -> alarm.getType() == AlarmType.EVENT_BASED)
               .filter(Alarm::isEnabled)
               .toList();
            enabledEventBasedAlarms.stream()
               .filter(alarm -> "time".equals(alarm.getTrigger()))
               .map(alarm -> Pair.of(alarm, alarm.getTimeReference()))
               .filter(p -> (Long)p.getRight() > currentTime)
               .forEach(p -> alarmHolders.add(new AlarmServiceImpl.EventBasedAlarmHolder((Alarm)p.getLeft(), (Long)p.getRight())));
            if (eventBasedEnabled) {
               Roast referenceRoast = cm.getCurrentReference();
               Collection<RoastingComment> referenceComments = referenceRoast.getComments();
               enabledEventBasedAlarms.stream()
                  .filter(alarm -> !"time".equals(alarm.getTrigger()))
                  .forEach(
                     alarm -> referenceComments.stream()
                           .filter(c -> alarm.getTrigger().equals(c.getEvent()))
                           .filter(c -> alarm.getTargetDirection() == AlarmTargetDirection.LOWER || alarm.getTargetDirection() == AlarmTargetDirection.ANY)
                           .map(c -> new AlarmServiceImpl.EventBasedAlarmHolder(alarm, c.getTime() - alarm.getTimeOffset()))
                           .filter(h -> h.getTriggerTime() > currentTime)
                           .forEach(alarmHolders::add)
                  );
               enabledEventBasedAlarms.stream()
                  .filter(alarm -> !"time".equals(alarm.getTrigger()))
                  .forEach(
                     alarm -> referenceComments.stream()
                           .filter(c -> alarm.getTrigger().equals(c.getEvent()))
                           .filter(c -> alarm.getTargetDirection() == AlarmTargetDirection.HIGHER || alarm.getTargetDirection() == AlarmTargetDirection.ANY)
                           .map(c -> new AlarmServiceImpl.EventBasedAlarmHolder(alarm, c.getTime() + alarm.getTimeOffset()))
                           .filter(h -> h.getTriggerTime() > currentTime)
                           .forEach(alarmHolders::add)
                  );
            }

            this.eventBasedAlarmsQueue.addAll(alarmHolders.stream().sorted((h1, h2) -> NumberUtils.compare(h1.getTriggerTime(), h2.getTriggerTime())).toList());
         }

         if (this.isMeasureBasedEnabled()) {
            MeasurementModel cm = this.currentModel.get();
            this.alarms
               .stream()
               .filter(this::matchesCurrentTriggeringPhase)
               .filter(alarm -> alarm.getType() == AlarmType.MEASURE_BASED)
               .filter(Alarm::isEnabled)
               .map(alarm -> Pair.of(alarm, cm.getChannel(alarm.getTrigger())))
               .filter(p -> p.getRight() != null)
               .map(p -> Triple.of((Alarm)p.getLeft(), (Channel)p.getRight(), cm.getCurveModel((Channel)p.getRight())))
               .filter(t -> t.getRight() != null)
               .forEach(t -> {
                  CurveModel curveModel = (CurveModel)t.getRight();
                  AlarmServiceImpl.MeasureBasedAlarmHolder holder = new AlarmServiceImpl.MeasureBasedAlarmHolder((Alarm)t.getLeft(), curveModel);
                  curveModel.addListener(holder);
                  this.measureBasedAlarmsSet.add(holder);
               });
         }
      }
   }

   private static class EventBasedAlarmHolder {
      private final Alarm alarm;
      private final long triggerTime;

      EventBasedAlarmHolder(Alarm alarm, long triggerTime) {
         this.alarm = alarm;
         this.triggerTime = triggerTime;
      }

      public Alarm getAlarm() {
         return this.alarm;
      }

      public long getTriggerTime() {
         return this.triggerTime;
      }
   }

   private class MeasureBasedAlarmHolder implements CurveModelListener {
      private final Alarm alarm;
      private final CurveModel curveModel;
      private Double previousMeasure = null;

      MeasureBasedAlarmHolder(Alarm alarm, CurveModel curveModel) {
         this.alarm = alarm;
         this.curveModel = curveModel;
      }

      public Alarm getAlarm() {
         return this.alarm;
      }

      public CurveModel getCurveModel() {
         return this.curveModel;
      }

      @Override
      public void notifyCurveChanged(CurveModel source, long time) {
         Double currentMeasure = source.getValue(time);
         if (currentMeasure != null) {
            if (this.isValidTimeRange(time)) {
               double targetValue = this.alarm.getTargetMeasure().getAmount();
               if (this.previousMeasure == null && currentMeasure == targetValue) {
                  AlarmServiceImpl.this.triggerAlarm(this.alarm, time, AlarmServiceImpl.this.currentTriggeringPhase.get());
               } else if (this.previousMeasure != null) {
                  this.triggerAlarmByDirection(time, currentMeasure, targetValue);
               }
            }

            this.previousMeasure = currentMeasure;
         }
      }

      @Override
      public void notifyCurveChanged(CurveModel source) {
      }

      private boolean isValidTimeRange(long time) {
         boolean validTimeRange = true;
         if (this.alarm.getTimeReference() > 0L && this.alarm.getTimeOffset() > 0L) {
            validTimeRange = time >= this.alarm.getTimeReference() - this.alarm.getTimeOffset() && time <= this.alarm.getTimeReference();
         }

         return validTimeRange;
      }

      private void triggerAlarmByDirection(long time, double currentMeasure, double targetValue) {
         switch (this.alarm.getTargetDirection()) {
            case LOWER:
               if (currentMeasure >= targetValue && this.previousMeasure < targetValue) {
                  AlarmServiceImpl.this.triggerAlarm(this.alarm, time, AlarmServiceImpl.this.currentTriggeringPhase.get());
               }
               break;
            case HIGHER:
               if (currentMeasure <= targetValue && this.previousMeasure > targetValue) {
                  AlarmServiceImpl.this.triggerAlarm(this.alarm, time, AlarmServiceImpl.this.currentTriggeringPhase.get());
               }
               break;
            case ANY:
               if (currentMeasure >= targetValue && this.previousMeasure < targetValue || currentMeasure <= targetValue && this.previousMeasure > targetValue) {
                  AlarmServiceImpl.this.triggerAlarm(this.alarm, time, AlarmServiceImpl.this.currentTriggeringPhase.get());
               }
         }
      }
   }
}
