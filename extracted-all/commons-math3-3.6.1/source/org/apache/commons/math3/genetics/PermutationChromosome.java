package org.apache.commons.math3.genetics;

import java.util.List;

public interface PermutationChromosome<T> {
   List<T> decode(List<T> var1);
}
