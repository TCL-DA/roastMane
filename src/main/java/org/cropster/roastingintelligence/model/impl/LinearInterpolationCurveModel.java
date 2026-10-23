package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.util.MathUtil;

public class LinearInterpolationCurveModel extends FacadeCurveModel implements CurveModelListener {
   private final TreeMap<Long, Double> _values = new TreeMap<>();

   public LinearInterpolationCurveModel(CurveModel source) {
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
   public void notifyCurveChanged(CurveModel source, SortedSet<Long> times) {
      if (!times.isEmpty()) {
         this.notifyCurveChanged(source, times.first(), times.last());
      }
   }

   public void notifyCurveChanged(CurveModel source, long timeFrom, long timeTo) {
      SortedSet<Long> deletedKeys = this.delete(source.getValues(), this._values, timeFrom, timeTo);
      SortedMap<Long, Double> changedValues = this._source.getValues().subMap(timeFrom, timeTo + 1L);
      this._values.putAll(changedValues);
      SortedSet<Long> interpolatedKeys = this.interpolate(source.getValues(), this._values, timeFrom, timeTo);
      SortedSet<Long> changedKeys = new TreeSet<>();
      changedKeys.addAll(deletedKeys);
      changedKeys.addAll(interpolatedKeys);
      if (changedKeys.size() == 1) {
         if (changedKeys.first() < 0L) {
            this.fireCurveChanged();
         } else {
            this.fireCurveChanged(changedKeys.first());
         }
      } else if (changedKeys.size() > 1) {
         if (changedKeys.first() < 0L) {
            this.fireCurveChanged();
         } else {
            this.fireCurveChanged(changedKeys);
         }
      } else if (timeFrom == timeTo) {
         this.fireCurveChanged(timeTo);
      }
   }

   @Override
   public void notifyCurveChanged(CurveModel source, long time) {
      this.notifyCurveChanged(source, time, time);
   }

   @Override
   public void notifyCurveChanged(CurveModel source) {
      this._values.clear();
      if (!source.getValues().isEmpty()) {
         Long previousTime = null;
         Double previousValue = null;

         for (Entry<Long, Double> entry : source.getValues().entrySet()) {
            Long time = entry.getKey();
            Double value = entry.getValue();
            this._values.put(time, value);
            if (previousTime != null) {
               this.interpolate(this._values, previousTime, previousValue, time, value);
            }

            previousTime = time;
            previousValue = value;
         }
      }

      this.fireCurveChanged();
   }

   private SortedSet<Long> delete(SortedMap<Long, Double> sourceValues, TreeMap<Long, Double> targetValues, long timeFrom, long timeTo) {
      Long lowerTimeFromKey = lowerKey(sourceValues, Long.valueOf(timeFrom));
      Long higherTimeToKey = higherKey(sourceValues, Long.valueOf(timeTo + 1L));
      SortedSet<Long> deletedKeys = new TreeSet<>();
      Long lowerBound = lowerTimeFromKey != null ? lowerTimeFromKey + 1L : timeFrom;
      Long upperBound = higherTimeToKey != null ? higherTimeToKey : timeTo + 1L;
      if (lowerBound <= upperBound) {
         SortedMap<Long, Double> subMap = targetValues.subMap(lowerBound, upperBound);
         deletedKeys.addAll(subMap.keySet());
         subMap.clear();
      }

      return deletedKeys;
   }

   private SortedSet<Long> interpolate(TreeMap<Long, Double> values, long startTime, double startValue, long endTime, double endValue) {
      SortedSet<Long> interpolatedKeys = new TreeSet<>();
      double k = (endValue - startValue) / (double)(endTime - startTime);

      for (long t = 1000L; t < endTime - startTime; t += 1000L) {
         long time = startTime + t;
         double value = startValue + (double)t * k;
         values.put(time, MathUtil.roundHalfToEven(value, 1));
         interpolatedKeys.add(Long.valueOf(time));
      }

      return interpolatedKeys;
   }

   private SortedSet<Long> interpolate(SortedMap<Long, Double> sourceValues, TreeMap<Long, Double> targetValues, long timeFrom, long timeTo) {
      SortedSet<Long> interpolatedKeys = new TreeSet<>();
      Long lowerTimeFromKey = lowerKey(sourceValues, Long.valueOf(timeFrom));
      Long higherTimeToKey = higherKey(sourceValues, Long.valueOf(timeTo + 1L));
      List<Long> keys = new ArrayList<>();
      if (lowerTimeFromKey != null) {
         keys.add(lowerTimeFromKey);
      }

      keys.addAll(sourceValues.subMap(timeFrom, timeTo + 1L).keySet());
      if (higherTimeToKey != null) {
         keys.add(higherTimeToKey);
      }

      if (keys.size() < 2) {
         return Collections.emptySortedSet();
      } else {
         for (int i = 1; i < keys.size(); i++) {
            Long time = keys.get(i);
            Long previousTime = keys.get(i - 1);
            interpolatedKeys.addAll(this.interpolate(targetValues, previousTime, sourceValues.get(previousTime), time, sourceValues.get(time)));
         }

         interpolatedKeys.addAll(keys);
         if (lowerTimeFromKey != null) {
            interpolatedKeys.remove(lowerTimeFromKey);
         }

         if (higherTimeToKey != null) {
            interpolatedKeys.remove(higherTimeToKey);
         }

         return interpolatedKeys;
      }
   }
}
