package org.apache.commons.math3.genetics;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;

public class NPointCrossover<T> implements CrossoverPolicy {
   private final int crossoverPoints;

   public NPointCrossover(int crossoverPoints) throws NotStrictlyPositiveException {
      if (crossoverPoints <= 0) {
         throw new NotStrictlyPositiveException(crossoverPoints);
      } else {
         this.crossoverPoints = crossoverPoints;
      }
   }

   public int getCrossoverPoints() {
      return this.crossoverPoints;
   }

   @Override
   public ChromosomePair crossover(Chromosome first, Chromosome second) throws DimensionMismatchException, MathIllegalArgumentException {
      if (first instanceof AbstractListChromosome && second instanceof AbstractListChromosome) {
         return this.mate((AbstractListChromosome<T>)first, (AbstractListChromosome<T>)second);
      } else {
         throw new MathIllegalArgumentException(LocalizedFormats.INVALID_FIXED_LENGTH_CHROMOSOME);
      }
   }

   private ChromosomePair mate(AbstractListChromosome<T> first, AbstractListChromosome<T> second) throws DimensionMismatchException, NumberIsTooLargeException {
      int length = first.getLength();
      if (length != second.getLength()) {
         throw new DimensionMismatchException(second.getLength(), length);
      } else if (this.crossoverPoints >= length) {
         throw new NumberIsTooLargeException(this.crossoverPoints, length, false);
      } else {
         List<T> parent1Rep = first.getRepresentation();
         List<T> parent2Rep = second.getRepresentation();
         List<T> child1Rep = new ArrayList<>(length);
         List<T> child2Rep = new ArrayList<>(length);
         RandomGenerator random = GeneticAlgorithm.getRandomGenerator();
         List<T> c1 = child1Rep;
         List<T> c2 = child2Rep;
         int remainingPoints = this.crossoverPoints;
         int lastIndex = 0;

         for (int i = 0; i < this.crossoverPoints; remainingPoints--) {
            int crossoverIndex = 1 + lastIndex + random.nextInt(length - lastIndex - remainingPoints);

            for (int j = lastIndex; j < crossoverIndex; j++) {
               c1.add(parent1Rep.get(j));
               c2.add(parent2Rep.get(j));
            }

            List<T> tmp = c1;
            c1 = c2;
            c2 = tmp;
            lastIndex = crossoverIndex;
            i++;
         }

         for (int j = lastIndex; j < length; j++) {
            c1.add(parent1Rep.get(j));
            c2.add(parent2Rep.get(j));
         }

         return new ChromosomePair(first.newFixedLengthChromosome(child1Rep), second.newFixedLengthChromosome(child2Rep));
      }
   }
}
