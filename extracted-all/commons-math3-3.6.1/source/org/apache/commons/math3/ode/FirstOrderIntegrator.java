package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;

public interface FirstOrderIntegrator extends ODEIntegrator {
   double integrate(FirstOrderDifferentialEquations var1, double var2, double[] var4, double var5, double[] var7) throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException;
}
