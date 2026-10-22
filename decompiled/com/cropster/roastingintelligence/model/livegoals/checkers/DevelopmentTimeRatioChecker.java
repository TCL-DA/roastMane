package com.cropster.roastingintelligence.model.livegoals.checkers;

import com.cropster.roastingintelligence.model.livegoals.CheckResult;
import org.cropster.roastingintelligence.RoastingModel;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Roast;

public class DevelopmentTimeRatioChecker extends MeasureChecker {
   public DevelopmentTimeRatioChecker() {
      super("Checker.developmentTimeRatio");
   }

   @Override
   public CheckResult evaluate(Goal goal, Measure actualValue, Measure referenceValue) {
      CheckResult evaluation = super.evaluate(goal, actualValue, referenceValue);
      return !evaluation.isPassed() && !evaluation.isFailed() ? evaluation : CheckResult.passed(evaluation);
   }

   @Override
   public boolean isGoalValid(Goal goal) {
      return super.isGoalValid(goal) && "developmentTimeRatio".equals(goal.getName());
   }

   @Override
   protected Measure computeMeasure(Roast roast, MeasurementModel model) {
      if (model instanceof RoastingModel roastingModel) {
         Long developmentTime = DevelopmentTimeChecker.getDevelopmentTime(roast, roastingModel);
         if (developmentTime != null) {
            return roastingModel.calcDevelopmentTimeRatio(roast.getDuration(), developmentTime);
         }
      }

      return roast.getMeasures().get(this.getMeasureName());
   }

   @Override
   protected String getMeasureName() {
      return "developmentTimeRatio";
   }
}
