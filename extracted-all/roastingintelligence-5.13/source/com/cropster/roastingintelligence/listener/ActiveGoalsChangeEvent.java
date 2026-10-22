package com.cropster.roastingintelligence.listener;

import com.cropster.roastingintelligence.service.LiveGoalService;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.tuple.Pair;

public final class ActiveGoalsChangeEvent {
   private final List<LiveGoalService.ActiveGoal> activeGoals;
   private final List<LiveGoalService.ActiveGoal> changedActiveGoals;
   private final List<Integer> changedActiveGoalsIndexes;
   private final ActiveGoalsChangeEvent.ChangeType changeType;

   private static List<Integer> indexesOf(List<LiveGoalService.ActiveGoal> activeGoals, Collection<LiveGoalService.ActiveGoal> changedActiveGoals) {
      return changedActiveGoals.stream().map(activeGoals::indexOf).toList();
   }

   public ActiveGoalsChangeEvent(
      List<LiveGoalService.ActiveGoal> activeGoals, Collection<LiveGoalService.ActiveGoal> changedActiveGoals, ActiveGoalsChangeEvent.ChangeType changeType
   ) {
      Objects.requireNonNull(activeGoals, "Null 'activeGoals' parameter.");
      Objects.requireNonNull(changeType, "Null 'changeType' parameter.");
      if (changeType != ActiveGoalsChangeEvent.ChangeType.CLEAR) {
         Objects.requireNonNull(changedActiveGoals, "Null 'changedActiveGoals' parameter.");
      }

      this.activeGoals = List.copyOf(activeGoals);
      this.changeType = changeType;
      if (changeType == ActiveGoalsChangeEvent.ChangeType.CLEAR) {
         this.changedActiveGoals = List.of();
      } else {
         this.changedActiveGoals = List.copyOf(changedActiveGoals)
            .stream()
            .map(cag -> Pair.of(activeGoals.indexOf(cag), cag))
            .sorted((p1, p2) -> new CompareToBuilder().append(p1.getLeft(), p2.getLeft()).toComparison())
            .<LiveGoalService.ActiveGoal>map(Pair::getRight)
            .toList();
      }

      this.changedActiveGoalsIndexes = indexesOf(activeGoals, this.changedActiveGoals);
   }

   public ActiveGoalsChangeEvent.ChangeType getChangeType() {
      return this.changeType;
   }

   public List<LiveGoalService.ActiveGoal> getActiveGoals() {
      return this.activeGoals;
   }

   public List<LiveGoalService.ActiveGoal> getChangedActiveGoals() {
      return this.changedActiveGoals;
   }

   public List<Integer> getChangedActiveGoalsIndexes() {
      return this.changedActiveGoalsIndexes;
   }

   public static enum ChangeType {
      ADD,
      REMOVE,
      CLEAR,
      UPDATE;
   }
}
