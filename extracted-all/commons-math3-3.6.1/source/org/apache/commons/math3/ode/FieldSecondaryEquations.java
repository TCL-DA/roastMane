package org.apache.commons.math3.ode;

import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

public interface FieldSecondaryEquations<T extends RealFieldElement<T>> {
   int getDimension();

   void init(T var1, T[] var2, T[] var3, T var4);

   T[] computeDerivatives(T var1, T[] var2, T[] var3, T[] var4) throws MaxCountExceededException, DimensionMismatchException;
}
