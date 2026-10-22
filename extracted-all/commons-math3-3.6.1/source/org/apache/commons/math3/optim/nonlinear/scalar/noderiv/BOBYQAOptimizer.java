package org.apache.commons.math3.optim.nonlinear.scalar.noderiv;

import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.util.FastMath;

public class BOBYQAOptimizer extends MultivariateOptimizer {
   public static final int MINIMUM_PROBLEM_DIMENSION = 2;
   public static final double DEFAULT_INITIAL_RADIUS = 10.0;
   public static final double DEFAULT_STOPPING_RADIUS = 1.0E-8;
   private static final double ZERO = 0.0;
   private static final double ONE = 1.0;
   private static final double TWO = 2.0;
   private static final double TEN = 10.0;
   private static final double SIXTEEN = 16.0;
   private static final double TWO_HUNDRED_FIFTY = 250.0;
   private static final double MINUS_ONE = -1.0;
   private static final double HALF = 0.5;
   private static final double ONE_OVER_FOUR = 0.25;
   private static final double ONE_OVER_EIGHT = 0.125;
   private static final double ONE_OVER_TEN = 0.1;
   private static final double ONE_OVER_A_THOUSAND = 0.001;
   private final int numberOfInterpolationPoints;
   private double initialTrustRegionRadius;
   private final double stoppingTrustRegionRadius;
   private boolean isMinimize;
   private ArrayRealVector currentBest;
   private double[] boundDifference;
   private int trustRegionCenterInterpolationPointIndex;
   private Array2DRowRealMatrix bMatrix;
   private Array2DRowRealMatrix zMatrix;
   private Array2DRowRealMatrix interpolationPoints;
   private ArrayRealVector originShift;
   private ArrayRealVector fAtInterpolationPoints;
   private ArrayRealVector trustRegionCenterOffset;
   private ArrayRealVector gradientAtTrustRegionCenter;
   private ArrayRealVector lowerDifference;
   private ArrayRealVector upperDifference;
   private ArrayRealVector modelSecondDerivativesParameters;
   private ArrayRealVector newPoint;
   private ArrayRealVector alternativeNewPoint;
   private ArrayRealVector trialStepPoint;
   private ArrayRealVector lagrangeValuesAtNewPoint;
   private ArrayRealVector modelSecondDerivativesValues;

   public BOBYQAOptimizer(int numberOfInterpolationPoints) {
      this(numberOfInterpolationPoints, 10.0, 1.0E-8);
   }

   public BOBYQAOptimizer(int numberOfInterpolationPoints, double initialTrustRegionRadius, double stoppingTrustRegionRadius) {
      super(null);
      this.numberOfInterpolationPoints = numberOfInterpolationPoints;
      this.initialTrustRegionRadius = initialTrustRegionRadius;
      this.stoppingTrustRegionRadius = stoppingTrustRegionRadius;
   }

   protected PointValuePair doOptimize() {
      double[] lowerBound = this.getLowerBound();
      double[] upperBound = this.getUpperBound();
      this.setup(lowerBound, upperBound);
      this.isMinimize = this.getGoalType() == GoalType.MINIMIZE;
      this.currentBest = new ArrayRealVector(this.getStartPoint());
      double value = this.bobyqa(lowerBound, upperBound);
      return new PointValuePair(this.currentBest.getDataRef(), this.isMinimize ? value : -value);
   }

   private double bobyqa(double[] lowerBound, double[] upperBound) {
      printMethod();
      int n = this.currentBest.getDimension();

      for (int j = 0; j < n; j++) {
         double boundDiff = this.boundDifference[j];
         this.lowerDifference.setEntry(j, lowerBound[j] - this.currentBest.getEntry(j));
         this.upperDifference.setEntry(j, upperBound[j] - this.currentBest.getEntry(j));
         if (this.lowerDifference.getEntry(j) >= -this.initialTrustRegionRadius) {
            if (this.lowerDifference.getEntry(j) >= 0.0) {
               this.currentBest.setEntry(j, lowerBound[j]);
               this.lowerDifference.setEntry(j, 0.0);
               this.upperDifference.setEntry(j, boundDiff);
            } else {
               this.currentBest.setEntry(j, lowerBound[j] + this.initialTrustRegionRadius);
               this.lowerDifference.setEntry(j, -this.initialTrustRegionRadius);
               double deltaOne = upperBound[j] - this.currentBest.getEntry(j);
               this.upperDifference.setEntry(j, FastMath.max(deltaOne, this.initialTrustRegionRadius));
            }
         } else if (this.upperDifference.getEntry(j) <= this.initialTrustRegionRadius) {
            if (this.upperDifference.getEntry(j) <= 0.0) {
               this.currentBest.setEntry(j, upperBound[j]);
               this.lowerDifference.setEntry(j, -boundDiff);
               this.upperDifference.setEntry(j, 0.0);
            } else {
               this.currentBest.setEntry(j, upperBound[j] - this.initialTrustRegionRadius);
               double deltaOne = lowerBound[j] - this.currentBest.getEntry(j);
               double deltaTwo = -this.initialTrustRegionRadius;
               this.lowerDifference.setEntry(j, FastMath.min(deltaOne, deltaTwo));
               this.upperDifference.setEntry(j, this.initialTrustRegionRadius);
            }
         }
      }

      return this.bobyqb(lowerBound, upperBound);
   }

