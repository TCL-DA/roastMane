package com.cropster.roastingintelligence.model.livegoals.checkers;

import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Roast;

public class EndWeightChecker extends CompletedOnPassedOrFailedChecker {
   public EndWeightChecker() {
      this(false);
   }

   public EndWeightChecker(boolean continuous) {
      super("Checker.endWeight", continuous);
   }

   @Override
   public boolean isGoalValid(Goal goal) {
      return super.isGoalValid(goal) && "endWeight".equals(goal.getName());
   }

   @Override
   protected Measure computeMeasure(Roast roast, MeasurementModel model) {
      return roast.getEndWeight();
   }
}
