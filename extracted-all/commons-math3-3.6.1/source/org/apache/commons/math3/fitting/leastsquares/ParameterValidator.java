package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealVector;

public interface ParameterValidator {
   RealVector validate(RealVector var1);
}