   private double bobyqb(double[] lowerBound, double[] upperBound) {
      printMethod();
      int n = this.currentBest.getDimension();
      int npt = this.numberOfInterpolationPoints;
      int np = n + 1;
      int nptm = npt - np;
      int nh = n * np / 2;
      ArrayRealVector work1 = new ArrayRealVector(n);
      ArrayRealVector work2 = new ArrayRealVector(npt);
      ArrayRealVector work3 = new ArrayRealVector(npt);
      double cauchy = Double.NaN;
      double alpha = Double.NaN;
      double dsq = Double.NaN;
      double crvmin = Double.NaN;
      this.trustRegionCenterInterpolationPointIndex = 0;
      this.prelim(lowerBound, upperBound);
      double xoptsq = 0.0;

      for (int i = 0; i < n; i++) {
         this.trustRegionCenterOffset.setEntry(i, this.interpolationPoints.getEntry(this.trustRegionCenterInterpolationPointIndex, i));
         double deltaOne = this.trustRegionCenterOffset.getEntry(i);
         xoptsq += deltaOne * deltaOne;
      }

      double fsave = this.fAtInterpolationPoints.getEntry(0);
      int kbase = 0;
      int ntrits = 0;
      int itest = 0;
      int knew = 0;
      int nfsav = this.getEvaluations();
      double rho = this.initialTrustRegionRadius;
      double delta = rho;
      double diffa = 0.0;
      double diffb = 0.0;
      double diffc = 0.0;
      double f = 0.0;
      double beta = 0.0;
      double adelt = 0.0;
      double denom = 0.0;
      double ratio = 0.0;
      double dnorm = 0.0;
      double scaden = 0.0;
      double biglsq = 0.0;
      double distsq = 0.0;
      int state = 20;

      label866:
      while (true) {
         label864:
         while (true) {
            label862:
            while (true) {
               label860:
               while (true) {
                  label857:
                  while (true) {
                     switch (state) {
                        case 20:
                           printState(20);
                           if (this.trustRegionCenterInterpolationPointIndex != 0) {
                              int ih = 0;

                              for (int j = 0; j < n; j++) {
                                 for (int i = 0; i <= j; i++) {
                                    if (i < j) {
                                       this.gradientAtTrustRegionCenter
                                          .setEntry(
                                             j,
                                             this.gradientAtTrustRegionCenter.getEntry(j)
                                                + this.modelSecondDerivativesValues.getEntry(ih) * this.trustRegionCenterOffset.getEntry(i)
                                          );
                                    }

                                    this.gradientAtTrustRegionCenter
                                       .setEntry(
                                          i,
                                          this.gradientAtTrustRegionCenter.getEntry(i)
                                             + this.modelSecondDerivativesValues.getEntry(ih) * this.trustRegionCenterOffset.getEntry(j)
                                       );
                                    ih++;
                                 }
                              }

                              if (this.getEvaluations() > npt) {
                                 for (int kx = 0; kx < npt; kx++) {
                                    double temp = 0.0;

                                    for (int j = 0; j < n; j++) {
                                       temp += this.interpolationPoints.getEntry(kx, j) * this.trustRegionCenterOffset.getEntry(j);
                                    }

                                    temp *= this.modelSecondDerivativesParameters.getEntry(kx);

                                    for (int i = 0; i < n; i++) {
                                       this.gradientAtTrustRegionCenter
                                          .setEntry(i, this.gradientAtTrustRegionCenter.getEntry(i) + temp * this.interpolationPoints.getEntry(kx, i));
                                    }
                                 }
                              }
                           }
                        case 60:
                           break label860;
                        case 90:
                           break label864;
                        case 210:
                           printState(210);
                           double[] alphaCauchy = this.altmov(knew, adelt);
                           alpha = alphaCauchy[0];
                           cauchy = alphaCauchy[1];

                           for (int i = 0; i < n; i++) {
                              this.trialStepPoint.setEntry(i, this.newPoint.getEntry(i) - this.trustRegionCenterOffset.getEntry(i));
                           }
                        case 230:
                           printState(230);

                           for (int k = 0; k < npt; k++) {
                              double suma = 0.0;
                              double sumb = 0.0;
                              double sum = 0.0;

                              for (int j = 0; j < n; j++) {
                                 suma += this.interpolationPoints.getEntry(k, j) * this.trialStepPoint.getEntry(j);
                                 sumb += this.interpolationPoints.getEntry(k, j) * this.trustRegionCenterOffset.getEntry(j);
                                 sum += this.bMatrix.getEntry(k, j) * this.trialStepPoint.getEntry(j);
                              }

                              work3.setEntry(k, suma * (0.5 * suma + sumb));
                              this.lagrangeValuesAtNewPoint.setEntry(k, sum);
                              work2.setEntry(k, suma);
                           }

                           beta = 0.0;

                           for (int m = 0; m < nptm; m++) {
                              double sum = 0.0;

                              for (int k = 0; k < npt; k++) {
                                 sum += this.zMatrix.getEntry(k, m) * work3.getEntry(k);
                              }

                              beta -= sum * sum;

                              for (int k = 0; k < npt; k++) {
                                 this.lagrangeValuesAtNewPoint.setEntry(k, this.lagrangeValuesAtNewPoint.getEntry(k) + sum * this.zMatrix.getEntry(k, m));
                              }
                           }

                           dsq = 0.0;
                           double bsum = 0.0;
                           double dx = 0.0;

                           for (int j = 0; j < n; j++) {
                              double d1 = this.trialStepPoint.getEntry(j);
                              dsq += d1 * d1;
                              double sum = 0.0;

                              for (int k = 0; k < npt; k++) {
                                 sum += work3.getEntry(k) * this.bMatrix.getEntry(k, j);
                              }

                              bsum += sum * this.trialStepPoint.getEntry(j);
                              int jp = npt + j;

                              for (int i = 0; i < n; i++) {
                                 sum += this.bMatrix.getEntry(jp, i) * this.trialStepPoint.getEntry(i);
                              }

                              this.lagrangeValuesAtNewPoint.setEntry(jp, sum);
                              bsum += sum * this.trialStepPoint.getEntry(j);
                              dx += this.trialStepPoint.getEntry(j) * this.trustRegionCenterOffset.getEntry(j);
                           }

                           beta = dx * dx + dsq * (xoptsq + dx + dx + 0.5 * dsq) + beta - bsum;
                           this.lagrangeValuesAtNewPoint
                              .setEntry(
                                 this.trustRegionCenterInterpolationPointIndex,
                                 this.lagrangeValuesAtNewPoint.getEntry(this.trustRegionCenterInterpolationPointIndex) + 1.0
                              );
                           if (ntrits == 0) {
                              double d1 = this.lagrangeValuesAtNewPoint.getEntry(knew);
                              denom = d1 * d1 + alpha * beta;
                              if (denom < cauchy && cauchy > 0.0) {
                                 for (int i = 0; i < n; i++) {
                                    this.newPoint.setEntry(i, this.alternativeNewPoint.getEntry(i));
                                    this.trialStepPoint.setEntry(i, this.newPoint.getEntry(i) - this.trustRegionCenterOffset.getEntry(i));
                                 }

                                 cauchy = 0.0;
                                 state = 230;
                                 continue;
                              }
                           } else {
                              double delsq = delta * delta;
                              scaden = 0.0;
                              biglsq = 0.0;
                              knew = 0;

                              for (int k = 0; k < npt; k++) {
                                 if (k != this.trustRegionCenterInterpolationPointIndex) {
                                    double hdiag = 0.0;

                                    for (int m = 0; m < nptm; m++) {
                                       double d1 = this.zMatrix.getEntry(k, m);
                                       hdiag += d1 * d1;
                                    }

                                    double d2 = this.lagrangeValuesAtNewPoint.getEntry(k);
                                    double den = beta * hdiag + d2 * d2;
                                    distsq = 0.0;

                                    for (int j = 0; j < n; j++) {
                                       double d3 = this.interpolationPoints.getEntry(k, j) - this.trustRegionCenterOffset.getEntry(j);
                                       distsq += d3 * d3;
                                    }

                                    double d4 = distsq / delsq;
                                    double temp = FastMath.max(1.0, d4 * d4);
                                    if (temp * den > scaden) {
                                       scaden = temp * den;
                                       knew = k;
                                       denom = den;
                                    }

                                    double d5 = this.lagrangeValuesAtNewPoint.getEntry(k);
                                    biglsq = FastMath.max(biglsq, temp * d5 * d5);
                                 }
                              }
                           }
                        case 360:
                           break;
                        case 650:
                           break label857;
                        case 680:
                           break label862;
                        case 720:
                           break label866;
                        default:
                           throw new MathIllegalStateException(LocalizedFormats.SIMPLE_MESSAGE, "bobyqb");
                     }

                     printState(360);

                     for (int i = 0; i < n; i++) {
                        double d3 = lowerBound[i];
                        double d4 = this.originShift.getEntry(i) + this.newPoint.getEntry(i);
                        double d1 = FastMath.max(d3, d4);
                        double d2 = upperBound[i];
                        this.currentBest.setEntry(i, FastMath.min(d1, d2));
                        if (this.newPoint.getEntry(i) == this.lowerDifference.getEntry(i)) {
                           this.currentBest.setEntry(i, lowerBound[i]);
                        }

                        if (this.newPoint.getEntry(i) == this.upperDifference.getEntry(i)) {
                           this.currentBest.setEntry(i, upperBound[i]);
                        }
                     }

                     f = this.computeObjectiveValue(this.currentBest.toArray());
                     if (!this.isMinimize) {
                        f = -f;
                     }

                     if (ntrits == -1) {
                        fsave = f;
                        state = 720;
                     } else {
                        double fopt = this.fAtInterpolationPoints.getEntry(this.trustRegionCenterInterpolationPointIndex);
                        double vquad = 0.0;
                        int ih = 0;

                        for (int j = 0; j < n; j++) {
                           vquad += this.trialStepPoint.getEntry(j) * this.gradientAtTrustRegionCenter.getEntry(j);

                           for (int i = 0; i <= j; i++) {
                              double temp = this.trialStepPoint.getEntry(i) * this.trialStepPoint.getEntry(j);
                              if (i == j) {
                                 temp *= 0.5;
                              }

                              vquad += this.modelSecondDerivativesValues.getEntry(ih) * temp;
                              ih++;
                           }
                        }

                        for (int kx = 0; kx < npt; kx++) {
                           double d1x = work2.getEntry(kx);
                           double d2x = d1x * d1x;
                           vquad += 0.5 * this.modelSecondDerivativesParameters.getEntry(kx) * d2x;
                        }

                        double diff = f - fopt - vquad;
                        diffc = diffb;
                        diffb = diffa;
                        diffa = FastMath.abs(diff);
                        if (dnorm > rho) {
                           nfsav = this.getEvaluations();
                        }

                        if (ntrits > 0) {
                           if (vquad >= 0.0) {
                              throw new MathIllegalStateException(LocalizedFormats.TRUST_REGION_STEP_FAILED, vquad);
                           }

                           ratio = (f - fopt) / vquad;
                           double hDelta = 0.5 * delta;
                           if (ratio <= 0.1) {
                              delta = FastMath.min(hDelta, dnorm);
                           } else if (ratio <= 0.7) {
                              delta = FastMath.max(hDelta, dnorm);
                           } else {
                              delta = FastMath.max(hDelta, 2.0 * dnorm);
                           }

                           if (delta <= rho * 1.5) {
                              delta = rho;
                           }

                           if (f < fopt) {
                              int ksav = knew;
                              double densav = denom;
                              double delsq = delta * delta;
                              scaden = 0.0;
                              biglsq = 0.0;
                              knew = 0;

                              for (int kx = 0; kx < npt; kx++) {
                                 double hdiag = 0.0;

                                 for (int m = 0; m < nptm; m++) {
                                    double d1x = this.zMatrix.getEntry(kx, m);
                                    hdiag += d1x * d1x;
                                 }

                                 double d1x = this.lagrangeValuesAtNewPoint.getEntry(kx);
                                 double den = beta * hdiag + d1x * d1x;
                                 distsq = 0.0;

                                 for (int j = 0; j < n; j++) {
                                    double d2x = this.interpolationPoints.getEntry(kx, j) - this.newPoint.getEntry(j);
                                    distsq += d2x * d2x;
                                 }

                                 double d3x = distsq / delsq;
                                 double temp = FastMath.max(1.0, d3x * d3x);
                                 if (temp * den > scaden) {
                                    scaden = temp * den;
                                    knew = kx;
                                    denom = den;
                                 }

                                 double d4x = this.lagrangeValuesAtNewPoint.getEntry(kx);
                                 double d5 = temp * d4x * d4x;
                                 biglsq = FastMath.max(biglsq, d5);
                              }

                              if (scaden <= 0.5 * biglsq) {
                                 knew = ksav;
                                 denom = densav;
                              }
                           }
                        }

                        this.update(beta, denom, knew);
                        ih = 0;
                        double pqold = this.modelSecondDerivativesParameters.getEntry(knew);
                        this.modelSecondDerivativesParameters.setEntry(knew, 0.0);

                        for (int i = 0; i < n; i++) {
                           double temp = pqold * this.interpolationPoints.getEntry(knew, i);

                           for (int j = 0; j <= i; j++) {
                              this.modelSecondDerivativesValues
                                 .setEntry(ih, this.modelSecondDerivativesValues.getEntry(ih) + temp * this.interpolationPoints.getEntry(knew, j));
                              ih++;
                           }
                        }

                        for (int m = 0; m < nptm; m++) {
                           double temp = diff * this.zMatrix.getEntry(knew, m);

                           for (int kx = 0; kx < npt; kx++) {
                              this.modelSecondDerivativesParameters
                                 .setEntry(kx, this.modelSecondDerivativesParameters.getEntry(kx) + temp * this.zMatrix.getEntry(kx, m));
                           }
                        }

                        this.fAtInterpolationPoints.setEntry(knew, f);

                        for (int i = 0; i < n; i++) {
                           this.interpolationPoints.setEntry(knew, i, this.newPoint.getEntry(i));
                           work1.setEntry(i, this.bMatrix.getEntry(knew, i));
                        }

                        for (int kx = 0; kx < npt; kx++) {
                           double suma = 0.0;

                           for (int m = 0; m < nptm; m++) {
                              suma += this.zMatrix.getEntry(knew, m) * this.zMatrix.getEntry(kx, m);
                           }

                           double sumb = 0.0;

                           for (int j = 0; j < n; j++) {
                              sumb += this.interpolationPoints.getEntry(kx, j) * this.trustRegionCenterOffset.getEntry(j);
                           }

                           double temp = suma * sumb;

                           for (int i = 0; i < n; i++) {
                              work1.setEntry(i, work1.getEntry(i) + temp * this.interpolationPoints.getEntry(kx, i));
                           }
                        }

                        for (int i = 0; i < n; i++) {
                           this.gradientAtTrustRegionCenter.setEntry(i, this.gradientAtTrustRegionCenter.getEntry(i) + diff * work1.getEntry(i));
                        }

                        if (f < fopt) {
                           this.trustRegionCenterInterpolationPointIndex = knew;
                           xoptsq = 0.0;
                           ih = 0;

                           for (int j = 0; j < n; j++) {
                              this.trustRegionCenterOffset.setEntry(j, this.newPoint.getEntry(j));
                              double d1x = this.trustRegionCenterOffset.getEntry(j);
                              xoptsq += d1x * d1x;

                              for (int i = 0; i <= j; i++) {
                                 if (i < j) {
                                    this.gradientAtTrustRegionCenter
                                       .setEntry(
                                          j,
                                          this.gradientAtTrustRegionCenter.getEntry(j)
                                             + this.modelSecondDerivativesValues.getEntry(ih) * this.trialStepPoint.getEntry(i)
                                       );
                                 }

                                 this.gradientAtTrustRegionCenter
                                    .setEntry(
                                       i,
                                       this.gradientAtTrustRegionCenter.getEntry(i)
                                          + this.modelSecondDerivativesValues.getEntry(ih) * this.trialStepPoint.getEntry(j)
                                    );
                                 ih++;
                              }
                           }

                           for (int kx = 0; kx < npt; kx++) {
                              double temp = 0.0;

                              for (int j = 0; j < n; j++) {
                                 temp += this.interpolationPoints.getEntry(kx, j) * this.trialStepPoint.getEntry(j);
                              }

                              temp *= this.modelSecondDerivativesParameters.getEntry(kx);

                              for (int i = 0; i < n; i++) {
                                 this.gradientAtTrustRegionCenter
                                    .setEntry(i, this.gradientAtTrustRegionCenter.getEntry(i) + temp * this.interpolationPoints.getEntry(kx, i));
                              }
                           }
                        }

                        if (ntrits > 0) {
                           for (int kx = 0; kx < npt; kx++) {
                              this.lagrangeValuesAtNewPoint
                                 .setEntry(
                                    kx,
                                    this.fAtInterpolationPoints.getEntry(kx)
                                       - this.fAtInterpolationPoints.getEntry(this.trustRegionCenterInterpolationPointIndex)
                                 );
                              work3.setEntry(kx, 0.0);
                           }

                           for (int j = 0; j < nptm; j++) {
                              double sum = 0.0;

                              for (int kx = 0; kx < npt; kx++) {
                                 sum += this.zMatrix.getEntry(kx, j) * this.lagrangeValuesAtNewPoint.getEntry(kx);
                              }

                              for (int kx = 0; kx < npt; kx++) {
                                 work3.setEntry(kx, work3.getEntry(kx) + sum * this.zMatrix.getEntry(kx, j));
                              }
                           }

                           for (int kx = 0; kx < npt; kx++) {
                              double sum = 0.0;

                              for (int j = 0; j < n; j++) {
                                 sum += this.interpolationPoints.getEntry(kx, j) * this.trustRegionCenterOffset.getEntry(j);
                              }

                              work2.setEntry(kx, work3.getEntry(kx));
                              work3.setEntry(kx, sum * work3.getEntry(kx));
                           }

                           double gqsq = 0.0;
                           double gisq = 0.0;

                           for (int i = 0; i < n; i++) {
                              double sum = 0.0;

                              for (int kx = 0; kx < npt; kx++) {
                                 sum += this.bMatrix.getEntry(kx, i) * this.lagrangeValuesAtNewPoint.getEntry(kx)
                                    + this.interpolationPoints.getEntry(kx, i) * work3.getEntry(kx);
                              }

                              if (this.trustRegionCenterOffset.getEntry(i) == this.lowerDifference.getEntry(i)) {
                                 double d1x = FastMath.min(0.0, this.gradientAtTrustRegionCenter.getEntry(i));
                                 gqsq += d1x * d1x;
                                 double d2x = FastMath.min(0.0, sum);
                                 gisq += d2x * d2x;
                              } else if (this.trustRegionCenterOffset.getEntry(i) == this.upperDifference.getEntry(i)) {
                                 double d1x = FastMath.max(0.0, this.gradientAtTrustRegionCenter.getEntry(i));
                                 gqsq += d1x * d1x;
                                 double d2x = FastMath.max(0.0, sum);
                                 gisq += d2x * d2x;
                              } else {
                                 double d1x = this.gradientAtTrustRegionCenter.getEntry(i);
                                 gqsq += d1x * d1x;
                                 gisq += sum * sum;
                              }

                              this.lagrangeValuesAtNewPoint.setEntry(npt + i, sum);
                           }

                           itest++;
                           if (gqsq < 10.0 * gisq) {
                              itest = 0;
                           }

                           if (itest >= 3) {
                              int i = 0;

                              for (int max = FastMath.max(npt, nh); i < max; i++) {
                                 if (i < n) {
                                    this.gradientAtTrustRegionCenter.setEntry(i, this.lagrangeValuesAtNewPoint.getEntry(npt + i));
                                 }

                                 if (i < npt) {
                                    this.modelSecondDerivativesParameters.setEntry(i, work2.getEntry(i));
                                 }

                                 if (i < nh) {
                                    this.modelSecondDerivativesValues.setEntry(i, 0.0);
                                 }

                                 itest = 0;
                              }
                           }
                        }

                        if (ntrits == 0) {
                           state = 60;
                        } else {
                           if (!(f <= fopt + 0.1 * vquad)) {
                              double d1x = 2.0 * delta;
                              double d2x = 10.0 * rho;
                              distsq = FastMath.max(d1x * d1x, d2x * d2x);
                              break;
                           }

                           state = 60;
                        }
                     }
                  }

                  printState(650);
                  knew = -1;

                  for (int kx = 0; kx < npt; kx++) {
                     double sum = 0.0;

                     for (int j = 0; j < n; j++) {
                        double d1x = this.interpolationPoints.getEntry(kx, j) - this.trustRegionCenterOffset.getEntry(j);
                        sum += d1x * d1x;
                     }

                     if (sum > distsq) {
                        knew = kx;
                        distsq = sum;
                     }
                  }

                  if (knew >= 0) {
                     double dist = FastMath.sqrt(distsq);
                     if (ntrits == -1) {
                        delta = FastMath.min(0.1 * delta, 0.5 * dist);
                        if (delta <= rho * 1.5) {
                           delta = rho;
                        }
                     }

                     ntrits = 0;
                     double d1x = FastMath.min(0.1 * dist, delta);
                     adelt = FastMath.max(d1x, rho);
                     dsq = adelt * adelt;
                     state = 90;
                  } else if (ntrits == -1) {
                     state = 680;
                  } else if (ratio > 0.0) {
                     state = 60;
                  } else {
                     if (FastMath.max(delta, dnorm) > rho) {
                        state = 60;
                        continue;
                     }
                     break label862;
                  }
               }

               printState(60);
               ArrayRealVector gnew = new ArrayRealVector(n);
               ArrayRealVector xbdi = new ArrayRealVector(n);
               ArrayRealVector s = new ArrayRealVector(n);
               ArrayRealVector hs = new ArrayRealVector(n);
               ArrayRealVector hred = new ArrayRealVector(n);
               double[] dsqCrvmin = this.trsbox(delta, gnew, xbdi, s, hs, hred);
               dsq = dsqCrvmin[0];
               crvmin = dsqCrvmin[1];
               double deltaTwo = FastMath.sqrt(dsq);
               dnorm = FastMath.min(delta, deltaTwo);
               if (dnorm < 0.5 * rho) {
                  ntrits = -1;
                  double var156 = 10.0 * rho;
                  distsq = var156 * var156;
                  if (this.getEvaluations() <= nfsav + 2) {
                     state = 650;
                     continue;
                  }

                  var156 = FastMath.max(diffa, diffb);
                  double errbig = FastMath.max(var156, diffc);
                  double frhosq = rho * 0.125 * rho;
                  if (crvmin > 0.0 && errbig > frhosq * crvmin) {
                     state = 650;
                     continue;
                  }

                  double bdtol = errbig / rho;

                  for (int j = 0; j < n; state = 680) {
                     double bdtest = bdtol;
                     if (this.newPoint.getEntry(j) == this.lowerDifference.getEntry(j)) {
                        bdtest = work1.getEntry(j);
                     }

                     if (this.newPoint.getEntry(j) == this.upperDifference.getEntry(j)) {
                        bdtest = -work1.getEntry(j);
                     }

                     if (bdtest < bdtol) {
                        double curv = this.modelSecondDerivativesValues.getEntry((j + j * j) / 2);

                        for (int kx = 0; kx < npt; kx++) {
                           double d1x = this.interpolationPoints.getEntry(kx, j);
                           curv += this.modelSecondDerivativesParameters.getEntry(kx) * d1x * d1x;
                        }

                        bdtest += 0.5 * curv * rho;
                        if (bdtest < bdtol) {
                           int var94 = 650;
                           continue;
                        }
                     }

                     j++;
                  }
               }

               ntrits++;
               break label864;
            }

            printState(680);
            if (rho > this.stoppingTrustRegionRadius) {
               delta = 0.5 * rho;
               ratio = rho / this.stoppingTrustRegionRadius;
               if (ratio <= 16.0) {
                  rho = this.stoppingTrustRegionRadius;
               } else if (ratio <= 250.0) {
                  rho = FastMath.sqrt(ratio) * this.stoppingTrustRegionRadius;
               } else {
                  rho *= 0.1;
               }

               delta = FastMath.max(delta, rho);
               ntrits = 0;
               nfsav = this.getEvaluations();
               state = 60;
            } else {
               if (ntrits == -1) {
                  state = 360;
                  continue;
               }
               break label866;
            }
         }

         printState(90);
         if (dsq <= xoptsq * 0.001) {
            double fracsq = xoptsq * 0.25;
            double sumpq = 0.0;

            for (int kx = 0; kx < npt; kx++) {
               sumpq += this.modelSecondDerivativesParameters.getEntry(kx);
               double sum = -0.5 * xoptsq;

               for (int i = 0; i < n; i++) {
                  sum += this.interpolationPoints.getEntry(kx, i) * this.trustRegionCenterOffset.getEntry(i);
               }

               work2.setEntry(kx, sum);
               double temp = fracsq - 0.5 * sum;

               for (int i = 0; i < n; i++) {
                  work1.setEntry(i, this.bMatrix.getEntry(kx, i));
                  this.lagrangeValuesAtNewPoint.setEntry(i, sum * this.interpolationPoints.getEntry(kx, i) + temp * this.trustRegionCenterOffset.getEntry(i));
                  int ip = npt + i;

                  for (int j = 0; j <= i; j++) {
                     this.bMatrix
                        .setEntry(
                           ip,
                           j,
                           this.bMatrix.getEntry(ip, j)
                              + work1.getEntry(i) * this.lagrangeValuesAtNewPoint.getEntry(j)
                              + this.lagrangeValuesAtNewPoint.getEntry(i) * work1.getEntry(j)
                        );
                  }
               }
            }

            for (int m = 0; m < nptm; m++) {
               double sumz = 0.0;
               double sumw = 0.0;

               for (int kx = 0; kx < npt; kx++) {
                  sumz += this.zMatrix.getEntry(kx, m);
                  this.lagrangeValuesAtNewPoint.setEntry(kx, work2.getEntry(kx) * this.zMatrix.getEntry(kx, m));
                  sumw += this.lagrangeValuesAtNewPoint.getEntry(kx);
               }

               for (int j = 0; j < n; j++) {
                  double sum = (fracsq * sumz - 0.5 * sumw) * this.trustRegionCenterOffset.getEntry(j);

                  for (int kx = 0; kx < npt; kx++) {
                     sum += this.lagrangeValuesAtNewPoint.getEntry(kx) * this.interpolationPoints.getEntry(kx, j);
                  }

                  work1.setEntry(j, sum);

                  for (int kx = 0; kx < npt; kx++) {
                     this.bMatrix.setEntry(kx, j, this.bMatrix.getEntry(kx, j) + sum * this.zMatrix.getEntry(kx, m));
                  }
               }

               for (int i = 0; i < n; i++) {
                  int ip = i + npt;
                  double temp = work1.getEntry(i);

                  for (int j = 0; j <= i; j++) {
                     this.bMatrix.setEntry(ip, j, this.bMatrix.getEntry(ip, j) + temp * work1.getEntry(j));
                  }
               }
            }

            int ih = 0;

            for (int j = 0; j < n; j++) {
               work1.setEntry(j, -0.5 * sumpq * this.trustRegionCenterOffset.getEntry(j));

               for (int kx = 0; kx < npt; kx++) {
                  work1.setEntry(j, work1.getEntry(j) + this.modelSecondDerivativesParameters.getEntry(kx) * this.interpolationPoints.getEntry(kx, j));
                  this.interpolationPoints.setEntry(kx, j, this.interpolationPoints.getEntry(kx, j) - this.trustRegionCenterOffset.getEntry(j));
               }

               for (int i = 0; i <= j; i++) {
                  this.modelSecondDerivativesValues
                     .setEntry(
                        ih,
                        this.modelSecondDerivativesValues.getEntry(ih)
                           + work1.getEntry(i) * this.trustRegionCenterOffset.getEntry(j)
                           + this.trustRegionCenterOffset.getEntry(i) * work1.getEntry(j)
                     );
                  this.bMatrix.setEntry(npt + i, j, this.bMatrix.getEntry(npt + j, i));
                  ih++;
               }
            }

            for (int i = 0; i < n; i++) {
               this.originShift.setEntry(i, this.originShift.getEntry(i) + this.trustRegionCenterOffset.getEntry(i));
               this.newPoint.setEntry(i, this.newPoint.getEntry(i) - this.trustRegionCenterOffset.getEntry(i));
               this.lowerDifference.setEntry(i, this.lowerDifference.getEntry(i) - this.trustRegionCenterOffset.getEntry(i));
               this.upperDifference.setEntry(i, this.upperDifference.getEntry(i) - this.trustRegionCenterOffset.getEntry(i));
               this.trustRegionCenterOffset.setEntry(i, 0.0);
            }

            xoptsq = 0.0;
         }

         if (ntrits == 0) {
            state = 210;
         } else {
            state = 230;
         }
      }

      printState(720);
      if (this.fAtInterpolationPoints.getEntry(this.trustRegionCenterInterpolationPointIndex) <= fsave) {
         for (int i = 0; i < n; i++) {
            double d3x = lowerBound[i];
            double d4x = this.originShift.getEntry(i) + this.trustRegionCenterOffset.getEntry(i);
            double d1x = FastMath.max(d3x, d4x);
            double d2x = upperBound[i];
            this.currentBest.setEntry(i, FastMath.min(d1x, d2x));
            if (this.trustRegionCenterOffset.getEntry(i) == this.lowerDifference.getEntry(i)) {
               this.currentBest.setEntry(i, lowerBound[i]);
            }

            if (this.trustRegionCenterOffset.getEntry(i) == this.upperDifference.getEntry(i)) {
               this.currentBest.setEntry(i, upperBound[i]);
            }
         }

         f = this.fAtInterpolationPoints.getEntry(this.trustRegionCenterInterpolationPointIndex);
      }

      return f;
   }

