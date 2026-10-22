package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public interface MultivariateRealDistribution {
   double density(double[] var1);

   void reseedRandomGenerator(long var1);

   int getDimension();

   double[] sample();

   double[][] sample(int var1) throws NotStrictlyPositiveException;
}
