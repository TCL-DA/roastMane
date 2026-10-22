package org.apache.commons.math3.ode;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.ode.sampling.FieldStepHandler;
import org.apache.commons.math3.ode.sampling.FieldStepInterpolator;
import org.apache.commons.math3.util.FastMath;

public class ContinuousOutputFieldModel<T extends RealFieldElement<T>> implements FieldStepHandler<T> {
   private T initialTime;
   private T finalTime;
   private boolean forward;
   private int index;
   private List<FieldStepInterpolator<T>> steps = new ArrayList<>();

   public ContinuousOutputFieldModel() {
      this.initialTime = null;
      this.finalTime = null;
      this.forward = true;
      this.index = 0;
   }

   public void append(ContinuousOutputFieldModel<T> model) throws MathIllegalArgumentException, MaxCountExceededException {
      if (model.steps.size() != 0) {
         if (this.steps.size() == 0) {
            this.initialTime = model.initialTime;
            this.forward = model.forward;
         } else {
            FieldODEStateAndDerivative<T> s1 = this.steps.get(0).getPreviousState();
            FieldODEStateAndDerivative<T> s2 = model.steps.get(0).getPreviousState();
            this.checkDimensionsEquality(s1.getStateDimension(), s2.getStateDimension());
            this.checkDimensionsEquality(s1.getNumberOfSecondaryStates(), s2.getNumberOfSecondaryStates());

            for (int i = 0; i < s1.getNumberOfSecondaryStates(); i++) {
               this.checkDimensionsEquality(s1.getSecondaryStateDimension(i), s2.getSecondaryStateDimension(i));
            }

            if (this.forward ^ model.forward) {
               throw new MathIllegalArgumentException(LocalizedFormats.PROPAGATION_DIRECTION_MISMATCH);
            }

            FieldStepInterpolator<T> lastInterpolator = this.steps.get(this.index);
            T current = lastInterpolator.getCurrentState().getTime();
            T previous = lastInterpolator.getPreviousState().getTime();
            T step = current.subtract(previous);
            T gap = model.getInitialTime().subtract(current);
            if (gap.abs().subtract(step.abs().multiply(0.001)).getReal() > 0.0) {
               throw new MathIllegalArgumentException(LocalizedFormats.HOLE_BETWEEN_MODELS_TIME_RANGES, gap.abs().getReal());
            }
         }

         for (FieldStepInterpolator<T> interpolator : model.steps) {
            this.steps.add(interpolator);
         }

         this.index = this.steps.size() - 1;
         this.finalTime = this.steps.get(this.index).getCurrentState().getTime();
      }
   }

   private void checkDimensionsEquality(int d1, int d2) throws DimensionMismatchException {
      if (d1 != d2) {
         throw new DimensionMismatchException(d2, d1);
      }
   }

   @Override
   public void init(FieldODEStateAndDerivative<T> initialState, T t) {
      this.initialTime = initialState.getTime();
      this.finalTime = t;
      this.forward = true;
      this.index = 0;
      this.steps.clear();
   }

   @Override
   public void handleStep(FieldStepInterpolator<T> interpolator, boolean isLast) throws MaxCountExceededException {
      if (this.steps.size() == 0) {
         this.initialTime = interpolator.getPreviousState().getTime();
         this.forward = interpolator.isForward();
      }

      this.steps.add(interpolator);
      if (isLast) {
         this.finalTime = interpolator.getCurrentState().getTime();
         this.index = this.steps.size() - 1;
      }
   }

   public T getInitialTime() {
      return this.initialTime;
   }

   public T getFinalTime() {
      return this.finalTime;
   }

   public FieldODEStateAndDerivative<T> getInterpolatedState(T time) {
      int iMin = 0;
      FieldStepInterpolator<T> sMin = this.steps.get(iMin);
      T tMin = sMin.getPreviousState().getTime().add(sMin.getCurrentState().getTime()).multiply(0.5);
      int iMax = this.steps.size() - 1;
      FieldStepInterpolator<T> sMax = this.steps.get(iMax);
      T tMax = sMax.getPreviousState().getTime().add(sMax.getCurrentState().getTime()).multiply(0.5);
      if (this.locatePoint(time, sMin) <= 0) {
         this.index = iMin;
         return sMin.getInterpolatedState(time);
      } else if (this.locatePoint(time, sMax) >= 0) {
         this.index = iMax;
         return sMax.getInterpolatedState(time);
      } else {
         while (iMax - iMin > 5) {
            FieldStepInterpolator<T> si = this.steps.get(this.index);
            int location = this.locatePoint(time, si);
            if (location < 0) {
               iMax = this.index;
               tMax = si.getPreviousState().getTime().add(si.getCurrentState().getTime()).multiply(0.5);
            } else {
               if (location <= 0) {
                  return si.getInterpolatedState(time);
               }

               iMin = this.index;
               tMin = si.getPreviousState().getTime().add(si.getCurrentState().getTime()).multiply(0.5);
            }

            int iMed = (iMin + iMax) / 2;
            FieldStepInterpolator<T> sMed = this.steps.get(iMed);
            T tMed = sMed.getPreviousState().getTime().add(sMed.getCurrentState().getTime()).multiply(0.5);
            if (!(tMed.subtract(tMin).abs().subtract(1.0E-6).getReal() < 0.0) && !(tMax.subtract(tMed).abs().subtract(1.0E-6).getReal() < 0.0)) {
               T d12 = tMax.subtract(tMed);
               T d23 = tMed.subtract(tMin);
               T d13 = tMax.subtract(tMin);
               T dt1 = time.subtract(tMax);
               T dt2 = time.subtract(tMed);
               T dt3 = time.subtract(tMin);
               T iLagrange = dt2.multiply(dt3)
                  .multiply(d23)
                  .multiply(iMax)
                  .subtract(dt1.multiply(dt3).multiply(d13).multiply(iMed))
                  .add(dt1.multiply(dt2).multiply(d12).multiply(iMin))
                  .divide(d12.multiply(d23).multiply(d13));
               this.index = (int)FastMath.rint(iLagrange.getReal());
            } else {
               this.index = iMed;
            }

            int low = FastMath.max(iMin + 1, (9 * iMin + iMax) / 10);
            int high = FastMath.min(iMax - 1, (iMin + 9 * iMax) / 10);
            if (this.index < low) {
               this.index = low;
            } else if (this.index > high) {
               this.index = high;
            }
         }

         this.index = iMin;

         while (this.index <= iMax && this.locatePoint(time, this.steps.get(this.index)) > 0) {
            this.index++;
         }

         return this.steps.get(this.index).getInterpolatedState(time);
      }
   }

   private int locatePoint(T time, FieldStepInterpolator<T> interval) {
      if (this.forward) {
         if (time.subtract(interval.getPreviousState().getTime()).getReal() < 0.0) {
            return -1;
         } else {
            return time.subtract(interval.getCurrentState().getTime()).getReal() > 0.0 ? 1 : 0;
         }
      } else if (time.subtract(interval.getPreviousState().getTime()).getReal() > 0.0) {
         return -1;
      } else {
         return time.subtract(interval.getCurrentState().getTime()).getReal() < 0.0 ? 1 : 0;
      }
   }
}
