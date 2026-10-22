package com.cropster.roastingintelligence.model.livegoals.checkers;

import com.cropster.util.model.Unit;
import java.util.Objects;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Roast;

public class DurationChecker extends SimpleChecker {
   private final boolean neverEvaluate;

   public static Measure getDuration(Roast roast) throws NullPointerException {
      return new Measure((double)roast.getDuration().longValue(), Unit.MILLISECOND).toUnit(Unit.DURATION);
   }

   public DurationChecker() {
      this(false);
   }

   public DurationChecker(boolean neverEvaluate) {
      super("Checker.duration");
      this.neverEvaluate = neverEvaluate;
   }

   @Override
   public Measure getActualValue(Roast roast, MeasurementModel model) throws NullPointerException {
      if (this.neverEvaluate) {
         Objects.requireNonNull(roast, "Null 'roast' parameter");
         return null;
      } else {
         return super.getActualValue(roast, model);
      }
   }

   @Override
   public boolean isGoalValid(Goal goal) {
      return super.isGoalValid(goal) && "duration".equals(goal.getName());
   }

   @Override
   protected Measure computeMeasure(Roast roast, MeasurementModel model) {
      return getDuration(roast);
   }
}
