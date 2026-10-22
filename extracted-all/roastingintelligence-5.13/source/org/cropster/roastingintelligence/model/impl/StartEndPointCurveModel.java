package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;

public class StartEndPointCurveModel extends FacadeCurveModel implements CurveModelListener {
   private Long _startTime;
   private Long _endTime;
   private TreeMap<Long, Double> _values = new TreeMap<>();

   public StartEndPointCurveModel(CurveModel source) {
      super(source);
      this._source.addListener(this);
      this.notifyCurveChanged(this._source);
   }

   @Override
   public CurveModel getRootModel() {
      return this._source.getRootModel();
   }

   @Override
   public String getName() {
      return this._source.getName();
   }

   @Override
   public Date getZeroPoint() {
      return this._source.getZeroPoint();
   }

   @Override
   public Unit getUnit() {
      return this._source.getUnit();
   }

   @Override
   public Double getValue(long time) {
      return this._values.get(time);
   }

   @Override
   public SortedMap<Long, Double> getValues() {
      return Collections.unmodifiableSortedMap(this._values);
   }

   public Long getStartTime() {
      return this._startTime;
   }

   public void setStartTime(Long startTime) {
      Long previousStartTime = this._startTime;
      this._startTime = startTime != null ? startTime - this.getZeroPoint().getTime() : null;
      this.resetTime(previousStartTime, this._startTime);
   }

   public Long getEndTime() {
      return this._endTime;
   }

   public void setEndTime(Long endTime) {
      Long previousEndTime = this._endTime;
      this._endTime = endTime != null ? endTime - this.getZeroPoint().getTime() : null;
      this.resetTime(previousEndTime, this._endTime);
   }

   void resetTime(Long previousTime, Long time) {
      if (previousTime != null && !previousTime.equals(time) && !this._source.getValues().containsKey(previousTime)) {
         this._values.remove(previousTime);
         this.fireCurveChanged(previousTime);
      }

      if (time != null && !this._values.containsKey(time)) {
         this.updatePoint(time, this._source);
      }
   }

   void updatePoint(Long pointTime, CurveModel source) {
      if (pointTime != null) {
         if (!this._source.getValues().containsKey(pointTime)) {
            Long lowerKey = lowerKey(source.getValues(), pointTime);
            if (lowerKey == null) {
               if (this._values.containsKey(pointTime) && !this._source.getValues().containsKey(pointTime)) {
                  this._values.remove(pointTime);
                  this.fireCurveChanged(pointTime);
               }
            } else {
               Double lowerValue = source.getValues().get(lowerKey);
               Double oldValue = this._values.put(pointTime, lowerValue);
               if (!lowerValue.equals(oldValue)) {
                  this.fireCurveChanged(pointTime);
               }
            }
         }
      }
   }

   @Override
   public void notifyCurveChanged(CurveModel source, long time) {
      Double sourceValue = source.getValues().get(Long.valueOf(time));
      if (sourceValue != null) {
         this._values.put(time, sourceValue);
      } else {
         this._values.remove(time);
      }

      this.fireCurveChanged(time);
      if (this._startTime != null && time < this._startTime) {
         this.updatePoint(this._startTime, source);
      }

      if (this._endTime != null && time < this._endTime) {
         this.updatePoint(this._endTime, source);
      }
   }

   @Override
   public void notifyCurveChanged(CurveModel source) {
      this._values = new TreeMap<>(source.getValues());
      if (this._startTime != null) {
         this.updatePoint(this._startTime, source);
      }

      if (this._endTime != null) {
         this.updatePoint(this._endTime, source);
      }

      this.fireCurveChanged();
   }
}
