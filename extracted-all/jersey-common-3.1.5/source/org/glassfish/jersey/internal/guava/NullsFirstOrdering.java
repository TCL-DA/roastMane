package org.glassfish.jersey.internal.guava;

import java.io.Serializable;

final class NullsFirstOrdering<T> extends Ordering<T> implements Serializable {
   private static final long serialVersionUID = 0L;
   private final Ordering<? super T> ordering;

   NullsFirstOrdering(Ordering<? super T> ordering) {
      this.ordering = ordering;
   }

   @Override
   public int compare(T left, T right) {
      if (left == right) {
         return 0;
      } else if (left == null) {
         return -1;
      } else {
         return right == null ? 1 : this.ordering.compare(left, right);
      }
   }

   @Override
   public <S extends T> Ordering<S> reverse() {
      return this.ordering.reverse().nullsLast();
   }

   @Override
   public <S extends T> Ordering<S> nullsFirst() {
      return this;
   }

   @Override
   public <S extends T> Ordering<S> nullsLast() {
      return this.ordering.nullsLast();
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object instanceof NullsFirstOrdering) {
         NullsFirstOrdering<?> that = (NullsFirstOrdering<?>)object;
         return this.ordering.equals(that.ordering);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.ordering.hashCode() ^ 957692532;
   }

   @Override
   public String toString() {
      return this.ordering + ".nullsFirst()";
   }
}
