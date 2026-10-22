package org.apache.commons.math3.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.random.RandomVectorGenerator;

@Deprecated
public class MultivariateMultiStartOptimizer extends BaseMultivariateMultiStartOptimizer<MultivariateFunction> implements MultivariateOptimizer {
   public MultivariateMultiStartOptimizer(MultivariateOptimizer optimizer, int starts, RandomVectorGenerator generator) {
      super(optimizer, starts, generator);
   }
}
