package org.apache.commons.collections4.bag;

import java.util.Comparator;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.SortedBag;

public class PredicatedSortedBag<E> extends PredicatedBag<E> implements SortedBag<E> {
   private static final long serialVersionUID = 3448581314086406616L;

   public static <E> PredicatedSortedBag<E> predicatedSortedBag(SortedBag<E> bag, Predicate<? super E> predicate) {
      return new PredicatedSortedBag<>(bag, predicate);
   }

   protected PredicatedSortedBag(SortedBag<E> bag, Predicate<? super E> predicate) {
      super(bag, predicate);
   }

   protected SortedBag<E> decorated() {
      return (SortedBag<E>)super.decorated();
   }

   @Override
   public E first() {
      return this.decorated().first();
   }

   @Override
   public E last() {
      return this.decorated().last();
   }

   @Override
   public Comparator<? super E> comparator() {
      return this.decorated().comparator();
   }
}
