package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface StorelessUnivariateStatistic extends UnivariateStatistic {
   void increment(double var1);

   void incrementAll(double[] var1) throws MathIllegalArgumentException;

   void incrementAll(double[] var1, int var2, int var3) throws MathIllegalArgumentException;

   double getResult();

   long getN();

   void clear();

   StorelessUnivariateStatistic copy();
}
