package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class Inverse implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction {
   @Override
   public double value(double x) {
      return 1.0 / x;
   }

   @Deprecated
   @Override
   public UnivariateFunction derivative() {
      return FunctionUtils.toDifferentiableUnivariateFunction(this).derivative();
   }

   @Override
   public DerivativeStructure value(DerivativeStructure t) {
      return t.reciprocal();
   }
}
