package org.apache.commons.math3.ode;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.util.MathArrays;

public class FieldExpandableODE<T extends RealFieldElement<T>> {
   private final FirstOrderFieldDifferentialEquations<T> primary;
   private List<FieldSecondaryEquations<T>> components;
   private FieldEquationsMapper<T> mapper;

   public FieldExpandableODE(FirstOrderFieldDifferentialEquations<T> primary) {
      this.primary = primary;
      this.components = new ArrayList<>();
      this.mapper = new FieldEquationsMapper<>(null, primary.getDimension());
   }

   public FieldEquationsMapper<T> getMapper() {
      return this.mapper;
   }

   public int addSecondaryEquations(FieldSecondaryEquations<T> secondary) {
      this.components.add(secondary);
      this.mapper = new FieldEquationsMapper<>(this.mapper, secondary.getDimension());
      return this.components.size();
   }

   public void init(T t0, T[] y0, T finalTime) {
      int index = 0;
      T[] primary0 = this.mapper.extractEquationData(index, y0);
      this.primary.init(t0, primary0, finalTime);

      while (++index < this.mapper.getNumberOfEquations()) {
         T[] secondary0 = this.mapper.extractEquationData(index, y0);
         this.components.get(index - 1).init(t0, primary0, secondary0, finalTime);
      }
   }

   public T[] computeDerivatives(T t, T[] y) throws MaxCountExceededException, DimensionMismatchException {
      T[] yDot = MathArrays.buildArray(t.getField(), this.mapper.getTotalDimension());
      int index = 0;
      T[] primaryState = this.mapper.extractEquationData(index, y);
      T[] primaryStateDot = this.primary.computeDerivatives(t, primaryState);
      this.mapper.insertEquationData(index, primaryStateDot, yDot);

      while (++index < this.mapper.getNumberOfEquations()) {
         T[] componentState = this.mapper.extractEquationData(index, y);
         T[] componentStateDot = this.components.get(index - 1).computeDerivatives(t, primaryState, primaryStateDot, componentState);
         this.mapper.insertEquationData(index, componentStateDot, yDot);
      }

      return yDot;
   }
}
