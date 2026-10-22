package org.apache.commons.math3.optimization;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomVectorGenerator;

@Deprecated
public class BaseMultivariateMultiStartOptimizer<FUNC extends MultivariateFunction> implements BaseMultivariateOptimizer<FUNC> {
   private final BaseMultivariateOptimizer<FUNC> optimizer;
   private int maxEvaluations;
   private int totalEvaluations;
   private int starts;
   private RandomVectorGenerator generator;
   private PointValuePair[] optima;

   protected BaseMultivariateMultiStartOptimizer(BaseMultivariateOptimizer<FUNC> optimizer, int starts, RandomVectorGenerator generator) {
      if (optimizer == null || generator == null) {
         throw new NullArgumentException();
      } else if (starts < 1) {
         throw new NotStrictlyPositiveException(starts);
      } else {
         this.optimizer = optimizer;
         this.starts = starts;
         this.generator = generator;
      }
   }

   public PointValuePair[] getOptima() {
      if (this.optima == null) {
         throw new MathIllegalStateException(LocalizedFormats.NO_OPTIMUM_COMPUTED_YET);
      } else {
         return (PointValuePair[])this.optima.clone();
      }
   }

   @Override
   public int getMaxEvaluations() {
      return this.maxEvaluations;
   }

   @Override
   public int getEvaluations() {
      return this.totalEvaluations;
   }

   @Override
   public ConvergenceChecker<PointValuePair> getConvergenceChecker() {
      return this.optimizer.getConvergenceChecker();
   }

   @Override
   public PointValuePair optimize(int maxEval, FUNC f, GoalType goal, double[] startPoint) {
      this.maxEvaluations = maxEval;
      RuntimeException lastException = null;
      this.optima = new PointValuePair[this.starts];
      this.totalEvaluations = 0;

      for (int i = 0; i < this.starts; i++) {
         try {
            this.optima[i] = this.optimizer.optimize(maxEval - this.totalEvaluations, f, goal, i == 0 ? startPoint : this.generator.nextVector());
         } catch (RuntimeException var8) {
            lastException = var8;
            this.optima[i] = null;
         }

         this.totalEvaluations = this.totalEvaluations + this.optimizer.getEvaluations();
      }

      this.sortPairs(goal);
      if (this.optima[0] == null) {
         throw lastException;
      } else {
         return this.optima[0];
      }
   }

   private void sortPairs(final GoalType goal) {
      Arrays.sort(this.optima, new Comparator<PointValuePair>() {
         public int compare(PointValuePair o1, PointValuePair o2) {
            if (o1 == null) {
               return o2 == null ? 0 : 1;
            } else if (o2 == null) {
               return -1;
            } else {
               double v1 = o1.getValue();
               double v2 = o2.getValue();
               return goal == GoalType.MINIMIZE ? Double.compare(v1, v2) : Double.compare(v2, v1);
            }
         }
      });
   }
}
