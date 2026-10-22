package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.util.Date;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;

public class OverwriteCurveModel extends AbstractCurveModel implements CurveModelListener {
   private volatile TreeMap<Long, Double> _values = new TreeMap<>();
   private final CurveModel _baseSource;
   private final CurveModel _overwriteSource;
   private final Supplier<Long> _startPointSupplier;
   private final Supplier<Long> _endPointSupplier;

   public OverwriteCurveModel(CurveModel baseSource, CurveModel overwriteSource, Supplier<Long> startPointSupplier, Supplier<Long> endPointSupplier) {
      this._baseSource = baseSource;
      this._overwriteSource = overwriteSource;
      this._startPointSupplier = startPointSupplier;
      this._endPointSupplier = endPointSupplier;
      this._baseSource.addListener(this);
      this._overwriteSource.addListener(this);
      this.notifyCurveChanged(this._baseSource);
      this.notifyCurveChanged(this._overwriteSource);
   }

   @Override
   public CurveModel getRootModel() {
      return this._baseSource.getRootModel();
   }

   @Override
   public <T extends CurveModel> T getModel(Class<T> type) {
      if (type.isInstance(this)) {
         return (T)this;
      } else {
         for (CurveModel source : new CurveModel[]{this._baseSource, this._overwriteSource}) {
            T result = source.getModel(type);
            if (result != null) {
               return result;
            }
         }

         return null;
      }
   }

   @Override
   public String getName() {
      String name = "";

      for (CurveModel source : new CurveModel[]{this._baseSource, this._overwriteSource}) {
         if (!name.isEmpty()) {
            name = name + "/";
         }

         name = name + source.getName();
      }

      return String.format("[%s]", name);
   }

   @Override
   public Date getZeroPoint() {
      return this._baseSource.getZeroPoint();
   }

   @Override
   public Unit getUnit() {
      return this._baseSource.getUnit();
   }

   @Override
   public Double getValue(long time) {
      return this._values.get(time);
   }

   @Override
   public SortedMap<Long, Double> getValues() {
      return new TreeMap<>(this._values);
   }

   @Override
   public synchronized void notifyCurveChanged(CurveModel source, SortedSet<Long> times) {
      Long startPoint = this._startPointSupplier.get();
      Long endPoint = this._endPointSupplier.get();
      SortedSet<Long> mergedKeys = new TreeSet<>();
      Long timeFrom = times.first();
      Long timeTo = times.last();
      mergedKeys.addAll(this._baseSource.getValues().subMap(timeFrom, timeTo + 1L).keySet());
      mergedKeys.addAll(this._overwriteSource.getValues().subMap(timeFrom, timeTo + 1L).keySet());
      mergedKeys.forEach(time -> this.update(time, startPoint, endPoint));
      this.fireCurveChanged(mergedKeys);
   }

   @Override
   public synchronized void notifyCurveChanged(CurveModel source, long time) {
      Long startPoint = this._startPointSupplier.get();
      Long endPoint = this._endPointSupplier.get();
      this.update(time, startPoint, endPoint);
      this.fireCurveChanged(time);
   }

   private void update(long time, Long startPoint, Long endPoint) {
      Double value = startPoint != null && time >= startPoint && endPoint != null && time <= endPoint
         ? this._overwriteSource.getValue(time)
         : this._baseSource.getValue(time);
      if (value != null) {
         this._values.put(time, value);
      }
   }

   @Override
   public synchronized void notifyCurveChanged(CurveModel source) {
      TreeMap<Long, Double> values = new TreeMap<>(this._baseSource.getValues());
      if (!this._overwriteSource.getValues().isEmpty()) {
         Long startPoint = this._startPointSupplier.get();
         Long endPoint = this._endPointSupplier.get();
         if (startPoint != null && endPoint != null && startPoint <= endPoint) {
            values.putAll(this._overwriteSource.getValues().subMap(startPoint, endPoint + 1L));
         }
      }

      this._values = values;
      this.fireCurveChanged();
   }
}
