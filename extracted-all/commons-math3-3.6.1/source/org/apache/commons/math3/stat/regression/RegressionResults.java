package org.apache.commons.math3.stat.regression;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

public class RegressionResults implements Serializable {
   private static final int SSE_IDX = 0;
   private static final int SST_IDX = 1;
   private static final int RSQ_IDX = 2;
   private static final int MSE_IDX = 3;
   private static final int ADJRSQ_IDX = 4;
   private static final long serialVersionUID = 1L;
   private final double[] parameters;
   private final double[][] varCovData;
   private final boolean isSymmetricVCD;
   private final int rank;
   private final long nobs;
   private final boolean containsConstant;
   private final double[] globalFitInfo;

   private RegressionResults() {
      this.parameters = null;
      this.varCovData = (double[][])null;
      this.rank = -1;
      this.nobs = -1L;
      this.containsConstant = false;
      this.isSymmetricVCD = false;
      this.globalFitInfo = null;
   }

   public RegressionResults(
      double[] parameters,
      double[][] varcov,
      boolean isSymmetricCompressed,
      long nobs,
      int rank,
      double sumy,
      double sumysq,
      double sse,
      boolean containsConstant,
      boolean copyData
   ) {
      if (copyData) {
         this.parameters = MathArrays.copyOf(parameters);
         this.varCovData = new double[varcov.length][];

         for (int i = 0; i < varcov.length; i++) {
            this.varCovData[i] = MathArrays.copyOf(varcov[i]);
         }
      } else {
         this.parameters = parameters;
         this.varCovData = varcov;
      }

      this.isSymmetricVCD = isSymmetricCompressed;
      this.nobs = nobs;
      this.rank = rank;
      this.containsConstant = containsConstant;
      this.globalFitInfo = new double[5];
      Arrays.fill(this.globalFitInfo, Double.NaN);
      if (rank > 0) {
         this.globalFitInfo[1] = containsConstant ? sumysq - sumy * sumy / (double)nobs : sumysq;
      }

      this.globalFitInfo[0] = sse;
      this.globalFitInfo[3] = this.globalFitInfo[0] / (double)(nobs - (long)rank);
      this.globalFitInfo[2] = 1.0 - this.globalFitInfo[0] / this.globalFitInfo[1];
      if (!containsConstant) {
         this.globalFitInfo[4] = 1.0 - (1.0 - this.globalFitInfo[2]) * ((double)nobs / (double)(nobs - (long)rank));
      } else {
         this.globalFitInfo[4] = 1.0 - sse * ((double)nobs - 1.0) / (this.globalFitInfo[1] * (double)(nobs - (long)rank));
      }
   }

   public double getParameterEstimate(int index) throws OutOfRangeException {
      if (this.parameters == null) {
         return Double.NaN;
      } else if (index >= 0 && index < this.parameters.length) {
         return this.parameters[index];
      } else {
         throw new OutOfRangeException(index, 0, this.parameters.length - 1);
      }
   }

   public double[] getParameterEstimates() {
      return this.parameters == null ? null : MathArrays.copyOf(this.parameters);
   }

   public double getStdErrorOfEstimate(int index) throws OutOfRangeException {
      if (this.parameters == null) {
         return Double.NaN;
      } else if (index >= 0 && index < this.parameters.length) {
         double var = this.getVcvElement(index, index);
         return !Double.isNaN(var) && var > Double.MIN_VALUE ? FastMath.sqrt(var) : Double.NaN;
      } else {
         throw new OutOfRangeException(index, 0, this.parameters.length - 1);
      }
   }

   public double[] getStdErrorOfEstimates() {
      if (this.parameters == null) {
         return null;
      } else {
         double[] se = new double[this.parameters.length];

         for (int i = 0; i < this.parameters.length; i++) {
            double var = this.getVcvElement(i, i);
            if (!Double.isNaN(var) && var > Double.MIN_VALUE) {
               se[i] = FastMath.sqrt(var);
            } else {
               se[i] = Double.NaN;
            }
         }

         return se;
      }
   }

   public double getCovarianceOfParameters(int i, int j) throws OutOfRangeException {
      if (this.parameters == null) {
         return Double.NaN;
      } else if (i < 0 || i >= this.parameters.length) {
         throw new OutOfRangeException(i, 0, this.parameters.length - 1);
      } else if (j >= 0 && j < this.parameters.length) {
         return this.getVcvElement(i, j);
      } else {
         throw new OutOfRangeException(j, 0, this.parameters.length - 1);
      }
   }

   public int getNumberOfParameters() {
      return this.parameters == null ? -1 : this.parameters.length;
   }

   public long getN() {
      return this.nobs;
   }

   public double getTotalSumSquares() {
      return this.globalFitInfo[1];
   }

   public double getRegressionSumSquares() {
      return this.globalFitInfo[1] - this.globalFitInfo[0];
   }

   public double getErrorSumSquares() {
      return this.globalFitInfo[0];
   }

   public double getMeanSquareError() {
      return this.globalFitInfo[3];
   }

   public double getRSquared() {
      return this.globalFitInfo[2];
   }

   public double getAdjustedRSquared() {
      return this.globalFitInfo[4];
   }

   public boolean hasIntercept() {
      return this.containsConstant;
   }

   private double getVcvElement(int i, int j) {
      if (this.isSymmetricVCD) {
         if (this.varCovData.length > 1) {
            if (i == j) {
               return this.varCovData[i][i];
            } else {
               return i >= this.varCovData[j].length ? this.varCovData[i][j] : this.varCovData[j][i];
            }
         } else {
            return i > j ? this.varCovData[0][(i + 1) * i / 2 + j] : this.varCovData[0][(j + 1) * j / 2 + i];
         }
      } else {
         return this.varCovData[i][j];
      }
   }
}
