package org.apache.commons.math3.optim.nonlinear.scalar.noderiv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

public class CMAESOptimizer extends MultivariateOptimizer {
   private int lambda;
   private final boolean isActiveCMA;
   private final int checkFeasableCount;
   private double[] inputSigma;
   private int dimension;
   private int diagonalOnly;
   private boolean isMinimize = true;
   private final boolean generateStatistics;
   private final int maxIterations;
   private final double stopFitness;
   private double stopTolUpX;
   private double stopTolX;
   private double stopTolFun;
   private double stopTolHistFun;
   private int mu;
   private double logMu2;
   private RealMatrix weights;
   private double mueff;
   private double sigma;
   private double cc;
   private double cs;
   private double damps;
   private double ccov1;
   private double ccovmu;
   private double chiN;
   private double ccov1Sep;
   private double ccovmuSep;
   private RealMatrix xmean;
   private RealMatrix pc;
   private RealMatrix ps;
   private double normps;
   private RealMatrix B;
   private RealMatrix D;
   private RealMatrix BD;
   private RealMatrix diagD;
   private RealMatrix C;
   private RealMatrix diagC;
   private int iterations;
   private double[] fitnessHistory;
   private int historySize;
   private final RandomGenerator random;
   private final List<Double> statisticsSigmaHistory = new ArrayList<>();
   private final List<RealMatrix> statisticsMeanHistory = new ArrayList<>();
   private final List<Double> statisticsFitnessHistory = new ArrayList<>();
   private final List<RealMatrix> statisticsDHistory = new ArrayList<>();

   public CMAESOptimizer(
      int maxIterations,
      double stopFitness,
      boolean isActiveCMA,
      int diagonalOnly,
      int checkFeasableCount,
      RandomGenerator random,
      boolean generateStatistics,
      ConvergenceChecker<PointValuePair> checker
   ) {
      super(checker);
      this.maxIterations = maxIterations;
      this.stopFitness = stopFitness;
      this.isActiveCMA = isActiveCMA;
      this.diagonalOnly = diagonalOnly;
      this.checkFeasableCount = checkFeasableCount;
      this.random = random;
      this.generateStatistics = generateStatistics;
   }

   public List<Double> getStatisticsSigmaHistory() {
      return this.statisticsSigmaHistory;
   }

   public List<RealMatrix> getStatisticsMeanHistory() {
      return this.statisticsMeanHistory;
   }

   public List<Double> getStatisticsFitnessHistory() {
      return this.statisticsFitnessHistory;
   }

   public List<RealMatrix> getStatisticsDHistory() {
      return this.statisticsDHistory;
   }

   @Override
   public PointValuePair optimize(OptimizationData... optData) throws TooManyEvaluationsException, DimensionMismatchException {
      return super.optimize(optData);
   }

