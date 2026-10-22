package org.apache.commons.math3.analysis.integration;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;

public interface UnivariateIntegrator {
   double getRelativeAccuracy();

   double getAbsoluteAccuracy();

   int getMinimalIterationCount();

   int getMaximalIterationCount();

   double integrate(int var1, UnivariateFunction var2, double var3, double var5) throws TooManyEvaluationsException, MaxCountExceededException, MathIllegalArgumentException, NullArgumentException;

   int getEvaluations();

   int getIterations();
}
