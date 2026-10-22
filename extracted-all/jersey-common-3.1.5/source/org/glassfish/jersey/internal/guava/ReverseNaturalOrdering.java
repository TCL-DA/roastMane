package org.glassfish.jersey.internal.guava;

import java.io.Serializable;
import java.util.Iterator;

final class ReverseNaturalOrdering extends Ordering<Comparable> implements Serializable {
   static final ReverseNaturalOrdering INSTANCE = new ReverseNaturalOrdering();
   private static final long serialVersionUID = 0L;

   private ReverseNaturalOrdering() {
   }

   public int compare(Comparable left, Comparable right) {
      Preconditions.checkNotNull(left);
      return left == right ? 0 : right.compareTo(left);
   }

   @Override
   public <S extends Comparable> Ordering<S> reverse() {
      return Ordering.natural();
   }

   public <E extends Comparable> E min(E a, E b) {
      return NaturalOrdering.INSTANCE.max(a, b);
   }

   public <E extends Comparable> E min(E a, E b, E c, E... rest) {
      return NaturalOrdering.INSTANCE.max(a, b, c, rest);
   }

   public <E extends Comparable> E min(Iterator<E> iterator) {
      return NaturalOrdering.INSTANCE.max(iterator);
   }

   public <E extends Comparable> E min(Iterable<E> iterable) {
      return NaturalOrdering.INSTANCE.max(iterable);
   }

   public <E extends Comparable> E max(E a, E b) {
      return NaturalOrdering.INSTANCE.min(a, b);
   }

   public <E extends Comparable> E max(E a, E b, E c, E... rest) {
      return NaturalOrdering.INSTANCE.min(a, b, c, rest);
   }

   public <E extends Comparable> E max(Iterator<E> iterator) {
      return NaturalOrdering.INSTANCE.min(iterator);
   }

   public <E extends Comparable> E max(Iterable<E> iterable) {
      return NaturalOrdering.INSTANCE.min(iterable);
   }

   private Object readResolve() {
      return INSTANCE;
   }

   @Override
   public String toString() {
      return "Ordering.natural().reverse()";
   }
}
