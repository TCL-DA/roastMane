package org.apache.commons.math3.genetics;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class RandomKeyMutation implements MutationPolicy {
   @Override
   public Chromosome mutate(Chromosome original) throws MathIllegalArgumentException {
      if (!(original instanceof RandomKey)) {
         throw new MathIllegalArgumentException(LocalizedFormats.RANDOMKEY_MUTATION_WRONG_CLASS, original.getClass().getSimpleName());
      } else {
         RandomKey<?> originalRk = (RandomKey<?>)original;
         List<Double> repr = originalRk.getRepresentation();
         int rInd = GeneticAlgorithm.getRandomGenerator().nextInt(repr.size());
         List<Double> newRepr = new ArrayList<>(repr);
         newRepr.set(rInd, GeneticAlgorithm.getRandomGenerator().nextDouble());
         return originalRk.newFixedLengthChromosome(newRepr);
      }
   }
}
