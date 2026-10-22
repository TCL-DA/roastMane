package org.apache.commons.math3.optimization.general;

@Deprecated
public interface Preconditioner {
   double[] precondition(double[] var1, double[] var2);
}
