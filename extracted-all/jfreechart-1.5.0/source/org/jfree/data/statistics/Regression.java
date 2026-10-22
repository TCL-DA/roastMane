package org.jfree.data.statistics;

import org.jfree.chart.util.Args;
import org.jfree.data.xy.XYDataset;

public abstract class Regression {
   public static double[] getOLSRegression(double[][] data) {
      int n = data.length;
      if (n < 2) {
         throw new IllegalArgumentException("Not enough data.");
      } else {
         double sumX = 0.0;
         double sumY = 0.0;
         double sumXX = 0.0;
         double sumXY = 0.0;

         for (int i = 0; i < n; i++) {
            double x = data[i][0];
            double y = data[i][1];
            sumX += x;
            sumY += y;
            double xx = x * x;
            sumXX += xx;
            double xy = x * y;
            sumXY += xy;
         }

         double sxx = sumXX - sumX * sumX / (double)n;
         double sxy = sumXY - sumX * sumY / (double)n;
         double xbar = sumX / (double)n;
         double ybar = sumY / (double)n;
         double[] result = new double[]{0.0, sxy / sxx};
         result[0] = ybar - result[1] * xbar;
         return result;
      }
   }

   public static double[] getOLSRegression(XYDataset data, int series) {
      int n = data.getItemCount(series);
      if (n < 2) {
         throw new IllegalArgumentException("Not enough data.");
      } else {
         double sumX = 0.0;
         double sumY = 0.0;
         double sumXX = 0.0;
         double sumXY = 0.0;

         for (int i = 0; i < n; i++) {
            double x = data.getXValue(series, i);
            double y = data.getYValue(series, i);
            sumX += x;
            sumY += y;
            double xx = x * x;
            sumXX += xx;
            double xy = x * y;
            sumXY += xy;
         }

         double sxx = sumXX - sumX * sumX / (double)n;
         double sxy = sumXY - sumX * sumY / (double)n;
         double xbar = sumX / (double)n;
         double ybar = sumY / (double)n;
         double[] result = new double[]{0.0, sxy / sxx};
         result[0] = ybar - result[1] * xbar;
         return result;
      }
   }

   public static double[] getPowerRegression(double[][] data) {
      int n = data.length;
      if (n < 2) {
         throw new IllegalArgumentException("Not enough data.");
      } else {
         double sumX = 0.0;
         double sumY = 0.0;
         double sumXX = 0.0;
         double sumXY = 0.0;

         for (int i = 0; i < n; i++) {
            double x = Math.log(data[i][0]);
            double y = Math.log(data[i][1]);
            sumX += x;
            sumY += y;
            double xx = x * x;
            sumXX += xx;
            double xy = x * y;
            sumXY += xy;
         }

         double sxx = sumXX - sumX * sumX / (double)n;
         double sxy = sumXY - sumX * sumY / (double)n;
         double xbar = sumX / (double)n;
         double ybar = sumY / (double)n;
         double[] result = new double[]{0.0, sxy / sxx};
         result[0] = Math.pow(Math.exp(1.0), ybar - result[1] * xbar);
         return result;
      }
   }

   public static double[] getPowerRegression(XYDataset data, int series) {
      int n = data.getItemCount(series);
      if (n < 2) {
         throw new IllegalArgumentException("Not enough data.");
      } else {
         double sumX = 0.0;
         double sumY = 0.0;
         double sumXX = 0.0;
         double sumXY = 0.0;

         for (int i = 0; i < n; i++) {
            double x = Math.log(data.getXValue(series, i));
            double y = Math.log(data.getYValue(series, i));
            sumX += x;
            sumY += y;
            double xx = x * x;
            sumXX += xx;
            double xy = x * y;
            sumXY += xy;
         }

         double sxx = sumXX - sumX * sumX / (double)n;
         double sxy = sumXY - sumX * sumY / (double)n;
         double xbar = sumX / (double)n;
         double ybar = sumY / (double)n;
         double[] result = new double[]{0.0, sxy / sxx};
         result[0] = Math.pow(Math.exp(1.0), ybar - result[1] * xbar);
         return result;
      }
   }

