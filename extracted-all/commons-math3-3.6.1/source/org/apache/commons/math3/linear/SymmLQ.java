package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.ExceptionContext;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.IterationManager;
import org.apache.commons.math3.util.MathUtils;

public class SymmLQ extends PreconditionedIterativeLinearSolver {
   private static final String OPERATOR = "operator";
   private static final String THRESHOLD = "threshold";
   private static final String VECTOR = "vector";
   private static final String VECTOR1 = "vector1";
   private static final String VECTOR2 = "vector2";
   private final boolean check;
   private final double delta;

   public SymmLQ(int maxIterations, double delta, boolean check) {
      super(maxIterations);
      this.delta = delta;
      this.check = check;
   }

   public SymmLQ(IterationManager manager, double delta, boolean check) {
      super(manager);
      this.delta = delta;
      this.check = check;
   }

   public final boolean getCheck() {
      return this.check;
   }

   @Override
   public RealVector solve(RealLinearOperator a, RealLinearOperator m, RealVector b) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException, NonSelfAdjointOperatorException, NonPositiveDefiniteOperatorException, IllConditionedOperatorException {
      MathUtils.checkNotNull(a);
      RealVector x = new ArrayRealVector(a.getColumnDimension());
      return this.solveInPlace(a, m, b, x, false, 0.0);
   }

   public RealVector solve(RealLinearOperator a, RealLinearOperator m, RealVector b, boolean goodb, double shift) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException, NonSelfAdjointOperatorException, NonPositiveDefiniteOperatorException, IllConditionedOperatorException {
      MathUtils.checkNotNull(a);
      RealVector x = new ArrayRealVector(a.getColumnDimension());
      return this.solveInPlace(a, m, b, x, goodb, shift);
   }

   @Override
   public RealVector solve(RealLinearOperator a, RealLinearOperator m, RealVector b, RealVector x) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, NonPositiveDefiniteOperatorException, IllConditionedOperatorException, MaxCountExceededException {
      MathUtils.checkNotNull(x);
      return this.solveInPlace(a, m, b, x.copy(), false, 0.0);
   }

   @Override
   public RealVector solve(RealLinearOperator a, RealVector b) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, IllConditionedOperatorException, MaxCountExceededException {
      MathUtils.checkNotNull(a);
      RealVector x = new ArrayRealVector(a.getColumnDimension());
      x.set(0.0);
      return this.solveInPlace(a, null, b, x, false, 0.0);
   }

   public RealVector solve(RealLinearOperator a, RealVector b, boolean goodb, double shift) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, IllConditionedOperatorException, MaxCountExceededException {
      MathUtils.checkNotNull(a);
      RealVector x = new ArrayRealVector(a.getColumnDimension());
      return this.solveInPlace(a, null, b, x, goodb, shift);
   }

   @Override
   public RealVector solve(RealLinearOperator a, RealVector b, RealVector x) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, IllConditionedOperatorException, MaxCountExceededException {
      MathUtils.checkNotNull(x);
      return this.solveInPlace(a, null, b, x.copy(), false, 0.0);
   }

   @Override
   public RealVector solveInPlace(RealLinearOperator a, RealLinearOperator m, RealVector b, RealVector x) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, NonPositiveDefiniteOperatorException, IllConditionedOperatorException, MaxCountExceededException {
      return this.solveInPlace(a, m, b, x, false, 0.0);
   }

