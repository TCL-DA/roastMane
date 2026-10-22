package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

class MidpointFieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T> {
   MidpointFieldStepInterpolator(
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

   protected MidpointFieldStepInterpolator<T> create(
      Field<T> newField,
      boolean newForward,
      T[][] newYDotK,
      FieldODEStateAndDerivative<T> newGlobalPreviousState,
      FieldODEStateAndDerivative<T> newGlobalCurrentState,
      FieldODEStateAndDerivative<T> newSoftPreviousState,
      FieldODEStateAndDerivative<T> newSoftCurrentState,
      FieldEquationsMapper<T> newMapper
   ) {
      return new MidpointFieldStepInterpolator<>(
         newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper
      );
   }

   @Override
   protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> mapper, T time, T theta, T thetaH, T oneMinusThetaH) {
      T coeffDot2 = theta.multiply(2);
      T coeffDot1 = time.getField().getOne().subtract(coeffDot2);
      T[] interpolatedState;
      T[] interpolatedDerivatives;
      if (this.getGlobalPreviousState() != null && theta.getReal() <= 0.5) {
         T coeff1 = theta.multiply(oneMinusThetaH);
         T coeff2 = theta.multiply(thetaH);
         interpolatedState = this.previousStateLinearCombination((T[])(new RealFieldElement[]{coeff1, coeff2}));
         interpolatedDerivatives = this.derivativeLinearCombination((T[])(new RealFieldElement[]{coeffDot1, coeffDot2}));
      } else {
         T coeff1 = oneMinusThetaH.multiply(theta);
         T coeff2 = oneMinusThetaH.multiply(theta.add(1.0)).negate();
         interpolatedState = this.currentStateLinearCombination((T[])(new RealFieldElement[]{coeff1, coeff2}));
         interpolatedDerivatives = this.derivativeLinearCombination((T[])(new RealFieldElement[]{coeffDot1, coeffDot2}));
      }

      return new FieldODEStateAndDerivative<>(time, interpolatedState, interpolatedDerivatives);
   }
}
