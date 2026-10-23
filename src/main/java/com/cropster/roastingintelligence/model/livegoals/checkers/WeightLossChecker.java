package com.cropster.roastingintelligence.model.livegoals.checkers;

import com.cropster.util.model.Unit;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Roast;

public class WeightLossChecker extends CompletedOnPassedOrFailedChecker {
   public static Measure getWeightLoss(Roast roast) {
      return roast.getStartWeight() != null && roast.getEndWeight() != null ? new Measure(-100.0 * roast.getWeightChange(), Unit.PERCENT) : null;
   }

   public WeightLossChecker() {
      this(false);
   }

   public WeightLossChecker(boolean continuous) {
      super("Checker.weightLoss", continuous);
   }

   @Override
   public boolean isGoalValid(Goal goal) {
      return super.isGoalValid(goal) && "weightLoss".equals(goal.getName());
   }

   @Override
   protected Measure computeMeasure(Roast roast, MeasurementModel model) {
      return getWeightLoss(roast);
   }
}
