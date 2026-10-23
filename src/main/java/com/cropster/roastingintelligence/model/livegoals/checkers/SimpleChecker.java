package com.cropster.roastingintelligence.model.livegoals.checkers;

import java.util.Objects;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Roast;

public abstract class SimpleChecker extends AbstractChecker {
   protected SimpleChecker(String name) {
      super(name);
   }

   @Override
   public Measure getActualValue(Roast roast, MeasurementModel model) throws NullPointerException {
      Objects.requireNonNull(roast, "Null 'roast' parameter");
      return this.computeMeasure(roast, model);
   }

   @Override
   public Measure getReferenceValue(Roast referenceRoast) throws NullPointerException {
      Objects.requireNonNull(referenceRoast, "Null 'referenceRoast' parameter");
      return this.computeMeasure(referenceRoast, null);
   }

   protected abstract Measure computeMeasure(Roast var1, MeasurementModel var2);
}
