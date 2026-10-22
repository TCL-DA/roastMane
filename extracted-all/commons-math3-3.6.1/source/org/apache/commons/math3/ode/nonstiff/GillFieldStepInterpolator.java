package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

class GillFieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T> {
   private final T one_minus_inv_sqrt_2;
   private final T one_plus_inv_sqrt_2;

   GillFieldStepInterpolator(
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
      T sqrt = field.getZero().add(0.5).sqrt();
      this.one_minus_inv_sqrt_2 = field.getOne().subtract(sqrt);
      this.one_plus_inv_sqrt_2 = field.getOne().add(sqrt);
   }

   protected GillFieldStepInterpolator<T> create(
      Field<T> newField,
      boolean newForward,
      T[][] newYDotK,
      FieldODEStateAndDerivative<T> newGlobalPreviousState,
      FieldODEStateAndDerivative<T> newGlobalCurrentState,
      FieldODEStateAndDerivative<T> newSoftPreviousState,
      FieldODEStateAndDerivative<T> newSoftCurrentState,
      FieldEquationsMapper<T> newMapper
   ) {
      return new GillFieldStepInterpolator<>(
         newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper
      );
   }

   @Override
   protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> mapper, T time, T theta, T thetaH, T oneMinusThetaH) {
      T one = time.getField().getOne();
      T twoTheta = theta.multiply(2);
      T fourTheta2 = twoTheta.multiply(twoTheta);
      T coeffDot1 = theta.multiply(twoTheta.subtract(3.0)).add(1.0);
      T cDot23 = twoTheta.multiply(one.subtract(theta));
      T coeffDot2 = cDot23.multiply(this.one_minus_inv_sqrt_2);
      T coeffDot3 = cDot23.multiply(this.one_plus_inv_sqrt_2);
      T coeffDot4 = theta.multiply(twoTheta.subtract(1.0));
      T[] interpolatedState;
      T[] interpolatedDerivatives;
      if (this.getGlobalPreviousState() != null && theta.getReal() <= 0.5) {
         T s = thetaH.divide(6.0);
         T c23 = s.multiply(theta.multiply(6).subtract(fourTheta2));
         T coeff1 = s.multiply(fourTheta2.subtract(theta.multiply(9)).add(6.0));
         T coeff2 = c23.multiply(this.one_minus_inv_sqrt_2);
         T coeff3 = c23.multiply(this.one_plus_inv_sqrt_2);
         T coeff4 = s.multiply(fourTheta2.subtract(theta.multiply(3)));
         interpolatedState = this.previousStateLinearCombination((T[])(new RealFieldElement[]{coeff1, coeff2, coeff3, coeff4}));
         interpolatedDerivatives = this.derivativeLinearCombination((T[])(new RealFieldElement[]{coeffDot1, coeffDot2, coeffDot3, coeffDot4}));
      } else {
         T s = oneMinusThetaH.divide(-6.0);
         T c23 = s.multiply(twoTheta.add(2.0).subtract(fourTheta2));
         T coeff1 = s.multiply(fourTheta2.subtract(theta.multiply(5)).add(1.0));
         T coeff2 = c23.multiply(this.one_minus_inv_sqrt_2);
         T coeff3 = c23.multiply(this.one_plus_inv_sqrt_2);
         T coeff4 = s.multiply(fourTheta2.add(theta).add(1.0));
         interpolatedState = this.currentStateLinearCombination((T[])(new RealFieldElement[]{coeff1, coeff2, coeff3, coeff4}));
         interpolatedDerivatives = this.derivativeLinearCombination((T[])(new RealFieldElement[]{coeffDot1, coeffDot2, coeffDot3, coeffDot4}));
      }

      return new FieldODEStateAndDerivative<>(time, interpolatedState, interpolatedDerivatives);
   }
}
