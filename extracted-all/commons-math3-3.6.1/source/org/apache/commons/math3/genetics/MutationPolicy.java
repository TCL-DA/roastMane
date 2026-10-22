package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface MutationPolicy {
   Chromosome mutate(Chromosome var1) throws MathIllegalArgumentException;
}
