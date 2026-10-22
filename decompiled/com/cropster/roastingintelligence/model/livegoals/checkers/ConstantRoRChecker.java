package com.cropster.roastingintelligence.model.livegoals.checkers;

import com.cropster.roastingintelligence.model.livegoals.CheckResult;
import com.cropster.util.model.Unit;
import java.util.SortedMap;
import java.util.Map.Entry;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.cropster.roastingintelligence.model.CurveData;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Roast;

public class ConstantRoRChecker extends AbstractCurveChecker {
   private static final int ROR_MEAN_WINDOW = 23;
   private static final double ROR_VARIANCE = 0.002;
   private static final int VARIANCE_WINDOW = 60;

   public ConstantRoRChecker() {
      super("Checker.constantRoR", "beanTemperature");
   }

   @Override
   public Measure getReferenceValue(Roast referenceRoast) throws NullPointerException, UnsupportedOperationException {
      return null;
   }

   @Override
   public boolean isGoalValid(Goal goal) {
      return goal == null ? false : "constantRoR".equals(goal.getName()) && goal.getLowerBound() == null && goal.getUpperBound() == null;
   }

   @Override
   protected CheckResult evaluateCurve(Goal goal, CurveData actualCurve, CurveData referenceCurve) {
      CurveData rorCurve = this.calcRoRCurve(actualCurve, 23, Unit.CELSIUS);
      if (rorCurve == null) {
         return CheckResult.missing(goal);
      } else {
         SortedMap<Long, Double> values = rorCurve.getValues();
         if (values != null && !values.isEmpty()) {
            DescriptiveStatistics stats = new DescriptiveStatistics(60);

            for (Entry<Long, Double> element : values.entrySet()) {
               Double value = element.getValue();
               stats.addValue(value);
               if (stats.getN() >= 60L) {
                  double variance = stats.getVariance();
                  if (variance < 0.002) {
                     return CheckResult.failed(goal);
                  }
               }
            }

            return CheckResult.passed(goal);
         } else {
            return CheckResult.missing(goal);
         }
      }
   }
}
