package org.apache.commons.math3.ode;

public interface ParameterizedODE extends Parameterizable {
   double getParameter(String var1) throws UnknownParameterException;

   void setParameter(String var1, double var2) throws UnknownParameterException;
}
