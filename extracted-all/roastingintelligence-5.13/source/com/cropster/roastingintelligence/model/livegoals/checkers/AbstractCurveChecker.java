package com.cropster.roastingintelligence.model.livegoals.checkers;

import com.cropster.roastingintelligence.model.livegoals.CheckResult;
import com.cropster.util.model.Unit;
import java.math.BigDecimal;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.CurveData;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.util.CollectionUtil;

public abstract class AbstractCurveChecker extends AbstractChecker {
   protected static final int MOVING_WINDOW_SIZE = 4;
   protected static final long RISE_RATE_MILLIS = 30000L;
   private final String curveName;

   protected AbstractCurveChecker(String name, String curveName) {
      super(name);
      this.curveName = curveName;
   }

   @Override
   public CheckResult evaluate(Goal goal, CurveData actualCurve, CurveData referenceCurve) {
      return actualCurve != null && actualCurve.getValues() != null && !actualCurve.getValues().isEmpty()
         ? this.evaluateCurve(goal, actualCurve, referenceCurve)
         : CheckResult.missing(goal);
   }

   protected abstract CheckResult evaluateCurve(Goal var1, CurveData var2, CurveData var3);

   @Override
   public boolean isTimeBound() {
      return false;
   }

   protected Measure getFirstValue(CurveData curve) {
      if (curve == null) {
         return null;
      } else {
         SortedMap<Long, Double> values = curve.getValues();
         return values != null && !values.isEmpty() ? new Measure(values.get(values.firstKey()), curve.getUnit()) : null;
      }
   }

   protected Measure getLastValue(CurveData curve) {
      if (curve == null) {
         return null;
      } else {
         SortedMap<Long, Double> values = curve.getValues();
         return values != null && !values.isEmpty() ? new Measure(values.get(values.lastKey()), curve.getUnit()) : null;
      }
   }

   protected Measure getValueAt(CurveData curve, long time) {
      return curve == null ? null : new Measure(this.getValueAt(curve.getValues(), time), curve.getUnit());
   }

   protected CurveData getCurveInUnit(CurveData curve, Unit unit) {
      Unit originalUnit = curve.getUnit();
      Unit finalUnit = unit == null ? originalUnit : unit;
      Curve convertedCurve = new Curve(finalUnit);
      curve.getValues().forEach((key, value) -> convertedCurve.put(key, this.convertValue(value, originalUnit, finalUnit)));
      return convertedCurve;
   }

   protected CurveData calcRoRCurve(CurveData curve, Unit targetUnit) {
      return this.calcRoRCurve(curve, 4, 30000L, targetUnit);
   }

   protected CurveData calcRoRCurve(CurveData curve, int windowSize, Unit targetUnit) {
      return this.calcRoRCurve(curve, windowSize, 30000L, targetUnit);
   }

   protected CurveData calcRoRCurve(CurveData curve, int windowSize, long rorIntervalMs, Unit targetUnit) {
      if (curve == null) {
         return null;
      } else {
         SortedMap<Long, Double> values = curve.getValues();
         if (values != null && !values.isEmpty()) {
            Unit unit = curve.getUnit();
            return !unit.isCompatible(Unit.CELSIUS) ? null : this.calcCurve(windowSize, rorIntervalMs, targetUnit, unit, values);
         } else {
            return null;
         }
      }
   }

   private Curve calcCurve(int windowSize, long rorIntervalMs, Unit targetUnit, Unit unit, SortedMap<Long, Double> values) {
      Unit tUnit = targetUnit == null ? unit : targetUnit;
      Curve rorCurve = new Curve(tUnit);
      DescriptiveStatistics stats = new DescriptiveStatistics(windowSize);

      for (Entry<Long, Double> element : this.convertValues(values, unit, tUnit).entrySet()) {
         long time = element.getKey();
         Double value = element.getValue();
         if (value != null && time >= rorIntervalMs) {
            long referenceTime = time - rorIntervalMs;
            Double valueAtReferenceTime = this.getValueAt(values, referenceTime);
            Double referenceValue = valueAtReferenceTime != null ? this.convertValue(valueAtReferenceTime, unit, tUnit) : null;
            if (referenceValue != null) {
               double riseRate = value - referenceValue;
               stats.addValue(riseRate);
               rorCurve.put(Long.valueOf(time), Double.valueOf(stats.getMean()));
            }
         }
      }

      return rorCurve;
   }

   public Curve getCurve(Roast roast) {
      return roast == null ? null : roast.getCurves().get(this.curveName);
   }

   public CurveModel getCurveModel(MeasurementModel measurementModel) {
      if (measurementModel == null) {
         return null;
      } else {
         Channel channel = measurementModel.getChannel(this.curveName);
         return channel == null ? null : measurementModel.getCurveModel(channel);
      }
   }

   protected Double convertValue(Double value, Unit originalUnit, Unit targetUnit) {
      return originalUnit == targetUnit ? value : targetUnit.from(BigDecimal.valueOf(value), originalUnit).doubleValue();
   }

   protected SortedMap<Long, Double> convertValues(SortedMap<Long, Double> values, Unit originalUnit, Unit targetUnit) {
      if (originalUnit == targetUnit) {
         return values;
      } else {
         SortedMap<Long, Double> convertedValues = new TreeMap<>();

         for (Entry<Long, Double> entry : values.entrySet()) {
            convertedValues.put(entry.getKey(), this.convertValue(entry.getValue(), originalUnit, targetUnit));
         }

         return convertedValues;
      }
   }

   protected Double getValueAt(SortedMap<Long, Double> values, long time) {
      return values == null ? null : CollectionUtil.getProximateValue(values, time, 500L);
   }

   protected Unit unitFromGoal(Goal goal) {
      if (goal == null) {
         return null;
      } else {
         Unit unit = goal.getLowerBound() != null ? goal.getLowerBound().getUnit() : null;
         return unit == null && goal.getUpperBound() != null ? goal.getUpperBound().getUnit() : null;
      }
   }
}
