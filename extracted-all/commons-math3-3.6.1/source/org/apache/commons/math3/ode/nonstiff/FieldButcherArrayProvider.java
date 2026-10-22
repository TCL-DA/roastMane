package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.RealFieldElement;

public interface FieldButcherArrayProvider<T extends RealFieldElement<T>> {
   T[] getC();

   T[][] getA();

   T[] getB();
}
