package com.cropster.roastingintelligence.model.livegoals.checkers;

import com.cropster.roastingintelligence.model.livegoals.CheckResult;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Roast;

public class StartWeightChecker extends CompletedOnPassedOrFailedChecker {
   public StartWeightChecker() {
      this(false);
   }

   public StartWeightChecker(boolean continuous) {
      super("Checker.startWeight", continuous);
   }

   @Override
   public CheckResult evaluate(Goal goal, Measure actualValue, Measure referenceValue) {
      return super.evaluate(goal, actualValue, referenceValue);
   }

   @Override
   public boolean isGoalValid(Goal goal) {
      return super.isGoalValid(goal) && "startWeight".equals(goal.getName());
   }

   @Override
   protected Measure computeMeasure(Roast roast, MeasurementModel model) {
      return roast.getStartWeight();
   }
}
