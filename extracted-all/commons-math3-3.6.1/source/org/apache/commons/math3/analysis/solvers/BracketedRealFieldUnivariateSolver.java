package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.analysis.RealFieldUnivariateFunction;

public interface BracketedRealFieldUnivariateSolver<T extends RealFieldElement<T>> {
   int getMaxEvaluations();

   int getEvaluations();

   T getAbsoluteAccuracy();

   T getRelativeAccuracy();

   T getFunctionValueAccuracy();

   T solve(int var1, RealFieldUnivariateFunction<T> var2, T var3, T var4, AllowedSolution var5);

   T solve(int var1, RealFieldUnivariateFunction<T> var2, T var3, T var4, T var5, AllowedSolution var6);
}
