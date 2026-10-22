package org.apache.commons.math3.ode.nonstiff;

import java.util.Arrays;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.FieldMatrixPreservingVisitor;
import org.apache.commons.math3.ode.FieldExpandableODE;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class AdamsMoultonFieldIntegrator<T extends RealFieldElement<T>> extends AdamsFieldIntegrator<T> {
   private static final String METHOD_NAME = "Adams-Moulton";

   public AdamsMoultonFieldIntegrator(Field<T> field, int nSteps, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) throws NumberIsTooSmallException {
      super(field, "Adams-Moulton", nSteps, nSteps + 1, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
   }

   public AdamsMoultonFieldIntegrator(Field<T> field, int nSteps, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance) throws IllegalArgumentException {
      super(field, "Adams-Moulton", nSteps, nSteps + 1, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
   }

   @Override
   public FieldODEStateAndDerivative<T> integrate(FieldExpandableODE<T> equations, FieldODEState<T> initialState, T finalTime) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
      this.sanityChecks(initialState, finalTime);
      T t0 = initialState.getTime();
      T[] y = equations.getMapper().mapState(initialState);
      this.setStepStart(this.initIntegration(equations, t0, y, finalTime));
      boolean forward = finalTime.subtract(initialState.getTime()).getReal() > 0.0;
      this.start(equations, this.getStepStart(), finalTime);
      FieldODEStateAndDerivative<T> stepStart = this.getStepStart();
      FieldODEStateAndDerivative<T> stepEnd = AdamsFieldStepInterpolator.taylor(
         stepStart, stepStart.getTime().add(this.getStepSize()), this.getStepSize(), this.scaled, this.nordsieck
      );
      this.setIsLastStep(false);

      do {
         T[] predictedY = null;
         T[] predictedScaled = MathArrays.buildArray(this.getField(), y.length);
         Array2DRowFieldMatrix<T> predictedNordsieck = null;
         T error = this.getField().getZero().add(10.0);

         while (error.subtract(1.0).getReal() >= 0.0) {
            predictedY = stepEnd.getState();
            T[] yDot = this.computeDerivatives(stepEnd.getTime(), predictedY);

            for (int j = 0; j < predictedScaled.length; j++) {
               predictedScaled[j] = this.getStepSize().multiply(yDot[j]);
            }

            predictedNordsieck = this.updateHighOrderDerivativesPhase1(this.nordsieck);
            this.updateHighOrderDerivativesPhase2(this.scaled, predictedScaled, predictedNordsieck);
            error = predictedNordsieck.walkInOptimizedOrder(new AdamsMoultonFieldIntegrator.Corrector(y, predictedScaled, predictedY));
            if (error.subtract(1.0).getReal() >= 0.0) {
               T factor = this.computeStepGrowShrinkFactor(error);
               this.rescale(this.filterStep(this.getStepSize().multiply(factor), forward, false));
               stepEnd = AdamsFieldStepInterpolator.taylor(
                  this.getStepStart(), this.getStepStart().getTime().add(this.getStepSize()), this.getStepSize(), this.scaled, this.nordsieck
               );
            }
         }

         T[] correctedYDot = this.computeDerivatives(stepEnd.getTime(), predictedY);
         T[] correctedScaled = MathArrays.buildArray(this.getField(), y.length);

         for (int j = 0; j < correctedScaled.length; j++) {
            correctedScaled[j] = this.getStepSize().multiply(correctedYDot[j]);
         }

         this.updateHighOrderDerivativesPhase2(predictedScaled, correctedScaled, predictedNordsieck);
         stepEnd = new FieldODEStateAndDerivative<>(stepEnd.getTime(), predictedY, correctedYDot);
         this.setStepStart(
            this.acceptStep(
               new AdamsFieldStepInterpolator<>(
                  this.getStepSize(), stepEnd, correctedScaled, predictedNordsieck, forward, this.getStepStart(), stepEnd, equations.getMapper()
               ),
               finalTime
            )
         );
         this.scaled = correctedScaled;
         this.nordsieck = predictedNordsieck;
         if (!this.isLastStep()) {
            System.arraycopy(predictedY, 0, y, 0, y.length);
            if (this.resetOccurred()) {
               this.start(equations, this.getStepStart(), finalTime);
            }

            T factor = this.computeStepGrowShrinkFactor(error);
            T scaledH = this.getStepSize().multiply(factor);
            T nextT = this.getStepStart().getTime().add(scaledH);
            boolean nextIsLast = forward ? nextT.subtract(finalTime).getReal() >= 0.0 : nextT.subtract(finalTime).getReal() <= 0.0;
            T hNew = this.filterStep(scaledH, forward, nextIsLast);
            T filteredNextT = this.getStepStart().getTime().add(hNew);
            boolean filteredNextIsLast = forward ? filteredNextT.subtract(finalTime).getReal() >= 0.0 : filteredNextT.subtract(finalTime).getReal() <= 0.0;
            if (filteredNextIsLast) {
               hNew = finalTime.subtract(this.getStepStart().getTime());
            }

            this.rescale(hNew);
            stepEnd = AdamsFieldStepInterpolator.taylor(
               this.getStepStart(), this.getStepStart().getTime().add(this.getStepSize()), this.getStepSize(), this.scaled, this.nordsieck
            );
         }
      } while (!this.isLastStep());

      FieldODEStateAndDerivative<T> finalState = this.getStepStart();
      this.setStepStart(null);
      this.setStepSize(null);
      return finalState;
   }

   private class Corrector implements FieldMatrixPreservingVisitor<T> {
      private final T[] previous;
      private final T[] scaled;
      private final T[] before;
      private final T[] after;

      Corrector(T[] previous, T[] scaled, T[] state) {
         this.previous = previous;
         this.scaled = scaled;
         this.after = state;
         this.before = (T[])((RealFieldElement[])state.clone());
      }

      @Override
      public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
         Arrays.fill(this.after, AdamsMoultonFieldIntegrator.this.getField().getZero());
      }

      public void visit(int row, int column, T value) {
         if ((row & 1) == 0) {
            this.after[column] = this.after[column].subtract(value);
         } else {
            this.after[column] = this.after[column].add(value);
         }
      }

      public T end() {
         T error = AdamsMoultonFieldIntegrator.this.getField().getZero();

         for (int i = 0; i < this.after.length; i++) {
            this.after[i] = this.after[i].add(this.previous[i].add(this.scaled[i]));
            if (i < AdamsMoultonFieldIntegrator.this.mainSetDimension) {
               T yScale = MathUtils.max(this.previous[i].abs(), this.after[i].abs());
               T tol = AdamsMoultonFieldIntegrator.this.vecAbsoluteTolerance == null
                  ? yScale.multiply(AdamsMoultonFieldIntegrator.this.scalRelativeTolerance).add(AdamsMoultonFieldIntegrator.this.scalAbsoluteTolerance)
                  : yScale.multiply(AdamsMoultonFieldIntegrator.this.vecRelativeTolerance[i]).add(AdamsMoultonFieldIntegrator.this.vecAbsoluteTolerance[i]);
               T ratio = this.after[i].subtract(this.before[i]).divide(tol);
               error = error.add(ratio.multiply(ratio));
            }
         }

         return error.divide((double)AdamsMoultonFieldIntegrator.this.mainSetDimension).sqrt();
      }
   }
}
