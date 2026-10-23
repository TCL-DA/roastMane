package com.cropster.roastingintelligence.model.livegoals.checkers;

import com.cropster.roastingintelligence.model.livegoals.CheckResult;
import java.util.SortedMap;
import org.cropster.roastingintelligence.model.CurveData;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Roast;

public class ChargeChecker extends AbstractCurveChecker {
   private final boolean continuousEvaluation;
   private final boolean fullValidation;
   private final String goalName;

   public ChargeChecker(String goalAndCurveName, boolean continuousEvaluation, boolean fullValidation) {
      this(goalAndCurveName, goalAndCurveName, continuousEvaluation, fullValidation);
   }

   public ChargeChecker(String goalName, String curveName, boolean continuousEvaluation, boolean fullValidation) {
      super("Checker." + goalName, curveName);
      this.goalName = goalName;
      this.continuousEvaluation = continuousEvaluation;
      this.fullValidation = fullValidation;
   }

   @Override
   public boolean isGoalValid(Goal goal) {
      if (goal == null) {
         return false;
      } else {
         return this.fullValidation ? super.isGoalValid(goal) && this.goalName.equals(goal.getName()) : this.goalName.equals(goal.getName());
      }
   }

   @Override
   public Measure getReferenceValue(Roast referenceRoast) throws NullPointerException, UnsupportedOperationException {
      return this.getFirstValue(this.getCurve(referenceRoast));
   }

   @Override
   protected CheckResult evaluateCurve(Goal goal, CurveData actualCurve, CurveData referenceCurve) {
      if (actualCurve == null) {
         return CheckResult.missing(goal);
      } else {
         SortedMap<Long, Double> values = actualCurve.getValues();
         if (values != null && !values.isEmpty()) {
            Measure actualValue = this.continuousEvaluation ? this.getLastValue(actualCurve) : this.getFirstValue(actualCurve);
            Measure referenceValue = null;
            if (goal != null && !goal.isAbsolute()) {
               referenceValue = this.getFirstValue(referenceCurve);
            }

            return this.continuousEvaluation
               ? this.evaluate(goal, actualValue, referenceValue)
               : CheckResult.completed(this.evaluate(goal, actualValue, referenceValue));
         } else {
            return CheckResult.missing(goal);
         }
      }
   }
}
