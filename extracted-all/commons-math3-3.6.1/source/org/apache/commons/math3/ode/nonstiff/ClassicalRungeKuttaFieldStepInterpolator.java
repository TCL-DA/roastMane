package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

class ClassicalRungeKuttaFieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T> {
   ClassicalRungeKuttaFieldStepInterpolator(
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
   }

   protected ClassicalRungeKuttaFieldStepInterpolator<T> create(
      Field<T> newField,
      boolean newForward,
      T[][] newYDotK,
      FieldODEStateAndDerivative<T> newGlobalPreviousState,
      FieldODEStateAndDerivative<T> newGlobalCurrentState,
      FieldODEStateAndDerivative<T> newSoftPreviousState,
      FieldODEStateAndDerivative<T> newSoftCurrentState,
      FieldEquationsMapper<T> newMapper
   ) {
      return new ClassicalRungeKuttaFieldStepInterpolator<>(
         newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper
      );
   }

   @Override
   protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> mapper, T time, T theta, T thetaH, T oneMinusThetaH) {
      T one = time.getField().getOne();
      T oneMinusTheta = one.subtract(theta);
      T oneMinus2Theta = one.subtract(theta.multiply(2));
      T coeffDot1 = oneMinusTheta.multiply(oneMinus2Theta);
      T coeffDot23 = theta.multiply(oneMinusTheta).multiply(2);
      T coeffDot4 = theta.multiply(oneMinus2Theta).negate();
      T[] interpolatedState;
      T[] interpolatedDerivatives;
      if (this.getGlobalPreviousState() != null && theta.getReal() <= 0.5) {
         T fourTheta2 = theta.multiply(theta).multiply(4);
         T s = thetaH.divide(6.0);
         T coeff1 = s.multiply(fourTheta2.subtract(theta.multiply(9)).add(6.0));
         T coeff23 = s.multiply(theta.multiply(6).subtract(fourTheta2));
         T coeff4 = s.multiply(fourTheta2.subtract(theta.multiply(3)));
         interpolatedState = this.previousStateLinearCombination((T[])(new RealFieldElement[]{coeff1, coeff23, coeff23, coeff4}));
         interpolatedDerivatives = this.derivativeLinearCombination((T[])(new RealFieldElement[]{coeffDot1, coeffDot23, coeffDot23, coeffDot4}));
      } else {
         T fourTheta = theta.multiply(4);
         T s = oneMinusThetaH.divide(6.0);
         T coeff1 = s.multiply(theta.multiply(fourTheta.negate().add(5.0)).subtract(1.0));
         T coeff23 = s.multiply(theta.multiply(fourTheta.subtract(2.0)).subtract(2.0));
         T coeff4 = s.multiply(theta.multiply(fourTheta.negate().subtract(1.0)).subtract(1.0));
         interpolatedState = this.currentStateLinearCombination((T[])(new RealFieldElement[]{coeff1, coeff23, coeff23, coeff4}));
         interpolatedDerivatives = this.derivativeLinearCombination((T[])(new RealFieldElement[]{coeffDot1, coeffDot23, coeffDot23, coeffDot4}));
      }

      return new FieldODEStateAndDerivative<>(time, interpolatedState, interpolatedDerivatives);
   }
}
