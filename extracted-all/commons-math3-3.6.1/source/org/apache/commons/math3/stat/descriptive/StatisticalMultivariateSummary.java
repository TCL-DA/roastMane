package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.linear.RealMatrix;

public interface StatisticalMultivariateSummary {
   int getDimension();

   double[] getMean();

   RealMatrix getCovariance();

   double[] getStandardDeviation();

   double[] getMax();

   double[] getMin();

   long getN();

   double[] getGeometricMean();

   double[] getSum();

   double[] getSumSq();

   double[] getSumLog();
}
