package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.util.FastMath;

public class Cos implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction {
   @Override
   public double value(double x) {
      return FastMath.cos(x);
   }

   @Deprecated
   @Override
   public UnivariateFunction derivative() {
      return FunctionUtils.toDifferentiableUnivariateFunction(this).derivative();
   }

   @Override
   public DerivativeStructure value(DerivativeStructure t) {
      return t.cos();
   }
}
