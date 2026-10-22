package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.LongUnaryOperator;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;

public class OffsetCurveModel extends FacadeCurveModel implements CurveModelListener {
   private final CurveModel _source;
   private final LongUnaryOperator _offsetFunction;
   private TreeMap<Long, Double> _values;

   public OffsetCurveModel(CurveModel source, long offset) {
      this(source, t -> t + offset);
   }

   public OffsetCurveModel(CurveModel source, LongUnaryOperator offsetFunction) {
      super(source);
      this._source = source;
      this._offsetFunction = offsetFunction;
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
      return new Date(0L);
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
      TreeMap<Long, Double> values = new TreeMap<>();

      for (Entry<Long, Double> entry : this._source.getValues().entrySet()) {
         long time = this._offsetFunction.applyAsLong(entry.getKey());
         values.put(time, entry.getValue());
      }

      this._values = values;
      this.fireCurveChanged();
   }

   @Override
   public void notifyCurveChanged(CurveModel source, long time) {
      Long transformedTime = this._offsetFunction.applyAsLong(time);
      Double value = source.getValue(time);
      if (value != null) {
         this._values.put(transformedTime, value);
         this.fireCurveChanged(transformedTime);
      } else if (this._values.remove(transformedTime) != null) {
         this.fireCurveChanged(transformedTime);
      }
   }
}