   private double[] altmov(int knew, double adelt) {
      printMethod();
      int n = this.currentBest.getDimension();
      int npt = this.numberOfInterpolationPoints;
      ArrayRealVector glag = new ArrayRealVector(n);
      ArrayRealVector hcol = new ArrayRealVector(npt);
      ArrayRealVector work1 = new ArrayRealVector(n);
      ArrayRealVector work2 = new ArrayRealVector(n);

      for (int k = 0; k < npt; k++) {
         hcol.setEntry(k, 0.0);
      }

      int j = 0;

      for (int max = npt - n - 1; j < max; j++) {
         double tmp = this.zMatrix.getEntry(knew, j);

         for (int k = 0; k < npt; k++) {
            hcol.setEntry(k, hcol.getEntry(k) + tmp * this.zMatrix.getEntry(k, j));
         }
      }

      double alpha = hcol.getEntry(knew);
      double ha = 0.5 * alpha;

      for (int i = 0; i < n; i++) {
         glag.setEntry(i, this.bMatrix.getEntry(knew, i));
      }

      for (int k = 0; k < npt; k++) {
         double tmp = 0.0;

         for (int jx = 0; jx < n; jx++) {
            tmp += this.interpolationPoints.getEntry(k, jx) * this.trustRegionCenterOffset.getEntry(jx);
         }

         tmp *= hcol.getEntry(k);

         for (int i = 0; i < n; i++) {
            glag.setEntry(i, glag.getEntry(i) + tmp * this.interpolationPoints.getEntry(k, i));
         }
      }

      double presav = 0.0;
      double step = Double.NaN;
      int ksav = 0;
      int ibdsav = 0;
      double stpsav = 0.0;

      for (int k = 0; k < npt; k++) {
         if (k != this.trustRegionCenterInterpolationPointIndex) {
            double dderiv = 0.0;
            double distsq = 0.0;

            for (int i = 0; i < n; i++) {
               double tmp = this.interpolationPoints.getEntry(k, i) - this.trustRegionCenterOffset.getEntry(i);
               dderiv += glag.getEntry(i) * tmp;
               distsq += tmp * tmp;
            }

            double subd = adelt / FastMath.sqrt(distsq);
            double slbd = -subd;
            int ilbd = 0;
            int iubd = 0;
            double sumin = FastMath.min(1.0, subd);

            for (int i = 0; i < n; i++) {
               double tmp = this.interpolationPoints.getEntry(k, i) - this.trustRegionCenterOffset.getEntry(i);
               if (tmp > 0.0) {
                  if (slbd * tmp < this.lowerDifference.getEntry(i) - this.trustRegionCenterOffset.getEntry(i)) {
                     slbd = (this.lowerDifference.getEntry(i) - this.trustRegionCenterOffset.getEntry(i)) / tmp;
                     ilbd = -i - 1;
                  }

                  if (subd * tmp > this.upperDifference.getEntry(i) - this.trustRegionCenterOffset.getEntry(i)) {
                     subd = FastMath.max(sumin, (this.upperDifference.getEntry(i) - this.trustRegionCenterOffset.getEntry(i)) / tmp);
                     iubd = i + 1;
                  }
               } else if (tmp < 0.0) {
                  if (slbd * tmp > this.upperDifference.getEntry(i) - this.trustRegionCenterOffset.getEntry(i)) {
                     slbd = (this.upperDifference.getEntry(i) - this.trustRegionCenterOffset.getEntry(i)) / tmp;
                     ilbd = i + 1;
                  }

                  if (subd * tmp < this.lowerDifference.getEntry(i) - this.trustRegionCenterOffset.getEntry(i)) {
                     subd = FastMath.max(sumin, (this.lowerDifference.getEntry(i) - this.trustRegionCenterOffset.getEntry(i)) / tmp);
                     iubd = -i - 1;
                  }
               }
            }

            step = slbd;
            int isbd = ilbd;
            double vlag = Double.NaN;
            if (k == knew) {
               double diff = dderiv - 1.0;
               vlag = slbd * (dderiv - slbd * diff);
               double d1 = subd * (dderiv - subd * diff);
               if (FastMath.abs(d1) > FastMath.abs(vlag)) {
                  step = subd;
                  vlag = d1;
                  isbd = iubd;
               }

               double d2 = 0.5 * dderiv;
               double d3 = d2 - diff * slbd;
               double d4 = d2 - diff * subd;
               if (d3 * d4 < 0.0) {
                  double d5 = d2 * d2 / diff;
                  if (FastMath.abs(d5) > FastMath.abs(vlag)) {
                     step = d2 / diff;
                     vlag = d5;
                     isbd = 0;
                  }
               }
            } else {
               vlag = slbd * (1.0 - slbd);
               double tmp = subd * (1.0 - subd);
               if (FastMath.abs(tmp) > FastMath.abs(vlag)) {
                  step = subd;
                  vlag = tmp;
                  isbd = iubd;
               }

               if (subd > 0.5 && FastMath.abs(vlag) < 0.25) {
                  step = 0.5;
                  vlag = 0.25;
                  isbd = 0;
               }

               vlag *= dderiv;
            }

            double tmpx = step * (1.0 - step) * distsq;
            double predsq = vlag * vlag * (vlag * vlag + ha * tmpx * tmpx);
            if (predsq > presav) {
               presav = predsq;
               ksav = k;
               stpsav = step;
               ibdsav = isbd;
            }
         }
      }

      for (int ix = 0; ix < n; ix++) {
         double tmpx = this.trustRegionCenterOffset.getEntry(ix)
            + stpsav * (this.interpolationPoints.getEntry(ksav, ix) - this.trustRegionCenterOffset.getEntry(ix));
         this.newPoint.setEntry(ix, FastMath.max(this.lowerDifference.getEntry(ix), FastMath.min(this.upperDifference.getEntry(ix), tmpx)));
      }

      if (ibdsav < 0) {
         this.newPoint.setEntry(-ibdsav - 1, this.lowerDifference.getEntry(-ibdsav - 1));
      }

      if (ibdsav > 0) {
         this.newPoint.setEntry(ibdsav - 1, this.upperDifference.getEntry(ibdsav - 1));
      }

      double bigstp = adelt + adelt;
      int iflag = 0;
      double cauchy = Double.NaN;
      double csave = 0.0;

      while (true) {
         double wfixsq = 0.0;
         double ggfree = 0.0;

         for (int ix = 0; ix < n; ix++) {
            double glagValue = glag.getEntry(ix);
            work1.setEntry(ix, 0.0);
            if (FastMath.min(this.trustRegionCenterOffset.getEntry(ix) - this.lowerDifference.getEntry(ix), glagValue) > 0.0
               || FastMath.max(this.trustRegionCenterOffset.getEntry(ix) - this.upperDifference.getEntry(ix), glagValue) < 0.0) {
               work1.setEntry(ix, bigstp);
               ggfree += glagValue * glagValue;
            }
         }

         if (ggfree == 0.0) {
            return new double[]{alpha, 0.0};
         }

         double tmp1 = adelt * adelt - wfixsq;
         if (tmp1 > 0.0) {
            step = FastMath.sqrt(tmp1 / ggfree);
            ggfree = 0.0;

            for (int ixx = 0; ixx < n; ixx++) {
               if (work1.getEntry(ixx) == bigstp) {
                  double tmp2 = this.trustRegionCenterOffset.getEntry(ixx) - step * glag.getEntry(ixx);
                  if (tmp2 <= this.lowerDifference.getEntry(ixx)) {
                     work1.setEntry(ixx, this.lowerDifference.getEntry(ixx) - this.trustRegionCenterOffset.getEntry(ixx));
                     double d1x = work1.getEntry(ixx);
                     wfixsq += d1x * d1x;
                  } else if (tmp2 >= this.upperDifference.getEntry(ixx)) {
                     work1.setEntry(ixx, this.upperDifference.getEntry(ixx) - this.trustRegionCenterOffset.getEntry(ixx));
                     double d1x = work1.getEntry(ixx);
                     wfixsq += d1x * d1x;
                  } else {
                     double d1x = glag.getEntry(ixx);
                     ggfree += d1x * d1x;
                  }
               }
            }
         }

         double gw = 0.0;

         for (int ixxx = 0; ixxx < n; ixxx++) {
            double glagValue = glag.getEntry(ixxx);
            if (work1.getEntry(ixxx) == bigstp) {
               work1.setEntry(ixxx, -step * glagValue);
               double min = FastMath.min(this.upperDifference.getEntry(ixxx), this.trustRegionCenterOffset.getEntry(ixxx) + work1.getEntry(ixxx));
               this.alternativeNewPoint.setEntry(ixxx, FastMath.max(this.lowerDifference.getEntry(ixxx), min));
            } else if (work1.getEntry(ixxx) == 0.0) {
               this.alternativeNewPoint.setEntry(ixxx, this.trustRegionCenterOffset.getEntry(ixxx));
            } else if (glagValue > 0.0) {
               this.alternativeNewPoint.setEntry(ixxx, this.lowerDifference.getEntry(ixxx));
            } else {
               this.alternativeNewPoint.setEntry(ixxx, this.upperDifference.getEntry(ixxx));
            }

            gw += glagValue * work1.getEntry(ixxx);
         }

         double curv = 0.0;

         for (int kx = 0; kx < npt; kx++) {
            double tmpx = 0.0;

            for (int jx = 0; jx < n; jx++) {
               tmpx += this.interpolationPoints.getEntry(kx, jx) * work1.getEntry(jx);
            }

            curv += hcol.getEntry(kx) * tmpx * tmpx;
         }

         if (iflag == 1) {
            curv = -curv;
         }

         if (curv > -gw && curv < -gw * (1.0 + FastMath.sqrt(2.0))) {
            double scale = -gw / curv;

            for (int ixxx = 0; ixxx < n; ixxx++) {
               double tmpx = this.trustRegionCenterOffset.getEntry(ixxx) + scale * work1.getEntry(ixxx);
               this.alternativeNewPoint
                  .setEntry(ixxx, FastMath.max(this.lowerDifference.getEntry(ixxx), FastMath.min(this.upperDifference.getEntry(ixxx), tmpx)));
            }

            double d1x = 0.5 * gw * scale;
            cauchy = d1x * d1x;
         } else {
            double d1x = gw + 0.5 * curv;
            cauchy = d1x * d1x;
         }

         if (iflag != 0) {
            if (csave > cauchy) {
               for (int ixxx = 0; ixxx < n; ixxx++) {
                  this.alternativeNewPoint.setEntry(ixxx, work2.getEntry(ixxx));
               }

               cauchy = csave;
            }

            return new double[]{alpha, cauchy};
         }

         for (int ixxx = 0; ixxx < n; ixxx++) {
            glag.setEntry(ixxx, -glag.getEntry(ixxx));
            work2.setEntry(ixxx, this.alternativeNewPoint.getEntry(ixxx));
         }

         csave = cauchy;
         iflag = 1;
      }
   }

