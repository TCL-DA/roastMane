package org.apache.commons.math3.linear;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class ArrayFieldVector<T extends FieldElement<T>> implements FieldVector<T>, Serializable {
   private static final long serialVersionUID = 7648186910365927050L;
   private T[] data;
   private final Field<T> field;

   public ArrayFieldVector(Field<T> field) {
      this(field, 0);
   }

   public ArrayFieldVector(Field<T> field, int size) {
      this.field = field;
      this.data = MathArrays.buildArray(field, size);
   }

   public ArrayFieldVector(int size, T preset) {
      this(preset.getField(), size);
      Arrays.fill(this.data, preset);
   }

   public ArrayFieldVector(T[] d) throws NullArgumentException, ZeroException {
      MathUtils.checkNotNull(d);

      try {
         this.field = d[0].getField();
         this.data = (T[])((FieldElement[])d.clone());
      } catch (ArrayIndexOutOfBoundsException var3) {
         throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
      }
   }

   public ArrayFieldVector(Field<T> field, T[] d) throws NullArgumentException {
      MathUtils.checkNotNull(d);
      this.field = field;
      this.data = (T[])((FieldElement[])d.clone());
   }

   public ArrayFieldVector(T[] d, boolean copyArray) throws NullArgumentException, ZeroException {
      MathUtils.checkNotNull(d);
      if (d.length == 0) {
         throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
      } else {
         this.field = d[0].getField();
         this.data = (T[])(copyArray ? (FieldElement[])d.clone() : d);
      }
   }

   public ArrayFieldVector(Field<T> field, T[] d, boolean copyArray) throws NullArgumentException {
      MathUtils.checkNotNull(d);
      this.field = field;
      this.data = (T[])(copyArray ? (FieldElement[])d.clone() : d);
   }

   public ArrayFieldVector(T[] d, int pos, int size) throws NullArgumentException, NumberIsTooLargeException {
      MathUtils.checkNotNull(d);
      if (d.length < pos + size) {
         throw new NumberIsTooLargeException(pos + size, d.length, true);
      } else {
         this.field = d[0].getField();
         this.data = MathArrays.buildArray(this.field, size);
         System.arraycopy(d, pos, this.data, 0, size);
      }
   }

   public ArrayFieldVector(Field<T> field, T[] d, int pos, int size) throws NullArgumentException, NumberIsTooLargeException {
      MathUtils.checkNotNull(d);
      if (d.length < pos + size) {
         throw new NumberIsTooLargeException(pos + size, d.length, true);
      } else {
         this.field = field;
         this.data = MathArrays.buildArray(field, size);
         System.arraycopy(d, pos, this.data, 0, size);
      }
   }

   public ArrayFieldVector(FieldVector<T> v) throws NullArgumentException {
      MathUtils.checkNotNull(v);
      this.field = v.getField();
      this.data = MathArrays.buildArray(this.field, v.getDimension());

      for (int i = 0; i < this.data.length; i++) {
         this.data[i] = v.getEntry(i);
      }
   }

   public ArrayFieldVector(ArrayFieldVector<T> v) throws NullArgumentException {
      MathUtils.checkNotNull(v);
      this.field = v.getField();
      this.data = (T[])((FieldElement[])v.data.clone());
   }

   public ArrayFieldVector(ArrayFieldVector<T> v, boolean deep) throws NullArgumentException {
      MathUtils.checkNotNull(v);
      this.field = v.getField();
      this.data = (T[])(deep ? (FieldElement[])v.data.clone() : v.data);
   }

   @Deprecated
   public ArrayFieldVector(ArrayFieldVector<T> v1, ArrayFieldVector<T> v2) throws NullArgumentException {
      this(v1, v2);
   }

   public ArrayFieldVector(FieldVector<T> v1, FieldVector<T> v2) throws NullArgumentException {
      MathUtils.checkNotNull(v1);
      MathUtils.checkNotNull(v2);
      this.field = v1.getField();
      T[] v1Data = v1 instanceof ArrayFieldVector ? ((ArrayFieldVector)v1).data : v1.toArray();
      T[] v2Data = v2 instanceof ArrayFieldVector ? ((ArrayFieldVector)v2).data : v2.toArray();
      this.data = MathArrays.buildArray(this.field, v1Data.length + v2Data.length);
      System.arraycopy(v1Data, 0, this.data, 0, v1Data.length);
      System.arraycopy(v2Data, 0, this.data, v1Data.length, v2Data.length);
   }

   @Deprecated
   public ArrayFieldVector(ArrayFieldVector<T> v1, T[] v2) throws NullArgumentException {
      this(v1, v2);
   }

   public ArrayFieldVector(FieldVector<T> v1, T[] v2) throws NullArgumentException {
      MathUtils.checkNotNull(v1);
      MathUtils.checkNotNull(v2);
      this.field = v1.getField();
      T[] v1Data = v1 instanceof ArrayFieldVector ? ((ArrayFieldVector)v1).data : v1.toArray();
      this.data = MathArrays.buildArray(this.field, v1Data.length + v2.length);
      System.arraycopy(v1Data, 0, this.data, 0, v1Data.length);
      System.arraycopy(v2, 0, this.data, v1Data.length, v2.length);
   }

   @Deprecated
   public ArrayFieldVector(T[] v1, ArrayFieldVector<T> v2) throws NullArgumentException {
      this(v1, v2);
   }

   public ArrayFieldVector(T[] v1, FieldVector<T> v2) throws NullArgumentException {
      MathUtils.checkNotNull(v1);
      MathUtils.checkNotNull(v2);
      this.field = v2.getField();
      T[] v2Data = v2 instanceof ArrayFieldVector ? ((ArrayFieldVector)v2).data : v2.toArray();
      this.data = MathArrays.buildArray(this.field, v1.length + v2Data.length);
      System.arraycopy(v1, 0, this.data, 0, v1.length);
      System.arraycopy(v2Data, 0, this.data, v1.length, v2Data.length);
   }

   public ArrayFieldVector(T[] v1, T[] v2) throws NullArgumentException, ZeroException {
      MathUtils.checkNotNull(v1);
      MathUtils.checkNotNull(v2);
      if (v1.length + v2.length == 0) {
         throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
      } else {
         this.data = MathArrays.buildArray(v1[0].getField(), v1.length + v2.length);
         System.arraycopy(v1, 0, this.data, 0, v1.length);
         System.arraycopy(v2, 0, this.data, v1.length, v2.length);
         this.field = this.data[0].getField();
      }
   }

   public ArrayFieldVector(Field<T> field, T[] v1, T[] v2) throws NullArgumentException, ZeroException {
      MathUtils.checkNotNull(v1);
      MathUtils.checkNotNull(v2);
      if (v1.length + v2.length == 0) {
         throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
      } else {
         this.data = MathArrays.buildArray(field, v1.length + v2.length);
         System.arraycopy(v1, 0, this.data, 0, v1.length);
         System.arraycopy(v2, 0, this.data, v1.length, v2.length);
         this.field = field;
      }
   }

   @Override
   public Field<T> getField() {
      return this.field;
   }

   @Override
   public FieldVector<T> copy() {
      return new ArrayFieldVector<>(this, true);
   }

   @Override
   public FieldVector<T> add(FieldVector<T> v) throws DimensionMismatchException {
      try {
         return this.add((ArrayFieldVector<T>)v);
      } catch (ClassCastException var5) {
         this.checkVectorDimensions(v);
         T[] out = MathArrays.buildArray(this.field, this.data.length);

         for (int i = 0; i < this.data.length; i++) {
            out[i] = this.data[i].add(v.getEntry(i));
         }

         return new ArrayFieldVector<>(this.field, out, false);
      }
   }

   public ArrayFieldVector<T> add(ArrayFieldVector<T> v) throws DimensionMismatchException {
      this.checkVectorDimensions(v.data.length);
      T[] out = MathArrays.buildArray(this.field, this.data.length);

      for (int i = 0; i < this.data.length; i++) {
         out[i] = this.data[i].add(v.data[i]);
      }

      return new ArrayFieldVector<>(this.field, out, false);
   }

   @Override
   public FieldVector<T> subtract(FieldVector<T> v) throws DimensionMismatchException {
      try {
         return this.subtract((ArrayFieldVector<T>)v);
      } catch (ClassCastException var5) {
         this.checkVectorDimensions(v);
         T[] out = MathArrays.buildArray(this.field, this.data.length);

         for (int i = 0; i < this.data.length; i++) {
            out[i] = this.data[i].subtract(v.getEntry(i));
         }

         return new ArrayFieldVector<>(this.field, out, false);
      }
   }

   public ArrayFieldVector<T> subtract(ArrayFieldVector<T> v) throws DimensionMismatchException {
      this.checkVectorDimensions(v.data.length);
      T[] out = MathArrays.buildArray(this.field, this.data.length);

      for (int i = 0; i < this.data.length; i++) {
         out[i] = this.data[i].subtract(v.data[i]);
      }

      return new ArrayFieldVector<>(this.field, out, false);
   }

   @Override
   public FieldVector<T> mapAdd(T d) throws NullArgumentException {
      T[] out = MathArrays.buildArray(this.field, this.data.length);

      for (int i = 0; i < this.data.length; i++) {
         out[i] = this.data[i].add(d);
      }

      return new ArrayFieldVector<>(this.field, out, false);
   }

   @Override
   public FieldVector<T> mapAddToSelf(T d) throws NullArgumentException {
      for (int i = 0; i < this.data.length; i++) {
         this.data[i] = this.data[i].add(d);
      }

      return this;
   }

   @Override
   public FieldVector<T> mapSubtract(T d) throws NullArgumentException {
      T[] out = MathArrays.buildArray(this.field, this.data.length);

      for (int i = 0; i < this.data.length; i++) {
         out[i] = this.data[i].subtract(d);
      }

      return new ArrayFieldVector<>(this.field, out, false);
   }

   @Override
   public FieldVector<T> mapSubtractToSelf(T d) throws NullArgumentException {
      for (int i = 0; i < this.data.length; i++) {
         this.data[i] = this.data[i].subtract(d);
      }

      return this;
   }

   @Override
   public FieldVector<T> mapMultiply(T d) throws NullArgumentException {
      T[] out = MathArrays.buildArray(this.field, this.data.length);

      for (int i = 0; i < this.data.length; i++) {
         out[i] = this.data[i].multiply(d);
      }

      return new ArrayFieldVector<>(this.field, out, false);
   }

   @Override
   public FieldVector<T> mapMultiplyToSelf(T d) throws NullArgumentException {
      for (int i = 0; i < this.data.length; i++) {
         this.data[i] = this.data[i].multiply(d);
      }

      return this;
   }

   @Override
   public FieldVector<T> mapDivide(T d) throws NullArgumentException, MathArithmeticException {
      MathUtils.checkNotNull(d);
      T[] out = MathArrays.buildArray(this.field, this.data.length);

      for (int i = 0; i < this.data.length; i++) {
         out[i] = this.data[i].divide(d);
      }

      return new ArrayFieldVector<>(this.field, out, false);
   }

   @Override
   public FieldVector<T> mapDivideToSelf(T d) throws NullArgumentException, MathArithmeticException {
      MathUtils.checkNotNull(d);

      for (int i = 0; i < this.data.length; i++) {
         this.data[i] = this.data[i].divide(d);
      }

      return this;
   }

   @Override
   public FieldVector<T> mapInv() throws MathArithmeticException {
      T[] out = MathArrays.buildArray(this.field, this.data.length);
      T one = this.field.getOne();

      for (int i = 0; i < this.data.length; i++) {
         try {
            out[i] = one.divide(this.data[i]);
         } catch (MathArithmeticException var5) {
            throw new MathArithmeticException(LocalizedFormats.INDEX, i);
         }
      }

      return new ArrayFieldVector<>(this.field, out, false);
   }

   @Override
   public FieldVector<T> mapInvToSelf() throws MathArithmeticException {
      T one = this.field.getOne();

      for (int i = 0; i < this.data.length; i++) {
         try {
            this.data[i] = one.divide(this.data[i]);
         } catch (MathArithmeticException var4) {
            throw new MathArithmeticException(LocalizedFormats.INDEX, i);
         }
      }

      return this;
   }

   @Override
   public FieldVector<T> ebeMultiply(FieldVector<T> v) throws DimensionMismatchException {
      try {
         return this.ebeMultiply((ArrayFieldVector<T>)v);
      } catch (ClassCastException var5) {
         this.checkVectorDimensions(v);
         T[] out = MathArrays.buildArray(this.field, this.data.length);

         for (int i = 0; i < this.data.length; i++) {
            out[i] = this.data[i].multiply(v.getEntry(i));
         }

         return new ArrayFieldVector<>(this.field, out, false);
      }
   }

   public ArrayFieldVector<T> ebeMultiply(ArrayFieldVector<T> v) throws DimensionMismatchException {
      this.checkVectorDimensions(v.data.length);
      T[] out = MathArrays.buildArray(this.field, this.data.length);

      for (int i = 0; i < this.data.length; i++) {
         out[i] = this.data[i].multiply(v.data[i]);
      }

      return new ArrayFieldVector<>(this.field, out, false);
   }

   @Override
   public FieldVector<T> ebeDivide(FieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
      try {
         return this.ebeDivide((ArrayFieldVector<T>)v);
      } catch (ClassCastException var7) {
         this.checkVectorDimensions(v);
         T[] out = MathArrays.buildArray(this.field, this.data.length);

         for (int i = 0; i < this.data.length; i++) {
            try {
               out[i] = this.data[i].divide(v.getEntry(i));
            } catch (MathArithmeticException var6) {
               throw new MathArithmeticException(LocalizedFormats.INDEX, i);
            }
         }

         return new ArrayFieldVector<>(this.field, out, false);
      }
   }

   public ArrayFieldVector<T> ebeDivide(ArrayFieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
      this.checkVectorDimensions(v.data.length);
      T[] out = MathArrays.buildArray(this.field, this.data.length);

      for (int i = 0; i < this.data.length; i++) {
         try {
            out[i] = this.data[i].divide(v.data[i]);
         } catch (MathArithmeticException var5) {
            throw new MathArithmeticException(LocalizedFormats.INDEX, i);
         }
      }

      return new ArrayFieldVector<>(this.field, out, false);
   }

   @Override
   public T[] getData() {
      return (T[])((FieldElement[])this.data.clone());
   }

   public T[] getDataRef() {
      return this.data;
   }

   @Override
   public T dotProduct(FieldVector<T> v) throws DimensionMismatchException {
      try {
         return this.dotProduct((ArrayFieldVector<T>)v);
      } catch (ClassCastException var5) {
         this.checkVectorDimensions(v);
         T dot = this.field.getZero();

         for (int i = 0; i < this.data.length; i++) {
            dot = dot.add(this.data[i].multiply(v.getEntry(i)));
         }

         return dot;
      }
   }

   public T dotProduct(ArrayFieldVector<T> v) throws DimensionMismatchException {
      this.checkVectorDimensions(v.data.length);
      T dot = this.field.getZero();

      for (int i = 0; i < this.data.length; i++) {
         dot = dot.add(this.data[i].multiply(v.data[i]));
      }

      return dot;
   }

   @Override
   public FieldVector<T> projection(FieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
      return v.mapMultiply(this.dotProduct(v).divide(v.dotProduct(v)));
   }

   public ArrayFieldVector<T> projection(ArrayFieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
      return (ArrayFieldVector<T>)v.mapMultiply(this.dotProduct(v).divide(v.dotProduct(v)));
   }

   @Override
   public FieldMatrix<T> outerProduct(FieldVector<T> v) {
      try {
         return this.outerProduct((ArrayFieldVector<T>)v);
      } catch (ClassCastException var8) {
         int m = this.data.length;
         int n = v.getDimension();
         FieldMatrix<T> out = new Array2DRowFieldMatrix<>(this.field, m, n);

         for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
               out.setEntry(i, j, this.data[i].multiply(v.getEntry(j)));
            }
         }

         return out;
      }
   }

   public FieldMatrix<T> outerProduct(ArrayFieldVector<T> v) {
      int m = this.data.length;
      int n = v.data.length;
      FieldMatrix<T> out = new Array2DRowFieldMatrix<>(this.field, m, n);

      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            out.setEntry(i, j, this.data[i].multiply(v.data[j]));
         }
      }

      return out;
   }

   @Override
   public T getEntry(int index) {
      return this.data[index];
   }

   @Override
   public int getDimension() {
      return this.data.length;
   }

   @Override
   public FieldVector<T> append(FieldVector<T> v) {
      try {
         return this.append((ArrayFieldVector<T>)v);
      } catch (ClassCastException var3) {
         return new ArrayFieldVector<>(this, new ArrayFieldVector<>(v));
      }
   }

   public ArrayFieldVector<T> append(ArrayFieldVector<T> v) {
      return new ArrayFieldVector<>(this, v);
   }

   @Override
   public FieldVector<T> append(T in) {
      T[] out = MathArrays.buildArray(this.field, this.data.length + 1);
      System.arraycopy(this.data, 0, out, 0, this.data.length);
      out[this.data.length] = in;
      return new ArrayFieldVector<>(this.field, out, false);
   }

   @Override
   public FieldVector<T> getSubVector(int index, int n) throws OutOfRangeException, NotPositiveException {
      if (n < 0) {
         throw new NotPositiveException(LocalizedFormats.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, n);
      } else {
         ArrayFieldVector<T> out = new ArrayFieldVector<>(this.field, n);

         try {
            System.arraycopy(this.data, index, out.data, 0, n);
         } catch (IndexOutOfBoundsException var5) {
            this.checkIndex(index);
            this.checkIndex(index + n - 1);
         }

         return out;
      }
   }

   @Override
   public void setEntry(int index, T value) {
      try {
         this.data[index] = value;
      } catch (IndexOutOfBoundsException var4) {
         this.checkIndex(index);
      }
   }

   @Override
   public void setSubVector(int index, FieldVector<T> v) throws OutOfRangeException {
      try {
         try {
            this.set(index, (ArrayFieldVector<T>)v);
         } catch (ClassCastException var5) {
            for (int i = index; i < index + v.getDimension(); i++) {
               this.data[i] = v.getEntry(i - index);
            }
         }
      } catch (IndexOutOfBoundsException var6) {
         this.checkIndex(index);
         this.checkIndex(index + v.getDimension() - 1);
      }
   }

   public void set(int index, ArrayFieldVector<T> v) throws OutOfRangeException {
      try {
         System.arraycopy(v.data, 0, this.data, index, v.data.length);
      } catch (IndexOutOfBoundsException var4) {
         this.checkIndex(index);
         this.checkIndex(index + v.data.length - 1);
      }
   }

   @Override
   public void set(T value) {
      Arrays.fill(this.data, value);
   }

   @Override
   public T[] toArray() {
      return (T[])((FieldElement[])this.data.clone());
   }

   protected void checkVectorDimensions(FieldVector<T> v) throws DimensionMismatchException {
      this.checkVectorDimensions(v.getDimension());
   }

   protected void checkVectorDimensions(int n) throws DimensionMismatchException {
      if (this.data.length != n) {
         throw new DimensionMismatchException(this.data.length, n);
      }
   }

   public T walkInDefaultOrder(FieldVectorPreservingVisitor<T> visitor) {
      int dim = this.getDimension();
      visitor.start(dim, 0, dim - 1);

      for (int i = 0; i < dim; i++) {
         visitor.visit(i, this.getEntry(i));
      }

      return visitor.end();
   }

   public T walkInDefaultOrder(FieldVectorPreservingVisitor<T> visitor, int start, int end) throws NumberIsTooSmallException, OutOfRangeException {
      this.checkIndices(start, end);
      visitor.start(this.getDimension(), start, end);

      for (int i = start; i <= end; i++) {
         visitor.visit(i, this.getEntry(i));
      }

      return visitor.end();
   }

   public T walkInOptimizedOrder(FieldVectorPreservingVisitor<T> visitor) {
      return this.walkInDefaultOrder(visitor);
   }

   public T walkInOptimizedOrder(FieldVectorPreservingVisitor<T> visitor, int start, int end) throws NumberIsTooSmallException, OutOfRangeException {
      return this.walkInDefaultOrder(visitor, start, end);
   }

   public T walkInDefaultOrder(FieldVectorChangingVisitor<T> visitor) {
      int dim = this.getDimension();
      visitor.start(dim, 0, dim - 1);

      for (int i = 0; i < dim; i++) {
         this.setEntry(i, visitor.visit(i, this.getEntry(i)));
      }

      return visitor.end();
   }

   public T walkInDefaultOrder(FieldVectorChangingVisitor<T> visitor, int start, int end) throws NumberIsTooSmallException, OutOfRangeException {
      this.checkIndices(start, end);
      visitor.start(this.getDimension(), start, end);

      for (int i = start; i <= end; i++) {
         this.setEntry(i, visitor.visit(i, this.getEntry(i)));
      }

      return visitor.end();
   }

   public T walkInOptimizedOrder(FieldVectorChangingVisitor<T> visitor) {
      return this.walkInDefaultOrder(visitor);
   }

   public T walkInOptimizedOrder(FieldVectorChangingVisitor<T> visitor, int start, int end) throws NumberIsTooSmallException, OutOfRangeException {
      return this.walkInDefaultOrder(visitor, start, end);
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other == null) {
         return false;
      } else {
         try {
            FieldVector<T> rhs = (FieldVector<T>)other;
            if (this.data.length != rhs.getDimension()) {
               return false;
            } else {
               for (int i = 0; i < this.data.length; i++) {
                  if (!this.data[i].equals(rhs.getEntry(i))) {
                     return false;
                  }
               }

               return true;
            }
         } catch (ClassCastException var4) {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 3542;

      for (T a : this.data) {
         h ^= a.hashCode();
      }

      return h;
   }

   private void checkIndex(int index) throws OutOfRangeException {
      if (index < 0 || index >= this.getDimension()) {
         throw new OutOfRangeException(LocalizedFormats.INDEX, index, 0, this.getDimension() - 1);
      }
   }

   private void checkIndices(int start, int end) throws NumberIsTooSmallException, OutOfRangeException {
      int dim = this.getDimension();
      if (start < 0 || start >= dim) {
         throw new OutOfRangeException(LocalizedFormats.INDEX, start, 0, dim - 1);
      } else if (end < 0 || end >= dim) {
         throw new OutOfRangeException(LocalizedFormats.INDEX, end, 0, dim - 1);
      } else if (end < start) {
         throw new NumberIsTooSmallException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW, end, start, false);
      }
   }
}