   protected PointValuePair doOptimize() {
      this.isMinimize = this.getGoalType().equals(GoalType.MINIMIZE);
      CMAESOptimizer.FitnessFunction fitfun = new CMAESOptimizer.FitnessFunction();
      double[] guess = this.getStartPoint();
      this.dimension = guess.length;
      this.initializeCMA(guess);
      this.iterations = 0;
      CMAESOptimizer.ValuePenaltyPair valuePenalty = fitfun.value(guess);
      double bestValue = valuePenalty.value + valuePenalty.penalty;
      push(this.fitnessHistory, bestValue);
      PointValuePair optimum = new PointValuePair(this.getStartPoint(), this.isMinimize ? bestValue : -bestValue);
      PointValuePair lastResult = null;

      for (this.iterations = 1; this.iterations <= this.maxIterations; this.iterations++) {
         this.incrementIterationCount();
         RealMatrix arz = this.randn1(this.dimension, this.lambda);
         RealMatrix arx = zeros(this.dimension, this.lambda);
         double[] fitness = new double[this.lambda];
         CMAESOptimizer.ValuePenaltyPair[] valuePenaltyPairs = new CMAESOptimizer.ValuePenaltyPair[this.lambda];

         for (int k = 0; k < this.lambda; k++) {
            RealMatrix arxk = null;

            for (int i = 0; i < this.checkFeasableCount + 1; i++) {
               if (this.diagonalOnly <= 0) {
                  arxk = this.xmean.add(this.BD.multiply(arz.getColumnMatrix(k)).scalarMultiply(this.sigma));
               } else {
                  arxk = this.xmean.add(times(this.diagD, arz.getColumnMatrix(k)).scalarMultiply(this.sigma));
               }

               if (i >= this.checkFeasableCount || fitfun.isFeasible(arxk.getColumn(0))) {
                  break;
               }

               arz.setColumn(k, this.randn(this.dimension));
            }

            copyColumn(arxk, 0, arx, k);

            try {
               valuePenaltyPairs[k] = fitfun.value(arx.getColumn(k));
            } catch (TooManyEvaluationsException var31) {
               return optimum;
            }
         }

         double valueRange = this.valueRange(valuePenaltyPairs);

         for (int iValue = 0; iValue < valuePenaltyPairs.length; iValue++) {
            fitness[iValue] = valuePenaltyPairs[iValue].value + valuePenaltyPairs[iValue].penalty * valueRange;
         }

         int[] arindex = this.sortedIndices(fitness);
         RealMatrix xold = this.xmean;
         RealMatrix bestArx = selectColumns(arx, MathArrays.copyOf(arindex, this.mu));
         this.xmean = bestArx.multiply(this.weights);
         RealMatrix bestArz = selectColumns(arz, MathArrays.copyOf(arindex, this.mu));
         RealMatrix zmean = bestArz.multiply(this.weights);
         boolean hsig = this.updateEvolutionPaths(zmean, xold);
         if (this.diagonalOnly <= 0) {
            this.updateCovariance(hsig, bestArx, arz, arindex, xold);
         } else {
            this.updateCovarianceDiagonalOnly(hsig, bestArz);
         }

         this.sigma = this.sigma * FastMath.exp(FastMath.min(1.0, (this.normps / this.chiN - 1.0) * this.cs / this.damps));
         double bestFitness = fitness[arindex[0]];
         double worstFitness = fitness[arindex[arindex.length - 1]];
         if (bestValue > bestFitness) {
            bestValue = bestFitness;
            lastResult = optimum;
            optimum = new PointValuePair(fitfun.repair(bestArx.getColumn(0)), this.isMinimize ? bestFitness : -bestFitness);
            if (this.getConvergenceChecker() != null && lastResult != null && this.getConvergenceChecker().converged(this.iterations, optimum, lastResult)) {
               break;
            }
         }

         if (this.stopFitness != 0.0 && bestFitness < (this.isMinimize ? this.stopFitness : -this.stopFitness)) {
            break;
         }

         double[] sqrtDiagC = sqrt(this.diagC).getColumn(0);
         double[] pcCol = this.pc.getColumn(0);

         for (int i = 0; i < this.dimension && !(this.sigma * FastMath.max(FastMath.abs(pcCol[i]), sqrtDiagC[i]) > this.stopTolX); i++) {
            if (i >= this.dimension - 1) {
               return optimum;
            }
         }

         for (int ix = 0; ix < this.dimension; ix++) {
            if (this.sigma * sqrtDiagC[ix] > this.stopTolUpX) {
               return optimum;
            }
         }

         double historyBest = min(this.fitnessHistory);
         double historyWorst = max(this.fitnessHistory);
         if (this.iterations > 2 && FastMath.max(historyWorst, worstFitness) - FastMath.min(historyBest, bestFitness) < this.stopTolFun
            || this.iterations > this.fitnessHistory.length && historyWorst - historyBest < this.stopTolHistFun
            || max(this.diagD) / min(this.diagD) > 1.0E7) {
            break;
         }

         if (this.getConvergenceChecker() != null) {
            PointValuePair current = new PointValuePair(bestArx.getColumn(0), this.isMinimize ? bestFitness : -bestFitness);
            if (lastResult != null && this.getConvergenceChecker().converged(this.iterations, current, lastResult)) {
               break;
            }

            lastResult = current;
         }

         if (bestValue == fitness[arindex[(int)(0.1 + (double)this.lambda / 4.0)]]) {
            this.sigma = this.sigma * FastMath.exp(0.2 + this.cs / this.damps);
         }

         if (this.iterations > 2 && FastMath.max(historyWorst, bestFitness) - FastMath.min(historyBest, bestFitness) == 0.0) {
            this.sigma = this.sigma * FastMath.exp(0.2 + this.cs / this.damps);
         }

         push(this.fitnessHistory, bestFitness);
         if (this.generateStatistics) {
            this.statisticsSigmaHistory.add(this.sigma);
            this.statisticsFitnessHistory.add(bestFitness);
            this.statisticsMeanHistory.add(this.xmean.transpose());
            this.statisticsDHistory.add(this.diagD.transpose().scalarMultiply(100000.0));
         }
      }

      return optimum;
   }

