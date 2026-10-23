package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;

public class MovingAverageCurveModel extends FacadeCurveModel implements CurveModelListener {
   private final CurveModel _source;
   private final int _windowSize;
   private TreeMap<Long, Double> _values = new TreeMap<>();

   public MovingAverageCurveModel(CurveModel source, int windowSize) {
      super(source);
      this._source = source;
      this._windowSize = windowSize;
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
   public void notifyCurveChanged(CurveModel source, long time) {
      SortedMap<Long, Double> subMap = source.getValues().subMap(time - (long)this._windowSize * 1000L, time + 1000L);
      if (!subMap.isEmpty()) {
         DescriptiveStatistics stats = new DescriptiveStatistics(this._windowSize);

         for (Double value : subMap.values()) {
            stats.addValue(value);
         }

         this._values.put(time, stats.getMean());
      } else {
         this._values.remove(time);
      }

      this.fireCurveChanged(time);
   }

   @Override
   public void notifyCurveChanged(CurveModel source) {
      this._values.clear();
      if (!source.getValues().isEmpty()) {
         DescriptiveStatistics stats = new DescriptiveStatistics(this._windowSize);
         TreeMap<Long, Double> values = new TreeMap<>();

         for (Entry<Long, Double> entry : this._source.getValues().entrySet()) {
            stats.addValue(entry.getValue());
            values.put(entry.getKey(), stats.getMean());
         }

         this._values = values;
      }

      this.fireCurveChanged();
   }
}
