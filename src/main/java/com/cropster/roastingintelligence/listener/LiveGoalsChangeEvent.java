package com.cropster.roastingintelligence.listener;

import com.cropster.roastingintelligence.model.livegoals.LiveGoal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.tuple.Pair;

public class LiveGoalsChangeEvent {
   private final List<LiveGoal> liveGoals;
   private final List<LiveGoal> changedLiveGoals;
   private final List<Integer> changedLiveGoalsIndexes;
   private final LiveGoalsChangeEvent.ChangeType changeType;

   private static List<Integer> indexesOf(List<LiveGoal> liveGoals, Collection<LiveGoal> changedLiveGoals) {
      return changedLiveGoals.stream().map(liveGoals::indexOf).toList();
   }

   public LiveGoalsChangeEvent(List<LiveGoal> liveGoals, Collection<LiveGoal> changedLiveGoals, LiveGoalsChangeEvent.ChangeType changeType) {
      Objects.requireNonNull(liveGoals, "Null 'liveGoals' parameter.");
      Objects.requireNonNull(changeType, "Null 'changeType' parameter.");
      if (changeType != LiveGoalsChangeEvent.ChangeType.CLEAR) {
         Objects.requireNonNull(changedLiveGoals, "Null 'changedLiveGoals' parameter.");
      }

      this.liveGoals = List.copyOf(liveGoals);
      this.changeType = changeType;
      this.changedLiveGoals = changeType == LiveGoalsChangeEvent.ChangeType.CLEAR
         ? List.of()
         : changedLiveGoals.stream()
            .map(ca -> Pair.of(liveGoals.indexOf(ca), ca))
            .sorted((p1, p2) -> new CompareToBuilder().append(p1.getLeft(), p2.getLeft()).toComparison())
            .<LiveGoal>map(Pair::getRight)
            .toList();
      this.changedLiveGoalsIndexes = indexesOf(liveGoals, this.changedLiveGoals);
   }

   public LiveGoalsChangeEvent.ChangeType getChangeType() {
      return this.changeType;
   }

   public List<LiveGoal> getLiveGoals() {
      return this.liveGoals;
   }

   public List<LiveGoal> getChangedLiveGoals() {
      return this.changedLiveGoals;
   }

   public List<Integer> getChangedLiveGoalsIndexes() {
      return this.changedLiveGoalsIndexes;
   }

   public static enum ChangeType {
      ADD,
      REMOVE,
      CLEAR;
   }
}
