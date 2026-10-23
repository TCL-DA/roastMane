package org.cropster.roastingintelligence.model;

public class Descriptor {
   private String _name;
   private Float _intensity;
   private boolean _positive;

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public Float getIntensity() {
      return this._intensity;
   }

   public void setIntensity(Float intensity) {
      this._intensity = intensity;
   }

   public boolean isPositive() {
      return this._positive;
   }

   public void setPositive(boolean positive) {
      this._positive = positive;
   }
}
