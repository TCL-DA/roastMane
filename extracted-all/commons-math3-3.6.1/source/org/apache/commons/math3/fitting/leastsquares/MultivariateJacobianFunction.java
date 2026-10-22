package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

public interface MultivariateJacobianFunction {
   Pair<RealVector, RealMatrix> value(RealVector var1);
}
