package org.apache.commons.math3.genetics;

public abstract class Chromosome implements Comparable<Chromosome>, Fitness {
   private static final double NO_FITNESS = Double.NEGATIVE_INFINITY;
   private double fitness = Double.NEGATIVE_INFINITY;

   public double getFitness() {
      if (this.fitness == Double.NEGATIVE_INFINITY) {
         this.fitness = this.fitness();
      }

      return this.fitness;
   }

   public int compareTo(Chromosome another) {
      return Double.compare(this.getFitness(), another.getFitness());
   }

   protected boolean isSame(Chromosome another) {
      return false;
   }

   protected Chromosome findSameChromosome(Population population) {
      for (Chromosome anotherChr : population) {
         if (this.isSame(anotherChr)) {
            return anotherChr;
         }
      }

      return null;
   }

   public void searchForFitnessUpdate(Population population) {
      Chromosome sameChromosome = this.findSameChromosome(population);
      if (sameChromosome != null) {
         this.fitness = sameChromosome.getFitness();
      }
   }
}
