package org.glassfish.jersey.internal.guava;

import java.util.Comparator;
import java.util.Iterator;

public abstract class Ordering<T> implements Comparator<T> {
   static final int LEFT_IS_GREATER = 1;
   static final int RIGHT_IS_GREATER = -1;

   Ordering() {
   }

   public static <C extends Comparable> Ordering<C> natural() {
      return NaturalOrdering.INSTANCE;
   }

   public static <T> Ordering<T> from(Comparator<T> comparator) {
      return (Ordering<T>)(comparator instanceof Ordering ? (Ordering)comparator : new ComparatorOrdering<>(comparator));
   }

   public <S extends T> Ordering<S> reverse() {
      return new ReverseOrdering<>(this);
   }

   <S extends T> Ordering<S> nullsFirst() {
      return new NullsFirstOrdering<>(this);
   }

   <S extends T> Ordering<S> nullsLast() {
      return new NullsLastOrdering<>(this);
   }

   @Override
   public abstract int compare(T var1, T var2);

   <E extends T> E min(Iterator<E> iterator) {
      E minSoFar = iterator.next();

      while (iterator.hasNext()) {
         minSoFar = this.min(minSoFar, iterator.next());
      }

      return minSoFar;
   }

   <E extends T> E min(Iterable<E> iterable) {
      return this.min(iterable.iterator());
   }

   <E extends T> E min(E a, E b) {
      return this.compare((T)a, (T)b) <= 0 ? a : b;
   }

   <E extends T> E min(E a, E b, E c, E... rest) {
      E minSoFar = this.min(this.min(a, b), c);

      for (E r : rest) {
         minSoFar = this.min(minSoFar, r);
      }

      return minSoFar;
   }

   <E extends T> E max(Iterator<E> iterator) {
      E maxSoFar = iterator.next();

      while (iterator.hasNext()) {
         maxSoFar = this.max(maxSoFar, iterator.next());
      }

      return maxSoFar;
   }

   <E extends T> E max(Iterable<E> iterable) {
      return this.max(iterable.iterator());
   }

   <E extends T> E max(E a, E b) {
      return this.compare((T)a, (T)b) >= 0 ? a : b;
   }

   <E extends T> E max(E a, E b, E c, E... rest) {
      E maxSoFar = this.max(this.max(a, b), c);

      for (E r : rest) {
         maxSoFar = this.max(maxSoFar, r);
      }

      return maxSoFar;
   }
}
