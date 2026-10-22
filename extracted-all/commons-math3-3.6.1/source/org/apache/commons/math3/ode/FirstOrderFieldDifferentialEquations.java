package org.apache.commons.math3.ode;

import org.apache.commons.math3.RealFieldElement;

public interface FirstOrderFieldDifferentialEquations<T extends RealFieldElement<T>> {
   int getDimension();

   void init(T var1, T[] var2, T var3);

   T[] computeDerivatives(T var1, T[] var2);
}
