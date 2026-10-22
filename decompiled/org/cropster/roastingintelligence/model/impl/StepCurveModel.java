package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;

public class StepCurveModel extends FacadeCurveModel implements CurveModelListener {
   private final CurveModel _source;
   private final TreeMap<Long, Double> _values = new TreeMap<>();

   public StepCurveModel(CurveModel source) {
      super(source);
      this._source = source;
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

   private void processIntermediateValues(CurveModel source, long time, TreeMap<Long, Double> values) {
      Long lastKey = values.isEmpty() ? null : values.lastKey();
      Long secondLastKey = null;
      if (lastKey != null) {
         SortedMap<Long, Double> headMap = values.headMap(lastKey);
         if (!headMap.isEmpty()) {
            secondLastKey = headMap.lastKey();
         }
      }

      Double value = source.getValue(time);
      values.put(time, value);
      if (lastKey != null && secondLastKey != null) {
         Double lastValue = values.get(lastKey);
         if (lastValue.equals(values.get(secondLastKey)) && value.equals(lastValue) && values.remove(lastKey) != null) {
            this.fireCurveChanged(lastKey);
         }
      }
   }

   @Override
   public void notifyCurveChanged(CurveModel source, long time) {
      if (source.getValues().containsKey(Long.valueOf(time))) {
         this.processIntermediateValues(source, time, this._values);
         this.fireCurveChanged(time);
      } else if (this._values.remove(time) != null) {
         this.fireCurveChanged(time);
      }
   }

   @Override
   public synchronized void notifyCurveChanged(CurveModel source) {
      this._values.clear();

      for (long time : source.getValues().keySet()) {
         this.processIntermediateValues(source, time, this._values);
      }

      this.fireCurveChanged();
   }
}
