package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface PivotingStrategyInterface {
   int pivotIndex(double[] var1, int var2, int var3) throws MathIllegalArgumentException;
}
