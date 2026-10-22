package org.apache.commons.math3.stat.regression;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NoDataException;

public interface UpdatingMultipleLinearRegression {
   boolean hasIntercept();

   long getN();

   void addObservation(double[] var1, double var2) throws ModelSpecificationException;

   void addObservations(double[][] var1, double[] var2) throws ModelSpecificationException;

   void clear();

   RegressionResults regress() throws ModelSpecificationException, NoDataException;

   RegressionResults regress(int[] var1) throws ModelSpecificationException, MathIllegalArgumentException;
}
