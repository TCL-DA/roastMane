package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.NumberIsTooLargeException;

public interface Population extends Iterable<Chromosome> {
   int getPopulationSize();

   int getPopulationLimit();

   Population nextGeneration();

   void addChromosome(Chromosome var1) throws NumberIsTooLargeException;

   Chromosome getFittestChromosome();
}
