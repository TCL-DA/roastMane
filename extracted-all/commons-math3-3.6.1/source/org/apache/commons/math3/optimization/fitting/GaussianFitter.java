package org.apache.commons.math3.optimization.fitting;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.optimization.DifferentiableMultivariateVectorOptimizer;
import org.apache.commons.math3.util.FastMath;

@Deprecated
public class GaussianFitter extends CurveFitter<Gaussian.Parametric> {
   public GaussianFitter(DifferentiableMultivariateVectorOptimizer optimizer) {
      super(optimizer);
   }

   public double[] fit(double[] initialGuess) {
      Gaussian.Parametric f = new Gaussian.Parametric() {
         @Override
         public double value(double x, double... p) {
            double v = Double.POSITIVE_INFINITY;

            try {
               v = super.value(x, p);
            } catch (NotStrictlyPositiveException var7) {
            }

            return v;
         }

         @Override
         public double[] gradient(double x, double... p) {
            double[] v = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};

            try {
               v = super.gradient(x, p);
            } catch (NotStrictlyPositiveException var6) {
            }

            return v;
         }
      };
      return this.fit(f, initialGuess);
   }

   public double[] fit() {
      double[] guess = new GaussianFitter.ParameterGuesser(this.getObservations()).guess();
      return this.fit(guess);
   }

   public static class ParameterGuesser {
      private final double norm;
      private final double mean;
      private final double sigma;

      public ParameterGuesser(WeightedObservedPoint[] observations) {
         if (observations == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
         } else if (observations.length < 3) {
            throw new NumberIsTooSmallException(observations.length, 3, true);
         } else {
            WeightedObservedPoint[] sorted = this.sortObservations(observations);
            double[] params = this.basicGuess(sorted);
            this.norm = params[0];
            this.mean = params[1];
            this.sigma = params[2];
         }
      }

      public double[] guess() {
         return new double[]{this.norm, this.mean, this.sigma};
      }

      private WeightedObservedPoint[] sortObservations(WeightedObservedPoint[] unsorted) {
         WeightedObservedPoint[] observations = (WeightedObservedPoint[])unsorted.clone();
         Comparator<WeightedObservedPoint> cmp = new Comparator<WeightedObservedPoint>() {
            public int compare(WeightedObservedPoint p1, WeightedObservedPoint p2) {
               if (p1 == null && p2 == null) {
                  return 0;
               } else if (p1 == null) {
                  return -1;
               } else if (p2 == null) {
                  return 1;
               } else {
                  int cmpX = Double.compare(p1.getX(), p2.getX());
                  if (cmpX < 0) {
                     return -1;
                  } else if (cmpX > 0) {
                     return 1;
                  } else {
                     int cmpY = Double.compare(p1.getY(), p2.getY());
                     if (cmpY < 0) {
                        return -1;
                     } else if (cmpY > 0) {
                        return 1;
                     } else {
                        int cmpW = Double.compare(p1.getWeight(), p2.getWeight());
                        if (cmpW < 0) {
                           return -1;
                        } else {
                           return cmpW > 0 ? 1 : 0;
                        }
                     }
                  }
               }
            }
         };
         Arrays.sort(observations, cmp);
         return observations;
      }

      private double[] basicGuess(WeightedObservedPoint[] points) {
         int maxYIdx = this.findMaxY(points);
         double n = points[maxYIdx].getY();
         double m = points[maxYIdx].getX();

         double fwhmApprox;
         try {
            double halfY = n + (m - n) / 2.0;
            double fwhmX1 = this.interpolateXAtY(points, maxYIdx, -1, halfY);
            double fwhmX2 = this.interpolateXAtY(points, maxYIdx, 1, halfY);
            fwhmApprox = fwhmX2 - fwhmX1;
         } catch (OutOfRangeException var15) {
            fwhmApprox = points[points.length - 1].getX() - points[0].getX();
         }

         double s = fwhmApprox / (2.0 * FastMath.sqrt(2.0 * FastMath.log(2.0)));
         return new double[]{n, m, s};
      }

      private int findMaxY(WeightedObservedPoint[] points) {
         int maxYIdx = 0;

         for (int i = 1; i < points.length; i++) {
            if (points[i].getY() > points[maxYIdx].getY()) {
               maxYIdx = i;
            }
         }

         return maxYIdx;
      }

      private double interpolateXAtY(WeightedObservedPoint[] points, int startIdx, int idxStep, double y) throws OutOfRangeException {
         if (idxStep == 0) {
            throw new ZeroException();
         } else {
            WeightedObservedPoint[] twoPoints = this.getInterpolationPointsForY(points, startIdx, idxStep, y);
            WeightedObservedPoint p1 = twoPoints[0];
            WeightedObservedPoint p2 = twoPoints[1];
            if (p1.getY() == y) {
               return p1.getX();
            } else {
               return p2.getY() == y ? p2.getX() : p1.getX() + (y - p1.getY()) * (p2.getX() - p1.getX()) / (p2.getY() - p1.getY());
            }
         }
      }

      private WeightedObservedPoint[] getInterpolationPointsForY(WeightedObservedPoint[] points, int startIdx, int idxStep, double y) throws OutOfRangeException {
         if (idxStep == 0) {
            throw new ZeroException();
         } else {
            for (int i = startIdx; idxStep < 0 ? i + idxStep >= 0 : i + idxStep < points.length; i += idxStep) {
               WeightedObservedPoint p1 = points[i];
               WeightedObservedPoint p2 = points[i + idxStep];
               if (this.isBetween(y, p1.getY(), p2.getY())) {
                  if (idxStep < 0) {
                     return new WeightedObservedPoint[]{p2, p1};
                  }

                  return new WeightedObservedPoint[]{p1, p2};
               }
            }

            throw new OutOfRangeException(y, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
         }
      }

      private boolean isBetween(double value, double boundary1, double boundary2) {
         return value >= boundary1 && value <= boundary2 || value >= boundary2 && value <= boundary1;
      }
   }
}
