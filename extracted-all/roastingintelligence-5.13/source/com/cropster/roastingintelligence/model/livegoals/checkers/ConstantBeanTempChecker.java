package com.cropster.roastingintelligence.model.livegoals.checkers;

import com.cropster.roastingintelligence.model.livegoals.CheckResult;
import java.util.SortedMap;
import java.util.Map.Entry;
import org.cropster.roastingintelligence.model.CurveData;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Roast;

public class ConstantBeanTempChecker extends AbstractCurveChecker {
   public static final double ZERO = 0.0;

   public ConstantBeanTempChecker() {
      super("Checker.constantBeanTemp", "beanTemperature");
   }

   @Override
   public Measure getReferenceValue(Roast referenceRoast) throws NullPointerException, UnsupportedOperationException {
      return null;
   }

   @Override
   public boolean isGoalValid(Goal goal) {
      return goal == null ? false : "constantBeanTemp".equals(goal.getName()) && goal.getLowerBound() == null && goal.getUpperBound() == null;
   }

   @Override
   protected CheckResult evaluateCurve(Goal goal, CurveData actualCurve, CurveData referenceCurve) {
      CurveData rorCurve = this.calcRoRCurve(actualCurve, 4, this.unitFromGoal(goal));
      if (rorCurve == null) {
         return CheckResult.missing(goal);
      } else {
         SortedMap<Long, Double> values = rorCurve.getValues();
         if (values != null && !values.isEmpty()) {
            boolean rorWasPositiveOnce = false;

            for (Entry<Long, Double> element : values.entrySet()) {
               if (element.getValue() > 0.0) {
                  rorWasPositiveOnce = true;
               } else if (rorWasPositiveOnce && element.getValue() <= 0.0) {
                  return CheckResult.failed(goal);
               }
            }

            return CheckResult.passed(goal);
         } else {
            return CheckResult.missing(goal);
         }
      }
   }
}
