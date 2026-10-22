package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

class LutherFieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T> {
   private final T c5a;
   private final T c5b;
   private final T c5c;
   private final T c5d;
   private final T c6a;
   private final T c6b;
   private final T c6c;
   private final T c6d;
   private final T d5a;
   private final T d5b;
   private final T d5c;
   private final T d6a;
   private final T d6b;
   private final T d6c;

   LutherFieldStepInterpolator(
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
      T q = field.getZero().add(21.0).sqrt();
      this.c5a = q.multiply(-49).add(-49.0);
      this.c5b = q.multiply(287).add(392.0);
      this.c5c = q.multiply(-357).add(-637.0);
      this.c5d = q.multiply(343).add(833.0);
      this.c6a = q.multiply(49).add(-49.0);
      this.c6b = q.multiply(-287).add(392.0);
      this.c6c = q.multiply(357).add(-637.0);
      this.c6d = q.multiply(-343).add(833.0);
      this.d5a = q.multiply(49).add(49.0);
      this.d5b = q.multiply(-847).add(-1372.0);
      this.d5c = q.multiply(1029).add(2254.0);
      this.d6a = q.multiply(-49).add(49.0);
      this.d6b = q.multiply(847).add(-1372.0);
      this.d6c = q.multiply(-1029).add(2254.0);
   }

   protected LutherFieldStepInterpolator<T> create(
      Field<T> newField,
      boolean newForward,
      T[][] newYDotK,
      FieldODEStateAndDerivative<T> newGlobalPreviousState,
      FieldODEStateAndDerivative<T> newGlobalCurrentState,
      FieldODEStateAndDerivative<T> newSoftPreviousState,
      FieldODEStateAndDerivative<T> newSoftCurrentState,
      FieldEquationsMapper<T> newMapper
   ) {
      return new LutherFieldStepInterpolator<>(
         newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper
      );
   }

   @Override
   protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> mapper, T time, T theta, T thetaH, T oneMinusThetaH) {
      T coeffDot1 = theta.multiply(theta.multiply(theta.multiply(theta.multiply(21).add(-47.0)).add(36.0)).add(-10.8)).add(1.0);
      T coeffDot2 = time.getField().getZero();
      T coeffDot3 = theta.multiply(
         theta.multiply(theta.multiply(theta.multiply(112).add(-202.66666666666666)).add(106.66666666666667)).add(-13.866666666666667)
      );
      T coeffDot4 = theta.multiply(theta.multiply(theta.multiply(theta.multiply(-113.4).add(194.4)).add(-97.2)).add(12.96));
      T coeffDot5 = theta.multiply(
         theta.multiply(theta.multiply(theta.multiply(this.c5a.divide(5.0)).add(this.c5b.divide(15.0))).add(this.c5c.divide(30.0))).add(this.c5d.divide(150.0))
      );
      T coeffDot6 = theta.multiply(
         theta.multiply(theta.multiply(theta.multiply(this.c6a.divide(5.0)).add(this.c6b.divide(15.0))).add(this.c6c.divide(30.0))).add(this.c6d.divide(150.0))
      );
      T coeffDot7 = theta.multiply(theta.multiply(theta.multiply(3.0).add(-3.0)).add(0.6));
      T[] interpolatedState;
      T[] interpolatedDerivatives;
      if (this.getGlobalPreviousState() != null && theta.getReal() <= 0.5) {
         T coeff1 = thetaH.multiply(theta.multiply(theta.multiply(theta.multiply(theta.multiply(4.2).add(-11.75)).add(12.0)).add(-5.4)).add(1.0));
         T coeff2 = time.getField().getZero();
         T coeff3 = thetaH.multiply(
            theta.multiply(theta.multiply(theta.multiply(theta.multiply(22.4).add(-50.666666666666664)).add(35.55555555555556)).add(-6.933333333333334))
         );
         T coeff4 = thetaH.multiply(theta.multiply(theta.multiply(theta.multiply(theta.multiply(-22.68).add(48.6)).add(-32.4)).add(6.48)));
         T coeff5 = thetaH.multiply(
            theta.multiply(
               theta.multiply(theta.multiply(theta.multiply(this.c5a.divide(25.0)).add(this.c5b.divide(60.0))).add(this.c5c.divide(90.0)))
                  .add(this.c5d.divide(300.0))
            )
         );
         T coeff6 = thetaH.multiply(
            theta.multiply(
               theta.multiply(theta.multiply(theta.multiply(this.c6a.divide(25.0)).add(this.c6b.divide(60.0))).add(this.c6c.divide(90.0)))
                  .add(this.c6d.divide(300.0))
            )
         );
         T coeff7 = thetaH.multiply(theta.multiply(theta.multiply(theta.multiply(0.75).add(-1.0)).add(0.3)));
         interpolatedState = this.previousStateLinearCombination((T[])(new RealFieldElement[]{coeff1, coeff2, coeff3, coeff4, coeff5, coeff6, coeff7}));
         interpolatedDerivatives = this.derivativeLinearCombination(
            (T[])(new RealFieldElement[]{coeffDot1, coeffDot2, coeffDot3, coeffDot4, coeffDot5, coeffDot6, coeffDot7})
         );
      } else {
         T coeff1 = oneMinusThetaH.multiply(theta.multiply(theta.multiply(theta.multiply(theta.multiply(-4.2).add(7.55)).add(-4.45)).add(0.95)).add(-0.05));
         T coeff2 = time.getField().getZero();
         T coeff3 = oneMinusThetaH.multiply(
            theta.multiply(theta.multiply(theta.multiply(theta.multiply(-22.4).add(28.266666666666666)).add(-7.288888888888889)).add(-0.35555555555555557))
               .add(-0.35555555555555557)
         );
         T coeff4 = oneMinusThetaH.multiply(theta.multiply(theta.multiply(theta.multiply(theta.multiply(22.68).add(-25.92)).add(6.48))));
         T coeff5 = oneMinusThetaH.multiply(
            theta.multiply(
                  theta.multiply(theta.multiply(theta.multiply(this.d5a.divide(25.0)).add(this.d5b.divide(300.0))).add(this.d5c.divide(900.0)))
                     .add(-0.2722222222222222)
               )
               .add(-0.2722222222222222)
         );
         T coeff6 = oneMinusThetaH.multiply(
            theta.multiply(
                  theta.multiply(theta.multiply(theta.multiply(this.d6a.divide(25.0)).add(this.d6b.divide(300.0))).add(this.d6c.divide(900.0)))
                     .add(-0.2722222222222222)
               )
               .add(-0.2722222222222222)
         );
         T coeff7 = oneMinusThetaH.multiply(theta.multiply(theta.multiply(theta.multiply(-0.75).add(0.25)).add(-0.05)).add(-0.05));
         interpolatedState = this.currentStateLinearCombination((T[])(new RealFieldElement[]{coeff1, coeff2, coeff3, coeff4, coeff5, coeff6, coeff7}));
         interpolatedDerivatives = this.derivativeLinearCombination(
            (T[])(new RealFieldElement[]{coeffDot1, coeffDot2, coeffDot3, coeffDot4, coeffDot5, coeffDot6, coeffDot7})
         );
      }

      return new FieldODEStateAndDerivative<>(time, interpolatedState, interpolatedDerivatives);
   }
}
