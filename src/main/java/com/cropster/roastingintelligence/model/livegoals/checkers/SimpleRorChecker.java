package com.cropster.roastingintelligence.model.livegoals.checkers;

import com.cropster.roastingintelligence.model.livegoals.CheckResult;
import java.util.SortedMap;
import java.util.Map.Entry;
import org.cropster.roastingintelligence.model.CurveData;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;

public class SimpleRorChecker extends AbstractCurveChecker {
   private final long rorIntervalMs;
   private final String measureName;
   private final boolean continuous;

   public SimpleRorChecker(String measureName, long rorIntervalSec) {
      this(measureName, rorIntervalSec, false);
   }

   public SimpleRorChecker(String measureName, long rorIntervalSec, boolean continuous) {
      super("Checker." + measureName, "beanTemperature");
      this.measureName = measureName;
      this.rorIntervalMs = rorIntervalSec * 1000L;
      this.continuous = continuous;
   }

   @Override
   public boolean isGoalValid(Goal goal) {
      return super.isGoalValid(goal) && this.measureName.equals(goal.getName());
   }

   @Override
   protected CheckResult evaluateCurve(Goal goal, CurveData actualCurve, CurveData referenceCurve) {
      if (this.continuous) {
         return CheckResult.missing(goal);
      } else {
         CurveData rorCurve = this.calcRoRCurve(actualCurve, 4, this.rorIntervalMs, this.unitFromGoal(goal));
         if (rorCurve == null) {
            return CheckResult.missing(goal);
         } else {
            SortedMap<Long, Double> values = rorCurve.getValues();
            return values != null && !values.isEmpty() ? this.evaluateRoRValues(goal, values, rorCurve) : CheckResult.missing(goal);
         }
      }
   }

   private CheckResult evaluateRoRValues(Goal goal, SortedMap<Long, Double> values, CurveData rorCurve) {
      if (goal == null) {
         return CheckResult.missing(goal);
      } else {
         Measure upperBound = goal.getUpperBound();
         Measure lowerBound = goal.getLowerBound();
         boolean passedLowerBound = false;
         Double maxRoR = Double.NEGATIVE_INFINITY;

         for (Entry<Long, Double> element : values.entrySet()) {
            Double elementValue = element.getValue();
            passedLowerBound = updatePassedLowerBound(lowerBound, elementValue, passedLowerBound);
            CheckResult violation = checkViolation(goal, elementValue, upperBound, lowerBound, passedLowerBound);
            if (violation != null) {
               return violation;
            }

            if (elementValue > maxRoR) {
               maxRoR = elementValue;
            }
         }

         return buildFinalResult(goal, lowerBound, passedLowerBound, maxRoR, rorCurve);
      }
   }

   private static boolean updatePassedLowerBound(Measure lowerBound, Double elementValue, boolean passedLowerBound) {
      return !passedLowerBound && lowerBound != null ? elementValue >= lowerBound.getAmount() : passedLowerBound;
   }

   private static CheckResult checkViolation(Goal goal, Double elementValue, Measure upperBound, Measure lowerBound, boolean passedLowerBound) {
      if (lowerBound != null && passedLowerBound && elementValue < lowerBound.getAmount()) {
         return CheckResult.failed(goal, new Measure(elementValue, lowerBound.getUnit()));
      } else {
         return upperBound != null && elementValue > upperBound.getAmount() ? CheckResult.failed(goal, new Measure(elementValue, upperBound.getUnit())) : null;
      }
   }

   private static CheckResult buildFinalResult(Goal goal, Measure lowerBound, boolean passedLowerBound, Double maxRoR, CurveData rorCurve) {
      return lowerBound != null && !passedLowerBound
         ? CheckResult.failed(goal, new Measure(maxRoR, lowerBound.getUnit()))
         : CheckResult.passed(goal, new Measure(maxRoR, rorCurve.getUnit()));
   }
}
