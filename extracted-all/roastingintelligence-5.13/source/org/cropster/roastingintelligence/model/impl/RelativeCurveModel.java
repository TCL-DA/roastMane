package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.cropster.roastingintelligence.RoastingModel;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.util.NanoTime;

public class RelativeCurveModel extends FacadeCurveModel implements CurveModelListener {
   private final RoastingModel _roastingModel;
   private final CurveModel _source;
   private Date _zeroPoint;
   private TreeMap<Long, Double> _values;

   public RelativeCurveModel(RoastingModel roastingModel, CurveModel source) {
      super(source);
      this._roastingModel = roastingModel;
      this._source = source;
      this._zeroPoint = new Date(NanoTime.currentTimeMillis());
      this._values = new TreeMap<>();
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
      return this._roastingModel.getStartDate() == null ? this._zeroPoint : this._roastingModel.getStartDate();
   }

   public void setZeroPoint(Date zeroPoint) {
      this._zeroPoint = zeroPoint;
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

   private long calcTime(long time) {
      long offsetMillis = this.getZeroPoint().getTime() - this._source.getZeroPoint().getTime();
      return time - offsetMillis;
   }

   @Override
   public void notifyCurveChanged(CurveModel source) {
      if (!this._zeroPoint.equals(this._roastingModel.getStartDate())) {
         this._zeroPoint = new Date(NanoTime.currentTimeMillis());
      }

      TreeMap<Long, Double> values = new TreeMap<>();

      for (Entry<Long, Double> entry : this._source.getValues().entrySet()) {
         long time = this.calcTime(entry.getKey());
         values.put(time, entry.getValue());
      }

      this._values = values;
      this.fireCurveChanged();
   }

   @Override
   public void notifyCurveChanged(CurveModel source, long time) {
      if (this._roastingModel.getStartDate() != null) {
         Long transformedTime = this.calcTime(time);
         Double value = source.getValue(time);
         if (value != null) {
            this._values.put(transformedTime, value);
            this.fireCurveChanged(transformedTime);
         } else if (this._values.remove(transformedTime) != null) {
            this.fireCurveChanged(transformedTime);
         }
      } else {
         this.notifyCurveChanged(source);
      }
   }

   @Override
   public void notifyCurveChanged(CurveModel source, SortedSet<Long> times) {
      Date zeroPoint = this.getZeroPoint();
      if (zeroPoint != null && !times.isEmpty() && times.first() < zeroPoint.getTime()) {
         this.notifyCurveChanged(source);
      } else {
         CurveModelListener.super.notifyCurveChanged(source, times);
      }
   }
}
