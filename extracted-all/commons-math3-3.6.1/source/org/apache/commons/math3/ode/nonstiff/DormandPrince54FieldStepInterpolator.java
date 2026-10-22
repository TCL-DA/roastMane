package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

class DormandPrince54FieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T> {
   private final T a70;
   private final T a72;
   private final T a73;
   private final T a74;
   private final T a75;
   private final T d0;
   private final T d2;
   private final T d3;
   private final T d4;
   private final T d5;
   private final T d6;

   DormandPrince54FieldStepInterpolator(
      Field<T> field,
      boolean forward,
      T[][] yDotK,
      FieldODEStateAndDerivative<T> globalPreviousState,
      FieldODEStateAndDerivative<T> globalCurrentState,
      FieldODEStateAndDerivative<T> softPreviousState,
      FieldODEStateAndDerivative<T> softCurrentState,
      FieldEquationsMapper<T> mapper
   ) {
      super(field, forward, yDotK, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, mapper);
      T one = field.getOne();
      this.a70 = one.multiply(35.0).divide(384.0);
      this.a72 = one.multiply(500.0).divide(1113.0);
      this.a73 = one.multiply(125.0).divide(192.0);
      this.a74 = one.multiply(-2187.0).divide(6784.0);
      this.a75 = one.multiply(11.0).divide(84.0);
      this.d0 = one.multiply(-1.2715105075E10).divide(1.1282082432E10);
      this.d2 = one.multiply(8.74874797E10).divide(3.2700410799E10);
      this.d3 = one.multiply(-1.0690763975E10).divide(1.880347072E9);
      this.d4 = one.multiply(7.01980252875E11).divide(1.99316789632E11);
      this.d5 = one.multiply(-1.453857185E9).divide(8.22651844E8);
      this.d6 = one.multiply(6.9997945E7).divide(2.9380423E7);
   }

   protected DormandPrince54FieldStepInterpolator<T> create(
      Field<T> newField,
      boolean newForward,
      T[][] newYDotK,
      FieldODEStateAndDerivative<T> newGlobalPreviousState,
      FieldODEStateAndDerivative<T> newGlobalCurrentState,
      FieldODEStateAndDerivative<T> newSoftPreviousState,
      FieldODEStateAndDerivative<T> newSoftCurrentState,
      FieldEquationsMapper<T> newMapper
   ) {
      return new DormandPrince54FieldStepInterpolator<>(
         newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper
      );
   }

