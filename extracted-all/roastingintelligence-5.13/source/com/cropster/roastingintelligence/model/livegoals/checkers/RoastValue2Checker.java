package com.cropster.roastingintelligence.model.livegoals.checkers;

import org.cropster.roastingintelligence.model.Goal;

public class RoastValue2Checker extends MeasureChecker {
   public RoastValue2Checker() {
      this(false);
   }

   public RoastValue2Checker(boolean continuous) {
      super("Checker.roastValue2", continuous);
   }

   @Override
   public boolean isGoalValid(Goal goal) {
      return super.isGoalValid(goal) && "roastValue2".equals(goal.getName());
   }

   @Override
   protected String getMeasureName() {
      return "roastValue2";
   }
}
