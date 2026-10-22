package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;

public class ScalingCurveModel extends FacadeCurveModel implements CurveModelListener {
   private final CurveModel _source;
   private final TreeMap<Long, Double> _values = new TreeMap<>();
   private final int _factor;

   public ScalingCurveModel(CurveModel source, int factor) {
      super(source);
      this._source = source;
      this._factor = factor;
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

   public int getFactor() {
      return this._factor;
   }

   @Override
   public void notifyCurveChanged(CurveModel source, long time) {
      if (this._source.getValues().containsKey(Long.valueOf(time))) {
         this._values.put(time, this._source.getValue(time) * (double)this._factor);
         this.fireCurveChanged(time);
      } else if (this._values.remove(time) != null) {
         this.fireCurveChanged(time);
      }
   }

   @Override
   public void notifyCurveChanged(CurveModel source) {
      this._values.clear();
      source.getValues().entrySet().forEach(e -> this._values.put(e.getKey(), e.getValue() * (double)this._factor));
      this.fireCurveChanged();
   }
}