   private void prelim(double[] lowerBound, double[] upperBound) {
      printMethod();
      int n = this.currentBest.getDimension();
      int npt = this.numberOfInterpolationPoints;
      int ndim = this.bMatrix.getRowDimension();
      double rhosq = this.initialTrustRegionRadius * this.initialTrustRegionRadius;
      double recip = 1.0 / rhosq;
      int np = n + 1;

      for (int j = 0; j < n; j++) {
         this.originShift.setEntry(j, this.currentBest.getEntry(j));

         for (int k = 0; k < npt; k++) {
            this.interpolationPoints.setEntry(k, j, 0.0);
         }

         for (int i = 0; i < ndim; i++) {
            this.bMatrix.setEntry(i, j, 0.0);
         }
      }

      int i = 0;

      for (int max = n * np / 2; i < max; i++) {
         this.modelSecondDerivativesValues.setEntry(i, 0.0);
      }

      for (int k = 0; k < npt; k++) {
         this.modelSecondDerivativesParameters.setEntry(k, 0.0);
         int j = 0;

         for (int max = npt - np; j < max; j++) {
            this.zMatrix.setEntry(k, j, 0.0);
         }
      }

      i = 0;
      int jpt = 0;
      double fbeg = Double.NaN;

      do {
         int nfm = this.getEvaluations();
         int nfx = nfm - n;
         int nfmm = nfm - 1;
         int nfxm = nfx - 1;
         double stepa = 0.0;
         double stepb = 0.0;
         if (nfm <= 2 * n) {
            if (nfm >= 1 && nfm <= n) {
               stepa = this.initialTrustRegionRadius;
               if (this.upperDifference.getEntry(nfmm) == 0.0) {
                  stepa = -stepa;
               }

               this.interpolationPoints.setEntry(nfm, nfmm, stepa);
            } else if (nfm > n) {
               stepa = this.interpolationPoints.getEntry(nfx, nfxm);
               stepb = -this.initialTrustRegionRadius;
               if (this.lowerDifference.getEntry(nfxm) == 0.0) {
                  stepb = FastMath.min(2.0 * this.initialTrustRegionRadius, this.upperDifference.getEntry(nfxm));
               }

               if (this.upperDifference.getEntry(nfxm) == 0.0) {
                  stepb = FastMath.max(-2.0 * this.initialTrustRegionRadius, this.lowerDifference.getEntry(nfxm));
               }

               this.interpolationPoints.setEntry(nfm, nfxm, stepb);
            }
         } else {
            int tmp1 = (nfm - np) / n;
            jpt = nfm - tmp1 * n - n;
            i = jpt + tmp1;
            if (i > n) {
               int tmp2 = jpt;
               jpt = i - n;
               i = tmp2;
            }

            int iptMinus1 = i - 1;
            int jptMinus1 = jpt - 1;
            this.interpolationPoints.setEntry(nfm, iptMinus1, this.interpolationPoints.getEntry(i, iptMinus1));
            this.interpolationPoints.setEntry(nfm, jptMinus1, this.interpolationPoints.getEntry(jpt, jptMinus1));
         }

         for (int j = 0; j < n; j++) {
            this.currentBest
               .setEntry(j, FastMath.min(FastMath.max(lowerBound[j], this.originShift.getEntry(j) + this.interpolationPoints.getEntry(nfm, j)), upperBound[j]));
            if (this.interpolationPoints.getEntry(nfm, j) == this.lowerDifference.getEntry(j)) {
               this.currentBest.setEntry(j, lowerBound[j]);
            }

            if (this.interpolationPoints.getEntry(nfm, j) == this.upperDifference.getEntry(j)) {
               this.currentBest.setEntry(j, upperBound[j]);
            }
         }

         double objectiveValue = this.computeObjectiveValue(this.currentBest.toArray());
         double f = this.isMinimize ? objectiveValue : -objectiveValue;
         int numEval = this.getEvaluations();
         this.fAtInterpolationPoints.setEntry(nfm, f);
         if (numEval == 1) {
            fbeg = f;
            this.trustRegionCenterInterpolationPointIndex = 0;
         } else if (f < this.fAtInterpolationPoints.getEntry(this.trustRegionCenterInterpolationPointIndex)) {
            this.trustRegionCenterInterpolationPointIndex = nfm;
         }

         if (numEval <= 2 * n + 1) {
            if (numEval >= 2 && numEval <= n + 1) {
               this.gradientAtTrustRegionCenter.setEntry(nfmm, (f - fbeg) / stepa);
               if (npt < numEval + n) {
                  double oneOverStepA = 1.0 / stepa;
                  this.bMatrix.setEntry(0, nfmm, -oneOverStepA);
                  this.bMatrix.setEntry(nfm, nfmm, oneOverStepA);
                  this.bMatrix.setEntry(npt + nfmm, nfmm, -0.5 * rhosq);
               }
            } else if (numEval >= n + 2) {
               int ih = nfx * (nfx + 1) / 2 - 1;
               double tmp = (f - fbeg) / stepb;
               double diff = stepb - stepa;
               this.modelSecondDerivativesValues.setEntry(ih, 2.0 * (tmp - this.gradientAtTrustRegionCenter.getEntry(nfxm)) / diff);
               this.gradientAtTrustRegionCenter.setEntry(nfxm, (this.gradientAtTrustRegionCenter.getEntry(nfxm) * stepb - tmp * stepa) / diff);
               if (stepa * stepb < 0.0 && f < this.fAtInterpolationPoints.getEntry(nfm - n)) {
                  this.fAtInterpolationPoints.setEntry(nfm, this.fAtInterpolationPoints.getEntry(nfm - n));
                  this.fAtInterpolationPoints.setEntry(nfm - n, f);
                  if (this.trustRegionCenterInterpolationPointIndex == nfm) {
                     this.trustRegionCenterInterpolationPointIndex = nfm - n;
                  }

                  this.interpolationPoints.setEntry(nfm - n, nfxm, stepb);
                  this.interpolationPoints.setEntry(nfm, nfxm, stepa);
               }

               this.bMatrix.setEntry(0, nfxm, -(stepa + stepb) / (stepa * stepb));
               this.bMatrix.setEntry(nfm, nfxm, -0.5 / this.interpolationPoints.getEntry(nfm - n, nfxm));
               this.bMatrix.setEntry(nfm - n, nfxm, -this.bMatrix.getEntry(0, nfxm) - this.bMatrix.getEntry(nfm, nfxm));
               this.zMatrix.setEntry(0, nfxm, FastMath.sqrt(2.0) / (stepa * stepb));
               this.zMatrix.setEntry(nfm, nfxm, FastMath.sqrt(0.5) / rhosq);
               this.zMatrix.setEntry(nfm - n, nfxm, -this.zMatrix.getEntry(0, nfxm) - this.zMatrix.getEntry(nfm, nfxm));
            }
         } else {
            this.zMatrix.setEntry(0, nfxm, recip);
            this.zMatrix.setEntry(nfm, nfxm, recip);
            this.zMatrix.setEntry(i, nfxm, -recip);
            this.zMatrix.setEntry(jpt, nfxm, -recip);
            int ih = i * (i - 1) / 2 + jpt - 1;
            double tmp = this.interpolationPoints.getEntry(nfm, i - 1) * this.interpolationPoints.getEntry(nfm, jpt - 1);
            this.modelSecondDerivativesValues
               .setEntry(ih, (fbeg - this.fAtInterpolationPoints.getEntry(i) - this.fAtInterpolationPoints.getEntry(jpt) + f) / tmp);
         }
      } while (this.getEvaluations() < npt);
   }

