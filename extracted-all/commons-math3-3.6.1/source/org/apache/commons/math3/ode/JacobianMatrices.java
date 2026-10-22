package org.apache.commons.math3.ode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class JacobianMatrices {
   private ExpandableStatefulODE efode = null;
   private int index = -1;
   private MainStateJacobianProvider jode;
   private ParameterizedODE pode;
   private int stateDim;
   private ParameterConfiguration[] selectedParameters;
   private List<ParameterJacobianProvider> jacobianProviders;
   private int paramDim;
   private boolean dirtyParameter;
   private double[] matricesData;

   public JacobianMatrices(FirstOrderDifferentialEquations fode, double[] hY, String... parameters) throws DimensionMismatchException {
      this(new JacobianMatrices.MainStateJacobianWrapper(fode, hY), parameters);
   }

   public JacobianMatrices(MainStateJacobianProvider jode, String... parameters) {
      this.jode = jode;
      this.pode = null;
      this.stateDim = jode.getDimension();
      if (parameters == null) {
         this.selectedParameters = null;
         this.paramDim = 0;
      } else {
         this.selectedParameters = new ParameterConfiguration[parameters.length];

         for (int i = 0; i < parameters.length; i++) {
            this.selectedParameters[i] = new ParameterConfiguration(parameters[i], Double.NaN);
         }

         this.paramDim = parameters.length;
      }

      this.dirtyParameter = false;
      this.jacobianProviders = new ArrayList<>();
      this.matricesData = new double[(this.stateDim + this.paramDim) * this.stateDim];

      for (int i = 0; i < this.stateDim; i++) {
         this.matricesData[i * (this.stateDim + 1)] = 1.0;
      }
   }

   public void registerVariationalEquations(ExpandableStatefulODE expandable) throws DimensionMismatchException, JacobianMatrices.MismatchedEquations {
      FirstOrderDifferentialEquations ode = (FirstOrderDifferentialEquations)(this.jode instanceof JacobianMatrices.MainStateJacobianWrapper
         ? ((JacobianMatrices.MainStateJacobianWrapper)this.jode).ode
         : this.jode);
      if (expandable.getPrimary() != ode) {
         throw new JacobianMatrices.MismatchedEquations();
      } else {
         this.efode = expandable;
         this.index = this.efode.addSecondaryEquations(new JacobianMatrices.JacobiansSecondaryEquations());
         this.efode.setSecondaryState(this.index, this.matricesData);
      }
   }

   public void addParameterJacobianProvider(ParameterJacobianProvider provider) {
      this.jacobianProviders.add(provider);
   }

   public void setParameterizedODE(ParameterizedODE parameterizedOde) {
      this.pode = parameterizedOde;
      this.dirtyParameter = true;
   }

   public void setParameterStep(String parameter, double hP) throws UnknownParameterException {
      for (ParameterConfiguration param : this.selectedParameters) {
         if (parameter.equals(param.getParameterName())) {
            param.setHP(hP);
            this.dirtyParameter = true;
            return;
         }
      }

      throw new UnknownParameterException(parameter);
   }

   public void setInitialMainStateJacobian(double[][] dYdY0) throws DimensionMismatchException {
      this.checkDimension(this.stateDim, dYdY0);
      this.checkDimension(this.stateDim, dYdY0[0]);
      int i = 0;

      for (double[] row : dYdY0) {
         System.arraycopy(row, 0, this.matricesData, i, this.stateDim);
         i += this.stateDim;
      }

      if (this.efode != null) {
         this.efode.setSecondaryState(this.index, this.matricesData);
      }
   }

   public void setInitialParameterJacobian(String pName, double[] dYdP) throws UnknownParameterException, DimensionMismatchException {
      this.checkDimension(this.stateDim, dYdP);
      int i = this.stateDim * this.stateDim;

      for (ParameterConfiguration param : this.selectedParameters) {
         if (pName.equals(param.getParameterName())) {
            System.arraycopy(dYdP, 0, this.matricesData, i, this.stateDim);
            if (this.efode != null) {
               this.efode.setSecondaryState(this.index, this.matricesData);
            }

            return;
         }

         i += this.stateDim;
      }

      throw new UnknownParameterException(pName);
   }

   public void getCurrentMainSetJacobian(double[][] dYdY0) {
      double[] p = this.efode.getSecondaryState(this.index);
      int j = 0;

      for (int i = 0; i < this.stateDim; i++) {
         System.arraycopy(p, j, dYdY0[i], 0, this.stateDim);
         j += this.stateDim;
      }
   }

   public void getCurrentParameterJacobian(String pName, double[] dYdP) {
      double[] p = this.efode.getSecondaryState(this.index);
      int i = this.stateDim * this.stateDim;

      for (ParameterConfiguration param : this.selectedParameters) {
         if (param.getParameterName().equals(pName)) {
            System.arraycopy(p, i, dYdP, 0, this.stateDim);
            return;
         }

         i += this.stateDim;
      }
   }

   private void checkDimension(int expected, Object array) throws DimensionMismatchException {
      int arrayDimension = array == null ? 0 : Array.getLength(array);
      if (arrayDimension != expected) {
         throw new DimensionMismatchException(arrayDimension, expected);
      }
   }

   private class JacobiansSecondaryEquations implements SecondaryEquations {
      private JacobiansSecondaryEquations() {
      }

      @Override
      public int getDimension() {
         return JacobianMatrices.this.stateDim * (JacobianMatrices.this.stateDim + JacobianMatrices.this.paramDim);
      }

      @Override
      public void computeDerivatives(double t, double[] y, double[] yDot, double[] z, double[] zDot) throws MaxCountExceededException, DimensionMismatchException {
         if (JacobianMatrices.this.dirtyParameter && JacobianMatrices.this.paramDim != 0) {
            JacobianMatrices.this.jacobianProviders
               .add(new ParameterJacobianWrapper(JacobianMatrices.this.jode, JacobianMatrices.this.pode, JacobianMatrices.this.selectedParameters));
            JacobianMatrices.this.dirtyParameter = false;
         }

         double[][] dFdY = new double[JacobianMatrices.this.stateDim][JacobianMatrices.this.stateDim];
         JacobianMatrices.this.jode.computeMainStateJacobian(t, y, yDot, dFdY);

         for (int i = 0; i < JacobianMatrices.this.stateDim; i++) {
            double[] dFdYi = dFdY[i];

            for (int j = 0; j < JacobianMatrices.this.stateDim; j++) {
               double s = 0.0;
               int zIndex = j;

               for (int l = 0; l < JacobianMatrices.this.stateDim; l++) {
                  s += dFdYi[l] * z[zIndex];
                  zIndex += JacobianMatrices.this.stateDim;
               }

               zDot[j + i * JacobianMatrices.this.stateDim] = s;
            }
         }

         if (JacobianMatrices.this.paramDim != 0) {
            double[] dFdP = new double[JacobianMatrices.this.stateDim];
            int startIndex = JacobianMatrices.this.stateDim * JacobianMatrices.this.stateDim;

            for (ParameterConfiguration param : JacobianMatrices.this.selectedParameters) {
               boolean found = false;

               for (int k = 0; !found && k < JacobianMatrices.this.jacobianProviders.size(); k++) {
                  ParameterJacobianProvider provider = JacobianMatrices.this.jacobianProviders.get(k);
                  if (provider.isSupported(param.getParameterName())) {
                     provider.computeParameterJacobian(t, y, yDot, param.getParameterName(), dFdP);

                     for (int i = 0; i < JacobianMatrices.this.stateDim; i++) {
                        double[] dFdYi = dFdY[i];
                        int zIndex = startIndex;
                        double s = dFdP[i];

                        for (int l = 0; l < JacobianMatrices.this.stateDim; l++) {
                           s += dFdYi[l] * z[zIndex];
                           zIndex++;
                        }

                        zDot[startIndex + i] = s;
                     }

                     found = true;
                  }
               }

               if (!found) {
                  Arrays.fill(zDot, startIndex, startIndex + JacobianMatrices.this.stateDim, 0.0);
               }

               startIndex += JacobianMatrices.this.stateDim;
            }
         }
      }
   }

   private static class MainStateJacobianWrapper implements MainStateJacobianProvider {
      private final FirstOrderDifferentialEquations ode;
      private final double[] hY;

      MainStateJacobianWrapper(FirstOrderDifferentialEquations ode, double[] hY) throws DimensionMismatchException {
         this.ode = ode;
         this.hY = (double[])hY.clone();
         if (hY.length != ode.getDimension()) {
            throw new DimensionMismatchException(ode.getDimension(), hY.length);
         }
      }

      @Override
      public int getDimension() {
         return this.ode.getDimension();
      }

      @Override
      public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
         this.ode.computeDerivatives(t, y, yDot);
      }

      @Override
      public void computeMainStateJacobian(double t, double[] y, double[] yDot, double[][] dFdY) throws MaxCountExceededException, DimensionMismatchException {
         int n = this.ode.getDimension();
         double[] tmpDot = new double[n];

         for (int j = 0; j < n; j++) {
            double savedYj = y[j];
            y[j] += this.hY[j];
            this.ode.computeDerivatives(t, y, tmpDot);

            for (int i = 0; i < n; i++) {
               dFdY[i][j] = (tmpDot[i] - yDot[i]) / this.hY[j];
            }

            y[j] = savedYj;
         }
      }
   }

   public static class MismatchedEquations extends MathIllegalArgumentException {
      private static final long serialVersionUID = 20120902L;

      public MismatchedEquations() {
         super(LocalizedFormats.UNMATCHED_ODE_IN_EXPANDED_SET);
      }
   }
}
