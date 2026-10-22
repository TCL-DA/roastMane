package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

class EulerFieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T> {
   EulerFieldStepInterpolator(
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

   protected EulerFieldStepInterpolator<T> create(
      Field<T> newField,
      boolean newForward,
      T[][] newYDotK,
      FieldODEStateAndDerivative<T> newGlobalPreviousState,
      FieldODEStateAndDerivative<T> newGlobalCurrentState,
      FieldODEStateAndDerivative<T> newSoftPreviousState,
      FieldODEStateAndDerivative<T> newSoftCurrentState,
      FieldEquationsMapper<T> newMapper
   ) {
      return new EulerFieldStepInterpolator<>(
         newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper
      );
   }

   @Override
   protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> mapper, T time, T theta, T thetaH, T oneMinusThetaH) {
      T[] interpolatedState;
      T[] interpolatedDerivatives;
      if (this.getGlobalPreviousState() != null && theta.getReal() <= 0.5) {
         interpolatedState = this.previousStateLinearCombination((T[])(new RealFieldElement[]{thetaH}));
         interpolatedDerivatives = this.derivativeLinearCombination((T[])(new RealFieldElement[]{time.getField().getOne()}));
      } else {
         interpolatedState = this.currentStateLinearCombination((T[])(new RealFieldElement[]{oneMinusThetaH.negate()}));
         interpolatedDerivatives = this.derivativeLinearCombination((T[])(new RealFieldElement[]{time.getField().getOne()}));
      }

      return new FieldODEStateAndDerivative<>(time, interpolatedState, interpolatedDerivatives);
   }
}
