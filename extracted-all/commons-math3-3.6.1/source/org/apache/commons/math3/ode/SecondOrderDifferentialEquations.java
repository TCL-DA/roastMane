package org.apache.commons.math3.ode;

public interface SecondOrderDifferentialEquations {
   int getDimension();

   void computeSecondDerivatives(double var1, double[] var3, double[] var4, double[] var5);
}
