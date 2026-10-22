package com.cropster.util;

import java.text.DecimalFormat;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixFormat;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RorUtil {
   private static final Logger logger = LoggerFactory.getLogger(RorUtil.class);

   private RorUtil() {
   }

   static SortedMap<Long, Double> fillValueGaps(SortedMap<Long, Double> vals) {
      if (vals.isEmpty()) {
         return vals;
      } else {
         SortedMap<Long, Double> resultVals = new TreeMap<>();

         for (long i = vals.firstKey(); i <= vals.lastKey(); i += 1000L) {
            Double currentValue = vals.get(Long.valueOf(i));
            if (currentValue == null) {
               Double startPoint = vals.get(Long.valueOf(i - 1000L));
               if (startPoint != null) {
                  Entry<Long, Double> endPointEntry = vals.subMap(i, vals.lastKey() + 1000L)
                     .entrySet()
                     .stream()
                     .filter(e -> e.getValue() != null)
                     .findFirst()
                     .orElse(null);
                  if (endPointEntry != null && endPointEntry.getValue() != null) {
                     Double endPoint = endPointEntry.getValue();
                     long missingPoints = (endPointEntry.getKey() - i) / 1000L;
                     long j = i;

                     for (long k = 1L; j < endPointEntry.getKey(); k++) {
                        Double missingVal = startPoint + (endPoint - startPoint) / (double)(missingPoints + 1L) * (double)k;
                        resultVals.put(Long.valueOf(j), missingVal);
                        i = j;
                        j += 1000L;
                     }
                  }
               }
            } else {
               resultVals.put(Long.valueOf(i), currentValue);
            }
         }

         return resultVals;
      }
   }

   static NavigableMap<Long, Double> normalizeValues(SortedMap<Long, Double> vals) {
      return transformCurve(vals, e -> e.getKey() / 1000L * 1000L, Entry::getValue);
   }

   static SortedMap<Long, Double> movingAverage(SortedMap<Long, Double> curve, int movingAvgWindow) {
      if (movingAvgWindow != 1 && !curve.isEmpty()) {
         SortedMap<Long, Double> filledCurve = fillValueGaps(curve);
         DescriptiveStatistics movingAverage = new DescriptiveStatistics(movingAvgWindow);
         SortedMap<Long, Double> smoothedRor = new TreeMap<>();

         for (long i = curve.firstKey(); i <= curve.lastKey(); i += 1000L) {
            Double val = curve.get(Long.valueOf(i));
            if (val == null) {
               movingAverage.addValue(filledCurve.get(Long.valueOf(i)));
            } else {
               movingAverage.addValue(val);
               smoothedRor.put(Long.valueOf(i), Double.valueOf(movingAverage.getMean()));
            }
         }

         return smoothedRor;
      } else {
         return curve;
      }
   }

   private static TreeMap<Long, Double> transformCurve(
      SortedMap<Long, Double> curve, Function<Entry<Long, Double>, Long> keyMapper, Function<Entry<Long, Double>, Double> valueMapper
   ) {
      return curve.entrySet().stream().filter(e -> e.getValue() != null).collect(Collectors.toMap(keyMapper, valueMapper, (v1, v2) -> v1, TreeMap::new));
   }

   static RealMatrix createVandermondeMatrix(int windowLength, int degree) {
      RealMatrix vanderMatrix = MatrixUtils.createRealMatrix(windowLength, degree + 1);

      for (int i = 0; i < windowLength; i++) {
         vanderMatrix.addToEntry(i, 0, 1.0);
         vanderMatrix.addToEntry(i, 1, (double)(-windowLength + i + 1));
         vanderMatrix.addToEntry(i, 2, Math.pow((double)(-windowLength + i + 1), 2.0));
      }

      if (logger.isTraceEnabled()) {
         logger.trace("Vandermonde matrix created: " + matrixToString(vanderMatrix));
      }

      return vanderMatrix;
   }

   private static String matrixToString(RealMatrix matrix) {
      RealMatrixFormat matrixFormat = new RealMatrixFormat("{\n", "\n}", "  { ", " },", "\n", ", ", new DecimalFormat("###"));
      return matrixFormat.format(matrix)
         .replaceAll(" (\\d{1}[^\\d])", StringUtils.leftPad("$1", 5))
         .replaceAll(" (\\d{2}[^\\d])", StringUtils.leftPad("$1", 4));
   }
}
