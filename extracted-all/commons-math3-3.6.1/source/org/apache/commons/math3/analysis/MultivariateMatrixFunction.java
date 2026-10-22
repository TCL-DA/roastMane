package org.apache.commons.math3.analysis;

public interface MultivariateMatrixFunction {
   double[][] value(double[] var1) throws IllegalArgumentException;
}
