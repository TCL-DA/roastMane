package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.RorPolyFitCalculator;
import com.cropster.util.model.Unit;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.util.Smoothing;

public class RorCurveModel extends FacadeCurveModel implements CurveModelListener {
   private final RorPolyFitCalculator _rorCalculator;
   private final CurveModel _source;
   private final TreeMap<Long, Double> _values = new TreeMap<>();
   private final Smoothing _smoothing;

   public RorCurveModel(CurveModel source, Smoothing smoothing) {
      super(source);
      this._source = source;
      this._smoothing = smoothing;
      this._rorCalculator = new RorPolyFitCalculator(smoothing.getWindowLength());
      this._source.addListener(this);
      this.notifyCurveChanged(this._source);
   }

   @Override
   public CurveModel getRootModel() {
      return this._source.getRootModel();
   }

   @Override
   public String getName() {
      return this._source.getName() + "RoR";
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
   public void notifyCurveChanged(CurveModel source, long time) {
      for (long t = time;
         t <= time + (long)(this._rorCalculator.getWindowLength() - 1) * 1000L && !source.getValues().isEmpty() && source.getValues().lastKey() >= t;
         t += 1000L
      ) {
         Double riseRate = this._rorCalculator.calcRorAtTime(source.getValues(), t, this._smoothing.getInterval(), this._smoothing.getOffset());
         if (riseRate != null) {
            Double oldValue = this._values.put(t, riseRate);
            if (!riseRate.equals(oldValue)) {
               this.fireCurveChanged(t);
            }
         } else if (this._values.remove(t) != null) {
            this.fireCurveChanged(t);
         }
      }
   }

   @Override
   public void notifyCurveChanged(CurveModel source) {
      this._values.clear();
      if (!source.getValues().isEmpty()) {
         this._values.putAll(this._rorCalculator.calcRor(source.getValues(), this._smoothing.getInterval(), this._smoothing.getOffset()));
      }

      this.fireCurveChanged();
   }
}
