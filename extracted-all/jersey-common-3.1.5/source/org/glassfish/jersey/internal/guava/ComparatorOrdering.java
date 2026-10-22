package org.glassfish.jersey.internal.guava;

import java.io.Serializable;
import java.util.Comparator;

final class ComparatorOrdering<T> extends Ordering<T> implements Serializable {
   private static final long serialVersionUID = 0L;
   private final Comparator<T> comparator;

   ComparatorOrdering(Comparator<T> comparator) {
      this.comparator = Preconditions.checkNotNull(comparator);
   }

   @Override
   public int compare(T a, T b) {
      return this.comparator.compare(a, b);
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object instanceof ComparatorOrdering) {
         ComparatorOrdering<?> that = (ComparatorOrdering<?>)object;
         return this.comparator.equals(that.comparator);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.comparator.hashCode();
   }

   @Override
   public String toString() {
      return this.comparator.toString();
   }
}