   @Override
   protected void parseOptimizationData(OptimizationData... optData) {
      super.parseOptimizationData(optData);

      for (OptimizationData data : optData) {
         if (data instanceof CMAESOptimizer.Sigma) {
            this.inputSigma = ((CMAESOptimizer.Sigma)data).getSigma();
         } else if (data instanceof CMAESOptimizer.PopulationSize) {
            this.lambda = ((CMAESOptimizer.PopulationSize)data).getPopulationSize();
         }
      }

      this.checkParameters();
   }

   private void checkParameters() {
      double[] init = this.getStartPoint();
      double[] lB = this.getLowerBound();
      double[] uB = this.getUpperBound();
      if (this.inputSigma != null) {
         if (this.inputSigma.length != init.length) {
            throw new DimensionMismatchException(this.inputSigma.length, init.length);
         }

         for (int i = 0; i < init.length; i++) {
            if (this.inputSigma[i] > uB[i] - lB[i]) {
               throw new OutOfRangeException(this.inputSigma[i], 0, uB[i] - lB[i]);
            }
         }
      }
   }

   private void initializeCMA(double[] guess) {
      if (this.lambda <= 0) {
         throw new NotStrictlyPositiveException(this.lambda);
      } else {
         double[][] sigmaArray = new double[guess.length][1];

         for (int i = 0; i < guess.length; i++) {
            sigmaArray[i][0] = this.inputSigma[i];
         }

         RealMatrix insigma = new Array2DRowRealMatrix(sigmaArray, false);
         this.sigma = max(insigma);
         this.stopTolUpX = 1000.0 * max(insigma);
         this.stopTolX = 1.0E-11 * max(insigma);
         this.stopTolFun = 1.0E-12;
         this.stopTolHistFun = 1.0E-13;
         this.mu = this.lambda / 2;
         this.logMu2 = FastMath.log((double)this.mu + 0.5);
         this.weights = log(sequence(1.0, (double)this.mu, 1.0)).scalarMultiply(-1.0).scalarAdd(this.logMu2);
         double sumw = 0.0;
         double sumwq = 0.0;

         for (int i = 0; i < this.mu; i++) {
            double w = this.weights.getEntry(i, 0);
            sumw += w;
            sumwq += w * w;
         }

         this.weights = this.weights.scalarMultiply(1.0 / sumw);
         this.mueff = sumw * sumw / sumwq;
         this.cc = (4.0 + this.mueff / (double)this.dimension) / ((double)(this.dimension + 4) + 2.0 * this.mueff / (double)this.dimension);
         this.cs = (this.mueff + 2.0) / ((double)this.dimension + this.mueff + 3.0);
         this.damps = (1.0 + 2.0 * FastMath.max(0.0, FastMath.sqrt((this.mueff - 1.0) / (double)(this.dimension + 1)) - 1.0))
               * FastMath.max(0.3, 1.0 - (double)this.dimension / (1.0E-6 + (double)this.maxIterations))
            + this.cs;
         this.ccov1 = 2.0 / (((double)this.dimension + 1.3) * ((double)this.dimension + 1.3) + this.mueff);
         this.ccovmu = FastMath.min(
            1.0 - this.ccov1, 2.0 * (this.mueff - 2.0 + 1.0 / this.mueff) / ((double)((this.dimension + 2) * (this.dimension + 2)) + this.mueff)
         );
         this.ccov1Sep = FastMath.min(1.0, this.ccov1 * ((double)this.dimension + 1.5) / 3.0);
         this.ccovmuSep = FastMath.min(1.0 - this.ccov1, this.ccovmu * ((double)this.dimension + 1.5) / 3.0);
         this.chiN = FastMath.sqrt((double)this.dimension)
            * (1.0 - 1.0 / (4.0 * (double)this.dimension) + 1.0 / (21.0 * (double)this.dimension * (double)this.dimension));
         this.xmean = MatrixUtils.createColumnRealMatrix(guess);
         this.diagD = insigma.scalarMultiply(1.0 / this.sigma);
         this.diagC = square(this.diagD);
         this.pc = zeros(this.dimension, 1);
         this.ps = zeros(this.dimension, 1);
         this.normps = this.ps.getFrobeniusNorm();
         this.B = eye(this.dimension, this.dimension);
         this.D = ones(this.dimension, 1);
         this.BD = times(this.B, repmat(this.diagD.transpose(), this.dimension, 1));
         this.C = this.B.multiply(diag(square(this.D)).multiply(this.B.transpose()));
         this.historySize = 10 + (int)((double)(30 * this.dimension) / (double)this.lambda);
         this.fitnessHistory = new double[this.historySize];

         for (int i = 0; i < this.historySize; i++) {
            this.fitnessHistory[i] = Double.MAX_VALUE;
         }
      }
   }

