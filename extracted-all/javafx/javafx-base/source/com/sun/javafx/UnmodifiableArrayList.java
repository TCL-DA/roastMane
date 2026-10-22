package com.sun.javafx;

import java.util.AbstractList;
import java.util.RandomAccess;

public class UnmodifiableArrayList<T> extends AbstractList<T> implements RandomAccess {
   private T[] elements;
   private final int size;

   public UnmodifiableArrayList(T[] var1, int var2) {
      assert var1 == null ? var2 == 0 : var2 <= var1.length;

      this.size = var2;
      this.elements = (T[])var1;
   }

   @Override
   public T get(int var1) {
      return this.elements[var1];
   }

   @Override
   public int size() {
      return this.size;
   }
}
