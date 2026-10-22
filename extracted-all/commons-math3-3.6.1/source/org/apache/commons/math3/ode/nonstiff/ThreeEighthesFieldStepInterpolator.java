package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

class ThreeEighthesFieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T> {
   ThreeEighthesFieldStepInterpolator(
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

   protected ThreeEighthesFieldStepInterpolator<T> create(
      Field<T> newField,
      boolean newForward,
      T[][] newYDotK,
      FieldODEStateAndDerivative<T> newGlobalPreviousState,
      FieldODEStateAndDerivative<T> newGlobalCurrentState,
      FieldODEStateAndDerivative<T> newSoftPreviousState,
      FieldODEStateAndDerivative<T> newSoftCurrentState,
      FieldEquationsMapper<T> newMapper
   ) {
      return new ThreeEighthesFieldStepInterpolator<>(
         newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper
      );
   }

   @Override
   protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> mapper, T time, T theta, T thetaH, T oneMinusThetaH) {
      T coeffDot3 = theta.multiply(0.75);
      T coeffDot1 = coeffDot3.multiply(theta.multiply(4).subtract(5.0)).add(1.0);
      T coeffDot2 = coeffDot3.multiply(theta.multiply(-6).add(5.0));
      T coeffDot4 = coeffDot3.multiply(theta.multiply(2).subtract(1.0));
      T[] interpolatedState;
      T[] interpolatedDerivatives;
      if (this.getGlobalPreviousState() != null && theta.getReal() <= 0.5) {
         T s = thetaH.divide(8.0);
         T fourTheta2 = theta.multiply(theta).multiply(4);
         T coeff1 = s.multiply(fourTheta2.multiply(2).subtract(theta.multiply(15)).add(8.0));
         T coeff2 = s.multiply(theta.multiply(5).subtract(fourTheta2)).multiply(3);
         T coeff3 = s.multiply(theta).multiply(3);
         T coeff4 = s.multiply(fourTheta2.subtract(theta.multiply(3)));
         interpolatedState = this.previousStateLinearCombination((T[])(new RealFieldElement[]{coeff1, coeff2, coeff3, coeff4}));
         interpolatedDerivatives = this.derivativeLinearCombination((T[])(new RealFieldElement[]{coeffDot1, coeffDot2, coeffDot3, coeffDot4}));
      } else {
         T s = oneMinusThetaH.divide(-8.0);
         T fourTheta2 = theta.multiply(theta).multiply(4);
         T thetaPlus1 = theta.add(1.0);
         T coeff1 = s.multiply(fourTheta2.multiply(2).subtract(theta.multiply(7)).add(1.0));
         T coeff2 = s.multiply(thetaPlus1.subtract(fourTheta2)).multiply(3);
         T coeff3 = s.multiply(thetaPlus1).multiply(3);
         T coeff4 = s.multiply(thetaPlus1.add(fourTheta2));
         interpolatedState = this.currentStateLinearCombination((T[])(new RealFieldElement[]{coeff1, coeff2, coeff3, coeff4}));
         interpolatedDerivatives = this.derivativeLinearCombination((T[])(new RealFieldElement[]{coeffDot1, coeffDot2, coeffDot3, coeffDot4}));
      }

      return new FieldODEStateAndDerivative<>(time, interpolatedState, interpolatedDerivatives);
   }
}
