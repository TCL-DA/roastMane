package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.AbstractFieldIntegrator;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldExpandableODE;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.ode.FirstOrderFieldDifferentialEquations;
import org.apache.commons.math3.util.MathArrays;

public abstract class RungeKuttaFieldIntegrator<T extends RealFieldElement<T>> extends AbstractFieldIntegrator<T> implements FieldButcherArrayProvider<T> {
   private final T[] c = this.getC();
   private final T[][] a = this.getA();
   private final T[] b = this.getB();
   private final T step;

   protected RungeKuttaFieldIntegrator(Field<T> field, String name, T step) {
      super(field, name);
      this.step = step.abs();
   }

   protected T fraction(int p, int q) {
      return this.getField().getZero().add((double)p).divide((double)q);
   }

   protected abstract RungeKuttaFieldStepInterpolator<T> createInterpolator(
      boolean var1, T[][] var2, FieldODEStateAndDerivative<T> var3, FieldODEStateAndDerivative<T> var4, FieldEquationsMapper<T> var5
   );

   @Override
   public FieldODEStateAndDerivative<T> integrate(FieldExpandableODE<T> equations, FieldODEState<T> initialState, T finalTime) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
      this.sanityChecks(initialState, finalTime);
      T t0 = initialState.getTime();
      T[] y0 = equations.getMapper().mapState(initialState);
      this.setStepStart(this.initIntegration(equations, t0, y0, finalTime));
      boolean forward = finalTime.subtract(initialState.getTime()).getReal() > 0.0;
      int stages = this.c.length + 1;
      T[][] yDotK = MathArrays.buildArray(this.getField(), stages, -1);
      T[] yTmp = MathArrays.buildArray(this.getField(), y0.length);
      if (forward) {
         if (this.getStepStart().getTime().add(this.step).subtract(finalTime).getReal() >= 0.0) {
            this.setStepSize(finalTime.subtract(this.getStepStart().getTime()));
         } else {
            this.setStepSize(this.step);
         }
      } else if (this.getStepStart().getTime().subtract(this.step).subtract(finalTime).getReal() <= 0.0) {
         this.setStepSize(finalTime.subtract(this.getStepStart().getTime()));
      } else {
         this.setStepSize(this.step.negate());
      }

      this.setIsLastStep(false);

      do {
         T[] y = equations.getMapper().mapState(this.getStepStart());
         yDotK[0] = equations.getMapper().mapDerivative(this.getStepStart());

         for (int k = 1; k < stages; k++) {
            for (int j = 0; j < y0.length; j++) {
               T sum = yDotK[0][j].multiply(this.a[k - 1][0]);

               for (int l = 1; l < k; l++) {
                  sum = sum.add(yDotK[l][j].multiply(this.a[k - 1][l]));
               }

               yTmp[j] = (T)y[j].add(this.getStepSize().multiply(sum));
            }

            yDotK[k] = this.computeDerivatives(this.getStepStart().getTime().add(this.getStepSize().multiply(this.c[k - 1])), yTmp);
         }

         for (int j = 0; j < y0.length; j++) {
            T sum = yDotK[0][j].multiply(this.b[0]);

            for (int l = 1; l < stages; l++) {
               sum = sum.add(yDotK[l][j].multiply(this.b[l]));
            }

            yTmp[j] = (T)y[j].add(this.getStepSize().multiply(sum));
         }

         T stepEnd = this.getStepStart().getTime().add(this.getStepSize());
         T[] yDotTmp = this.computeDerivatives(stepEnd, yTmp);
         FieldODEStateAndDerivative<T> stateTmp = new FieldODEStateAndDerivative<>(stepEnd, yTmp, yDotTmp);
         System.arraycopy(yTmp, 0, y, 0, y0.length);
         this.setStepStart(this.acceptStep(this.createInterpolator(forward, yDotK, this.getStepStart(), stateTmp, equations.getMapper()), finalTime));
         if (!this.isLastStep()) {
            T nextT = this.getStepStart().getTime().add(this.getStepSize());
            boolean nextIsLast = forward ? nextT.subtract(finalTime).getReal() >= 0.0 : nextT.subtract(finalTime).getReal() <= 0.0;
            if (nextIsLast) {
               this.setStepSize(finalTime.subtract(this.getStepStart().getTime()));
            }
         }
      } while (!this.isLastStep());

      FieldODEStateAndDerivative<T> finalState = this.getStepStart();
      this.setStepStart(null);
      this.setStepSize(null);
      return finalState;
   }

   public T[] singleStep(FirstOrderFieldDifferentialEquations<T> equations, T t0, T[] y0, T t) {
      T[] y = (T[])((RealFieldElement[])y0.clone());
      int stages = this.c.length + 1;
      T[][] yDotK = MathArrays.buildArray(this.getField(), stages, -1);
      T[] yTmp = (T[])((RealFieldElement[])y0.clone());
      T h = t.subtract(t0);
      yDotK[0] = equations.computeDerivatives(t0, y);

      for (int k = 1; k < stages; k++) {
         for (int j = 0; j < y0.length; j++) {
            T sum = yDotK[0][j].multiply(this.a[k - 1][0]);

            for (int l = 1; l < k; l++) {
               sum = sum.add(yDotK[l][j].multiply(this.a[k - 1][l]));
            }

            yTmp[j] = y[j].add(h.multiply(sum));
         }

         yDotK[k] = equations.computeDerivatives(t0.add(h.multiply(this.c[k - 1])), yTmp);
      }

      for (int j = 0; j < y0.length; j++) {
         T sum = yDotK[0][j].multiply(this.b[0]);

         for (int l = 1; l < stages; l++) {
            sum = sum.add(yDotK[l][j].multiply(this.b[l]));
         }

         y[j] = y[j].add(h.multiply(sum));
      }

      return y;
   }
}
