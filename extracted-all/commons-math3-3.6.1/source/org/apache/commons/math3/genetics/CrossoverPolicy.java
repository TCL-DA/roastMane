package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface CrossoverPolicy {
   ChromosomePair crossover(Chromosome var1, Chromosome var2) throws MathIllegalArgumentException;
}
