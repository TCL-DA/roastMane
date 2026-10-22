package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

public interface ParameterJacobianProvider extends Parameterizable {
   void computeParameterJacobian(double var1, double[] var3, double[] var4, String var5, double[] var6) throws DimensionMismatchException, MaxCountExceededException, UnknownParameterException;
}
