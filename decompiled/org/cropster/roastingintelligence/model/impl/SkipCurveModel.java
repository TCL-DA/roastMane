package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;

public class SkipCurveModel extends FacadeCurveModel implements CurveModelListener {
   public SkipCurveModel(CurveModel source) {
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
      long secs = (time - this._source.getValues().firstKey()) / 1000L;
      return secs % 5L != 0L && time != this._source.getValues().lastKey() ? null : this._source.getValue(time);
   }

   @Override
   public SortedMap<Long, Double> getValues() {
      TreeMap<Long, Double> values = new TreeMap<>();

      for (Long time : this._source.getValues().keySet()) {
         Double value = this.getValue(time);
         if (value != null) {
            values.put(time, value);
         }
      }

      return values;
   }

   @Override
   public void notifyCurveChanged(CurveModel source, long time) {
      this.fireCurveChanged(time);
   }

   @Override
   public void notifyCurveChanged(CurveModel source) {
      this.fireCurveChanged();
   }
}