   private double[] trsbox(double delta, ArrayRealVector gnew, ArrayRealVector xbdi, ArrayRealVector s, ArrayRealVector hs, ArrayRealVector hred) {
      printMethod();
      int n = this.currentBest.getDimension();
      int npt = this.numberOfInterpolationPoints;
      double dsq = Double.NaN;
      double crvmin = Double.NaN;
      double beta = 0.0;
      int iact = -1;
      int nact = 0;
      double angt = 0.0;
      double temp = 0.0;
      double xsav = 0.0;
      double xsum = 0.0;
      double angbd = 0.0;
      double dredg = 0.0;
      double sredg = 0.0;
      double resid = 0.0;
      double delsq = 0.0;
      double ggsav = 0.0;
      double tempa = 0.0;
      double tempb = 0.0;
      double redmax = 0.0;
      double dredsq = 0.0;
      double redsav = 0.0;
      double gredsq = 0.0;
      double rednew = 0.0;
      int itcsav = 0;
      double rdprev = 0.0;
      double rdnext = 0.0;
      double stplen = 0.0;
      double stepsq = 0.0;
      int itermax = 0;
      int iterc = 0;
      nact = 0;

      for (int i = 0; i < n; i++) {
         xbdi.setEntry(i, 0.0);
         if (this.trustRegionCenterOffset.getEntry(i) <= this.lowerDifference.getEntry(i)) {
            if (this.gradientAtTrustRegionCenter.getEntry(i) >= 0.0) {
               xbdi.setEntry(i, -1.0);
            }
         } else if (this.trustRegionCenterOffset.getEntry(i) >= this.upperDifference.getEntry(i) && this.gradientAtTrustRegionCenter.getEntry(i) <= 0.0) {
            xbdi.setEntry(i, 1.0);
         }

         if (xbdi.getEntry(i) != 0.0) {
            nact++;
         }

         this.trialStepPoint.setEntry(i, 0.0);
         gnew.setEntry(i, this.gradientAtTrustRegionCenter.getEntry(i));
      }

      delsq = delta * delta;
      double qred = 0.0;
      crvmin = -1.0;
      int state = 20;

      label372:
      while (true) {
         label370:
         while (true) {
            label366:
            while (true) {
               switch (state) {
                  case 20:
                     printState(20);
                     beta = 0.0;
                  case 30:
                     break;
                  case 50:
                     printState(50);
                     resid = delsq;
                     double ds = 0.0;
                     double shs = 0.0;

                     for (int ix = 0; ix < n; ix++) {
                        if (xbdi.getEntry(ix) == 0.0) {
                           double d1 = this.trialStepPoint.getEntry(ix);
                           resid -= d1 * d1;
                           ds += s.getEntry(ix) * this.trialStepPoint.getEntry(ix);
                           shs += s.getEntry(ix) * hs.getEntry(ix);
                        }
                     }

                     if (resid <= 0.0) {
                        state = 90;
                        continue;
                     } else {
                        temp = FastMath.sqrt(stepsq * resid + ds * ds);
                        double blen;
                        if (ds < 0.0) {
                           blen = (temp - ds) / stepsq;
                        } else {
                           blen = resid / (temp + ds);
                        }

                        stplen = blen;
                        if (shs > 0.0) {
                           stplen = FastMath.min(blen, gredsq / shs);
                        }

                        iact = -1;

                        for (int ixx = 0; ixx < n; ixx++) {
                           if (s.getEntry(ixx) != 0.0) {
                              xsum = this.trustRegionCenterOffset.getEntry(ixx) + this.trialStepPoint.getEntry(ixx);
                              if (s.getEntry(ixx) > 0.0) {
                                 temp = (this.upperDifference.getEntry(ixx) - xsum) / s.getEntry(ixx);
                              } else {
                                 temp = (this.lowerDifference.getEntry(ixx) - xsum) / s.getEntry(ixx);
                              }

                              if (temp < stplen) {
                                 stplen = temp;
                                 iact = ixx;
                              }
                           }
                        }

                        double sdec = 0.0;
                        if (stplen > 0.0) {
                           iterc++;
                           temp = shs / stepsq;
                           if (iact == -1 && temp > 0.0) {
                              crvmin = FastMath.min(crvmin, temp);
                              if (crvmin == -1.0) {
                                 crvmin = temp;
                              }
                           }

                           ggsav = gredsq;
                           gredsq = 0.0;

                           for (int ixxx = 0; ixxx < n; ixxx++) {
                              gnew.setEntry(ixxx, gnew.getEntry(ixxx) + stplen * hs.getEntry(ixxx));
                              if (xbdi.getEntry(ixxx) == 0.0) {
                                 double d1 = gnew.getEntry(ixxx);
                                 gredsq += d1 * d1;
                              }

                              this.trialStepPoint.setEntry(ixxx, this.trialStepPoint.getEntry(ixxx) + stplen * s.getEntry(ixxx));
                           }

                           double d1 = stplen * (ggsav - 0.5 * stplen * shs);
                           sdec = FastMath.max(d1, 0.0);
                           qred += sdec;
                        }

                        if (iact >= 0) {
                           nact++;
                           xbdi.setEntry(iact, 1.0);
                           if (s.getEntry(iact) < 0.0) {
                              xbdi.setEntry(iact, -1.0);
                           }

                           double d1 = this.trialStepPoint.getEntry(iact);
                           delsq -= d1 * d1;
                           if (delsq <= 0.0) {
                              state = 190;
                           } else {
                              state = 20;
                           }
                           continue;
                        } else if (stplen < blen) {
                           if (iterc == itermax) {
                              state = 190;
                           } else if (sdec <= qred * 0.01) {
                              state = 190;
                           } else {
                              beta = gredsq / ggsav;
                              state = 30;
                           }
                           continue;
                        }
                     }
                  case 90:
                     printState(90);
                     crvmin = 0.0;
                  case 100:
                     break label366;
                  case 120:
                     printState(120);
                     iterc++;
                     temp = gredsq * dredsq - dredg * dredg;
                     if (temp <= qred * 1.0E-4 * qred) {
                        state = 190;
                        continue;
                     } else {
                        temp = FastMath.sqrt(temp);

                        for (int ixxx = 0; ixxx < n; ixxx++) {
                           if (xbdi.getEntry(ixxx) == 0.0) {
                              s.setEntry(ixxx, (dredg * this.trialStepPoint.getEntry(ixxx) - dredsq * gnew.getEntry(ixxx)) / temp);
                           } else {
                              s.setEntry(ixxx, 0.0);
                           }
                        }

                        sredg = -temp;
                        angbd = 1.0;
                        iact = -1;
                        int ixxxx = 0;

                        while (true) {
                           if (ixxxx >= n) {
                              break label370;
                           }

                           label308: {
                              if (xbdi.getEntry(ixxxx) == 0.0) {
                                 tempa = this.trustRegionCenterOffset.getEntry(ixxxx)
                                    + this.trialStepPoint.getEntry(ixxxx)
                                    - this.lowerDifference.getEntry(ixxxx);
                                 tempb = this.upperDifference.getEntry(ixxxx)
                                    - this.trustRegionCenterOffset.getEntry(ixxxx)
                                    - this.trialStepPoint.getEntry(ixxxx);
                                 if (tempa <= 0.0) {
                                    nact++;
                                    xbdi.setEntry(ixxxx, -1.0);
                                    int var120 = 100;
                                    break label308;
                                 }

                                 if (tempb <= 0.0) {
                                    nact++;
                                    xbdi.setEntry(ixxxx, 1.0);
                                    int var119 = 100;
                                    break label308;
                                 }

                                 double d1 = this.trialStepPoint.getEntry(ixxxx);
                                 double d2 = s.getEntry(ixxxx);
                                 double ssq = d1 * d1 + d2 * d2;
                                 d1 = this.trustRegionCenterOffset.getEntry(ixxxx) - this.lowerDifference.getEntry(ixxxx);
                                 temp = ssq - d1 * d1;
                                 if (temp > 0.0) {
                                    temp = FastMath.sqrt(temp) - s.getEntry(ixxxx);
                                    if (angbd * temp > tempa) {
                                       angbd = tempa / temp;
                                       xsav = -1.0;
                                    }
                                 }

                                 d1 = this.upperDifference.getEntry(ixxxx) - this.trustRegionCenterOffset.getEntry(ixxxx);
                                 temp = ssq - d1 * d1;
                                 if (temp > 0.0) {
                                    temp = FastMath.sqrt(temp) + s.getEntry(ixxxx);
                                    if (angbd * temp > tempb) {
                                       angbd = tempb / temp;
                                       xsav = 1.0;
                                    }
                                 }
                              }

                              ixxxx++;
                           }

                           state = 210;
                           continue label366;
                        }
                     }
                  case 150:
                     printState(150);
                     double shs = 0.0;
                     double dhs = 0.0;
                     double dhd = 0.0;

                     for (int i = 0; i < n; i++) {
                        if (xbdi.getEntry(i) == 0.0) {
                           shs += s.getEntry(i) * hs.getEntry(i);
                           dhs += this.trialStepPoint.getEntry(i) * hs.getEntry(i);
                           dhd += this.trialStepPoint.getEntry(i) * hred.getEntry(i);
                        }
                     }

                     redmax = 0.0;
                     int isav = -1;
                     redsav = 0.0;
                     int iu = (int)(angbd * 17.0 + 3.1);

                     for (int ix = 0; ix < iu; ix++) {
                        angt = angbd * (double)ix / (double)iu;
                        double sth = (angt + angt) / (1.0 + angt * angt);
                        temp = shs + angt * (angt * dhd - dhs - dhs);
                        rednew = sth * (angt * dredg - sredg - 0.5 * sth * temp);
                        if (rednew > redmax) {
                           redmax = rednew;
                           isav = ix;
                           rdprev = redsav;
                        } else if (ix == isav + 1) {
                           rdnext = rednew;
                        }

                        redsav = rednew;
                     }

                     if (isav < 0) {
                        state = 190;
                        continue;
                     } else {
                        if (isav < iu) {
                           temp = (rdnext - rdprev) / (redmax + redmax - rdprev - rdnext);
                           angt = angbd * ((double)isav + 0.5 * temp) / (double)iu;
                        }

                        double cth = (1.0 - angt * angt) / (1.0 + angt * angt);
                        double sth = (angt + angt) / (1.0 + angt * angt);
                        temp = shs + angt * (angt * dhd - dhs - dhs);
                        double sdec = sth * (angt * dredg - sredg - 0.5 * sth * temp);
                        if (sdec <= 0.0) {
                           state = 190;
                           continue;
                        } else {
                           dredg = 0.0;
                           gredsq = 0.0;

                           for (int ix = 0; ix < n; ix++) {
                              gnew.setEntry(ix, gnew.getEntry(ix) + (cth - 1.0) * hred.getEntry(ix) + sth * hs.getEntry(ix));
                              if (xbdi.getEntry(ix) == 0.0) {
                                 this.trialStepPoint.setEntry(ix, cth * this.trialStepPoint.getEntry(ix) + sth * s.getEntry(ix));
                                 dredg += this.trialStepPoint.getEntry(ix) * gnew.getEntry(ix);
                                 double d1 = gnew.getEntry(ix);
                                 gredsq += d1 * d1;
                              }

                              hred.setEntry(ix, cth * hred.getEntry(ix) + sth * hs.getEntry(ix));
                           }

                           qred += sdec;
                           if (iact >= 0 && isav == iu) {
                              nact++;
                              xbdi.setEntry(iact, xsav);
                              state = 100;
                              continue;
                           } else if (sdec > qred * 0.01) {
                              state = 120;
                              continue;
                           }
                        }
                     }
                  case 190:
                     break label372;
                  case 210:
                     break label370;
                  default:
                     throw new MathIllegalStateException(LocalizedFormats.SIMPLE_MESSAGE, "trsbox");
               }

               printState(30);
               stepsq = 0.0;

               for (int ixxxx = 0; ixxxx < n; ixxxx++) {
                  if (xbdi.getEntry(ixxxx) != 0.0) {
                     s.setEntry(ixxxx, 0.0);
                  } else if (beta == 0.0) {
                     s.setEntry(ixxxx, -gnew.getEntry(ixxxx));
                  } else {
                     s.setEntry(ixxxx, beta * s.getEntry(ixxxx) - gnew.getEntry(ixxxx));
                  }

                  double d1x = s.getEntry(ixxxx);
                  stepsq += d1x * d1x;
               }

               if (stepsq == 0.0) {
                  state = 190;
               } else {
                  if (beta == 0.0) {
                     gredsq = stepsq;
                     itermax = iterc + n - nact;
                  }

                  if (gredsq * delsq <= qred * 1.0E-4 * qred) {
                     state = 190;
                  } else {
                     state = 210;
                  }
               }
            }

            printState(100);
            if (nact >= n - 1) {
               state = 190;
            } else {
               dredsq = 0.0;
               dredg = 0.0;
               gredsq = 0.0;

               for (int ixxxx = 0; ixxxx < n; ixxxx++) {
                  if (xbdi.getEntry(ixxxx) == 0.0) {
                     double d1x = this.trialStepPoint.getEntry(ixxxx);
                     dredsq += d1x * d1x;
                     dredg += this.trialStepPoint.getEntry(ixxxx) * gnew.getEntry(ixxxx);
                     d1x = gnew.getEntry(ixxxx);
                     gredsq += d1x * d1x;
                     s.setEntry(ixxxx, this.trialStepPoint.getEntry(ixxxx));
                  } else {
                     s.setEntry(ixxxx, 0.0);
                  }
               }

               itcsav = iterc;
               state = 210;
            }
         }

         printState(210);
         int ih = 0;

         for (int j = 0; j < n; j++) {
            hs.setEntry(j, 0.0);

            for (int ixxxxx = 0; ixxxxx <= j; ixxxxx++) {
               if (ixxxxx < j) {
                  hs.setEntry(j, hs.getEntry(j) + this.modelSecondDerivativesValues.getEntry(ih) * s.getEntry(ixxxxx));
               }

               hs.setEntry(ixxxxx, hs.getEntry(ixxxxx) + this.modelSecondDerivativesValues.getEntry(ih) * s.getEntry(j));
               ih++;
            }
         }

         RealVector tmp = this.interpolationPoints.operate(s).ebeMultiply(this.modelSecondDerivativesParameters);

         for (int k = 0; k < npt; k++) {
            if (this.modelSecondDerivativesParameters.getEntry(k) != 0.0) {
               for (int ixxxxx = 0; ixxxxx < n; ixxxxx++) {
                  hs.setEntry(ixxxxx, hs.getEntry(ixxxxx) + tmp.getEntry(k) * this.interpolationPoints.getEntry(k, ixxxxx));
               }
            }
         }

         if (crvmin != 0.0) {
            state = 50;
         } else if (iterc > itcsav) {
            state = 150;
         } else {
            for (int ixxxxx = 0; ixxxxx < n; ixxxxx++) {
               hred.setEntry(ixxxxx, hs.getEntry(ixxxxx));
            }

            state = 120;
         }
      }

      printState(190);
      dsq = 0.0;

      for (int ixxxxx = 0; ixxxxx < n; ixxxxx++) {
         double min = FastMath.min(this.trustRegionCenterOffset.getEntry(ixxxxx) + this.trialStepPoint.getEntry(ixxxxx), this.upperDifference.getEntry(ixxxxx));
         this.newPoint.setEntry(ixxxxx, FastMath.max(min, this.lowerDifference.getEntry(ixxxxx)));
         if (xbdi.getEntry(ixxxxx) == -1.0) {
            this.newPoint.setEntry(ixxxxx, this.lowerDifference.getEntry(ixxxxx));
         }

         if (xbdi.getEntry(ixxxxx) == 1.0) {
            this.newPoint.setEntry(ixxxxx, this.upperDifference.getEntry(ixxxxx));
         }

         this.trialStepPoint.setEntry(ixxxxx, this.newPoint.getEntry(ixxxxx) - this.trustRegionCenterOffset.getEntry(ixxxxx));
         double d1x = this.trialStepPoint.getEntry(ixxxxx);
         dsq += d1x * d1x;
      }

      return new double[]{dsq, crvmin};
   }