   private boolean updateEvolutionPaths(RealMatrix zmean, RealMatrix xold) {
      this.ps = this.ps.scalarMultiply(1.0 - this.cs).add(this.B.multiply(zmean).scalarMultiply(FastMath.sqrt(this.cs * (2.0 - this.cs) * this.mueff)));
      this.normps = this.ps.getFrobeniusNorm();
      boolean hsig = this.normps / FastMath.sqrt(1.0 - FastMath.pow(1.0 - this.cs, 2 * this.iterations)) / this.chiN
         < 1.4 + 2.0 / ((double)this.dimension + 1.0);
      this.pc = this.pc.scalarMultiply(1.0 - this.cc);
      if (hsig) {
         this.pc = this.pc.add(this.xmean.subtract(xold).scalarMultiply(FastMath.sqrt(this.cc * (2.0 - this.cc) * this.mueff) / this.sigma));
      }

      return hsig;
   }

   private void updateCovarianceDiagonalOnly(boolean hsig, RealMatrix bestArz) {
      double oldFac = hsig ? 0.0 : this.ccov1Sep * this.cc * (2.0 - this.cc);
      oldFac += 1.0 - this.ccov1Sep - this.ccovmuSep;
      this.diagC = this.diagC
         .scalarMultiply(oldFac)
         .add(square(this.pc).scalarMultiply(this.ccov1Sep))
         .add(times(this.diagC, square(bestArz).multiply(this.weights)).scalarMultiply(this.ccovmuSep));
      this.diagD = sqrt(this.diagC);
      if (this.diagonalOnly > 1 && this.iterations > this.diagonalOnly) {
         this.diagonalOnly = 0;
         this.B = eye(this.dimension, this.dimension);
         this.BD = diag(this.diagD);
         this.C = diag(this.diagC);
      }
   }