   public RealVector solveInPlace(RealLinearOperator a, RealLinearOperator m, RealVector b, RealVector x, boolean goodb, double shift) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, NonPositiveDefiniteOperatorException, IllConditionedOperatorException, MaxCountExceededException {
      checkParameters(a, m, b, x);
      IterationManager manager = this.getIterationManager();
      manager.resetIterationCount();
      manager.incrementIterationCount();
      SymmLQ.State state = new SymmLQ.State(a, m, b, goodb, shift, this.delta, this.check);
      state.init();
      state.refineSolution(x);
      IterativeLinearSolverEvent event = new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), x, b, state.getNormOfResidual());
      if (state.bEqualsNullVector()) {
         manager.fireTerminationEvent(event);
         return x;
      } else {
         boolean earlyStop = state.betaEqualsZero() || state.hasConverged();
         manager.fireInitializationEvent(event);
         if (!earlyStop) {
            do {
               manager.incrementIterationCount();
               event = new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), x, b, state.getNormOfResidual());
               manager.fireIterationStartedEvent(event);
               state.update();
               state.refineSolution(x);
               event = new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), x, b, state.getNormOfResidual());
               manager.fireIterationPerformedEvent(event);
            } while (!state.hasConverged());
         }

         event = new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), x, b, state.getNormOfResidual());
         manager.fireTerminationEvent(event);
         return x;
      }
   }

   @Override
   public RealVector solveInPlace(RealLinearOperator a, RealVector b, RealVector x) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, IllConditionedOperatorException, MaxCountExceededException {
      return this.solveInPlace(a, null, b, x, false, 0.0);
   }

   private static class State {
      static final double CBRT_MACH_PREC = FastMath.cbrt(SymmLQ.State.MACH_PREC);
      static final double MACH_PREC = FastMath.ulp(1.0);
      private final RealLinearOperator a;
      private final RealVector b;
      private final boolean check;
      private final double delta;
      private double beta;
      private double beta1;
      private double bstep;
      private double cgnorm;
      private double dbar;
      private double gammaZeta;
      private double gbar;
      private double gmax;
      private double gmin;
      private final boolean goodb;
      private boolean hasConverged;
      private double lqnorm;
      private final RealLinearOperator m;
      private double minusEpsZeta;
      private final RealVector mb;
      private double oldb;
      private RealVector r1;
      private RealVector r2;
      private double rnorm;
      private final double shift;
      private double snprod;
      private double tnorm;
      private RealVector wbar;
      private final RealVector xL;
      private RealVector y;
      private double ynorm2;
      private boolean bIsNull;

      State(RealLinearOperator a, RealLinearOperator m, RealVector b, boolean goodb, double shift, double delta, boolean check) {
         this.a = a;
         this.m = m;
         this.b = b;
         this.xL = new ArrayRealVector(b.getDimension());
         this.goodb = goodb;
         this.shift = shift;
         this.mb = m == null ? b : m.operate(b);
         this.hasConverged = false;
         this.check = check;
         this.delta = delta;
      }

      private static void checkSymmetry(RealLinearOperator l, RealVector x, RealVector y, RealVector z) throws NonSelfAdjointOperatorException {
         double s = y.dotProduct(y);
         double t = x.dotProduct(z);
         double epsa = (s + MACH_PREC) * CBRT_MACH_PREC;
         if (FastMath.abs(s - t) > epsa) {
            NonSelfAdjointOperatorException e = new NonSelfAdjointOperatorException();
            ExceptionContext context = e.getContext();
            context.setValue("operator", l);
            context.setValue("vector1", x);
            context.setValue("vector2", y);
            context.setValue("threshold", epsa);
            throw e;
         }
      }

      private static void throwNPDLOException(RealLinearOperator l, RealVector v) throws NonPositiveDefiniteOperatorException {
         NonPositiveDefiniteOperatorException e = new NonPositiveDefiniteOperatorException();
         ExceptionContext context = e.getContext();
         context.setValue("operator", l);
         context.setValue("vector", v);
         throw e;
      }

      private static void daxpy(double a, RealVector x, RealVector y) {
         int n = x.getDimension();

         for (int i = 0; i < n; i++) {
            y.setEntry(i, a * x.getEntry(i) + y.getEntry(i));
         }
      }

      private static void daxpbypz(double a, RealVector x, double b, RealVector y, RealVector z) {
         int n = z.getDimension();

         for (int i = 0; i < n; i++) {
            double zi = a * x.getEntry(i) + b * y.getEntry(i) + z.getEntry(i);
            z.setEntry(i, zi);
         }
      }

      void refineSolution(RealVector x) {
         int n = this.xL.getDimension();
         if (this.lqnorm < this.cgnorm) {
            if (!this.goodb) {
               x.setSubVector(0, this.xL);
            } else {
               double step = this.bstep / this.beta1;

               for (int i = 0; i < n; i++) {
                  double bi = this.mb.getEntry(i);
                  double xi = this.xL.getEntry(i);
                  x.setEntry(i, xi + step * bi);
               }
            }
         } else {
            double anorm = FastMath.sqrt(this.tnorm);
            double diag = this.gbar == 0.0 ? anorm * MACH_PREC : this.gbar;
            double zbar = this.gammaZeta / diag;
            double step = (this.bstep + this.snprod * zbar) / this.beta1;
            if (!this.goodb) {
               for (int i = 0; i < n; i++) {
                  double xi = this.xL.getEntry(i);
                  double wi = this.wbar.getEntry(i);
                  x.setEntry(i, xi + zbar * wi);
               }
            } else {
               for (int i = 0; i < n; i++) {
                  double xi = this.xL.getEntry(i);
                  double wi = this.wbar.getEntry(i);
                  double bi = this.mb.getEntry(i);
                  x.setEntry(i, xi + zbar * wi + step * bi);
               }
            }
         }
      }

      void init() {
         this.xL.set(0.0);
         this.r1 = this.b.copy();
         this.y = this.m == null ? this.b.copy() : this.m.operate(this.r1);
         if (this.m != null && this.check) {
            checkSymmetry(this.m, this.r1, this.y, this.m.operate(this.y));
         }

         this.beta1 = this.r1.dotProduct(this.y);
         if (this.beta1 < 0.0) {
            throwNPDLOException(this.m, this.y);
         }

         if (this.beta1 == 0.0) {
            this.bIsNull = true;
         } else {
            this.bIsNull = false;
            this.beta1 = FastMath.sqrt(this.beta1);
            RealVector v = this.y.mapMultiply(1.0 / this.beta1);
            this.y = this.a.operate(v);
            if (this.check) {
               checkSymmetry(this.a, v, this.y, this.a.operate(this.y));
            }

            daxpy(-this.shift, v, this.y);
            double alpha = v.dotProduct(this.y);
            daxpy(-alpha / this.beta1, this.r1, this.y);
            double vty = v.dotProduct(this.y);
            double vtv = v.dotProduct(v);
            daxpy(-vty / vtv, v, this.y);
            this.r2 = this.y.copy();
            if (this.m != null) {
               this.y = this.m.operate(this.r2);
            }

            this.oldb = this.beta1;
            this.beta = this.r2.dotProduct(this.y);
            if (this.beta < 0.0) {
               throwNPDLOException(this.m, this.y);
            }

            this.beta = FastMath.sqrt(this.beta);
            this.cgnorm = this.beta1;
            this.gbar = alpha;
            this.dbar = this.beta;
            this.gammaZeta = this.beta1;
            this.minusEpsZeta = 0.0;
            this.bstep = 0.0;
            this.snprod = 1.0;
            this.tnorm = alpha * alpha + this.beta * this.beta;
            this.ynorm2 = 0.0;
            this.gmax = FastMath.abs(alpha) + MACH_PREC;
            this.gmin = this.gmax;
            if (this.goodb) {
               this.wbar = new ArrayRealVector(this.a.getRowDimension());
               this.wbar.set(0.0);
            } else {
               this.wbar = v;
            }

            this.updateNorms();
         }
      }

      void update() {
         RealVector v = this.y.mapMultiply(1.0 / this.beta);
         this.y = this.a.operate(v);
         daxpbypz(-this.shift, v, -this.beta / this.oldb, this.r1, this.y);
         double alpha = v.dotProduct(this.y);
         daxpy(-alpha / this.beta, this.r2, this.y);
         this.r1 = this.r2;
         this.r2 = this.y;
         if (this.m != null) {
            this.y = this.m.operate(this.r2);
         }

         this.oldb = this.beta;
         this.beta = this.r2.dotProduct(this.y);
         if (this.beta < 0.0) {
            throwNPDLOException(this.m, this.y);
         }

         this.beta = FastMath.sqrt(this.beta);
         this.tnorm = this.tnorm + alpha * alpha + this.oldb * this.oldb + this.beta * this.beta;
         double gamma = FastMath.sqrt(this.gbar * this.gbar + this.oldb * this.oldb);
         double c = this.gbar / gamma;
         double s = this.oldb / gamma;
         double deltak = c * this.dbar + s * alpha;
         this.gbar = s * this.dbar - c * alpha;
         double eps = s * this.beta;
         this.dbar = -c * this.beta;
         double zeta = this.gammaZeta / gamma;
         double zetaC = zeta * c;
         double zetaS = zeta * s;
         int n = this.xL.getDimension();

         for (int i = 0; i < n; i++) {
            double xi = this.xL.getEntry(i);
            double vi = v.getEntry(i);
            double wi = this.wbar.getEntry(i);
            this.xL.setEntry(i, xi + wi * zetaC + vi * zetaS);
            this.wbar.setEntry(i, wi * s - vi * c);
         }

         this.bstep = this.bstep + this.snprod * c * zeta;
         this.snprod *= s;
         this.gmax = FastMath.max(this.gmax, gamma);
         this.gmin = FastMath.min(this.gmin, gamma);
         this.ynorm2 += zeta * zeta;
         this.gammaZeta = this.minusEpsZeta - deltak * zeta;
         this.minusEpsZeta = -eps * zeta;
         this.updateNorms();
      }

      private void updateNorms() {
         double anorm = FastMath.sqrt(this.tnorm);
         double ynorm = FastMath.sqrt(this.ynorm2);
         double epsa = anorm * MACH_PREC;
         double epsx = anorm * ynorm * MACH_PREC;
         double epsr = anorm * ynorm * this.delta;
         double diag = this.gbar == 0.0 ? epsa : this.gbar;
         this.lqnorm = FastMath.sqrt(this.gammaZeta * this.gammaZeta + this.minusEpsZeta * this.minusEpsZeta);
         double qrnorm = this.snprod * this.beta1;
         this.cgnorm = qrnorm * this.beta / FastMath.abs(diag);
         double acond;
         if (this.lqnorm <= this.cgnorm) {
            acond = this.gmax / this.gmin;
         } else {
            acond = this.gmax / FastMath.min(this.gmin, FastMath.abs(diag));
         }

         if (acond * MACH_PREC >= 0.1) {
            throw new IllConditionedOperatorException(acond);
         } else if (this.beta1 <= epsx) {
            throw new SingularOperatorException();
         } else {
            this.rnorm = FastMath.min(this.cgnorm, this.lqnorm);
            this.hasConverged = this.cgnorm <= epsx || this.cgnorm <= epsr;
         }
      }

      boolean hasConverged() {
         return this.hasConverged;
      }

      boolean bEqualsNullVector() {
         return this.bIsNull;
      }

      boolean betaEqualsZero() {
         return this.beta < MACH_PREC;
      }

      double getNormOfResidual() {
         return this.rnorm;
      }
   }
}
