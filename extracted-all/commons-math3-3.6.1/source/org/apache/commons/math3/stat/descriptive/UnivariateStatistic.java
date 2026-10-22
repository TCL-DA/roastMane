package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.MathArrays;

public interface UnivariateStatistic extends MathArrays.Function {
   @Override
   double evaluate(double[] var1) throws MathIllegalArgumentException;

   @Override
   double evaluate(double[] var1, int var2, int var3) throws MathIllegalArgumentException;

   UnivariateStatistic copy();
}