   public static double[] getPolynomialRegression(XYDataset dataset, int series, int order) {
      Args.nullNotPermitted(dataset, "dataset");
      int itemCount = dataset.getItemCount(series);
      if (itemCount < order + 1) {
         throw new IllegalArgumentException("Not enough data.");
      } else {
         int validItems = 0;
         double[][] data = new double[2][itemCount];

         for (int item = 0; item < itemCount; item++) {
            double x = dataset.getXValue(series, item);
            double y = dataset.getYValue(series, item);
            if (!Double.isNaN(x) && !Double.isNaN(y)) {
               data[0][validItems] = x;
               data[1][validItems] = y;
               validItems++;
            }
         }

         if (validItems < order + 1) {
            throw new IllegalArgumentException("Not enough data.");
         } else {
            int equations = order + 1;
            int coefficients = order + 2;
            double[] result = new double[equations + 1];
            double[][] matrix = new double[equations][coefficients];
            double sumX = 0.0;
            double sumY = 0.0;

            for (int itemx = 0; itemx < validItems; itemx++) {
               sumX += data[0][itemx];
               sumY += data[1][itemx];

               for (int eq = 0; eq < equations; eq++) {
                  for (int coe = 0; coe < coefficients - 1; coe++) {
                     matrix[eq][coe] = matrix[eq][coe] + Math.pow(data[0][itemx], (double)(eq + coe));
                  }

                  matrix[eq][coefficients - 1] = matrix[eq][coefficients - 1] + data[1][itemx] * Math.pow(data[0][itemx], (double)eq);
               }
            }

            double[][] subMatrix = calculateSubMatrix(matrix);

            for (int eq = 1; eq < equations; eq++) {
               matrix[eq][0] = 0.0;

               for (int coe = 1; coe < coefficients; coe++) {
                  matrix[eq][coe] = subMatrix[eq - 1][coe - 1];
               }
            }

            for (int eq = equations - 1; eq > -1; eq--) {
               double value = matrix[eq][coefficients - 1];

               for (int coe = eq; coe < coefficients - 1; coe++) {
                  value -= matrix[eq][coe] * result[coe];
               }

               result[eq] = value / matrix[eq][eq];
            }

            double meanY = sumY / (double)validItems;
            double yObsSquare = 0.0;
            double yRegSquare = 0.0;

            for (int itemx = 0; itemx < validItems; itemx++) {
               double yCalc = 0.0;

               for (int eq = 0; eq < equations; eq++) {
                  yCalc += result[eq] * Math.pow(data[0][itemx], (double)eq);
               }

               yRegSquare += Math.pow(yCalc - meanY, 2.0);
               yObsSquare += Math.pow(data[1][itemx] - meanY, 2.0);
            }

            double rSquare = yRegSquare / yObsSquare;
            result[equations] = rSquare;
            return result;
         }
      }
   }

   private static double[][] calculateSubMatrix(double[][] matrix) {
      int equations = matrix.length;
      int coefficients = matrix[0].length;
      double[][] result = new double[equations - 1][coefficients - 1];

      for (int eq = 1; eq < equations; eq++) {
         double factor = matrix[0][0] / matrix[eq][0];

         for (int coe = 1; coe < coefficients; coe++) {
            result[eq - 1][coe - 1] = matrix[0][coe] - matrix[eq][coe] * factor;
         }
      }

      if (equations == 1) {
         return result;
      } else {
         if (result[0][0] == 0.0) {
            boolean found = false;

            for (int i = 0; i < result.length; i++) {
               if (result[i][0] != 0.0) {
                  found = true;
                  double[] temp = result[0];
                  System.arraycopy(result[i], 0, result[0], 0, result[i].length);
                  System.arraycopy(temp, 0, result[i], 0, temp.length);
                  break;
               }
            }

            if (!found) {
               return new double[equations - 1][coefficients - 1];
            }
         }

         double[][] subMatrix = calculateSubMatrix(result);

         for (int eq = 1; eq < equations - 1; eq++) {
            result[eq][0] = 0.0;

            for (int coe = 1; coe < coefficients - 1; coe++) {
               result[eq][coe] = subMatrix[eq - 1][coe - 1];
            }
         }

         return result;
      }
   }
}
