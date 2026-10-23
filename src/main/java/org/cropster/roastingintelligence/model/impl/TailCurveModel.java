package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.function.Supplier;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;

public class TailCurveModel extends FacadeCurveModel implements CurveModelListener {
   private final Supplier<Long> _offsetSupplier;

   public TailCurveModel(CurveModel source, Supplier<Long> offsetSupplier) {
      super(source);
      this._offsetSupplier = offsetSupplier;
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
      Long offset = this._offsetSupplier.get();
      return offset != null && time >= offset ? this._source.getValue(time) : null;
   }

   @Override
   public SortedMap<Long, Double> getValues() {
      Long offset = this._offsetSupplier.get();
      return offset != null ? this._source.getValues().tailMap(offset) : Collections.emptySortedMap();
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