   private void updateCovariance(boolean hsig, RealMatrix bestArx, RealMatrix arz, int[] arindex, RealMatrix xold) {
      double negccov = 0.0;
      if (this.ccov1 + this.ccovmu > 0.0) {
         RealMatrix arpos = bestArx.subtract(repmat(xold, 1, this.mu)).scalarMultiply(1.0 / this.sigma);
         RealMatrix roneu = this.pc.multiply(this.pc.transpose()).scalarMultiply(this.ccov1);
         double oldFac = hsig ? 0.0 : this.ccov1 * this.cc * (2.0 - this.cc);
         oldFac += 1.0 - this.ccov1 - this.ccovmu;
         if (this.isActiveCMA) {
            negccov = (1.0 - this.ccovmu) * 0.25 * this.mueff / (FastMath.pow((double)(this.dimension + 2), 1.5) + 2.0 * this.mueff);
            double negminresidualvariance = 0.66;
            double negalphaold = 0.5;
            int[] arReverseIndex = reverse(arindex);
            RealMatrix arzneg = selectColumns(arz, MathArrays.copyOf(arReverseIndex, this.mu));
            RealMatrix arnorms = sqrt(sumRows(square(arzneg)));
            int[] idxnorms = this.sortedIndices(arnorms.getRow(0));
            RealMatrix arnormsSorted = selectColumns(arnorms, idxnorms);
            int[] idxReverse = reverse(idxnorms);
            RealMatrix arnormsReverse = selectColumns(arnorms, idxReverse);
            arnorms = divide(arnormsReverse, arnormsSorted);
            int[] idxInv = inverse(idxnorms);
            RealMatrix arnormsInv = selectColumns(arnorms, idxInv);
            double negcovMax = 0.33999999999999997 / square(arnormsInv).multiply(this.weights).getEntry(0, 0);
            if (negccov > negcovMax) {
               negccov = negcovMax;
            }

            arzneg = times(arzneg, repmat(arnormsInv, this.dimension, 1));
            RealMatrix artmp = this.BD.multiply(arzneg);
            RealMatrix Cneg = artmp.multiply(diag(this.weights)).multiply(artmp.transpose());
            oldFac += 0.5 * negccov;
            this.C = this.C
               .scalarMultiply(oldFac)
               .add(roneu)
               .add(arpos.scalarMultiply(this.ccovmu + 0.5 * negccov).multiply(times(repmat(this.weights, 1, this.dimension), arpos.transpose())))
               .subtract(Cneg.scalarMultiply(negccov));
         } else {
            this.C = this.C
               .scalarMultiply(oldFac)
               .add(roneu)
               .add(arpos.scalarMultiply(this.ccovmu).multiply(times(repmat(this.weights, 1, this.dimension), arpos.transpose())));
         }
      }

      this.updateBD(negccov);
   }

   private void updateBD(double negccov) {
      if (this.ccov1 + this.ccovmu + negccov > 0.0
         && (double)this.iterations % 1.0 / (this.ccov1 + this.ccovmu + negccov) / (double)this.dimension / 10.0 < 1.0) {
         this.C = triu(this.C, 0).add(triu(this.C, 1).transpose());
         EigenDecomposition eig = new EigenDecomposition(this.C);
         this.B = eig.getV();
         this.D = eig.getD();
         this.diagD = diag(this.D);
         if (min(this.diagD) <= 0.0) {
            for (int i = 0; i < this.dimension; i++) {
               if (this.diagD.getEntry(i, 0) < 0.0) {
                  this.diagD.setEntry(i, 0, 0.0);
               }
            }

            double tfac = max(this.diagD) / 1.0E14;
            this.C = this.C.add(eye(this.dimension, this.dimension).scalarMultiply(tfac));
            this.diagD = this.diagD.add(ones(this.dimension, 1).scalarMultiply(tfac));
         }

         if (max(this.diagD) > 1.0E14 * min(this.diagD)) {
            double tfac = max(this.diagD) / 1.0E14 - min(this.diagD);
            this.C = this.C.add(eye(this.dimension, this.dimension).scalarMultiply(tfac));
            this.diagD = this.diagD.add(ones(this.dimension, 1).scalarMultiply(tfac));
         }

         this.diagC = diag(this.C);
         this.diagD = sqrt(this.diagD);
         this.BD = times(this.B, repmat(this.diagD.transpose(), this.dimension, 1));
      }
   }

   private static void push(double[] vals, double val) {
      for (int i = vals.length - 1; i > 0; i--) {
         vals[i] = vals[i - 1];
      }

      vals[0] = val;
   }

