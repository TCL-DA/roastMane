package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.analysis.UnivariateVectorFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface UnivariateDifferentiableVectorFunction extends UnivariateVectorFunction {
   DerivativeStructure[] value(DerivativeStructure var1) throws MathIllegalArgumentException;
}
