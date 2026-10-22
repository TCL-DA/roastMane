package org.glassfish.jersey.internal.guava;

import java.io.Serializable;

public abstract class Equivalence<T> {
   public static Equivalence<Object> equals() {
      return Equivalence.Equals.INSTANCE;
   }

   public static Equivalence<Object> identity() {
      return Equivalence.Identity.INSTANCE;
   }

   public final boolean equivalent(T a, T b) {
      if (a == b) {
         return true;
      } else {
         return a != null && b != null ? this.doEquivalent(a, b) : false;
      }
   }

   protected abstract boolean doEquivalent(T var1, T var2);

   public final int hash(T t) {
      return t == null ? 0 : this.doHash(t);
   }

   protected abstract int doHash(T var1);

   static final class Equals extends Equivalence<Object> implements Serializable {
      static final Equivalence.Equals INSTANCE = new Equivalence.Equals();
      private static final long serialVersionUID = 1L;

      @Override
      protected boolean doEquivalent(Object a, Object b) {
         return a.equals(b);
      }

      @Override
      protected int doHash(Object o) {
         return o.hashCode();
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }

   static final class Identity extends Equivalence<Object> implements Serializable {
      static final Equivalence.Identity INSTANCE = new Equivalence.Identity();
      private static final long serialVersionUID = 1L;

      @Override
      protected boolean doEquivalent(Object a, Object b) {
         return false;
      }

      @Override
      protected int doHash(Object o) {
         return System.identityHashCode(o);
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }
}
