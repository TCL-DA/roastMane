package org.apache.commons.math3.transform;

import java.io.Serializable;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.ArithmeticUtils;

public class FastHadamardTransformer implements RealTransformer, Serializable {
   static final long serialVersionUID = 20120211L;

   @Override
   public double[] transform(double[] f, TransformType type) {
      return type == TransformType.FORWARD ? this.fht(f) : TransformUtils.scaleArray(this.fht(f), 1.0 / (double)f.length);
   }

   @Override
   public double[] transform(UnivariateFunction f, double min, double max, int n, TransformType type) {
      return this.transform(FunctionUtils.sample(f, min, max, n), type);
   }

   public int[] transform(int[] f) {
      return this.fht(f);
   }

   protected double[] fht(double[] x) throws MathIllegalArgumentException {
      int n = x.length;
      int halfN = n / 2;
      if (!ArithmeticUtils.isPowerOfTwo((long)n)) {
         throw new MathIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO, n);
      } else {
         double[] yPrevious = new double[n];
         double[] yCurrent = (double[])x.clone();

         for (int j = 1; j < n; j <<= 1) {
            double[] yTmp = yCurrent;
            yCurrent = yPrevious;
            yPrevious = yTmp;

            for (int i = 0; i < halfN; i++) {
               int twoI = 2 * i;
               yCurrent[i] = yPrevious[twoI] + yPrevious[twoI + 1];
            }

            for (int i = halfN; i < n; i++) {
               int twoI = 2 * i;
               yCurrent[i] = yPrevious[twoI - n] - yPrevious[twoI - n + 1];
            }
         }

         return yCurrent;
      }
   }

   protected int[] fht(int[] x) throws MathIllegalArgumentException {
      int n = x.length;
      int halfN = n / 2;
      if (!ArithmeticUtils.isPowerOfTwo((long)n)) {
         throw new MathIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO, n);
      } else {
         int[] yPrevious = new int[n];
         int[] yCurrent = (int[])x.clone();

         for (int j = 1; j < n; j <<= 1) {
            int[] yTmp = yCurrent;
            yCurrent = yPrevious;
            yPrevious = yTmp;

            for (int i = 0; i < halfN; i++) {
               int twoI = 2 * i;
               yCurrent[i] = yPrevious[twoI] + yPrevious[twoI + 1];
            }

            for (int i = halfN; i < n; i++) {
               int twoI = 2 * i;
               yCurrent[i] = yPrevious[twoI - n] - yPrevious[twoI - n + 1];
            }
         }

         return yCurrent;
      }
   }
}
