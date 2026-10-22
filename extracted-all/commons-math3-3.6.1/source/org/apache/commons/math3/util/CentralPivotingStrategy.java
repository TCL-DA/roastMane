package org.apache.commons.math3.util;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class CentralPivotingStrategy implements PivotingStrategyInterface, Serializable {
   private static final long serialVersionUID = 20140713L;

   @Override
   public int pivotIndex(double[] work, int begin, int end) throws MathIllegalArgumentException {
      MathArrays.verifyValues(work, begin, end - begin);
      return begin + (end - begin) / 2;
   }
}
