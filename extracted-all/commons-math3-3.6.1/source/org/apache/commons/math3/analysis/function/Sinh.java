package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.util.FastMath;

public class Sinh implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction {
   @Override
   public double value(double x) {
      return FastMath.sinh(x);
   }

   @Deprecated
   public DifferentiableUnivariateFunction derivative() {
      return new Cosh();
   }

   @Override
   public DerivativeStructure value(DerivativeStructure t) {
      return t.sinh();
   }
}
