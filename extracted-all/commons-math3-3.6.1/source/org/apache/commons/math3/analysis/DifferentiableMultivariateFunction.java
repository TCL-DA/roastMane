package org.apache.commons.math3.analysis;

@Deprecated
public interface DifferentiableMultivariateFunction extends MultivariateFunction {
   MultivariateFunction partialDerivative(int var1);

   MultivariateVectorFunction gradient();
}
