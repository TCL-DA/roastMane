package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.analysis.UnivariateMatrixFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface UnivariateDifferentiableMatrixFunction extends UnivariateMatrixFunction {
   DerivativeStructure[][] value(DerivativeStructure var1) throws MathIllegalArgumentException;
}
