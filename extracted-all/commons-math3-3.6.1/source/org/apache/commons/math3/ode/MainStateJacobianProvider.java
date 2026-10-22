package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

public interface MainStateJacobianProvider extends FirstOrderDifferentialEquations {
   void computeMainStateJacobian(double var1, double[] var3, double[] var4, double[][] var5) throws MaxCountExceededException, DimensionMismatchException;
}
