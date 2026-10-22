package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface NumberTransformer {
   double transform(Object var1) throws MathIllegalArgumentException;
}
