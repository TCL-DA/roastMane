package com.cropster.roastingintelligence.listener;

import com.cropster.roastingintelligence.model.RoastingPhase;
import com.cropster.roastingintelligence.model.alarms.Alarm;
import com.cropster.roastingintelligence.ui.audio.Sound;
import java.time.Instant;
import java.util.UUID;

public class TriggeredAlarmEvent {
   private final long actualTriggerTime = Instant.now().toEpochMilli();
   private final Alarm alarm;
   private final long triggerTime;
   private final String identifier = UUID.randomUUID().toString();
   private final RoastingPhase triggerPhase;

   public TriggeredAlarmEvent(Alarm alarm, long triggerTime, RoastingPhase triggerPhase) {
      this.alarm = alarm;
      this.triggerTime = triggerTime;
      this.triggerPhase = triggerPhase;
      Sound audibleFeedback = alarm.getAudibleFeedback();
      if (audibleFeedback != null) {
         audibleFeedback.play(this.identifier);
      }
   }

   public Alarm getAlarm() {
      return this.alarm;
   }

   public long getActualTriggerTime() {
      return this.actualTriggerTime;
   }

   public long getTriggerTime() {
      return this.triggerTime;
   }

   public RoastingPhase getTriggerPhase() {
      return this.triggerPhase;
   }

   public String getIdentifier() {
      return this.identifier;
   }
}
