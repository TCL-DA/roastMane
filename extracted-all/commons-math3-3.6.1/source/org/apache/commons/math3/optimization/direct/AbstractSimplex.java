package org.apache.commons.math3.optimization.direct;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.optimization.OptimizationData;
import org.apache.commons.math3.optimization.PointValuePair;

@Deprecated
public abstract class AbstractSimplex implements OptimizationData {
   private PointValuePair[] simplex;
   private double[][] startConfiguration;
   private final int dimension;

   protected AbstractSimplex(int n) {
      this(n, 1.0);
   }

   protected AbstractSimplex(int n, double sideLength) {
      this(createHypercubeSteps(n, sideLength));
   }

   protected AbstractSimplex(double[] steps) {
      if (steps == null) {
         throw new NullArgumentException();
      } else if (steps.length == 0) {
         throw new ZeroException();
      } else {
         this.dimension = steps.length;
         this.startConfiguration = new double[this.dimension][this.dimension];

         for (int i = 0; i < this.dimension; i++) {
            double[] vertexI = this.startConfiguration[i];

            for (int j = 0; j < i + 1; j++) {
               if (steps[j] == 0.0) {
                  throw new ZeroException(LocalizedFormats.EQUAL_VERTICES_IN_SIMPLEX);
               }

               System.arraycopy(steps, 0, vertexI, 0, j + 1);
            }
         }
      }
   }

   protected AbstractSimplex(double[][] referenceSimplex) {
      if (referenceSimplex.length <= 0) {
         throw new NotStrictlyPositiveException(LocalizedFormats.SIMPLEX_NEED_ONE_POINT, referenceSimplex.length);
      } else {
         this.dimension = referenceSimplex.length - 1;
         this.startConfiguration = new double[this.dimension][this.dimension];
         double[] ref0 = referenceSimplex[0];

         for (int i = 0; i < referenceSimplex.length; i++) {
            double[] refI = referenceSimplex[i];
            if (refI.length != this.dimension) {
               throw new DimensionMismatchException(refI.length, this.dimension);
            }

            for (int j = 0; j < i; j++) {
               double[] refJ = referenceSimplex[j];
               boolean allEquals = true;

               for (int k = 0; k < this.dimension; k++) {
                  if (refI[k] != refJ[k]) {
                     allEquals = false;
                     break;
                  }
               }

               if (allEquals) {
                  throw new MathIllegalArgumentException(LocalizedFormats.EQUAL_VERTICES_IN_SIMPLEX, i, j);
               }
            }

            if (i > 0) {
               double[] confI = this.startConfiguration[i - 1];

               for (int kx = 0; kx < this.dimension; kx++) {
                  confI[kx] = refI[kx] - ref0[kx];
               }
            }
         }
      }
   }

   public int getDimension() {
      return this.dimension;
   }

   public int getSize() {
      return this.simplex.length;
   }

   public abstract void iterate(MultivariateFunction var1, Comparator<PointValuePair> var2);

   public void build(double[] startPoint) {
      if (this.dimension != startPoint.length) {
         throw new DimensionMismatchException(this.dimension, startPoint.length);
      } else {
         this.simplex = new PointValuePair[this.dimension + 1];
         this.simplex[0] = new PointValuePair(startPoint, Double.NaN);

         for (int i = 0; i < this.dimension; i++) {
            double[] confI = this.startConfiguration[i];
            double[] vertexI = new double[this.dimension];

            for (int k = 0; k < this.dimension; k++) {
               vertexI[k] = startPoint[k] + confI[k];
            }

            this.simplex[i + 1] = new PointValuePair(vertexI, Double.NaN);
         }
      }
   }

   public void evaluate(MultivariateFunction evaluationFunction, Comparator<PointValuePair> comparator) {
      for (int i = 0; i < this.simplex.length; i++) {
         PointValuePair vertex = this.simplex[i];
         double[] point = vertex.getPointRef();
         if (Double.isNaN(vertex.getValue())) {
            this.simplex[i] = new PointValuePair(point, evaluationFunction.value(point), false);
         }
      }

      Arrays.sort(this.simplex, comparator);
   }

   protected void replaceWorstPoint(PointValuePair pointValuePair, Comparator<PointValuePair> comparator) {
      for (int i = 0; i < this.dimension; i++) {
         if (comparator.compare(this.simplex[i], pointValuePair) > 0) {
            PointValuePair tmp = this.simplex[i];
            this.simplex[i] = pointValuePair;
            pointValuePair = tmp;
         }
      }

      this.simplex[this.dimension] = pointValuePair;
   }

   public PointValuePair[] getPoints() {
      PointValuePair[] copy = new PointValuePair[this.simplex.length];
      System.arraycopy(this.simplex, 0, copy, 0, this.simplex.length);
      return copy;
   }

   public PointValuePair getPoint(int index) {
      if (index >= 0 && index < this.simplex.length) {
         return this.simplex[index];
      } else {
         throw new OutOfRangeException(index, 0, this.simplex.length - 1);
      }
   }

   protected void setPoint(int index, PointValuePair point) {
      if (index >= 0 && index < this.simplex.length) {
         this.simplex[index] = point;
      } else {
         throw new OutOfRangeException(index, 0, this.simplex.length - 1);
      }
   }

   protected void setPoints(PointValuePair[] points) {
      if (points.length != this.simplex.length) {
         throw new DimensionMismatchException(points.length, this.simplex.length);
      } else {
         this.simplex = points;
      }
   }

   private static double[] createHypercubeSteps(int n, double sideLength) {
      double[] steps = new double[n];

      for (int i = 0; i < n; i++) {
         steps[i] = sideLength;
      }

      return steps;
   }
}
