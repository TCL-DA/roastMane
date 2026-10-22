package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

public interface FirstOrderDifferentialEquations {
   int getDimension();

   void computeDerivatives(double var1, double[] var3, double[] var4) throws MaxCountExceededException, DimensionMismatchException;
}
