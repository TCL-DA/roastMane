package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.cropster.roastingintelligence.LastStepTimeListener;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;

public class RemoveDuplicatesCurveModel extends FacadeCurveModel implements CurveModelListener {
   private final CurveModel _source;
   private final TreeMap<Long, Double> _values = new TreeMap<>();
   private final double _threshold;
   private Long _lastStepTime;
   private LastStepTimeListener lastStepTimeListener;

   public RemoveDuplicatesCurveModel(CurveModel source, double threshold) {
      super(source);
      this._source = source;

      assert threshold >= 0.0;

      this._threshold = Unit.FAHRENHEIT.equals(this.getUnit())
         ? Unit.FAHRENHEIT.fromRelative(BigDecimal.valueOf(threshold), Unit.CELSIUS).doubleValue()
         : threshold;
      this._source.addListener(this);
      this.notifyCurveChanged(this._source);
   }

   public RemoveDuplicatesCurveModel(CurveModel source) {
      this(source, 0.0);
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

   public CurveModel getSource() {
      return this._source;
   }

   public Long getLastStepTime() {
      return this._lastStepTime;
   }

   @Override
   public void notifyCurveChanged(CurveModel source, long time) {
      if (source.getValues().containsKey(Long.valueOf(time))) {
         double value = source.getValue(time);
         if (this._lastStepTime == null || Math.abs(source.getValues().get(this._lastStepTime) - value) > this._threshold) {
            this._values.put(time, value);
            this._lastStepTime = time;
            if (this.lastStepTimeListener != null) {
               this.lastStepTimeListener.lastStepTimeChanged(this._lastStepTime);
            }
         }
      } else {
         this._values.remove(time);
      }

      this.fireCurveChanged(time);
   }

   @Override
   public synchronized void notifyCurveChanged(CurveModel source) {
      this._values.clear();
      Long lastStepTime = null;

      for (Entry<Long, Double> entry : source.getValues().entrySet()) {
         Long time = entry.getKey();
         if (lastStepTime == null || Math.abs(this._values.get(lastStepTime) - entry.getValue()) > this._threshold) {
            this._values.put(time, entry.getValue());
            lastStepTime = time;
         }
      }

      this._lastStepTime = lastStepTime;
      if (this.lastStepTimeListener != null && this._lastStepTime != null) {
         this.lastStepTimeListener.lastStepTimeChanged(this._lastStepTime);
      }

      this.fireCurveChanged();
   }

   public LastStepTimeListener getLastStepTimeListener() {
      return this.lastStepTimeListener;
   }

   public void setLastStepTimeListener(LastStepTimeListener lastStepTimeListener) {
      this.lastStepTimeListener = lastStepTimeListener;
   }
}
