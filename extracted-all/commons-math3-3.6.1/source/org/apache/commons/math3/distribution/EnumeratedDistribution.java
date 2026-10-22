package org.apache.commons.math3.distribution;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.Pair;

public class EnumeratedDistribution<T> implements Serializable {
   private static final long serialVersionUID = 20123308L;
   protected final RandomGenerator random;
   private final List<T> singletons;
   private final double[] probabilities;
   private final double[] cumulativeProbabilities;

   public EnumeratedDistribution(List<Pair<T, Double>> pmf) throws NotPositiveException, MathArithmeticException, NotFiniteNumberException, NotANumberException {
      this(new Well19937c(), pmf);
   }

   public EnumeratedDistribution(RandomGenerator rng, List<Pair<T, Double>> pmf) throws NotPositiveException, MathArithmeticException, NotFiniteNumberException, NotANumberException {
      this.random = rng;
      this.singletons = new ArrayList<>(pmf.size());
      double[] probs = new double[pmf.size()];

      for (int i = 0; i < pmf.size(); i++) {
         Pair<T, Double> sample = pmf.get(i);
         this.singletons.add(sample.getKey());
         double p = sample.getValue();
         if (p < 0.0) {
            throw new NotPositiveException(sample.getValue());
         }

         if (Double.isInfinite(p)) {
            throw new NotFiniteNumberException(p);
         }

         if (Double.isNaN(p)) {
            throw new NotANumberException();
         }

         probs[i] = p;
      }

      this.probabilities = MathArrays.normalizeArray(probs, 1.0);
      this.cumulativeProbabilities = new double[this.probabilities.length];
      double sum = 0.0;

      for (int i = 0; i < this.probabilities.length; i++) {
         sum += this.probabilities[i];
         this.cumulativeProbabilities[i] = sum;
      }
   }

   public void reseedRandomGenerator(long seed) {
      this.random.setSeed(seed);
   }

   double probability(T x) {
      double probability = 0.0;

      for (int i = 0; i < this.probabilities.length; i++) {
         if (x == null && this.singletons.get(i) == null || x != null && x.equals(this.singletons.get(i))) {
            probability += this.probabilities[i];
         }
      }

      return probability;
   }

   public List<Pair<T, Double>> getPmf() {
      List<Pair<T, Double>> samples = new ArrayList<>(this.probabilities.length);

      for (int i = 0; i < this.probabilities.length; i++) {
         samples.add(new Pair<>(this.singletons.get(i), this.probabilities[i]));
      }

      return samples;
   }

   public T sample() {
      double randomValue = this.random.nextDouble();
      int index = Arrays.binarySearch(this.cumulativeProbabilities, randomValue);
      if (index < 0) {
         index = -index - 1;
      }

      return index >= 0 && index < this.probabilities.length && randomValue < this.cumulativeProbabilities[index]
         ? this.singletons.get(index)
         : this.singletons.get(this.singletons.size() - 1);
   }

   public Object[] sample(int sampleSize) throws NotStrictlyPositiveException {
      if (sampleSize <= 0) {
         throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, sampleSize);
      } else {
         Object[] out = new Object[sampleSize];

         for (int i = 0; i < sampleSize; i++) {
            out[i] = this.sample();
         }

         return out;
      }
   }

   public T[] sample(int sampleSize, T[] array) throws NotStrictlyPositiveException {
      if (sampleSize <= 0) {
         throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, sampleSize);
      } else if (array == null) {
         throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
      } else {
         T[] out;
         if (array.length < sampleSize) {
            T[] unchecked = (T[])((Object[])Array.newInstance(array.getClass().getComponentType(), sampleSize));
            out = unchecked;
         } else {
            out = array;
         }

         for (int i = 0; i < sampleSize; i++) {
            out[i] = this.sample();
         }

         return out;
      }
   }
}
