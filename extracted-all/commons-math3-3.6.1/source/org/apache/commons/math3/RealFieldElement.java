package org.apache.commons.math3;

import org.apache.commons.math3.exception.DimensionMismatchException;

public interface RealFieldElement<T> extends FieldElement<T> {
   double getReal();

   T add(double var1);

   T subtract(double var1);

   T multiply(double var1);

   T divide(double var1);

   T remainder(double var1);

   T remainder(T var1) throws DimensionMismatchException;

   T abs();

   T ceil();

   T floor();

   T rint();

   long round();

   T signum();

   T copySign(T var1);

   T copySign(double var1);

   T scalb(int var1);

   T hypot(T var1) throws DimensionMismatchException;

   @Override
   T reciprocal();

   T sqrt();

   T cbrt();

   T rootN(int var1);

   T pow(double var1);

   T pow(int var1);

   T pow(T var1) throws DimensionMismatchException;

   T exp();

   T expm1();

   T log();

   T log1p();

   T cos();

   T sin();

   T tan();

   T acos();

   T asin();

   T atan();

   T atan2(T var1) throws DimensionMismatchException;

   T cosh();

   T sinh();

   T tanh();

   T acosh();

   T asinh();

   T atanh();

   T linearCombination(T[] var1, T[] var2) throws DimensionMismatchException;

   T linearCombination(double[] var1, T[] var2) throws DimensionMismatchException;

   T linearCombination(T var1, T var2, T var3, T var4);

   T linearCombination(double var1, T var3, double var4, T var6);

   T linearCombination(T var1, T var2, T var3, T var4, T var5, T var6);

   T linearCombination(double var1, T var3, double var4, T var6, double var7, T var9);

   T linearCombination(T var1, T var2, T var3, T var4, T var5, T var6, T var7, T var8);

   T linearCombination(double var1, T var3, double var4, T var6, double var7, T var9, double var10, T var12);
}
