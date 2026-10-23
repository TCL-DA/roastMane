package com.cropster.roastingintelligence.model.livegoals;

import org.cropster.roastingintelligence.model.CurveData;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Roast;

public interface Checker {
   default CheckResult evaluate(Goal goal, Measure actualValue, Measure referenceValue) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Measure evaluation is not supported by checker " + this.getName());
   }

   default CheckResult evaluate(Goal goal, Measure actualValue) throws UnsupportedOperationException {
      return this.evaluate(goal, actualValue, null);
   }

   default CheckResult evaluate(Goal goal, CurveData actualCurve, CurveData referenceCurve) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Roast evaluation is not supported by checker " + this.getName());
   }

   default CheckResult evaluate(Goal goal, CurveData actualCurve) throws UnsupportedOperationException {
      return this.evaluate(goal, actualCurve, null);
   }

   String getName();

   default Measure getReferenceValue(Roast referenceRoast) throws NullPointerException, UnsupportedOperationException {
      throw new UnsupportedOperationException("Getting reference value from roast is not supported by checker " + this.getName());
   }

   default Measure getActualValue(Roast roast, MeasurementModel model) throws NullPointerException, UnsupportedOperationException {
      throw new UnsupportedOperationException("Getting actual value from roast is not supported by checker " + this.getName());
   }

   boolean isGoalValid(Goal var1);
}
