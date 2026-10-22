package org.apache.commons.math3.util;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class MedianOf3PivotingStrategy implements PivotingStrategyInterface, Serializable {
   private static final long serialVersionUID = 20140713L;

   @Override
   public int pivotIndex(double[] work, int begin, int end) throws MathIllegalArgumentException {
      MathArrays.verifyValues(work, begin, end - begin);
      int inclusiveEnd = end - 1;
      int middle = begin + (inclusiveEnd - begin) / 2;
      double wBegin = work[begin];
      double wMiddle = work[middle];
      double wEnd = work[inclusiveEnd];
      if (wBegin < wMiddle) {
         if (wMiddle < wEnd) {
            return middle;
         } else {
            return wBegin < wEnd ? inclusiveEnd : begin;
         }
      } else if (wBegin < wEnd) {
         return begin;
      } else {
         return wMiddle < wEnd ? inclusiveEnd : middle;
      }
   }
}
