package org.apache.commons.math3.genetics;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class BinaryMutation implements MutationPolicy {
   @Override
   public Chromosome mutate(Chromosome original) throws MathIllegalArgumentException {
      if (!(original instanceof BinaryChromosome)) {
         throw new MathIllegalArgumentException(LocalizedFormats.INVALID_BINARY_CHROMOSOME);
      } else {
         BinaryChromosome origChrom = (BinaryChromosome)original;
         List<Integer> newRepr = new ArrayList<>(origChrom.getRepresentation());
         int geneIndex = GeneticAlgorithm.getRandomGenerator().nextInt(origChrom.getLength());
         newRepr.set(geneIndex, origChrom.getRepresentation().get(geneIndex) == 0 ? 1 : 0);
         Chromosome newChrom = origChrom.newFixedLengthChromosome(newRepr);
         return newChrom;
      }
   }
}
