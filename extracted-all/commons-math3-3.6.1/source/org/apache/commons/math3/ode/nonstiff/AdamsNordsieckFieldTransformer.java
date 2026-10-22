package org.apache.commons.math3.ode.nonstiff;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.ArrayFieldVector;
import org.apache.commons.math3.linear.FieldDecompositionSolver;
import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.util.MathArrays;

public class AdamsNordsieckFieldTransformer<T extends RealFieldElement<T>> {
   private static final Map<Integer, Map<Field<? extends RealFieldElement<?>>, AdamsNordsieckFieldTransformer<? extends RealFieldElement<?>>>> CACHE = new HashMap<>();
   private final Field<T> field;
   private final Array2DRowFieldMatrix<T> update;
   private final T[] c1;

   private AdamsNordsieckFieldTransformer(Field<T> field, int n) {
      this.field = field;
      int rows = n - 1;
      FieldMatrix<T> bigP = this.buildP(rows);
      FieldDecompositionSolver<T> pSolver = new FieldLUDecomposition<>(bigP).getSolver();
      T[] u = MathArrays.buildArray(field, rows);
      Arrays.fill(u, field.getOne());
      this.c1 = pSolver.solve(new ArrayFieldVector<>(u, false)).toArray();
      T[][] shiftedP = bigP.getData();

      for (int i = shiftedP.length - 1; i > 0; i--) {
         shiftedP[i] = shiftedP[i - 1];
      }

      shiftedP[0] = MathArrays.buildArray(field, rows);
      Arrays.fill(shiftedP[0], field.getZero());
      this.update = new Array2DRowFieldMatrix<>(pSolver.solve(new Array2DRowFieldMatrix<>(shiftedP, false)).getData());
   }

   public static <T extends RealFieldElement<T>> AdamsNordsieckFieldTransformer<T> getInstance(Field<T> field, int nSteps) {
      synchronized (CACHE) {
         Map<Field<? extends RealFieldElement<?>>, AdamsNordsieckFieldTransformer<? extends RealFieldElement<?>>> map = CACHE.get(nSteps);
         if (map == null) {
            map = new HashMap<>();
            CACHE.put(nSteps, map);
         }

         AdamsNordsieckFieldTransformer t = map.get(field);
         if (t == null) {
            t = new AdamsNordsieckFieldTransformer<>(field, nSteps);
            map.put(field, t);
         }

         return t;
      }
   }

   private FieldMatrix<T> buildP(int rows) {
      T[][] pData = MathArrays.buildArray(this.field, rows, rows);

      for (int i = 1; i <= pData.length; i++) {
         T[] pI = pData[i - 1];
         int factor = -i;
         T aj = this.field.getZero().add((double)factor);

         for (int j = 1; j <= pI.length; j++) {
            pI[j - 1] = aj.multiply(j + 1);
            aj = aj.multiply(factor);
         }
      }

      return new Array2DRowFieldMatrix<>(pData, false);
   }

   public Array2DRowFieldMatrix<T> initializeHighOrderDerivatives(T h, T[] t, T[][] y, T[][] yDot) {
      T[][] a = MathArrays.buildArray(this.field, this.c1.length + 1, this.c1.length + 1);
      T[][] b = MathArrays.buildArray(this.field, this.c1.length + 1, y[0].length);
      T[] y0 = y[0];
      T[] yDot0 = yDot[0];

      for (int i = 1; i < y.length; i++) {
         T di = t[i].subtract(t[0]);
         T ratio = di.divide(h);
         T dikM1Ohk = h.reciprocal();
         T[] aI = a[2 * i - 2];
         T[] aDotI = 2 * i - 1 < a.length ? a[2 * i - 1] : null;

         for (int j = 0; j < aI.length; j++) {
            dikM1Ohk = dikM1Ohk.multiply(ratio);
            aI[j] = di.multiply(dikM1Ohk);
            if (aDotI != null) {
               aDotI[j] = dikM1Ohk.multiply(j + 2);
            }
         }

         T[] yI = y[i];
         T[] yDotI = yDot[i];
         T[] bI = b[2 * i - 2];
         T[] bDotI = 2 * i - 1 < b.length ? b[2 * i - 1] : null;

         for (int jx = 0; jx < yI.length; jx++) {
            bI[jx] = yI[jx].subtract(y0[jx]).subtract(di.multiply(yDot0[jx]));
            if (bDotI != null) {
               bDotI[jx] = yDotI[jx].subtract(yDot0[jx]);
            }
         }
      }

      FieldLUDecomposition<T> decomposition = new FieldLUDecomposition<>(new Array2DRowFieldMatrix<>(a, false));
      FieldMatrix<T> x = decomposition.getSolver().solve(new Array2DRowFieldMatrix<>(b, false));
      Array2DRowFieldMatrix<T> truncatedX = new Array2DRowFieldMatrix<>(this.field, x.getRowDimension() - 1, x.getColumnDimension());

      for (int i = 0; i < truncatedX.getRowDimension(); i++) {
         for (int jxx = 0; jxx < truncatedX.getColumnDimension(); jxx++) {
            truncatedX.setEntry(i, jxx, x.getEntry(i, jxx));
         }
      }

      return truncatedX;
   }

   public Array2DRowFieldMatrix<T> updateHighOrderDerivativesPhase1(Array2DRowFieldMatrix<T> highOrder) {
      return this.update.multiply(highOrder);
   }

   public void updateHighOrderDerivativesPhase2(T[] start, T[] end, Array2DRowFieldMatrix<T> highOrder) {
      T[][] data = highOrder.getDataRef();

      for (int i = 0; i < data.length; i++) {
         T[] dataI = data[i];
         T c1I = this.c1[i];

         for (int j = 0; j < dataI.length; j++) {
            dataI[j] = dataI[j].add(c1I.multiply(start[j].subtract(end[j])));
         }
      }
   }
}
