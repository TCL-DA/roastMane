package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.util.MathUtil;

public class RoundingCurveModel extends FacadeCurveModel {
   private final CurveModel _source;
   private final int _precision;

   public RoundingCurveModel(CurveModel source, int precision) {
      super(source);
      this._source = source;
      this._precision = precision;
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
      return MathUtil.roundHalfToEven(this._source.getValue(time), this._precision);
   }

   @Override
   public SortedMap<Long, Double> getValues() {
      TreeMap<Long, Double> roundedValues = new TreeMap<>();

      for (Entry<Long, Double> entry : this._source.getValues().entrySet()) {
         roundedValues.put(entry.getKey(), MathUtil.roundHalfToEven(entry.getValue(), this._precision));
      }

      return roundedValues;
   }
}
