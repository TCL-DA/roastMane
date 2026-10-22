package org.apache.commons.math3.optim;

import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.util.Incrementor;

public abstract class AbstractOptimizationProblem<PAIR> implements OptimizationProblem<PAIR> {
   private static final AbstractOptimizationProblem.MaxEvalCallback MAX_EVAL_CALLBACK = new AbstractOptimizationProblem.MaxEvalCallback();
   private static final AbstractOptimizationProblem.MaxIterCallback MAX_ITER_CALLBACK = new AbstractOptimizationProblem.MaxIterCallback();
   private final int maxEvaluations;
   private final int maxIterations;
   private final ConvergenceChecker<PAIR> checker;

   protected AbstractOptimizationProblem(int maxEvaluations, int maxIterations, ConvergenceChecker<PAIR> checker) {
      this.maxEvaluations = maxEvaluations;
      this.maxIterations = maxIterations;
      this.checker = checker;
   }

   @Override
   public Incrementor getEvaluationCounter() {
      return new Incrementor(this.maxEvaluations, MAX_EVAL_CALLBACK);
   }

   @Override
   public Incrementor getIterationCounter() {
      return new Incrementor(this.maxIterations, MAX_ITER_CALLBACK);
   }

   @Override
   public ConvergenceChecker<PAIR> getConvergenceChecker() {
      return this.checker;
   }

   private static class MaxEvalCallback implements Incrementor.MaxCountExceededCallback {
      private MaxEvalCallback() {
      }

      @Override
      public void trigger(int max) {
         throw new TooManyEvaluationsException(max);
      }
   }

   private static class MaxIterCallback implements Incrementor.MaxCountExceededCallback {
      private MaxIterCallback() {
      }

      @Override
      public void trigger(int max) {
         throw new TooManyIterationsException(max);
      }
   }
}
