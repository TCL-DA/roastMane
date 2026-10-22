package org.glassfish.jersey.internal.guava;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;

abstract class AbstractSortedSetMultimap<K, V> extends AbstractSetMultimap<K, V> implements SortedSetMultimap<K, V> {
   private static final long serialVersionUID = 430848587173315748L;

   AbstractSortedSetMultimap(Map<K, Collection<V>> map) {
      super(map);
   }

   abstract SortedSet<V> createCollection();

   SortedSet<V> createUnmodifiableEmptyCollection() {
      return Collections.unmodifiableSortedSet(this.createCollection());
   }

   @Override
   public SortedSet<V> get(K key) {
      return (SortedSet<V>)super.get(key);
   }

   @Override
   public SortedSet<V> removeAll(Object key) {
      return (SortedSet<V>)super.removeAll(key);
   }

   @Override
   public Map<K, Collection<V>> asMap() {
      return super.asMap();
   }

   @Override
   public Collection<V> values() {
      return super.values();
   }
}
