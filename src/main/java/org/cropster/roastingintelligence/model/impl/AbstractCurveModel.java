package org.cropster.roastingintelligence.model.impl;

import java.util.Optional;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;

public abstract class AbstractCurveModel implements CurveModel {
   private final CopyOnWriteArrayList<CurveModelListener> _listeners = new CopyOnWriteArrayList<>();

   @Override
   public SortedMap<Long, Double> tailCurve(Long fromKey) {
      Double previousValue = null;
      TreeMap<Long, Double> subMap = new TreeMap<>();

      for (Entry<Long, Double> e : this.getValues().entrySet()) {
         if (e.getKey() >= fromKey) {
            subMap.put(e.getKey(), e.getValue());
         } else {
            previousValue = e.getValue();
         }
      }

      if (!subMap.containsKey(fromKey) && previousValue != null) {
         subMap.put(fromKey, previousValue);
      }

      return subMap;
   }

   @Override
   public void addListener(CurveModelListener listener) {
      this._listeners.add(listener);
   }

   @Override
   public void removeListener(CurveModelListener listener) {
      this._listeners.remove(listener);
   }

   @Override
   public void fireCurveChanged(long time) {
      for (CurveModelListener listener : this._listeners) {
         listener.notifyCurveChanged(this, time);
      }
   }

   @Override
   public void fireCurveChanged() {
      for (CurveModelListener listener : this._listeners) {
         listener.notifyCurveChanged(this);
      }
   }

   @Override
   public void fireCurveChanged(SortedSet<Long> times) {
      for (CurveModelListener listener : this._listeners) {
         listener.notifyCurveChanged(this, times);
      }
   }

   public static Long lowerKey(SortedMap<Long, Double> sourceValues, Long time) {
      return Optional.ofNullable(sourceValues.headMap(time)).map(headMap -> !headMap.isEmpty() ? headMap.lastKey() : null).orElse(null);
   }

   public static Long higherKey(SortedMap<Long, Double> sourceValues, Long time) {
      return Optional.ofNullable(sourceValues.tailMap(time + 1L)).map(tailMap -> !tailMap.isEmpty() ? tailMap.firstKey() : null).orElse(null);
   }

   public static void addLowerEntry(long time, SortedMap<Long, Double> sourceValues, SortedMap<Long, Double> targetValues) {
      SortedMap<Long, Double> headMap = sourceValues.headMap(time);
      if (!headMap.isEmpty()) {
         targetValues.putAll(sourceValues.tailMap(headMap.lastKey()));
      }
   }
}
