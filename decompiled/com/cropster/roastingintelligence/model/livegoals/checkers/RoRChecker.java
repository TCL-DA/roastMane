package com.cropster.roastingintelligence.model.livegoals.checkers;

import com.cropster.roastingintelligence.model.livegoals.CheckResult;
import com.cropster.util.model.Unit;
import java.util.SortedMap;
import java.util.Map.Entry;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.cropster.roastingintelligence.model.CurveData;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.jetbrains.annotations.NotNull;

public class RoRChecker extends AbstractCurveChecker {
   public RoRChecker() {
      super("Checker.beanRoR", "beanTemperature");
   }

   @Override
   public boolean isGoalValid(Goal goal) {
      return super.isGoalValid(goal) && "beanRoR".equals(goal.getName()) && goal.getStartTime() != null && goal.getEndTime() != null;
   }

   @Override
   public boolean isTimeBound() {
      return true;
   }

   @Override
   protected CheckResult evaluateCurve(Goal goal, CurveData actualCurve, CurveData referenceCurve) {
      Unit goalUnit = this.unitFromGoal(goal);
      if (goalUnit == null) {
         goalUnit = actualCurve.getUnit();
      }

      CurveData curveInGoalUnit = this.getCurveInUnit(actualCurve, goalUnit);
      if (curveInGoalUnit == null) {
         return CheckResult.missing(goal);
      } else {
         SortedMap<Long, Double> values = curveInGoalUnit.getValues();
         return values != null && !values.isEmpty() ? this.evaluateCurve(goal, values, goalUnit) : CheckResult.missing(goal);
      }
   }

   @NotNull
   private CheckResult evaluateCurve(Goal goal, SortedMap<Long, Double> values, Unit goalUnit) {
      DescriptiveStatistics stats = new DescriptiveStatistics(4);
      CheckResult evaluation = null;

      for (Entry<Long, Double> element : values.entrySet()) {
         Long time = element.getKey();
         Double value = element.getValue();
         Measure movingAverage = this.computeMovingAverage(value, time, values, stats, goalUnit);
         evaluation = this.evaluateAtTime(goal, time, movingAverage, evaluation);
         if (evaluation != null && !evaluation.isPassed()) {
            return evaluation;
         }

         if (goal != null && time.compareTo(goal.getEndTime()) >= 0) {
            break;
         }
      }

      if (stats.getN() == 0L) {
         return CheckResult.missing(goal);
      } else {
         return evaluation != null ? evaluation : CheckResult.missing(goal);
      }
   }

   private Measure computeMovingAverage(Double value, Long time, SortedMap<Long, Double> values, DescriptiveStatistics stats, Unit goalUnit) {
      if (value != null && time >= 30000L) {
         Double referenceValue = this.getValueAt(values, time - 30000L);
         if (referenceValue == null) {
            return null;
         } else {
            stats.addValue(value - referenceValue);
            return new Measure(stats.getMean(), goalUnit);
         }
      } else {
         return null;
      }
   }

   private CheckResult evaluateAtTime(Goal goal, Long time, Measure movingAverage, CheckResult previousEvaluation) {
      return goal == null || movingAverage != null && time.compareTo(goal.getStartTime()) >= 0 && time.compareTo(goal.getEndTime()) <= 0
         ? this.evaluate(goal, movingAverage, null)
         : previousEvaluation;
   }
}