   private void update(double beta, double denom, int knew) {
      printMethod();
      int n = this.currentBest.getDimension();
      int npt = this.numberOfInterpolationPoints;
      int nptm = npt - n - 1;
      ArrayRealVector work = new ArrayRealVector(npt + n);
      double ztest = 0.0;

      for (int k = 0; k < npt; k++) {
         for (int j = 0; j < nptm; j++) {
            ztest = FastMath.max(ztest, FastMath.abs(this.zMatrix.getEntry(k, j)));
         }
      }

      ztest *= 1.0E-20;

      for (int j = 1; j < nptm; j++) {
         double d1 = this.zMatrix.getEntry(knew, j);
         if (FastMath.abs(d1) > ztest) {
            double d2 = this.zMatrix.getEntry(knew, 0);
            double d3 = this.zMatrix.getEntry(knew, j);
            double d4 = FastMath.sqrt(d2 * d2 + d3 * d3);
            double d5 = this.zMatrix.getEntry(knew, 0) / d4;
            double d6 = this.zMatrix.getEntry(knew, j) / d4;

            for (int i = 0; i < npt; i++) {
               double d7 = d5 * this.zMatrix.getEntry(i, 0) + d6 * this.zMatrix.getEntry(i, j);
               this.zMatrix.setEntry(i, j, d5 * this.zMatrix.getEntry(i, j) - d6 * this.zMatrix.getEntry(i, 0));
               this.zMatrix.setEntry(i, 0, d7);
            }
         }

         this.zMatrix.setEntry(knew, j, 0.0);
      }

      for (int i = 0; i < npt; i++) {
         work.setEntry(i, this.zMatrix.getEntry(knew, 0) * this.zMatrix.getEntry(i, 0));
      }

      double alpha = work.getEntry(knew);
      double tau = this.lagrangeValuesAtNewPoint.getEntry(knew);
      this.lagrangeValuesAtNewPoint.setEntry(knew, this.lagrangeValuesAtNewPoint.getEntry(knew) - 1.0);
      double sqrtDenom = FastMath.sqrt(denom);
      double d1 = tau / sqrtDenom;
      double d2 = this.zMatrix.getEntry(knew, 0) / sqrtDenom;

      for (int i = 0; i < npt; i++) {
         this.zMatrix.setEntry(i, 0, d1 * this.zMatrix.getEntry(i, 0) - d2 * this.lagrangeValuesAtNewPoint.getEntry(i));
      }

      for (int j = 0; j < n; j++) {
         int jp = npt + j;
         work.setEntry(jp, this.bMatrix.getEntry(knew, j));
         double d3 = (alpha * this.lagrangeValuesAtNewPoint.getEntry(jp) - tau * work.getEntry(jp)) / denom;
         double d4 = (-beta * work.getEntry(jp) - tau * this.lagrangeValuesAtNewPoint.getEntry(jp)) / denom;

         for (int i = 0; i <= jp; i++) {
            this.bMatrix.setEntry(i, j, this.bMatrix.getEntry(i, j) + d3 * this.lagrangeValuesAtNewPoint.getEntry(i) + d4 * work.getEntry(i));
            if (i >= npt) {
               this.bMatrix.setEntry(jp, i - npt, this.bMatrix.getEntry(i, j));
            }
         }
      }
   }

