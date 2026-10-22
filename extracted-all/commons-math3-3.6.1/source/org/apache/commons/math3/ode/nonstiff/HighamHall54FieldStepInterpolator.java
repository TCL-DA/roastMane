package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

class HighamHall54FieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T> {
   HighamHall54FieldStepInterpolator(
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

   protected HighamHall54FieldStepInterpolator<T> create(
      Field<T> newField,
      boolean newForward,
      T[][] newYDotK,
      FieldODEStateAndDerivative<T> newGlobalPreviousState,
      FieldODEStateAndDerivative<T> newGlobalCurrentState,
      FieldODEStateAndDerivative<T> newSoftPreviousState,
      FieldODEStateAndDerivative<T> newSoftCurrentState,
      FieldEquationsMapper<T> newMapper
   ) {
      return new HighamHall54FieldStepInterpolator<>(
         newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper
      );
   }

   @Override
   protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> mapper, T time, T theta, T thetaH, T oneMinusThetaH) {
      T bDot0 = theta.multiply(theta.multiply(theta.multiply(-10.0).add(16.0)).add(-7.5)).add(1.0);
      T bDot1 = time.getField().getZero();
      T bDot2 = theta.multiply(theta.multiply(theta.multiply(67.5).add(-91.125)).add(28.6875));
      T bDot3 = theta.multiply(theta.multiply(theta.multiply(-120.0).add(152.0)).add(-44.0));
      T bDot4 = theta.multiply(theta.multiply(theta.multiply(62.5).add(-78.125)).add(23.4375));
      T bDot5 = theta.multiply(0.625).multiply(theta.multiply(2).subtract(1.0));
      T[] interpolatedState;
      T[] interpolatedDerivatives;
      if (this.getGlobalPreviousState() != null && theta.getReal() <= 0.5) {
         T b0 = thetaH.multiply(theta.multiply(theta.multiply(theta.multiply(-2.5).add(5.333333333333333)).add(-3.75)).add(1.0));
         T b1 = time.getField().getZero();
         T b2 = thetaH.multiply(theta.multiply(theta.multiply(theta.multiply(16.875).add(-30.375)).add(14.34375)));
         T b3 = thetaH.multiply(theta.multiply(theta.multiply(theta.multiply(-30.0).add(50.666666666666664)).add(-22.0)));
         T b4 = thetaH.multiply(theta.multiply(theta.multiply(theta.multiply(15.625).add(-26.041666666666668)).add(11.71875)));
         T b5 = thetaH.multiply(theta.multiply(theta.multiply(0.4166666666666667).add(-0.3125)));
         interpolatedState = this.previousStateLinearCombination((T[])(new RealFieldElement[]{b0, b1, b2, b3, b4, b5}));
         interpolatedDerivatives = this.derivativeLinearCombination((T[])(new RealFieldElement[]{bDot0, bDot1, bDot2, bDot3, bDot4, bDot5}));
      } else {
         T theta2 = theta.multiply(theta);
         T h = thetaH.divide(theta);
         T b0 = h.multiply(
            theta.multiply(theta.multiply(theta.multiply(theta.multiply(-2.5).add(5.333333333333333)).add(-3.75)).add(1.0)).add(-0.08333333333333333)
         );
         T b1 = time.getField().getZero();
         T b2 = h.multiply(theta2.multiply(theta.multiply(theta.multiply(16.875).add(-30.375)).add(14.34375)).add(-0.84375));
         T b3 = h.multiply(theta2.multiply(theta.multiply(theta.multiply(-30.0).add(50.666666666666664)).add(-22.0)).add(1.3333333333333333));
         T b4 = h.multiply(theta2.multiply(theta.multiply(theta.multiply(15.625).add(-26.041666666666668)).add(11.71875)).add(-1.3020833333333333));
         T b5 = h.multiply(theta2.multiply(theta.multiply(0.4166666666666667).add(-0.3125)).add(-0.10416666666666667));
         interpolatedState = this.currentStateLinearCombination((T[])(new RealFieldElement[]{b0, b1, b2, b3, b4, b5}));
         interpolatedDerivatives = this.derivativeLinearCombination((T[])(new RealFieldElement[]{bDot0, bDot1, bDot2, bDot3, bDot4, bDot5}));
      }

      return new FieldODEStateAndDerivative<>(time, interpolatedState, interpolatedDerivatives);
   }
}
