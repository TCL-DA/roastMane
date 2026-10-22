package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface SelectionPolicy {
   ChromosomePair select(Population var1) throws MathIllegalArgumentException;
}
