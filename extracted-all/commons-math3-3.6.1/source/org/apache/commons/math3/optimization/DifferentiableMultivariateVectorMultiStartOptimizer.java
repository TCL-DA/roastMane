package org.apache.commons.math3.optimization;

import org.apache.commons.math3.analysis.DifferentiableMultivariateVectorFunction;
import org.apache.commons.math3.random.RandomVectorGenerator;

@Deprecated
public class DifferentiableMultivariateVectorMultiStartOptimizer
   extends BaseMultivariateVectorMultiStartOptimizer<DifferentiableMultivariateVectorFunction>
   implements DifferentiableMultivariateVectorOptimizer {
   public DifferentiableMultivariateVectorMultiStartOptimizer(DifferentiableMultivariateVectorOptimizer optimizer, int starts, RandomVectorGenerator generator) {
      super(optimizer, starts, generator);
   }
}
