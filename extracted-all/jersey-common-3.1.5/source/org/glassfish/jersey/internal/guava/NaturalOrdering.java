package org.glassfish.jersey.internal.guava;

import java.io.Serializable;

final class NaturalOrdering extends Ordering<Comparable> implements Serializable {
   static final NaturalOrdering INSTANCE = new NaturalOrdering();
   private static final long serialVersionUID = 0L;

   private NaturalOrdering() {
   }

   public int compare(Comparable left, Comparable right) {
      Preconditions.checkNotNull(left);
      Preconditions.checkNotNull(right);
      return left.compareTo(right);
   }

   @Override
   public <S extends Comparable> Ordering<S> reverse() {
      return ReverseNaturalOrdering.INSTANCE;
   }

   private Object readResolve() {
      return INSTANCE;
   }

   @Override
   public String toString() {
      return "Ordering.natural()";
   }
}
