package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.util.MathUtil;

public class ConverterCurveModel extends FacadeCurveModel implements CurveModelListener {
   private final CurveModel _source;
   private Unit _unit;
   private double _offset;
   private TreeMap<Long, Double> _values = new TreeMap<>();

   public ConverterCurveModel(CurveModel source, Unit unit, double offset) {
      super(source);
      this._source = source;
      this._unit = unit;
      this._offset = offset;
      this._source.addListener(this);
      this.notifyCurveChanged(this._source);
   }

   public ConverterCurveModel(CurveModel source, Unit unit) {
      this(source, unit, 0.0);
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
   public synchronized Date getZeroPoint() {
      return this._source.getZeroPoint();
   }

   @Override
   public Unit getUnit() {
      return this._unit;
   }

   public synchronized void setUnit(Unit unit) {
      this._unit = unit;
   }

   public double getOffset() {
      return this._offset;
   }

   public synchronized void setOffset(double offset) {
      this._offset = offset;
   }

   @Override
   public synchronized Double getValue(long time) {
      return this._values.get(time);
   }

   @Override
   public synchronized SortedMap<Long, Double> getValues() {
      return Collections.unmodifiableSortedMap(this._values);
   }

   private Double convertToUnit(Double value) {
      if (value == null) {
         return null;
      } else {
         double convertedValue = value;
         if (!this._unit.equals(this._source.getUnit())) {
            convertedValue = this._unit.from(BigDecimal.valueOf(convertedValue), this._source.getUnit()).doubleValue();
         }

         if (this._offset != 0.0) {
            convertedValue = Double.valueOf(convertedValue + this._offset);
         }

         convertedValue = MathUtil.roundHalfToEven(convertedValue, 1);
         return convertedValue;
      }
   }

   @Override
   public synchronized void notifyCurveChanged(CurveModel source) {
      TreeMap<Long, Double> values = new TreeMap<>();

      for (Entry<Long, Double> entry : this._source.getValues().entrySet()) {
         values.put(entry.getKey(), this.convertToUnit(entry.getValue()));
      }

      this._values = values;
      this.fireCurveChanged();
   }

   @Override
   public synchronized void notifyCurveChanged(CurveModel source, long time) {
      Double value = source.getValue(time);
      if (value != null) {
         this._values.put(time, this.convertToUnit(this._source.getValue(time)));
         this.fireCurveChanged(time);
      } else if (this._values.remove(time) != null) {
         this.fireCurveChanged(time);
      }
   }
}