   private int[] sortedIndices(double[] doubles) {
      CMAESOptimizer.DoubleIndex[] dis = new CMAESOptimizer.DoubleIndex[doubles.length];

      for (int i = 0; i < doubles.length; i++) {
         dis[i] = new CMAESOptimizer.DoubleIndex(doubles[i], i);
      }

      Arrays.sort((Object[])dis);
      int[] indices = new int[doubles.length];

      for (int i = 0; i < doubles.length; i++) {
         indices[i] = dis[i].index;
      }

      return indices;
   }

   private double valueRange(CMAESOptimizer.ValuePenaltyPair[] vpPairs) {
      double max = Double.NEGATIVE_INFINITY;
      double min = Double.MAX_VALUE;

      for (CMAESOptimizer.ValuePenaltyPair vpPair : vpPairs) {
         if (vpPair.value > max) {
            max = vpPair.value;
         }

         if (vpPair.value < min) {
            min = vpPair.value;
         }
      }

      return max - min;
   }

   private static RealMatrix log(RealMatrix m) {
      double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];

      for (int r = 0; r < m.getRowDimension(); r++) {
         for (int c = 0; c < m.getColumnDimension(); c++) {
            d[r][c] = FastMath.log(m.getEntry(r, c));
         }
      }

      return new Array2DRowRealMatrix(d, false);
   }

   private static RealMatrix sqrt(RealMatrix m) {
      double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];

      for (int r = 0; r < m.getRowDimension(); r++) {
         for (int c = 0; c < m.getColumnDimension(); c++) {
            d[r][c] = FastMath.sqrt(m.getEntry(r, c));
         }
      }

      return new Array2DRowRealMatrix(d, false);
   }

   private static RealMatrix square(RealMatrix m) {
      double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];

      for (int r = 0; r < m.getRowDimension(); r++) {
         for (int c = 0; c < m.getColumnDimension(); c++) {
            double e = m.getEntry(r, c);
            d[r][c] = e * e;
         }
      }

      return new Array2DRowRealMatrix(d, false);
   }

   private static RealMatrix times(RealMatrix m, RealMatrix n) {
      double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];

      for (int r = 0; r < m.getRowDimension(); r++) {
         for (int c = 0; c < m.getColumnDimension(); c++) {
            d[r][c] = m.getEntry(r, c) * n.getEntry(r, c);
         }
      }

      return new Array2DRowRealMatrix(d, false);
   }

   private static RealMatrix divide(RealMatrix m, RealMatrix n) {
      double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];

      for (int r = 0; r < m.getRowDimension(); r++) {
         for (int c = 0; c < m.getColumnDimension(); c++) {
            d[r][c] = m.getEntry(r, c) / n.getEntry(r, c);
         }
      }

      return new Array2DRowRealMatrix(d, false);
   }

   private static RealMatrix selectColumns(RealMatrix m, int[] cols) {
      double[][] d = new double[m.getRowDimension()][cols.length];

      for (int r = 0; r < m.getRowDimension(); r++) {
         for (int c = 0; c < cols.length; c++) {
            d[r][c] = m.getEntry(r, cols[c]);
         }
      }

      return new Array2DRowRealMatrix(d, false);
   }

   private static RealMatrix triu(RealMatrix m, int k) {
      double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];

      for (int r = 0; r < m.getRowDimension(); r++) {
         for (int c = 0; c < m.getColumnDimension(); c++) {
            d[r][c] = r <= c - k ? m.getEntry(r, c) : 0.0;
         }
      }

      return new Array2DRowRealMatrix(d, false);
   }

   private static RealMatrix sumRows(RealMatrix m) {
      double[][] d = new double[1][m.getColumnDimension()];

      for (int c = 0; c < m.getColumnDimension(); c++) {
         double sum = 0.0;

         for (int r = 0; r < m.getRowDimension(); r++) {
            sum += m.getEntry(r, c);
         }

         d[0][c] = sum;
      }

      return new Array2DRowRealMatrix(d, false);
   }

   private static RealMatrix diag(RealMatrix m) {
      if (m.getColumnDimension() == 1) {
         double[][] d = new double[m.getRowDimension()][m.getRowDimension()];

         for (int i = 0; i < m.getRowDimension(); i++) {
            d[i][i] = m.getEntry(i, 0);
         }

         return new Array2DRowRealMatrix(d, false);
      } else {
         double[][] d = new double[m.getRowDimension()][1];

         for (int i = 0; i < m.getColumnDimension(); i++) {
            d[i][0] = m.getEntry(i, i);
         }

         return new Array2DRowRealMatrix(d, false);
      }
   }

   private static void copyColumn(RealMatrix m1, int col1, RealMatrix m2, int col2) {
      for (int i = 0; i < m1.getRowDimension(); i++) {
         m2.setEntry(i, col2, m1.getEntry(i, col1));
      }
   }

   private static RealMatrix ones(int n, int m) {
      double[][] d = new double[n][m];

      for (int r = 0; r < n; r++) {
         Arrays.fill(d[r], 1.0);
      }

      return new Array2DRowRealMatrix(d, false);
   }

   private static RealMatrix eye(int n, int m) {
      double[][] d = new double[n][m];

      for (int r = 0; r < n; r++) {
         if (r < m) {
            d[r][r] = 1.0;
         }
      }

      return new Array2DRowRealMatrix(d, false);
   }

   private static RealMatrix zeros(int n, int m) {
      return new Array2DRowRealMatrix(n, m);
   }

   private static RealMatrix repmat(RealMatrix mat, int n, int m) {
      int rd = mat.getRowDimension();
      int cd = mat.getColumnDimension();
      double[][] d = new double[n * rd][m * cd];

      for (int r = 0; r < n * rd; r++) {
         for (int c = 0; c < m * cd; c++) {
            d[r][c] = mat.getEntry(r % rd, c % cd);
         }
      }

      return new Array2DRowRealMatrix(d, false);
   }

   private static RealMatrix sequence(double start, double end, double step) {
      int size = (int)((end - start) / step + 1.0);
      double[][] d = new double[size][1];
      double value = start;

      for (int r = 0; r < size; r++) {
         d[r][0] = value;
         value += step;
      }

      return new Array2DRowRealMatrix(d, false);
   }

   private static double max(RealMatrix m) {
      double max = -Double.MAX_VALUE;

      for (int r = 0; r < m.getRowDimension(); r++) {
         for (int c = 0; c < m.getColumnDimension(); c++) {
            double e = m.getEntry(r, c);
            if (max < e) {
               max = e;
            }
         }
      }

      return max;
   }

   private static double min(RealMatrix m) {
      double min = Double.MAX_VALUE;

      for (int r = 0; r < m.getRowDimension(); r++) {
         for (int c = 0; c < m.getColumnDimension(); c++) {
            double e = m.getEntry(r, c);
            if (min > e) {
               min = e;
            }
         }
      }

      return min;
   }

   private static double max(double[] m) {
      double max = -Double.MAX_VALUE;

      for (int r = 0; r < m.length; r++) {
         if (max < m[r]) {
            max = m[r];
         }
      }

      return max;
   }

   private static double min(double[] m) {
      double min = Double.MAX_VALUE;

      for (int r = 0; r < m.length; r++) {
         if (min > m[r]) {
            min = m[r];
         }
      }

      return min;
   }

   private static int[] inverse(int[] indices) {
      int[] inverse = new int[indices.length];
      int i = 0;

      while (i < indices.length) {
         inverse[indices[i]] = i++;
      }

      return inverse;
   }

   private static int[] reverse(int[] indices) {
      int[] reverse = new int[indices.length];

      for (int i = 0; i < indices.length; i++) {
         reverse[i] = indices[indices.length - i - 1];
      }

      return reverse;
   }

   private double[] randn(int size) {
      double[] randn = new double[size];

      for (int i = 0; i < size; i++) {
         randn[i] = this.random.nextGaussian();
      }

      return randn;
   }

   private RealMatrix randn1(int size, int popSize) {
      double[][] d = new double[size][popSize];

      for (int r = 0; r < size; r++) {
         for (int c = 0; c < popSize; c++) {
            d[r][c] = this.random.nextGaussian();
         }
      }

      return new Array2DRowRealMatrix(d, false);
   }

   private static class DoubleIndex implements Comparable<CMAESOptimizer.DoubleIndex> {
      private final double value;
      private final int index;

      DoubleIndex(double value, int index) {
         this.value = value;
         this.index = index;
      }

      public int compareTo(CMAESOptimizer.DoubleIndex o) {
         return Double.compare(this.value, o.value);
      }

      @Override
      public boolean equals(Object other) {
         if (this == other) {
            return true;
         } else {
            return other instanceof CMAESOptimizer.DoubleIndex ? Double.compare(this.value, ((CMAESOptimizer.DoubleIndex)other).value) == 0 : false;
         }
      }

      @Override
      public int hashCode() {
         long bits = Double.doubleToLongBits(this.value);
         return (int)((1438542L ^ bits >>> 32 ^ bits) & -1L);
      }
   }

   private class FitnessFunction {
      private final boolean isRepairMode = true;

      FitnessFunction() {
      }

      public CMAESOptimizer.ValuePenaltyPair value(double[] point) {
         double penalty = 0.0;
         double value;
         if (this.isRepairMode) {
            double[] repaired = this.repair(point);
            value = CMAESOptimizer.this.computeObjectiveValue(repaired);
            penalty = this.penalty(point, repaired);
         } else {
            value = CMAESOptimizer.this.computeObjectiveValue(point);
         }

         value = CMAESOptimizer.this.isMinimize ? value : -value;
         penalty = CMAESOptimizer.this.isMinimize ? penalty : -penalty;
         return new CMAESOptimizer.ValuePenaltyPair(value, penalty);
      }

      public boolean isFeasible(double[] x) {
         double[] lB = CMAESOptimizer.this.getLowerBound();
         double[] uB = CMAESOptimizer.this.getUpperBound();

         for (int i = 0; i < x.length; i++) {
            if (x[i] < lB[i]) {
               return false;
            }

            if (x[i] > uB[i]) {
               return false;
            }
         }

         return true;
      }

      private double[] repair(double[] x) {
         double[] lB = CMAESOptimizer.this.getLowerBound();
         double[] uB = CMAESOptimizer.this.getUpperBound();
         double[] repaired = new double[x.length];

         for (int i = 0; i < x.length; i++) {
            if (x[i] < lB[i]) {
               repaired[i] = lB[i];
            } else if (x[i] > uB[i]) {
               repaired[i] = uB[i];
            } else {
               repaired[i] = x[i];
            }
         }

         return repaired;
      }

      private double penalty(double[] x, double[] repaired) {
         double penalty = 0.0;

         for (int i = 0; i < x.length; i++) {
            double diff = FastMath.abs(x[i] - repaired[i]);
            penalty += diff;
         }

         return CMAESOptimizer.this.isMinimize ? penalty : -penalty;
      }
   }

   public static class PopulationSize implements OptimizationData {
      private final int lambda;

      public PopulationSize(int size) throws NotStrictlyPositiveException {
         if (size <= 0) {
            throw new NotStrictlyPositiveException(size);
         } else {
            this.lambda = size;
         }
      }

      public int getPopulationSize() {
         return this.lambda;
      }
   }

   public static class Sigma implements OptimizationData {
      private final double[] sigma;

      public Sigma(double[] s) throws NotPositiveException {
         for (int i = 0; i < s.length; i++) {
            if (s[i] < 0.0) {
               throw new NotPositiveException(s[i]);
            }
         }

         this.sigma = (double[])s.clone();
      }

      public double[] getSigma() {
         return (double[])this.sigma.clone();
      }
   }

   private static class ValuePenaltyPair {
      private double value;
      private double penalty;

      ValuePenaltyPair(double value, double penalty) {
         this.value = value;
         this.penalty = penalty;
      }
   }
}
