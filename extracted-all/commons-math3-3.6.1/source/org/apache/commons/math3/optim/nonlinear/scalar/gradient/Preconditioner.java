package org.apache.commons.math3.optim.nonlinear.scalar.gradient;

public interface Preconditioner {
   double[] precondition(double[] var1, double[] var2);
}
