package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;

public interface BaseUnivariateSolver<FUNC extends UnivariateFunction> {
   int getMaxEvaluations();

   int getEvaluations();

   double getAbsoluteAccuracy();

   double getRelativeAccuracy();

   double getFunctionValueAccuracy();

   double solve(int var1, FUNC var2, double var3, double var5) throws MathIllegalArgumentException, TooManyEvaluationsException;

   double solve(int var1, FUNC var2, double var3, double var5, double var7) throws MathIllegalArgumentException, TooManyEvaluationsException;

   double solve(int var1, FUNC var2, double var3);
}
