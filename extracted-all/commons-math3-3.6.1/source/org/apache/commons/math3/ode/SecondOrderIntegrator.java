package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathIllegalStateException;

public interface SecondOrderIntegrator extends ODEIntegrator {
   void integrate(SecondOrderDifferentialEquations var1, double var2, double[] var4, double[] var5, double var6, double[] var8, double[] var9) throws MathIllegalStateException, MathIllegalArgumentException;
}
