package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.NumberIsTooSmallException;

public class FixedGenerationCount implements StoppingCondition {
   private int numGenerations = 0;
   private final int maxGenerations;

   public FixedGenerationCount(int maxGenerations) throws NumberIsTooSmallException {
      if (maxGenerations <= 0) {
         throw new NumberIsTooSmallException(maxGenerations, 1, true);
      } else {
         this.maxGenerations = maxGenerations;
      }
   }

   @Override
   public boolean isSatisfied(Population population) {
      if (this.numGenerations < this.maxGenerations) {
         this.numGenerations++;
         return false;
      } else {
         return true;
      }
   }

   public int getNumGenerations() {
      return this.numGenerations;
   }
}
