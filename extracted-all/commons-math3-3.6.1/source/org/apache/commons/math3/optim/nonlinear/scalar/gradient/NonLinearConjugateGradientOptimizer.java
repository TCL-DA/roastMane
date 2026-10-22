package org.apache.commons.math3.optim.nonlinear.scalar.gradient;

import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.GradientMultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.LineSearch;

public class NonLinearConjugateGradientOptimizer extends GradientMultivariateOptimizer {
   private final NonLinearConjugateGradientOptimizer.Formula updateFormula;
   private final Preconditioner preconditioner;
   private final LineSearch line;

   public NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula updateFormula, ConvergenceChecker<PointValuePair> checker) {
      this(updateFormula, checker, 1.0E-8, 1.0E-8, 1.0E-8, new NonLinearConjugateGradientOptimizer.IdentityPreconditioner());
   }

   @Deprecated
   public NonLinearConjugateGradientOptimizer(
      NonLinearConjugateGradientOptimizer.Formula updateFormula, ConvergenceChecker<PointValuePair> checker, UnivariateSolver lineSearchSolver
   ) {
      this(updateFormula, checker, lineSearchSolver, new NonLinearConjugateGradientOptimizer.IdentityPreconditioner());
   }

   public NonLinearConjugateGradientOptimizer(
      NonLinearConjugateGradientOptimizer.Formula updateFormula,
      ConvergenceChecker<PointValuePair> checker,
      double relativeTolerance,
      double absoluteTolerance,
      double initialBracketingRange
   ) {
      this(
         updateFormula, checker, relativeTolerance, absoluteTolerance, initialBracketingRange, new NonLinearConjugateGradientOptimizer.IdentityPreconditioner()
      );
   }

   @Deprecated
   public NonLinearConjugateGradientOptimizer(
      NonLinearConjugateGradientOptimizer.Formula updateFormula,
      ConvergenceChecker<PointValuePair> checker,
      UnivariateSolver lineSearchSolver,
      Preconditioner preconditioner
   ) {
      this(
         updateFormula,
         checker,
         lineSearchSolver.getRelativeAccuracy(),
         lineSearchSolver.getAbsoluteAccuracy(),
         lineSearchSolver.getAbsoluteAccuracy(),
         preconditioner
      );
   }

   public NonLinearConjugateGradientOptimizer(
      NonLinearConjugateGradientOptimizer.Formula updateFormula,
      ConvergenceChecker<PointValuePair> checker,
      double relativeTolerance,
      double absoluteTolerance,
      double initialBracketingRange,
      Preconditioner preconditioner
   ) {
      super(checker);
      this.updateFormula = updateFormula;
      this.preconditioner = preconditioner;
      this.line = new LineSearch(this, relativeTolerance, absoluteTolerance, initialBracketingRange);
   }

   @Override
   public PointValuePair optimize(OptimizationData... optData) throws TooManyEvaluationsException {
      return super.optimize(optData);
   }

   protected PointValuePair doOptimize() {
      ConvergenceChecker<PointValuePair> checker = this.getConvergenceChecker();
      double[] point = this.getStartPoint();
      GoalType goal = this.getGoalType();
      int n = point.length;
      double[] r = this.computeObjectiveGradient(point);
      if (goal == GoalType.MINIMIZE) {
         for (int i = 0; i < n; i++) {
            r[i] = -r[i];
         }
      }

      double[] steepestDescent = this.preconditioner.precondition(point, r);
      double[] searchDirection = (double[])steepestDescent.clone();
      double delta = 0.0;

      for (int i = 0; i < n; i++) {
         delta += r[i] * searchDirection[i];
      }

      PointValuePair current = null;

      while (true) {
         this.incrementIterationCount();
         double objective = this.computeObjectiveValue(point);
         PointValuePair previous = current;
         current = new PointValuePair(point, objective);
         if (previous != null && checker.converged(this.getIterations(), previous, current)) {
            return current;
         }

         double step = this.line.search(point, searchDirection).getPoint();

         for (int i = 0; i < point.length; i++) {
            point[i] += step * searchDirection[i];
         }

         r = this.computeObjectiveGradient(point);
         if (goal == GoalType.MINIMIZE) {
            for (int i = 0; i < n; i++) {
               r[i] = -r[i];
            }
         }

         double deltaOld = delta;
         double[] newSteepestDescent = this.preconditioner.precondition(point, r);
         delta = 0.0;

         for (int i = 0; i < n; i++) {
            delta += r[i] * newSteepestDescent[i];
         }

         double beta;
         switch (this.updateFormula) {
            case FLETCHER_REEVES:
               beta = delta / deltaOld;
               break;
            case POLAK_RIBIERE:
               double deltaMid = 0.0;

               for (int i = 0; i < r.length; i++) {
                  deltaMid += r[i] * steepestDescent[i];
               }

               beta = (delta - deltaMid) / deltaOld;
               break;
            default:
               throw new MathInternalError();
         }

         steepestDescent = newSteepestDescent;
         if (this.getIterations() % n != 0 && !(beta < 0.0)) {
            for (int i = 0; i < n; i++) {
               searchDirection[i] = steepestDescent[i] + beta * searchDirection[i];
            }
         } else {
            searchDirection = (double[])newSteepestDescent.clone();
         }
      }
   }

   @Override
   protected void parseOptimizationData(OptimizationData... optData) {
      super.parseOptimizationData(optData);
      this.checkParameters();
   }

   private void checkParameters() {
      if (this.getLowerBound() != null || this.getUpperBound() != null) {
         throw new MathUnsupportedOperationException(LocalizedFormats.CONSTRAINT);
      }
   }

   @Deprecated
   public static class BracketingStep implements OptimizationData {
      private final double initialStep;

      public BracketingStep(double step) {
         this.initialStep = step;
      }

      public double getBracketingStep() {
         return this.initialStep;
      }
   }

   public static enum Formula {
      FLETCHER_REEVES,
      POLAK_RIBIERE;
   }

   public static class IdentityPreconditioner implements Preconditioner {
      @Override
      public double[] precondition(double[] variables, double[] r) {
         return (double[])r.clone();
      }
   }
}
