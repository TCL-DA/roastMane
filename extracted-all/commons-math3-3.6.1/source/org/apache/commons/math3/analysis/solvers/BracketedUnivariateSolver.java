package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.UnivariateFunction;

public interface BracketedUnivariateSolver<FUNC extends UnivariateFunction> extends BaseUnivariateSolver<FUNC> {
   double solve(int var1, FUNC var2, double var3, double var5, AllowedSolution var7);

   double solve(int var1, FUNC var2, double var3, double var5, double var7, AllowedSolution var9);
}
