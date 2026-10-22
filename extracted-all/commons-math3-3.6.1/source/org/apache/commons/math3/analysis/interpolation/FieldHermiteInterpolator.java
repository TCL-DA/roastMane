package org.apache.commons.math3.analysis.interpolation;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class FieldHermiteInterpolator<T extends FieldElement<T>> {
   private final List<T> abscissae = new ArrayList<>();
   private final List<T[]> topDiagonal = new ArrayList();
   private final List<T[]> bottomDiagonal = new ArrayList();

   public void addSamplePoint(T x, T[]... value) throws ZeroException, MathArithmeticException, DimensionMismatchException, NullArgumentException {
      MathUtils.checkNotNull(x);
      T factorial = x.getField().getOne();

      for (int i = 0; i < value.length; i++) {
         T[] y = (T[])((FieldElement[])value[i].clone());
         if (i > 1) {
            factorial = factorial.multiply(i);
            T inv = factorial.reciprocal();

            for (int j = 0; j < y.length; j++) {
               y[j] = y[j].multiply(inv);
            }
         }

         int n = this.abscissae.size();
         this.bottomDiagonal.add(n - i, y);
         T[] bottom0 = y;

         for (int j = i; j < n; j++) {
            T[] bottom1 = (T[])((FieldElement[])this.bottomDiagonal.get(n - (j + 1)));
            if (x.equals(this.abscissae.get(n - (j + 1)))) {
               throw new ZeroException(LocalizedFormats.DUPLICATED_ABSCISSA_DIVISION_BY_ZERO, x);
            }

            T inv = x.subtract(this.abscissae.get(n - (j + 1))).reciprocal();

            for (int k = 0; k < y.length; k++) {
               bottom1[k] = inv.multiply(bottom0[k].subtract(bottom1[k]));
            }

            bottom0 = bottom1;
         }

         this.topDiagonal.add(bottom0.clone());
         this.abscissae.add(x);
      }
   }

   public T[] value(T x) throws NoDataException, NullArgumentException {
      MathUtils.checkNotNull(x);
      if (this.abscissae.isEmpty()) {
         throw new NoDataException(LocalizedFormats.EMPTY_INTERPOLATION_SAMPLE);
      } else {
         T[] value = MathArrays.buildArray(x.getField(), ((FieldElement[])this.topDiagonal.get(0)).length);
         T valueCoeff = x.getField().getOne();

         for (int i = 0; i < this.topDiagonal.size(); i++) {
            T[] dividedDifference = (T[])((FieldElement[])this.topDiagonal.get(i));

            for (int k = 0; k < value.length; k++) {
               value[k] = value[k].add(dividedDifference[k].multiply(valueCoeff));
            }

            T deltaX = x.subtract(this.abscissae.get(i));
            valueCoeff = valueCoeff.multiply(deltaX);
         }

         return value;
      }
   }

   public T[][] derivatives(T x, int order) throws NoDataException, NullArgumentException {
      MathUtils.checkNotNull(x);
      if (this.abscissae.isEmpty()) {
         throw new NoDataException(LocalizedFormats.EMPTY_INTERPOLATION_SAMPLE);
      } else {
         T zero = x.getField().getZero();
         T one = x.getField().getOne();
         T[] tj = MathArrays.buildArray(x.getField(), order + 1);
         tj[0] = zero;

         for (int i = 0; i < order; i++) {
            tj[i + 1] = tj[i].add(one);
         }

         T[][] derivatives = MathArrays.buildArray(x.getField(), order + 1, ((FieldElement[])this.topDiagonal.get(0)).length);
         T[] valueCoeff = MathArrays.buildArray(x.getField(), order + 1);
         valueCoeff[0] = x.getField().getOne();

         for (int i = 0; i < this.topDiagonal.size(); i++) {
            T[] dividedDifference = (T[])((FieldElement[])this.topDiagonal.get(i));
            T deltaX = x.subtract(this.abscissae.get(i));

            for (int j = order; j >= 0; j--) {
               for (int k = 0; k < derivatives[j].length; k++) {
                  derivatives[j][k] = derivatives[j][k].add(dividedDifference[k].multiply(valueCoeff[j]));
               }

               valueCoeff[j] = valueCoeff[j].multiply(deltaX);
               if (j > 0) {
                  valueCoeff[j] = valueCoeff[j].add(tj[j].multiply(valueCoeff[j - 1]));
               }
            }
         }

         return derivatives;
      }
   }
}
