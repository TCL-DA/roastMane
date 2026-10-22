package org.apache.commons.math3.optimization;

import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;
import org.apache.commons.math3.random.RandomVectorGenerator;

@Deprecated
public class MultivariateDifferentiableVectorMultiStartOptimizer
   extends BaseMultivariateVectorMultiStartOptimizer<MultivariateDifferentiableVectorFunction>
   implements MultivariateDifferentiableVectorOptimizer {
   public MultivariateDifferentiableVectorMultiStartOptimizer(MultivariateDifferentiableVectorOptimizer optimizer, int starts, RandomVectorGenerator generator) {
      super(optimizer, starts, generator);
   }
}
