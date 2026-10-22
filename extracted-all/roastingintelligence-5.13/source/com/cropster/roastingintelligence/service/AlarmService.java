package com.cropster.roastingintelligence.service;

import com.cropster.roastingintelligence.listener.AlarmsChangeListener;
import com.cropster.roastingintelligence.listener.TriggeredAlarmListener;
import com.cropster.roastingintelligence.model.RoastingPhase;
import com.cropster.roastingintelligence.model.alarms.Alarm;
import com.cropster.roastingintelligence.model.alarms.AlarmTargetDirection;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.cropster.roastingintelligence.model.MeasurementModel;

public interface AlarmService {
   String DIRTY_PROPERTY = "dirty";
   String DIRTY_NOTIFICATION_ENABLED_PROPERTY = "dirtyNotificationEnabled";
   String SUSPENDED_PROPERTY = "suspended";

   default void addAlarms(Alarm... alarms) {
      this.addAlarms(List.of(alarms));
   }

   void addAlarms(Collection<Alarm> var1);

   default boolean removeAlarms(Alarm... alarms) {
      return this.removeAlarms(List.of(alarms));
   }

   boolean removeAlarms(Collection<Alarm> var1);

   void clearAlarms();

   List<Alarm> getAlarms();

   default void setAlarms(Alarm... alarms) {
      this.setAlarms(List.of(alarms));
   }

   void setAlarms(Collection<Alarm> var1);

   default boolean replaceAlarms(Alarm... alarms) {
      return this.replaceAlarms(List.of(alarms));
   }

   boolean replaceAlarms(Collection<Alarm> var1);

   default boolean containsAlarm(Alarm alarm) {
      return alarm != null && this.containsAlarm(alarm.getId());
   }

   boolean containsAlarm(String var1);

   default void createDefaultAlarms() {
      Alarm firstCrackAlarm = Alarm.eventBasedBuilder(RoastingPhase.ROASTING, "firstCrack")
         .withTargetDirection(AlarmTargetDirection.LOWER)
         .withTimeOffset(60L, TimeUnit.SECONDS)
         .build();
      Alarm profileEndAlarm = Alarm.eventBasedBuilder(RoastingPhase.ROASTING, "end")
         .withTargetDirection(AlarmTargetDirection.LOWER)
         .withTimeOffset(60L, TimeUnit.SECONDS)
         .build();
      this.setAlarms(firstCrackAlarm, profileEndAlarm);
   }

   boolean isDirty();

   boolean isDirtyNotificationEnabled();

   void setDirtyNotificationEnabled(boolean var1);

   void setDirtyNotificationEnabled(boolean var1, boolean var2);

   void importFrom(Collection<Alarm> var1);

   List<Alarm> exportTo();

   void attach(MeasurementModel var1, RoastingPhase var2);

   void detach();

   void refresh();

   void suspend();

   void resume();

   boolean isSuspended();

   void addAlarmsChangeListener(AlarmsChangeListener var1);

   void removeAlarmsChangeListener(AlarmsChangeListener var1);

   void addTriggeredAlarmListener(TriggeredAlarmListener var1);

   void removeTriggeredAlarmListener(TriggeredAlarmListener var1);

   void addPropertyChangeListener(PropertyChangeListener var1);

   void removePropertyChangeListener(PropertyChangeListener var1);

   void addPropertyChangeListener(String var1, PropertyChangeListener var2);

   void removePropertyChangeListener(String var1, PropertyChangeListener var2);
}
