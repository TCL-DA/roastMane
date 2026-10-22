package org.apache.commons.math3.dfp;

import org.apache.commons.math3.analysis.RealFieldUnivariateFunction;
import org.apache.commons.math3.analysis.solvers.AllowedSolution;
import org.apache.commons.math3.analysis.solvers.FieldBracketingNthOrderBrentSolver;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.MathUtils;

@Deprecated
public class BracketingNthOrderBrentSolverDFP extends FieldBracketingNthOrderBrentSolver<Dfp> {
   public BracketingNthOrderBrentSolverDFP(Dfp relativeAccuracy, Dfp absoluteAccuracy, Dfp functionValueAccuracy, int maximalOrder) throws NumberIsTooSmallException {
      super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy, maximalOrder);
   }

   public Dfp getAbsoluteAccuracy() {
      return (Dfp)super.getAbsoluteAccuracy();
   }

   public Dfp getRelativeAccuracy() {
      return (Dfp)super.getRelativeAccuracy();
   }

   public Dfp getFunctionValueAccuracy() {
      return (Dfp)super.getFunctionValueAccuracy();
   }

   public Dfp solve(int maxEval, UnivariateDfpFunction f, Dfp min, Dfp max, AllowedSolution allowedSolution) throws NullArgumentException, NoBracketingException {
      return this.solve(maxEval, f, min, max, min.add(max).divide(2), allowedSolution);
   }

   public Dfp solve(int maxEval, final UnivariateDfpFunction f, Dfp min, Dfp max, Dfp startValue, AllowedSolution allowedSolution) throws NullArgumentException, NoBracketingException {
      MathUtils.checkNotNull(f);
      RealFieldUnivariateFunction<Dfp> fieldF = new RealFieldUnivariateFunction<Dfp>() {
         public Dfp value(Dfp x) {
            return f.value(x);
         }
      };
      return this.solve(maxEval, fieldF, min, max, startValue, allowedSolution);
   }
}
