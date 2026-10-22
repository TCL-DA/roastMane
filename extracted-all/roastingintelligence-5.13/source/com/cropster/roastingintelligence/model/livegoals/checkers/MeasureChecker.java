package com.cropster.roastingintelligence.model.livegoals.checkers;

import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Roast;

public abstract class MeasureChecker extends CompletedOnPassedOrFailedChecker {
   public MeasureChecker(String name) {
      super(name);
   }

   public MeasureChecker(String name, boolean continuous) {
      super(name, continuous);
   }

   @Override
   protected Measure computeMeasure(Roast roast, MeasurementModel model) {
      return roast.getMeasures().get(this.getMeasureName());
   }

   protected abstract String getMeasureName();
}
