package org.apache.commons.math3.optimization.univariate;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optimization.BaseOptimizer;
import org.apache.commons.math3.optimization.GoalType;

@Deprecated
public interface BaseUnivariateOptimizer<FUNC extends UnivariateFunction> extends BaseOptimizer<UnivariatePointValuePair> {
   UnivariatePointValuePair optimize(int var1, FUNC var2, GoalType var3, double var4, double var6);

   UnivariatePointValuePair optimize(int var1, FUNC var2, GoalType var3, double var4, double var6, double var8);
}
