package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;

public interface RealDistribution {
   double probability(double var1);

   double density(double var1);

   double cumulativeProbability(double var1);

   @Deprecated
   double cumulativeProbability(double var1, double var3) throws NumberIsTooLargeException;

   double inverseCumulativeProbability(double var1) throws OutOfRangeException;

   double getNumericalMean();

   double getNumericalVariance();

   double getSupportLowerBound();

   double getSupportUpperBound();

   @Deprecated
   boolean isSupportLowerBoundInclusive();

   @Deprecated
   boolean isSupportUpperBoundInclusive();

   boolean isSupportConnected();

   void reseedRandomGenerator(long var1);

   double sample();

   double[] sample(int var1);
}
