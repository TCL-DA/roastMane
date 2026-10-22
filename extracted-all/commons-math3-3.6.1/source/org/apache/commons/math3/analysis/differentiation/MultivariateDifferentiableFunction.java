package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface MultivariateDifferentiableFunction extends MultivariateFunction {
   DerivativeStructure value(DerivativeStructure[] var1) throws MathIllegalArgumentException;
}
