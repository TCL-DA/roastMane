package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableOrderedMapIterator<K, V> implements OrderedMapIterator<K, V>, Unmodifiable {
   private final OrderedMapIterator<? extends K, ? extends V> iterator;

   public static <K, V> OrderedMapIterator<K, V> unmodifiableOrderedMapIterator(OrderedMapIterator<K, ? extends V> iterator) {
      if (iterator == null) {
         throw new NullPointerException("OrderedMapIterator must not be null");
      } else {
         return (OrderedMapIterator<K, V>)(iterator instanceof Unmodifiable ? iterator : new UnmodifiableOrderedMapIterator<>(iterator));
      }
   }

   private UnmodifiableOrderedMapIterator(OrderedMapIterator<K, ? extends V> iterator) {
      this.iterator = iterator;
   }

   @Override
   public boolean hasNext() {
      return this.iterator.hasNext();
   }

   @Override
   public K next() {
      return (K)this.iterator.next();
   }

   @Override
   public boolean hasPrevious() {
      return this.iterator.hasPrevious();
   }

   @Override
   public K previous() {
      return (K)this.iterator.previous();
   }

   @Override
   public K getKey() {
      return (K)this.iterator.getKey();
   }

   @Override
   public V getValue() {
      return (V)this.iterator.getValue();
   }

   @Override
   public V setValue(V value) {
      throw new UnsupportedOperationException("setValue() is not supported");
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException("remove() is not supported");
   }
}
