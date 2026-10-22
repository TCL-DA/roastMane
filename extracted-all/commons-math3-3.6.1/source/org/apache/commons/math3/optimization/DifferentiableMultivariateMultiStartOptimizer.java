package org.apache.commons.math3.optimization;

import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.random.RandomVectorGenerator;

@Deprecated
public class DifferentiableMultivariateMultiStartOptimizer
   extends BaseMultivariateMultiStartOptimizer<DifferentiableMultivariateFunction>
   implements DifferentiableMultivariateOptimizer {
   public DifferentiableMultivariateMultiStartOptimizer(DifferentiableMultivariateOptimizer optimizer, int starts, RandomVectorGenerator generator) {
      super(optimizer, starts, generator);
   }
}
