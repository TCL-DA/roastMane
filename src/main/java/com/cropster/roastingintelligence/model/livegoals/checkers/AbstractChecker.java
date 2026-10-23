package com.cropster.roastingintelligence.model.livegoals.checkers;

import com.cropster.roastingintelligence.model.livegoals.CheckResult;
import com.cropster.roastingintelligence.model.livegoals.Checker;
import com.cropster.util.model.Unit;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;

public abstract class AbstractChecker implements Checker {
   public static final double ONE = 1.0;
   public static final double ZERO = 0.0;
   private final String name;

   public static Measure calculateLowerBound(Goal goal, Measure referenceValue) {
      Measure lowerBound = goal.getLowerBound();
      if (lowerBound == null) {
         return null;
      } else if (referenceValue == null) {
         return lowerBound;
      } else {
         Unit referenceUnit = referenceValue.getUnit();
         Measure convertedLowerBound = lowerBound.toUnit(referenceUnit);
         return goal.isAbsolute() ? convertedLowerBound : referenceValue.add(convertedLowerBound);
      }
   }

   public static Measure calculateUpperBound(Goal goal, Measure referenceValue) {
      Measure upperBound = goal.getUpperBound();
      if (upperBound == null) {
         return null;
      } else if (referenceValue == null) {
         return upperBound;
      } else {
         Unit referenceUnit = referenceValue.getUnit();
         Measure convertedUpperBound = upperBound.toUnit(referenceUnit);
         return goal.isAbsolute() ? convertedUpperBound : referenceValue.add(convertedUpperBound);
      }
   }

   protected AbstractChecker(String name) {
      this.name = name;
   }

   @Override
   public CheckResult evaluate(Goal goal, Measure actualValue, Measure referenceValue) {
      if (actualValue == null) {
         return CheckResult.missing(goal);
      } else {
         Measure error = this.getError(goal, actualValue, referenceValue);
         return error != null && (!(error.getAmount() >= 0.0) || !(error.getAmount() <= 1.0))
            ? CheckResult.failed(goal, actualValue, referenceValue, error)
            : CheckResult.passed(goal, actualValue, referenceValue, error);
      }
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public boolean isGoalValid(Goal goal) {
      if (goal == null) {
         return false;
      } else {
         Long startTime = goal.getStartTime();
         Long endTime = goal.getEndTime();
         Measure lower = goal.getLowerBound();
         Measure upper = goal.getUpperBound();
         return (startTime == null && endTime == null || startTime != null && endTime != null)
            && lower != null
            && upper != null
            && lower.getUnit() != null
            && upper.getUnit() != null
            && lower.getUnit() == upper.getUnit();
      }
   }

   public boolean isTimeBound() {
      return false;
   }

   protected Measure getError(Goal goal, Measure actualValue, Measure referenceValue) {
      if (invalidParameters(goal, actualValue, referenceValue)) {
         return null;
      } else {
         Unit goalUnit = goal.getLowerBound() != null ? goal.getLowerBound().getUnit() : goal.getUpperBound().getUnit();
         Measure convertedValue = actualValue.toUnit(goalUnit);
         Measure upperBound = calculateUpperBound(goal, referenceValue);
         Measure lowerBound = calculateLowerBound(goal, referenceValue);
         if (upperBound != null) {
            Measure error = convertedValue.subtract(upperBound);
            if (error.getAmount() > 0.0) {
               return error.add(new Measure(1.0, convertedValue.getUnit()));
            }
         }

         if (lowerBound != null) {
            Measure error = convertedValue.subtract(lowerBound);
            if (error.getAmount() < 0.0) {
               return error;
            }
         }

         if (lowerBound != null && upperBound != null) {
            double validPercentage = (convertedValue.getAmount() - lowerBound.getAmount()) / (upperBound.getAmount() - lowerBound.getAmount());
            return new Measure(validPercentage, Unit.NUMBER);
         } else {
            return null;
         }
      }
   }

   private static boolean invalidParameters(Goal goal, Measure actualValue, Measure referenceValue) {
      return goal != null && actualValue != null && (goal.isAbsolute() || referenceValue != null) ? !goal.hasBoundsDefined() : true;
   }
}
