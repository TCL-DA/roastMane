package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;

public class SnapshotCurveModel extends FacadeCurveModel implements CurveModelListener {
   private TreeMap<Long, Double> _values = new TreeMap<>();

   public SnapshotCurveModel(CurveModel source) {
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

   @Override
   public void notifyCurveChanged(CurveModel source) {
      if (this._values.isEmpty() && !source.getValues().isEmpty()) {
         this._values.putAll(source.getValues());
      }
   }

   @Override
   public void notifyCurveChanged(CurveModel source, long time) {
   }

   public void clear() {
      this._values.clear();
   }
}
