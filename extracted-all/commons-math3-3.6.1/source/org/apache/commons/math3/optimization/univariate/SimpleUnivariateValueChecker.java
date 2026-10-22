package org.apache.commons.math3.optimization.univariate;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.optimization.AbstractConvergenceChecker;
import org.apache.commons.math3.util.FastMath;

@Deprecated
public class SimpleUnivariateValueChecker extends AbstractConvergenceChecker<UnivariatePointValuePair> {
   private static final int ITERATION_CHECK_DISABLED = -1;
   private final int maxIterationCount;

   @Deprecated
   public SimpleUnivariateValueChecker() {
      this.maxIterationCount = -1;
   }

   public SimpleUnivariateValueChecker(double relativeThreshold, double absoluteThreshold) {
      super(relativeThreshold, absoluteThreshold);
      this.maxIterationCount = -1;
   }

   public SimpleUnivariateValueChecker(double relativeThreshold, double absoluteThreshold, int maxIter) {
      super(relativeThreshold, absoluteThreshold);
      if (maxIter <= 0) {
         throw new NotStrictlyPositiveException(maxIter);
      } else {
         this.maxIterationCount = maxIter;
      }
   }

   public boolean converged(int iteration, UnivariatePointValuePair previous, UnivariatePointValuePair current) {
      if (this.maxIterationCount != -1 && iteration >= this.maxIterationCount) {
         return true;
      } else {
         double p = previous.getValue();
         double c = current.getValue();
         double difference = FastMath.abs(p - c);
         double size = FastMath.max(FastMath.abs(p), FastMath.abs(c));
         return difference <= size * this.getRelativeThreshold() || difference <= this.getAbsoluteThreshold();
      }
   }
}
