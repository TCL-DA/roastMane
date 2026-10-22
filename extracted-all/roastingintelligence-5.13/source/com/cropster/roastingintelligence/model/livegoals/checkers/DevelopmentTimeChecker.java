package com.cropster.roastingintelligence.model.livegoals.checkers;

import com.cropster.roastingintelligence.model.livegoals.CheckResult;
import com.cropster.util.model.Unit;
import org.cropster.roastingintelligence.RoastingModel;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Roast;

public class DevelopmentTimeChecker extends MeasureChecker {
   public static Long getDevelopmentTime(Roast roast, RoastingModel roastingModel) {
      Long duration = roast.getDuration();
      Long firstCrackTime = roastingModel.getEventTime("firstCrack");
      return duration != null && firstCrackTime != null ? duration - firstCrackTime : null;
   }

   public DevelopmentTimeChecker() {
      super("Checker.developmentTime");
   }

   @Override
   public CheckResult evaluate(Goal goal, Measure actualValue, Measure referenceValue) {
      CheckResult evaluation = super.evaluate(goal, actualValue, referenceValue);
      return !evaluation.isPassed() && !evaluation.isFailed() ? evaluation : CheckResult.passed(evaluation);
   }

   @Override
   public boolean isGoalValid(Goal goal) {
      return super.isGoalValid(goal) && "developmentTime".equals(goal.getName());
   }

   @Override
   protected Measure computeMeasure(Roast roast, MeasurementModel model) {
      if (model instanceof RoastingModel roastingModel) {
         Long developmentTime = getDevelopmentTime(roast, roastingModel);
         if (developmentTime != null) {
            return new Measure((double)developmentTime.longValue(), Unit.MILLISECOND).toUnit(Unit.DURATION);
         }
      }

      return roast.getMeasures().get(this.getMeasureName());
   }

   @Override
   protected String getMeasureName() {
      return "developmentTime";
   }
}
