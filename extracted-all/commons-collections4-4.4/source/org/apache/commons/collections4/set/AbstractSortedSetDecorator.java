package org.apache.commons.collections4.set;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;

public abstract class AbstractSortedSetDecorator<E> extends AbstractSetDecorator<E> implements SortedSet<E> {
   private static final long serialVersionUID = -3462240946294214398L;

   protected AbstractSortedSetDecorator() {
   }

   protected AbstractSortedSetDecorator(Set<E> set) {
      super(set);
   }

   protected SortedSet<E> decorated() {
      return (SortedSet<E>)super.decorated();
   }

   @Override
   public SortedSet<E> subSet(E fromElement, E toElement) {
      return this.decorated().subSet(fromElement, toElement);
   }

   @Override
   public SortedSet<E> headSet(E toElement) {
      return this.decorated().headSet(toElement);
   }

   @Override
   public SortedSet<E> tailSet(E fromElement) {
      return this.decorated().tailSet(fromElement);
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
