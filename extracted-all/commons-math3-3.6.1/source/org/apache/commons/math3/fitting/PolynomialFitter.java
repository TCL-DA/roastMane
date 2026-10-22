package org.apache.commons.math3.fitting;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.optim.nonlinear.vector.MultivariateVectorOptimizer;

@Deprecated
public class PolynomialFitter extends CurveFitter<PolynomialFunction.Parametric> {
   public PolynomialFitter(MultivariateVectorOptimizer optimizer) {
      super(optimizer);
   }

   public double[] fit(int maxEval, double[] guess) {
      return this.fit(maxEval, new PolynomialFunction.Parametric(), guess);
   }

   public double[] fit(double[] guess) {
      return this.fit(new PolynomialFunction.Parametric(), guess);
   }
}
