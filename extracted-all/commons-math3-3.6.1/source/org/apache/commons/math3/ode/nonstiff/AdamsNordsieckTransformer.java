package org.apache.commons.math3.ode.nonstiff;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayFieldVector;
import org.apache.commons.math3.linear.FieldDecompositionSolver;
import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

public class AdamsNordsieckTransformer {
   private static final Map<Integer, AdamsNordsieckTransformer> CACHE = new HashMap<>();
   private final Array2DRowRealMatrix update;
   private final double[] c1;

   private AdamsNordsieckTransformer(int n) {
      int rows = n - 1;
      FieldMatrix<BigFraction> bigP = this.buildP(rows);
      FieldDecompositionSolver<BigFraction> pSolver = new FieldLUDecomposition<>(bigP).getSolver();
      BigFraction[] u = new BigFraction[rows];
      Arrays.fill(u, BigFraction.ONE);
      BigFraction[] bigC1 = pSolver.solve(new ArrayFieldVector<>(u, false)).toArray();
      BigFraction[][] shiftedP = bigP.getData();

      for (int i = shiftedP.length - 1; i > 0; i--) {
         shiftedP[i] = shiftedP[i - 1];
      }

      shiftedP[0] = new BigFraction[rows];
      Arrays.fill(shiftedP[0], BigFraction.ZERO);
      FieldMatrix<BigFraction> bigMSupdate = pSolver.solve(new Array2DRowFieldMatrix<>(shiftedP, false));
      this.update = MatrixUtils.bigFractionMatrixToRealMatrix(bigMSupdate);
      this.c1 = new double[rows];

      for (int i = 0; i < rows; i++) {
         this.c1[i] = bigC1[i].doubleValue();
      }
   }

   public static AdamsNordsieckTransformer getInstance(int nSteps) {
      synchronized (CACHE) {
         AdamsNordsieckTransformer t = CACHE.get(nSteps);
         if (t == null) {
            t = new AdamsNordsieckTransformer(nSteps);
            CACHE.put(nSteps, t);
         }

         return t;
      }
   }

   @Deprecated
   public int getNSteps() {
      return this.c1.length;
   }

   private FieldMatrix<BigFraction> buildP(int rows) {
      BigFraction[][] pData = new BigFraction[rows][rows];

      for (int i = 1; i <= pData.length; i++) {
         BigFraction[] pI = pData[i - 1];
         int factor = -i;
         int aj = factor;

         for (int j = 1; j <= pI.length; j++) {
            pI[j - 1] = new BigFraction(aj * (j + 1));
            aj *= factor;
         }
      }

      return new Array2DRowFieldMatrix<>(pData, false);
   }

   public Array2DRowRealMatrix initializeHighOrderDerivatives(double h, double[] t, double[][] y, double[][] yDot) {
      double[][] a = new double[this.c1.length + 1][this.c1.length + 1];
      double[][] b = new double[this.c1.length + 1][y[0].length];
      double[] y0 = y[0];
      double[] yDot0 = yDot[0];

      for (int i = 1; i < y.length; i++) {
         double di = t[i] - t[0];
         double ratio = di / h;
         double dikM1Ohk = 1.0 / h;
         double[] aI = a[2 * i - 2];
         double[] aDotI = 2 * i - 1 < a.length ? a[2 * i - 1] : null;

         for (int j = 0; j < aI.length; j++) {
            dikM1Ohk *= ratio;
            aI[j] = di * dikM1Ohk;
            if (aDotI != null) {
               aDotI[j] = (double)(j + 2) * dikM1Ohk;
            }
         }

         double[] yI = y[i];
         double[] yDotI = yDot[i];
         double[] bI = b[2 * i - 2];
         double[] bDotI = 2 * i - 1 < b.length ? b[2 * i - 1] : null;

         for (int jx = 0; jx < yI.length; jx++) {
            bI[jx] = yI[jx] - y0[jx] - di * yDot0[jx];
            if (bDotI != null) {
               bDotI[jx] = yDotI[jx] - yDot0[jx];
            }
         }
      }

      QRDecomposition decomposition = new QRDecomposition(new Array2DRowRealMatrix(a, false));
      RealMatrix x = decomposition.getSolver().solve(new Array2DRowRealMatrix(b, false));
      Array2DRowRealMatrix truncatedX = new Array2DRowRealMatrix(x.getRowDimension() - 1, x.getColumnDimension());

      for (int i = 0; i < truncatedX.getRowDimension(); i++) {
         for (int jxx = 0; jxx < truncatedX.getColumnDimension(); jxx++) {
            truncatedX.setEntry(i, jxx, x.getEntry(i, jxx));
         }
      }

      return truncatedX;
   }

   public Array2DRowRealMatrix updateHighOrderDerivativesPhase1(Array2DRowRealMatrix highOrder) {
      return this.update.multiply(highOrder);
   }

   public void updateHighOrderDerivativesPhase2(double[] start, double[] end, Array2DRowRealMatrix highOrder) {
      double[][] data = highOrder.getDataRef();

      for (int i = 0; i < data.length; i++) {
         double[] dataI = data[i];
         double c1I = this.c1[i];

         for (int j = 0; j < dataI.length; j++) {
            dataI[j] += c1I * (start[j] - end[j]);
         }
      }
   }
}
