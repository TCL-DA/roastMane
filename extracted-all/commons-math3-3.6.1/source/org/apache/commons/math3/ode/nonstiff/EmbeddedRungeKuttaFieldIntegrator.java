package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldExpandableODE;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public abstract class EmbeddedRungeKuttaFieldIntegrator<T extends RealFieldElement<T>>
   extends AdaptiveStepsizeFieldIntegrator<T>
   implements FieldButcherArrayProvider<T> {
   private final int fsal;
   private final T[] c;
   private final T[][] a;
   private final T[] b;
   private final T exp;
   private T safety;
   private T minReduction;
   private T maxGrowth;

   protected EmbeddedRungeKuttaFieldIntegrator(
      Field<T> field, String name, int fsal, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance
   ) {
      super(field, name, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
      this.fsal = fsal;
      this.c = this.getC();
      this.a = this.getA();
      this.b = this.getB();
      this.exp = field.getOne().divide((double)(-this.getOrder()));
      this.setSafety(field.getZero().add(0.9));
      this.setMinReduction(field.getZero().add(0.2));
      this.setMaxGrowth(field.getZero().add(10.0));
   }

   protected EmbeddedRungeKuttaFieldIntegrator(
      Field<T> field, String name, int fsal, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance
   ) {
      super(field, name, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
      this.fsal = fsal;
      this.c = this.getC();
      this.a = this.getA();
      this.b = this.getB();
      this.exp = field.getOne().divide((double)(-this.getOrder()));
      this.setSafety(field.getZero().add(0.9));
      this.setMinReduction(field.getZero().add(0.2));
      this.setMaxGrowth(field.getZero().add(10.0));
   }

   protected T fraction(int p, int q) {
      return this.getField().getOne().multiply(p).divide((double)q);
   }

   protected T fraction(double p, double q) {
      return this.getField().getOne().multiply(p).divide(q);
   }

   protected abstract RungeKuttaFieldStepInterpolator<T> createInterpolator(
      boolean var1, T[][] var2, FieldODEStateAndDerivative<T> var3, FieldODEStateAndDerivative<T> var4, FieldEquationsMapper<T> var5
   );

   public abstract int getOrder();

   public T getSafety() {
      return this.safety;
   }

   public void setSafety(T safety) {
      this.safety = safety;
   }

   @Override
   public FieldODEStateAndDerivative<T> integrate(FieldExpandableODE<T> equations, FieldODEState<T> initialState, T finalTime) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
      this.sanityChecks(initialState, finalTime);
      T t0 = initialState.getTime();
      T[] y0 = equations.getMapper().mapState(initialState);
      this.setStepStart(this.initIntegration(equations, t0, y0, finalTime));
      boolean forward = finalTime.subtract(initialState.getTime()).getReal() > 0.0;
      int stages = this.c.length + 1;
      T[] y = y0;
      T[][] yDotK = MathArrays.buildArray(this.getField(), stages, -1);
      T[] yTmp = MathArrays.buildArray(this.getField(), y0.length);
      T hNew = this.getField().getZero();
      boolean firstTime = true;
      this.setIsLastStep(false);

      do {
         T error = this.getField().getZero().add(10.0);

         while (error.subtract(1.0).getReal() >= 0.0) {
            y = equations.getMapper().mapState(this.getStepStart());
            yDotK[0] = equations.getMapper().mapDerivative(this.getStepStart());
            if (firstTime) {
               T[] scale = MathArrays.buildArray(this.getField(), this.mainSetDimension);
               if (this.vecAbsoluteTolerance == null) {
                  for (int i = 0; i < scale.length; i++) {
                     scale[i] = y[i].abs().multiply(this.scalRelativeTolerance).add(this.scalAbsoluteTolerance);
                  }
               } else {
                  for (int i = 0; i < scale.length; i++) {
                     scale[i] = y[i].abs().multiply(this.vecRelativeTolerance[i]).add(this.vecAbsoluteTolerance[i]);
                  }
               }

               hNew = this.initializeStep(forward, this.getOrder(), scale, this.getStepStart(), equations.getMapper());
               firstTime = false;
            }

            this.setStepSize(hNew);
            if (forward) {
               if (this.getStepStart().getTime().add(this.getStepSize()).subtract(finalTime).getReal() >= 0.0) {
                  this.setStepSize(finalTime.subtract(this.getStepStart().getTime()));
               }
            } else if (this.getStepStart().getTime().add(this.getStepSize()).subtract(finalTime).getReal() <= 0.0) {
               this.setStepSize(finalTime.subtract(this.getStepStart().getTime()));
            }

            for (int k = 1; k < stages; k++) {
               for (int j = 0; j < y0.length; j++) {
                  T sum = yDotK[0][j].multiply(this.a[k - 1][0]);

                  for (int l = 1; l < k; l++) {
                     sum = sum.add(yDotK[l][j].multiply(this.a[k - 1][l]));
                  }

                  yTmp[j] = y[j].add(this.getStepSize().multiply(sum));
               }

               yDotK[k] = this.computeDerivatives(this.getStepStart().getTime().add(this.getStepSize().multiply(this.c[k - 1])), yTmp);
            }

            for (int j = 0; j < y0.length; j++) {
               T sum = yDotK[0][j].multiply(this.b[0]);

               for (int l = 1; l < stages; l++) {
                  sum = sum.add(yDotK[l][j].multiply(this.b[l]));
               }

               yTmp[j] = y[j].add(this.getStepSize().multiply(sum));
            }

            error = this.estimateError(yDotK, y, yTmp, this.getStepSize());
            if (error.subtract(1.0).getReal() >= 0.0) {
               T factor = MathUtils.min(this.maxGrowth, MathUtils.max(this.minReduction, this.safety.multiply(error.pow(this.exp))));
               hNew = this.filterStep(this.getStepSize().multiply(factor), forward, false);
            }
         }

         T stepEnd = this.getStepStart().getTime().add(this.getStepSize());
         T[] yDotTmp = this.fsal >= 0 ? yDotK[this.fsal] : this.computeDerivatives(stepEnd, yTmp);
         FieldODEStateAndDerivative<T> stateTmp = new FieldODEStateAndDerivative<>(stepEnd, yTmp, yDotTmp);
         System.arraycopy(yTmp, 0, y, 0, y0.length);
         this.setStepStart(this.acceptStep(this.createInterpolator(forward, yDotK, this.getStepStart(), stateTmp, equations.getMapper()), finalTime));
         if (!this.isLastStep()) {
            T factor = MathUtils.min(this.maxGrowth, MathUtils.max(this.minReduction, this.safety.multiply(error.pow(this.exp))));
            T scaledH = this.getStepSize().multiply(factor);
            T nextT = this.getStepStart().getTime().add(scaledH);
            boolean nextIsLast = forward ? nextT.subtract(finalTime).getReal() >= 0.0 : nextT.subtract(finalTime).getReal() <= 0.0;
            hNew = this.filterStep(scaledH, forward, nextIsLast);
            T filteredNextT = this.getStepStart().getTime().add(hNew);
            boolean filteredNextIsLast = forward ? filteredNextT.subtract(finalTime).getReal() >= 0.0 : filteredNextT.subtract(finalTime).getReal() <= 0.0;
            if (filteredNextIsLast) {
               hNew = finalTime.subtract(this.getStepStart().getTime());
            }
         }
      } while (!this.isLastStep());

      FieldODEStateAndDerivative<T> finalState = this.getStepStart();
      this.resetInternalState();
      return finalState;
   }

   public T getMinReduction() {
      return this.minReduction;
   }

   public void setMinReduction(T minReduction) {
      this.minReduction = minReduction;
   }

   public T getMaxGrowth() {
      return this.maxGrowth;
   }

   public void setMaxGrowth(T maxGrowth) {
      this.maxGrowth = maxGrowth;
   }

   protected abstract T estimateError(T[][] var1, T[] var2, T[] var3, T var4);
}