   private void setup(double[] lowerBound, double[] upperBound) {
      printMethod();
      double[] init = this.getStartPoint();
      int dimension = init.length;
      if (dimension < 2) {
         throw new NumberIsTooSmallException(dimension, 2, true);
      } else {
         int[] nPointsInterval = new int[]{dimension + 2, (dimension + 2) * (dimension + 1) / 2};
         if (this.numberOfInterpolationPoints >= nPointsInterval[0] && this.numberOfInterpolationPoints <= nPointsInterval[1]) {
            this.boundDifference = new double[dimension];
            double requiredMinDiff = 2.0 * this.initialTrustRegionRadius;
            double minDiff = Double.POSITIVE_INFINITY;

            for (int i = 0; i < dimension; i++) {
               this.boundDifference[i] = upperBound[i] - lowerBound[i];
               minDiff = FastMath.min(minDiff, this.boundDifference[i]);
            }

            if (minDiff < requiredMinDiff) {
               this.initialTrustRegionRadius = minDiff / 3.0;
            }

            this.bMatrix = new Array2DRowRealMatrix(dimension + this.numberOfInterpolationPoints, dimension);
            this.zMatrix = new Array2DRowRealMatrix(this.numberOfInterpolationPoints, this.numberOfInterpolationPoints - dimension - 1);
            this.interpolationPoints = new Array2DRowRealMatrix(this.numberOfInterpolationPoints, dimension);
            this.originShift = new ArrayRealVector(dimension);
            this.fAtInterpolationPoints = new ArrayRealVector(this.numberOfInterpolationPoints);
            this.trustRegionCenterOffset = new ArrayRealVector(dimension);
            this.gradientAtTrustRegionCenter = new ArrayRealVector(dimension);
            this.lowerDifference = new ArrayRealVector(dimension);
            this.upperDifference = new ArrayRealVector(dimension);
            this.modelSecondDerivativesParameters = new ArrayRealVector(this.numberOfInterpolationPoints);
            this.newPoint = new ArrayRealVector(dimension);
            this.alternativeNewPoint = new ArrayRealVector(dimension);
            this.trialStepPoint = new ArrayRealVector(dimension);
            this.lagrangeValuesAtNewPoint = new ArrayRealVector(dimension + this.numberOfInterpolationPoints);
            this.modelSecondDerivativesValues = new ArrayRealVector(dimension * (dimension + 1) / 2);
         } else {
            throw new OutOfRangeException(
               LocalizedFormats.NUMBER_OF_INTERPOLATION_POINTS, this.numberOfInterpolationPoints, nPointsInterval[0], nPointsInterval[1]
            );
         }
      }
   }

   private static String caller(int n) {
      Throwable t = new Throwable();
      StackTraceElement[] elements = t.getStackTrace();
      StackTraceElement e = elements[n];
      return e.getMethodName() + " (at line " + e.getLineNumber() + ")";
   }

   private static void printState(int s) {
   }

   private static void printMethod() {
   }

   private static class PathIsExploredException extends RuntimeException {
      private static final long serialVersionUID = 745350979634801853L;
      private static final String PATH_IS_EXPLORED = "If this exception is thrown, just remove it from the code";

      PathIsExploredException() {
         super("If this exception is thrown, just remove it from the code " + BOBYQAOptimizer.caller(3));
      }
   }
}
