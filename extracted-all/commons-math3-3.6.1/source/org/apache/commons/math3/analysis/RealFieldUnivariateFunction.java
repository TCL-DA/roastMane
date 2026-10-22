package org.apache.commons.math3.analysis;

import org.apache.commons.math3.RealFieldElement;

public interface RealFieldUnivariateFunction<T extends RealFieldElement<T>> {
   T value(T var1);
}
