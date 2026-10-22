package com.yoctopuce.YoctoAPI;

import java.util.Date;

public class YMeasure {
   protected double _start = 0.0;
   protected double _end = 0.0;
   protected double _minVal = 0.0;
   protected double _avgVal = 0.0;
   protected double _maxVal = 0.0;

   YMeasure(double start, double end, double minVal, double avgVal, double maxVal) {
      this._start = start;
      this._end = end;
      this._minVal = minVal;
      this._avgVal = avgVal;
      this._maxVal = maxVal;
   }

   YMeasure() {
   }

   public Date get_startTimeUTC_asDate() {
      return new Date((long)(this._start * 1000.0 + 0.5));
   }

   public Date get_endTimeUTC_asDate() {
      return new Date((long)(this._end * 1000.0 + 0.5));
   }

   public double get_startTimeUTC() {
      return this._start;
   }

   public double get_endTimeUTC() {
      return this._end;
   }

   public double get_minValue() {
      return this._minVal;
   }

   public double get_averageValue() {
      return this._avgVal;
   }

   public double get_maxValue() {
      return this._maxVal;
   }
}
