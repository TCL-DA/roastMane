package org.apache.commons.math3.optim;

import org.apache.commons.math3.util.Incrementor;

public interface OptimizationProblem<PAIR> {
   Incrementor getEvaluationCounter();

   Incrementor getIterationCounter();

   ConvergenceChecker<PAIR> getConvergenceChecker();
}