   @Override
   protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> mapper, T time, T theta, T thetaH, T oneMinusThetaH) {
      T one = time.getField().getOne();
      T eta = one.subtract(theta);
      T twoTheta = theta.multiply(2);
      T dot2 = one.subtract(twoTheta);
      T dot3 = theta.multiply(theta.multiply(-3).add(2.0));
      T dot4 = twoTheta.multiply(theta.multiply(twoTheta.subtract(3.0)).add(1.0));
      T[] interpolatedState;
      T[] interpolatedDerivatives;
      if (this.getGlobalPreviousState() != null && theta.getReal() <= 0.5) {
         T f2 = thetaH.multiply(eta);
         T f3 = f2.multiply(theta);
         T f4 = f3.multiply(eta);
         T coeff0 = thetaH.multiply(this.a70)
            .subtract(f2.multiply(this.a70.subtract(1.0)))
            .add(f3.multiply(this.a70.multiply(2).subtract(1.0)))
            .add(f4.multiply(this.d0));
         T coeff1 = time.getField().getZero();
         T coeff2 = thetaH.multiply(this.a72).subtract(f2.multiply(this.a72)).add(f3.multiply(this.a72.multiply(2))).add(f4.multiply(this.d2));
         T coeff3 = thetaH.multiply(this.a73).subtract(f2.multiply(this.a73)).add(f3.multiply(this.a73.multiply(2))).add(f4.multiply(this.d3));
         T coeff4 = thetaH.multiply(this.a74).subtract(f2.multiply(this.a74)).add(f3.multiply(this.a74.multiply(2))).add(f4.multiply(this.d4));
         T coeff5 = thetaH.multiply(this.a75).subtract(f2.multiply(this.a75)).add(f3.multiply(this.a75.multiply(2))).add(f4.multiply(this.d5));
         T coeff6 = f4.multiply(this.d6).subtract(f3);
         T coeffDot0 = this.a70
            .subtract(dot2.multiply(this.a70.subtract(1.0)))
            .add(dot3.multiply(this.a70.multiply(2).subtract(1.0)))
            .add(dot4.multiply(this.d0));
         T coeffDot1 = time.getField().getZero();
         T coeffDot2 = this.a72.subtract(dot2.multiply(this.a72)).add(dot3.multiply(this.a72.multiply(2))).add(dot4.multiply(this.d2));
         T coeffDot3 = this.a73.subtract(dot2.multiply(this.a73)).add(dot3.multiply(this.a73.multiply(2))).add(dot4.multiply(this.d3));
         T coeffDot4 = this.a74.subtract(dot2.multiply(this.a74)).add(dot3.multiply(this.a74.multiply(2))).add(dot4.multiply(this.d4));
         T coeffDot5 = this.a75.subtract(dot2.multiply(this.a75)).add(dot3.multiply(this.a75.multiply(2))).add(dot4.multiply(this.d5));
         T coeffDot6 = dot4.multiply(this.d6).subtract(dot3);
         interpolatedState = this.previousStateLinearCombination((T[])(new RealFieldElement[]{coeff0, coeff1, coeff2, coeff3, coeff4, coeff5, coeff6}));
         interpolatedDerivatives = this.derivativeLinearCombination(
            (T[])(new RealFieldElement[]{coeffDot0, coeffDot1, coeffDot2, coeffDot3, coeffDot4, coeffDot5, coeffDot6})
         );
      } else {
         T f1 = oneMinusThetaH.negate();
         T f2 = oneMinusThetaH.multiply(theta);
         T f3 = f2.multiply(theta);
         T f4 = f3.multiply(eta);
         T coeff0 = f1.multiply(this.a70)
            .subtract(f2.multiply(this.a70.subtract(1.0)))
            .add(f3.multiply(this.a70.multiply(2).subtract(1.0)))
            .add(f4.multiply(this.d0));
         T coeff1 = time.getField().getZero();
         T coeff2 = f1.multiply(this.a72).subtract(f2.multiply(this.a72)).add(f3.multiply(this.a72.multiply(2))).add(f4.multiply(this.d2));
         T coeff3 = f1.multiply(this.a73).subtract(f2.multiply(this.a73)).add(f3.multiply(this.a73.multiply(2))).add(f4.multiply(this.d3));
         T coeff4 = f1.multiply(this.a74).subtract(f2.multiply(this.a74)).add(f3.multiply(this.a74.multiply(2))).add(f4.multiply(this.d4));
         T coeff5 = f1.multiply(this.a75).subtract(f2.multiply(this.a75)).add(f3.multiply(this.a75.multiply(2))).add(f4.multiply(this.d5));
         T coeff6 = f4.multiply(this.d6).subtract(f3);
         T coeffDot0 = this.a70
            .subtract(dot2.multiply(this.a70.subtract(1.0)))
            .add(dot3.multiply(this.a70.multiply(2).subtract(1.0)))
            .add(dot4.multiply(this.d0));
         T coeffDot1 = time.getField().getZero();
         T coeffDot2 = this.a72.subtract(dot2.multiply(this.a72)).add(dot3.multiply(this.a72.multiply(2))).add(dot4.multiply(this.d2));
         T coeffDot3 = this.a73.subtract(dot2.multiply(this.a73)).add(dot3.multiply(this.a73.multiply(2))).add(dot4.multiply(this.d3));
         T coeffDot4 = this.a74.subtract(dot2.multiply(this.a74)).add(dot3.multiply(this.a74.multiply(2))).add(dot4.multiply(this.d4));
         T coeffDot5 = this.a75.subtract(dot2.multiply(this.a75)).add(dot3.multiply(this.a75.multiply(2))).add(dot4.multiply(this.d5));
         T coeffDot6 = dot4.multiply(this.d6).subtract(dot3);
         interpolatedState = this.currentStateLinearCombination((T[])(new RealFieldElement[]{coeff0, coeff1, coeff2, coeff3, coeff4, coeff5, coeff6}));
         interpolatedDerivatives = this.derivativeLinearCombination(
            (T[])(new RealFieldElement[]{coeffDot0, coeffDot1, coeffDot2, coeffDot3, coeffDot4, coeffDot5, coeffDot6})
         );
      }

      return new FieldODEStateAndDerivative<>(time, interpolatedState, interpolatedDerivatives);
   }
}
