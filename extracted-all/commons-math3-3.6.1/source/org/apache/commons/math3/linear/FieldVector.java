package org.apache.commons.math3.linear;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;

public interface FieldVector<T extends FieldElement<T>> {
   Field<T> getField();

   FieldVector<T> copy();

   FieldVector<T> add(FieldVector<T> var1) throws DimensionMismatchException;

   FieldVector<T> subtract(FieldVector<T> var1) throws DimensionMismatchException;

   FieldVector<T> mapAdd(T var1) throws NullArgumentException;

   FieldVector<T> mapAddToSelf(T var1) throws NullArgumentException;

   FieldVector<T> mapSubtract(T var1) throws NullArgumentException;

   FieldVector<T> mapSubtractToSelf(T var1) throws NullArgumentException;

   FieldVector<T> mapMultiply(T var1) throws NullArgumentException;

   FieldVector<T> mapMultiplyToSelf(T var1) throws NullArgumentException;

   FieldVector<T> mapDivide(T var1) throws NullArgumentException, MathArithmeticException;

   FieldVector<T> mapDivideToSelf(T var1) throws NullArgumentException, MathArithmeticException;

   FieldVector<T> mapInv() throws MathArithmeticException;

   FieldVector<T> mapInvToSelf() throws MathArithmeticException;

   FieldVector<T> ebeMultiply(FieldVector<T> var1) throws DimensionMismatchException;

   FieldVector<T> ebeDivide(FieldVector<T> var1) throws DimensionMismatchException, MathArithmeticException;

   @Deprecated
   T[] getData();

   T dotProduct(FieldVector<T> var1) throws DimensionMismatchException;

   FieldVector<T> projection(FieldVector<T> var1) throws DimensionMismatchException, MathArithmeticException;

   FieldMatrix<T> outerProduct(FieldVector<T> var1);

   T getEntry(int var1) throws OutOfRangeException;

   void setEntry(int var1, T var2) throws OutOfRangeException;

   int getDimension();

   FieldVector<T> append(FieldVector<T> var1);

   FieldVector<T> append(T var1);

   FieldVector<T> getSubVector(int var1, int var2) throws OutOfRangeException, NotPositiveException;

   void setSubVector(int var1, FieldVector<T> var2) throws OutOfRangeException;

   void set(T var1);

   T[] toArray();
}
