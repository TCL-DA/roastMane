package org.apache.commons.math3.optimization;

import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableFunction;
import org.apache.commons.math3.random.RandomVectorGenerator;

@Deprecated
public class MultivariateDifferentiableMultiStartOptimizer
   extends BaseMultivariateMultiStartOptimizer<MultivariateDifferentiableFunction>
   implements MultivariateDifferentiableOptimizer {
   public MultivariateDifferentiableMultiStartOptimizer(MultivariateDifferentiableOptimizer optimizer, int starts, RandomVectorGenerator generator) {
      super(optimizer, starts, generator);
   }
}
