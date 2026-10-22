package com.cropster.util;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RorPolyFitCalculator {
   private static final int DEGREE = 2;
   private static final double CHECK_WINDOW_COUNT_RATIO = 0.7;
   private static final int CHECK_WINDOW_MAX_GAP = 10;
   private static final Logger logger = LoggerFactory.getLogger(RorPolyFitCalculator.class);
   private final int windowLength;
   private final RealMatrix matrix;

   public RorPolyFitCalculator(int windowLength) {
      if (windowLength < 3) {
         throw new IllegalArgumentException("windowLength must be >= 3");
      } else {
         this.windowLength = windowLength;
         RealMatrix vandermondeMatrix = RorUtil.createVandermondeMatrix(windowLength, 2);
         RealMatrix transposedMatrix = vandermondeMatrix.transpose();
         this.matrix = MatrixUtils.inverse(transposedMatrix.multiply(vandermondeMatrix)).multiply(transposedMatrix);
      }
   }

   private SortedMap<Long, Double> differentiate(SortedMap<Long, Double> curve, int step) {
      SortedMap<Long, Double> result = new TreeMap<>();

      for (Long time : curve.keySet()) {
         if (time - curve.firstKey() >= (long)(step * 1000)) {
            Double currentValue = curve.get(time);
            Double diffValue = curve.get(Long.valueOf(time - (long)(step * 1000)));
            if (currentValue != null && diffValue != null) {
               result.put(time, Double.valueOf(currentValue - diffValue));
            }
         }
      }

      return result;
   }

   private Double calcRorValue(NavigableMap<Long, Double> curve, Supplier<SortedMap<Long, Double>> curveFiller, long time, int interval, int lag, int offset) {
      if (!this.canCalculateRor(curve, lag, time)) {
         return null;
      } else {
         SortedMap<Long, Double> filledCurve = curveFiller.get();
         if (filledCurve == null) {
            return null;
         } else {
            SortedMap<Long, Double> diffCurve = this.differentiate(filledCurve, lag);
            SortedMap<Long, Double> windowCurve = diffCurve.subMap(time - (long)(this.windowLength - 1) * 1000L, time + 1000L);
            if (windowCurve.size() < this.windowLength) {
               Double diffValue = windowCurve.get(Long.valueOf(time));
               return diffValue == null ? null : diffValue * (double)interval / (double)lag;
            } else {
               double[] window = windowCurve.values().stream().mapToDouble(e -> e).toArray();
               return this.polyFit(window, offset) * (double)interval / (double)lag;
            }
         }
      }
   }

   private boolean canCalculateRor(NavigableMap<Long, Double> curve, int lag, long time) {
      int checkWindowLength = this.windowLength + lag;
      long checkWindowStart = time - (long)(checkWindowLength - 1) * 1000L;
      Collection<Long> checkWindow = curve.subMap(checkWindowStart, time + 1000L)
         .entrySet()
         .stream()
         .filter(e -> e.getValue() != null)
         .map(Entry::getKey)
         .collect(Collectors.toCollection(LinkedHashSet::new));
      long checkWindowSize = (long)checkWindow.size();
      if ((double)checkWindowSize / (double)checkWindowLength < 0.7) {
         Long previousTime = Math.max(0L, checkWindowStart);
         checkWindow.add(time);

         for (Long currentTime : checkWindow) {
            long timeDelta = currentTime - previousTime;
            if (timeDelta >= 10000L) {
               return false;
            }

            previousTime = currentTime;
         }
      }

      return true;
   }

   private double polyFit(double[] window, int step) {
      double[] coeff = this.matrix.operate(window);
      PolynomialFunction polynomial = new PolynomialFunction(coeff);
      return polynomial.value((double)step);
   }

   public int getWindowLength() {
      return this.windowLength;
   }

   public Double calcRorAtTime(SortedMap<Long, Double> curve, long time, int lag, int offset) {
      NavigableMap<Long, Double> normalizedCurve = RorUtil.normalizeValues(curve);
      Supplier<SortedMap<Long, Double>> curveFiller = () -> {
         Long startKey = normalizedCurve.floorKey(time - (long)(this.windowLength + lag) * 1000L);
         startKey = startKey == null ? 0L : startKey;
         Long endKey = normalizedCurve.ceilingKey(time);
         if (endKey == null) {
            return null;
         } else {
            SortedMap<Long, Double> windowCurve = normalizedCurve.subMap(startKey, endKey + 1000L);
            return RorUtil.fillValueGaps(windowCurve);
         }
      };
      return this.calcRorValue(normalizedCurve, curveFiller, time, 1, lag, offset);
   }

   public SortedMap<Long, Double> calcRor(SortedMap<Long, Double> curve, int interval, int smoothingWindow, int lag, int offset) {
      if (interval <= 0) {
         throw new IllegalArgumentException("interval must be > 0");
      } else if (smoothingWindow <= 0) {
         throw new IllegalArgumentException("smoothingWindow must be > 0");
      } else if (lag <= 0) {
         throw new IllegalArgumentException("lag must be > 0");
      } else {
         NavigableMap<Long, Double> normalizedCurve = RorUtil.normalizeValues(curve);
         SortedMap<Long, Double> filledCurve = RorUtil.fillValueGaps(normalizedCurve);
         SortedMap<Long, Double> rorCurve = new TreeMap<>();

         for (Long time : normalizedCurve.keySet()) {
            Double rorValue = this.calcRorValue(normalizedCurve, () -> filledCurve, time, interval, lag, offset);
            if (rorValue != null) {
               rorCurve.put(time, rorValue);
            }
         }

         return RorUtil.movingAverage(rorCurve, smoothingWindow);
      }
   }

   public SortedMap<Long, Double> calcRor(SortedMap<Long, Double> curve, int lag, int offset) {
      return this.calcRor(curve, 1, 1, lag, offset);
   }
}
