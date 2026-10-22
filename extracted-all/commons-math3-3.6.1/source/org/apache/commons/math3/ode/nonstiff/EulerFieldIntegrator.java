package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;

public class EulerFieldIntegrator<T extends RealFieldElement<T>> extends RungeKuttaFieldIntegrator<T> {
   public EulerFieldIntegrator(Field<T> field, T step) {
      super(field, "Euler", step);
   }

   @Override
   public T[] getC() {
      return MathArrays.buildArray(this.getField(), 0);
   }

   @Override
   public T[][] getA() {
      return MathArrays.buildArray(this.getField(), 0, 0);
   }

   @Override
   public T[] getB() {
      T[] b = MathArrays.buildArray(this.getField(), 1);
      b[0] = this.getField().getOne();
      return b;
   }

   protected EulerFieldStepInterpolator<T> createInterpolator(
      boolean forward,
      T[][] yDotK,
      FieldODEStateAndDerivative<T> globalPreviousState,
      FieldODEStateAndDerivative<T> globalCurrentState,
      FieldEquationsMapper<T> mapper
   ) {
      return new EulerFieldStepInterpolator<>(
         this.getField(), forward, yDotK, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, mapper
      );
   }
}
