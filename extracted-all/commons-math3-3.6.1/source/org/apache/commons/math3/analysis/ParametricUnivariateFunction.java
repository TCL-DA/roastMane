package org.apache.commons.math3.analysis;

public interface ParametricUnivariateFunction {
   double value(double var1, double... var3);

   double[] gradient(double var1, double... var3);
}
