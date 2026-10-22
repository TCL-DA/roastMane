package com.cropster.roastingintelligence.model.livegoals.checkers;

import org.cropster.roastingintelligence.model.Goal;

public class RoastValueChecker extends MeasureChecker {
   public RoastValueChecker() {
      this(false);
   }

   public RoastValueChecker(boolean continuous) {
      super("Checker.roastValue", continuous);
   }

   @Override
   public boolean isGoalValid(Goal goal) {
      return super.isGoalValid(goal) && "roastValue".equals(goal.getName());
   }

   @Override
   protected String getMeasureName() {
      return "roastValue";
   }
}
