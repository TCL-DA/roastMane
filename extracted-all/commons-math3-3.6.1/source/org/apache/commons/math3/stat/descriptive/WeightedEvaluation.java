package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface WeightedEvaluation {
   double evaluate(double[] var1, double[] var2) throws MathIllegalArgumentException;

   double evaluate(double[] var1, double[] var2, int var3, int var4) throws MathIllegalArgumentException;
}
