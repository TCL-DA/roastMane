package org.apache.commons.math3.optimization.linear;

import java.util.Collection;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;

@Deprecated
public interface LinearOptimizer {
   void setMaxIterations(int var1);

   int getMaxIterations();

   int getIterations();

   PointValuePair optimize(LinearObjectiveFunction var1, Collection<LinearConstraint> var2, GoalType var3, boolean var4) throws MathIllegalStateException;
}
