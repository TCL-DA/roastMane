package org.apache.commons.math3.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;

@Deprecated
public interface BaseMultivariateSimpleBoundsOptimizer<FUNC extends MultivariateFunction> extends BaseMultivariateOptimizer<FUNC> {
   PointValuePair optimize(int var1, FUNC var2, GoalType var3, double[] var4, double[] var5, double[] var6);
}
