package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class DormandPrince54FieldIntegrator<T extends RealFieldElement<T>> extends EmbeddedRungeKuttaFieldIntegrator<T> {
   private static final String METHOD_NAME = "Dormand-Prince 5(4)";
   private final T e1 = this.fraction(71, 57600);
   private final T e3 = this.fraction(-71, 16695);
   private final T e4 = this.fraction(71, 1920);
   private final T e5 = this.fraction(-17253, 339200);
   private final T e6 = this.fraction(22, 525);
   private final T e7 = this.fraction(-1, 40);

   public DormandPrince54FieldIntegrator(Field<T> field, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) {
      super(field, "Dormand-Prince 5(4)", 6, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
   }

   public DormandPrince54FieldIntegrator(Field<T> field, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance) {
      super(field, "Dormand-Prince 5(4)", 6, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
   }

   @Override
   public T[] getC() {
      T[] c = MathArrays.buildArray(this.getField(), 6);
      c[0] = this.fraction(1, 5);
      c[1] = this.fraction(3, 10);
      c[2] = this.fraction(4, 5);
      c[3] = this.fraction(8, 9);
      c[4] = this.getField().getOne();
      c[5] = this.getField().getOne();
      return c;
   }

   @Override
   public T[][] getA() {
      T[][] a = MathArrays.buildArray(this.getField(), 6, -1);

      for (int i = 0; i < a.length; i++) {
         a[i] = MathArrays.buildArray(this.getField(), i + 1);
      }

      a[0][0] = this.fraction(1, 5);
      a[1][0] = this.fraction(3, 40);
      a[1][1] = this.fraction(9, 40);
      a[2][0] = this.fraction(44, 45);
      a[2][1] = this.fraction(-56, 15);
      a[2][2] = this.fraction(32, 9);
      a[3][0] = this.fraction(19372, 6561);
      a[3][1] = this.fraction(-25360, 2187);
      a[3][2] = this.fraction(64448, 6561);
      a[3][3] = this.fraction(-212, 729);
      a[4][0] = this.fraction(9017, 3168);
      a[4][1] = this.fraction(-355, 33);
      a[4][2] = this.fraction(46732, 5247);
      a[4][3] = this.fraction(49, 176);
      a[4][4] = this.fraction(-5103, 18656);
      a[5][0] = this.fraction(35, 384);
      a[5][1] = this.getField().getZero();
      a[5][2] = this.fraction(500, 1113);
      a[5][3] = this.fraction(125, 192);
      a[5][4] = this.fraction(-2187, 6784);
      a[5][5] = this.fraction(11, 84);
      return a;
   }

   @Override
   public T[] getB() {
      T[] b = MathArrays.buildArray(this.getField(), 7);
      b[0] = this.fraction(35, 384);
      b[1] = this.getField().getZero();
      b[2] = this.fraction(500, 1113);
      b[3] = this.fraction(125, 192);
      b[4] = this.fraction(-2187, 6784);
      b[5] = this.fraction(11, 84);
      b[6] = this.getField().getZero();
      return b;
   }

   protected DormandPrince54FieldStepInterpolator<T> createInterpolator(
      boolean forward,
      T[][] yDotK,
      FieldODEStateAndDerivative<T> globalPreviousState,
      FieldODEStateAndDerivative<T> globalCurrentState,
      FieldEquationsMapper<T> mapper
   ) {
      return new DormandPrince54FieldStepInterpolator<>(
         this.getField(), forward, yDotK, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, mapper
      );
   }

   @Override
   public int getOrder() {
      return 5;
   }

   @Override
   protected T estimateError(T[][] yDotK, T[] y0, T[] y1, T h) {
      T error = this.getField().getZero();

      for (int j = 0; j < this.mainSetDimension; j++) {
         T errSum = yDotK[0][j]
            .multiply(this.e1)
            .add(yDotK[2][j].multiply(this.e3))
            .add(yDotK[3][j].multiply(this.e4))
            .add(yDotK[4][j].multiply(this.e5))
            .add(yDotK[5][j].multiply(this.e6))
            .add(yDotK[6][j].multiply(this.e7));
         T yScale = MathUtils.max(y0[j].abs(), y1[j].abs());
         T tol = this.vecAbsoluteTolerance == null
            ? yScale.multiply(this.scalRelativeTolerance).add(this.scalAbsoluteTolerance)
            : yScale.multiply(this.vecRelativeTolerance[j]).add(this.vecAbsoluteTolerance[j]);
         T ratio = h.multiply(errSum).divide(tol);
         error = error.add(ratio.multiply(ratio));
      }

      return error.divide((double)this.mainSetDimension).sqrt();
   }
}
