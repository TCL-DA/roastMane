package org.apache.commons.math3.optimization.general;

import org.apache.commons.math3.analysis.DifferentiableMultivariateVectorFunction;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.DifferentiableMultivariateVectorOptimizer;
import org.apache.commons.math3.optimization.InitialGuess;
import org.apache.commons.math3.optimization.OptimizationData;
import org.apache.commons.math3.optimization.PointVectorValuePair;
import org.apache.commons.math3.optimization.Target;
import org.apache.commons.math3.optimization.Weight;
import org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateVectorOptimizer;
import org.apache.commons.math3.util.FastMath;

@Deprecated
public abstract class AbstractLeastSquaresOptimizer
   extends BaseAbstractMultivariateVectorOptimizer<DifferentiableMultivariateVectorFunction>
   implements DifferentiableMultivariateVectorOptimizer {
   @Deprecated
   private static final double DEFAULT_SINGULARITY_THRESHOLD = 1.0E-14;
   @Deprecated
   protected double[][] weightedResidualJacobian;
   @Deprecated
   protected int cols;
   @Deprecated
   protected int rows;
   @Deprecated
   protected double[] point;
   @Deprecated
   protected double[] objective;
   @Deprecated
   protected double[] weightedResiduals;
   @Deprecated
   protected double cost;
   private MultivariateDifferentiableVectorFunction jF;
   private int jacobianEvaluations;
   private RealMatrix weightMatrixSqrt;

   @Deprecated
   protected AbstractLeastSquaresOptimizer() {
   }

   protected AbstractLeastSquaresOptimizer(ConvergenceChecker<PointVectorValuePair> checker) {
      super(checker);
   }

   public int getJacobianEvaluations() {
      return this.jacobianEvaluations;
   }

   @Deprecated
   protected void updateJacobian() {
      RealMatrix weightedJacobian = this.computeWeightedJacobian(this.point);
      this.weightedResidualJacobian = weightedJacobian.scalarMultiply(-1.0).getData();
   }

   protected RealMatrix computeWeightedJacobian(double[] params) {
      this.jacobianEvaluations++;
      DerivativeStructure[] dsPoint = new DerivativeStructure[params.length];
      int nC = params.length;

      for (int i = 0; i < nC; i++) {
         dsPoint[i] = new DerivativeStructure(nC, 1, i, params[i]);
      }

      DerivativeStructure[] dsValue = this.jF.value(dsPoint);
      int nR = this.getTarget().length;
      if (dsValue.length != nR) {
         throw new DimensionMismatchException(dsValue.length, nR);
      } else {
         double[][] jacobianData = new double[nR][nC];

         for (int i = 0; i < nR; i++) {
            int[] orders = new int[nC];

            for (int j = 0; j < nC; j++) {
               orders[j] = 1;
               jacobianData[i][j] = dsValue[i].getPartialDerivative(orders);
               orders[j] = 0;
            }
         }

         return this.weightMatrixSqrt.multiply(MatrixUtils.createRealMatrix(jacobianData));
      }
   }

   @Deprecated
   protected void updateResidualsAndCost() {
      this.objective = this.computeObjectiveValue(this.point);
      double[] res = this.computeResiduals(this.objective);
      this.cost = this.computeCost(res);
      ArrayRealVector residuals = new ArrayRealVector(res);
      this.weightedResiduals = this.weightMatrixSqrt.operate(residuals).toArray();
   }

   protected double computeCost(double[] residuals) {
      ArrayRealVector r = new ArrayRealVector(residuals);
      return FastMath.sqrt(r.dotProduct(this.getWeight().operate(r)));
   }

   public double getRMS() {
      return FastMath.sqrt(this.getChiSquare() / (double)this.rows);
   }

   public double getChiSquare() {
      return this.cost * this.cost;
   }

   public RealMatrix getWeightSquareRoot() {
      return this.weightMatrixSqrt.copy();
   }

   protected void setCost(double cost) {
      this.cost = cost;
   }

   @Deprecated
   public double[][] getCovariances() {
      return this.getCovariances(1.0E-14);
   }

   @Deprecated
   public double[][] getCovariances(double threshold) {
      return this.computeCovariances(this.point, threshold);
   }

   public double[][] computeCovariances(double[] params, double threshold) {
      RealMatrix j = this.computeWeightedJacobian(params);
      RealMatrix jTj = j.transpose().multiply(j);
      DecompositionSolver solver = new QRDecomposition(jTj, threshold).getSolver();
      return solver.getInverse().getData();
   }

   @Deprecated
   public double[] guessParametersErrors() {
      if (this.rows <= this.cols) {
         throw new NumberIsTooSmallException(LocalizedFormats.NO_DEGREES_OF_FREEDOM, this.rows, this.cols, false);
      } else {
         double[] errors = new double[this.cols];
         double c = FastMath.sqrt(this.getChiSquare() / (double)(this.rows - this.cols));
         double[][] covar = this.computeCovariances(this.point, 1.0E-14);

         for (int i = 0; i < errors.length; i++) {
            errors[i] = FastMath.sqrt(covar[i][i]) * c;
         }

         return errors;
      }
   }

   public double[] computeSigma(double[] params, double covarianceSingularityThreshold) {
      int nC = params.length;
      double[] sig = new double[nC];
      double[][] cov = this.computeCovariances(params, covarianceSingularityThreshold);

      for (int i = 0; i < nC; i++) {
         sig[i] = FastMath.sqrt(cov[i][i]);
      }

      return sig;
   }

   @Deprecated
   public PointVectorValuePair optimize(int maxEval, DifferentiableMultivariateVectorFunction f, double[] target, double[] weights, double[] startPoint) {
      return this.optimizeInternal(
         maxEval, FunctionUtils.toMultivariateDifferentiableVectorFunction(f), new Target(target), new Weight(weights), new InitialGuess(startPoint)
      );
   }

   @Deprecated
   public PointVectorValuePair optimize(int maxEval, MultivariateDifferentiableVectorFunction f, double[] target, double[] weights, double[] startPoint) {
      return this.optimizeInternal(maxEval, f, new Target(target), new Weight(weights), new InitialGuess(startPoint));
   }

   @Deprecated
   protected PointVectorValuePair optimizeInternal(int maxEval, MultivariateDifferentiableVectorFunction f, OptimizationData... optData) {
      return super.optimizeInternal(maxEval, FunctionUtils.toDifferentiableMultivariateVectorFunction(f), optData);
   }

   @Override
   protected void setUp() {
      super.setUp();
      this.jacobianEvaluations = 0;
      this.weightMatrixSqrt = this.squareRoot(this.getWeight());
      this.jF = FunctionUtils.toMultivariateDifferentiableVectorFunction(this.getObjectiveFunction());
      this.point = this.getStartPoint();
      this.rows = this.getTarget().length;
      this.cols = this.point.length;
   }

   protected double[] computeResiduals(double[] objectiveValue) {
      double[] target = this.getTarget();
      if (objectiveValue.length != target.length) {
         throw new DimensionMismatchException(target.length, objectiveValue.length);
      } else {
         double[] residuals = new double[target.length];

         for (int i = 0; i < target.length; i++) {
            residuals[i] = target[i] - objectiveValue[i];
         }

         return residuals;
      }
   }

   private RealMatrix squareRoot(RealMatrix m) {
      if (!(m instanceof DiagonalMatrix)) {
         EigenDecomposition dec = new EigenDecomposition(m);
         return dec.getSquareRoot();
      } else {
         int dim = m.getRowDimension();
         RealMatrix sqrtM = new DiagonalMatrix(dim);

         for (int i = 0; i < dim; i++) {
            sqrtM.setEntry(i, i, FastMath.sqrt(m.getEntry(i, i)));
         }

         return sqrtM;
      }
   }
}
