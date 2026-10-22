package org.apache.commons.math3.stat.correlation;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Pair;

public class KendallsCorrelation {
   private final RealMatrix correlationMatrix;

   public KendallsCorrelation() {
      this.correlationMatrix = null;
   }

   public KendallsCorrelation(double[][] data) {
      this(MatrixUtils.createRealMatrix(data));
   }

   public KendallsCorrelation(RealMatrix matrix) {
      this.correlationMatrix = this.computeCorrelationMatrix(matrix);
   }

   public RealMatrix getCorrelationMatrix() {
      return this.correlationMatrix;
   }

   public RealMatrix computeCorrelationMatrix(RealMatrix matrix) {
      int nVars = matrix.getColumnDimension();
      RealMatrix outMatrix = new BlockRealMatrix(nVars, nVars);

      for (int i = 0; i < nVars; i++) {
         for (int j = 0; j < i; j++) {
            double corr = this.correlation(matrix.getColumn(i), matrix.getColumn(j));
            outMatrix.setEntry(i, j, corr);
            outMatrix.setEntry(j, i, corr);
         }

         outMatrix.setEntry(i, i, 1.0);
      }

      return outMatrix;
   }

   public RealMatrix computeCorrelationMatrix(double[][] matrix) {
      return this.computeCorrelationMatrix(new BlockRealMatrix(matrix));
   }

   public double correlation(double[] xArray, double[] yArray) throws DimensionMismatchException {
      if (xArray.length != yArray.length) {
         throw new DimensionMismatchException(xArray.length, yArray.length);
      } else {
         int n = xArray.length;
         long numPairs = sum((long)(n - 1));
         Pair<Double, Double>[] pairs = new Pair[n];

         for (int i = 0; i < n; i++) {
            pairs[i] = new Pair<>(xArray[i], yArray[i]);
         }

         Arrays.sort(pairs, new Comparator<Pair<Double, Double>>() {
            public int compare(Pair<Double, Double> pair1, Pair<Double, Double> pair2) {
               int compareFirst = pair1.getFirst().compareTo(pair2.getFirst());
               return compareFirst != 0 ? compareFirst : pair1.getSecond().compareTo(pair2.getSecond());
            }
         });
         long tiedXPairs = 0L;
         long tiedXYPairs = 0L;
         long consecutiveXTies = 1L;
         long consecutiveXYTies = 1L;
         Pair<Double, Double> prev = pairs[0];

         for (int i = 1; i < n; i++) {
            Pair<Double, Double> curr = pairs[i];
            if (curr.getFirst().equals(prev.getFirst())) {
               consecutiveXTies++;
               if (curr.getSecond().equals(prev.getSecond())) {
                  consecutiveXYTies++;
               } else {
                  tiedXYPairs += sum(consecutiveXYTies - 1L);
                  consecutiveXYTies = 1L;
               }
            } else {
               tiedXPairs += sum(consecutiveXTies - 1L);
               consecutiveXTies = 1L;
               tiedXYPairs += sum(consecutiveXYTies - 1L);
               consecutiveXYTies = 1L;
            }

            prev = curr;
         }

         tiedXPairs += sum(consecutiveXTies - 1L);
         tiedXYPairs += sum(consecutiveXYTies - 1L);
         long swaps = 0L;
         Pair<Double, Double>[] pairsDestination = new Pair[n];

         for (int segmentSize = 1; segmentSize < n; segmentSize <<= 1) {
            for (int offset = 0; offset < n; offset += 2 * segmentSize) {
               int i = offset;
               int iEnd = FastMath.min(offset + segmentSize, n);
               int j = iEnd;
               int jEnd = FastMath.min(iEnd + segmentSize, n);

               for (int copyLocation = offset; i < iEnd || j < jEnd; copyLocation++) {
                  if (i < iEnd) {
                     if (j < jEnd) {
                        if (pairs[i].getSecond().compareTo(pairs[j].getSecond()) <= 0) {
                           pairsDestination[copyLocation] = pairs[i];
                           i++;
                        } else {
                           pairsDestination[copyLocation] = pairs[j];
                           j++;
                           swaps += (long)(iEnd - i);
                        }
                     } else {
                        pairsDestination[copyLocation] = pairs[i];
                        i++;
                     }
                  } else {
                     pairsDestination[copyLocation] = pairs[j];
                     j++;
                  }
               }
            }

            Pair<Double, Double>[] pairsTemp = pairs;
            pairs = pairsDestination;
            pairsDestination = pairsTemp;
         }

         long tiedYPairs = 0L;
         long consecutiveYTies = 1L;
         prev = pairs[0];

         for (int i = 1; i < n; i++) {
            Pair<Double, Double> curr = pairs[i];
            if (curr.getSecond().equals(prev.getSecond())) {
               consecutiveYTies++;
            } else {
               tiedYPairs += sum(consecutiveYTies - 1L);
               consecutiveYTies = 1L;
            }

            prev = curr;
         }

         tiedYPairs += sum(consecutiveYTies - 1L);
         long concordantMinusDiscordant = numPairs - tiedXPairs - tiedYPairs + tiedXYPairs - 2L * swaps;
         double nonTiedPairsMultiplied = (double)(numPairs - tiedXPairs) * (double)(numPairs - tiedYPairs);
         return (double)concordantMinusDiscordant / FastMath.sqrt(nonTiedPairsMultiplied);
      }
   }

   private static long sum(long n) {
      return n * (n + 1L) / 2L;
   }
}
