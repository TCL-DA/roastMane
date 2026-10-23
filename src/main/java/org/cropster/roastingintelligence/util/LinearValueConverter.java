package org.cropster.roastingintelligence.util;

public class LinearValueConverter implements ValueConverter {
   private final double _inMin;
   private final double _inMax;
   private final double _outMin;
   private final double _outMax;

   public LinearValueConverter(double inMin, double inMax, double outMin, double outMax) {
      this._inMin = inMin;
      this._inMax = inMax;
      this._outMin = outMin;
      this._outMax = outMax;
   }

   @Override
   public double convertValueToPercent(double value) {
      return (value - this._inMin) * (this._outMax - this._outMin) / (this._inMax - this._inMin) + this._outMin;
   }

   @Override
   public double convertPercentToValue(double percent) {
      return (percent + this._outMin) * (this._inMax - this._inMin) / (this._outMax - this._outMin) + this._inMin;
   }
}
