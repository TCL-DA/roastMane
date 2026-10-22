package com.cropster.roastingintelligence.listener;

import com.cropster.roastingintelligence.model.alarms.Alarm;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.tuple.Pair;

public class AlarmsChangeEvent {
   private final List<Alarm> alarms;
   private final List<Alarm> changedAlarms;
   private final List<Integer> changedAlarmsIndexes;
   private final AlarmsChangeEvent.ChangeType changeType;

   private static List<Integer> indexesOf(List<Alarm> alarms, Collection<Alarm> changedAlarms) {
      return changedAlarms.stream().map(alarms::indexOf).toList();
   }

   public AlarmsChangeEvent(List<Alarm> alarms, Collection<Alarm> changedAlarms, AlarmsChangeEvent.ChangeType changeType) {
      Objects.requireNonNull(alarms, "Null 'alarms' parameter.");
      Objects.requireNonNull(changeType, "Null 'changeType' parameter.");
      if (changeType != AlarmsChangeEvent.ChangeType.CLEAR) {
         Objects.requireNonNull(changedAlarms, "Null 'changedAlarms' parameter.");
      }

      this.alarms = List.copyOf(alarms);
      this.changeType = changeType;
      this.changedAlarms = changeType == AlarmsChangeEvent.ChangeType.CLEAR
         ? List.of()
         : changedAlarms.stream()
            .map(ca -> Pair.of(alarms.indexOf(ca), ca))
            .sorted((p1, p2) -> new CompareToBuilder().append(p1.getLeft(), p2.getLeft()).toComparison())
            .<Alarm>map(Pair::getRight)
            .toList();
      this.changedAlarmsIndexes = indexesOf(alarms, this.changedAlarms);
   }

   public AlarmsChangeEvent.ChangeType getChangeType() {
      return this.changeType;
   }

   public List<Alarm> getAlarms() {
      return this.alarms;
   }

   public List<Alarm> getChangedAlarms() {
      return this.changedAlarms;
   }

   public List<Integer> getChangedAlarmsIndexes() {
      return this.changedAlarmsIndexes;
   }

   public static enum ChangeType {
      ADD,
      REMOVE,
      CLEAR;
   }
}
