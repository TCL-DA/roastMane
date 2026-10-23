package com.cropster.roastingintelligence.model;

import org.cropster.roastingintelligence.model.Measure;

public class CombinedMeasure {
   private Measure measure;
   private Measure rorMeasure;

   public CombinedMeasure(Measure measure) {
      this.measure = measure;
      this.rorMeasure = null;
   }

   public CombinedMeasure(Measure measure, Measure rorMeasure) {
      this.measure = measure;
      this.rorMeasure = rorMeasure;
   }

   public Measure getMeasure() {
      return this.measure;
   }

   public void setMeasure(Measure measure) {
      this.measure = measure;
   }

   public Measure getRorMeasure() {
      return this.rorMeasure;
   }

   public void setRorMeasure(Measure rorMeasure) {
      this.rorMeasure = rorMeasure;
   }
}
