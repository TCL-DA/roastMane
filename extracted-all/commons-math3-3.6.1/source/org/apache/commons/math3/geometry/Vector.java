package org.apache.commons.math3.geometry;

import java.text.NumberFormat;
import org.apache.commons.math3.exception.MathArithmeticException;

public interface Vector<S extends Space> extends Point<S> {
   Vector<S> getZero();

   double getNorm1();

   double getNorm();

   double getNormSq();

   double getNormInf();

   Vector<S> add(Vector<S> var1);

   Vector<S> add(double var1, Vector<S> var3);

   Vector<S> subtract(Vector<S> var1);

   Vector<S> subtract(double var1, Vector<S> var3);

   Vector<S> negate();

   Vector<S> normalize() throws MathArithmeticException;

   Vector<S> scalarMultiply(double var1);

   boolean isInfinite();

   double distance1(Vector<S> var1);

   double distance(Vector<S> var1);

   double distanceInf(Vector<S> var1);

   double distanceSq(Vector<S> var1);

   double dotProduct(Vector<S> var1);

   String toString(NumberFormat var1);
}
