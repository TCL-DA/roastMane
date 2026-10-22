package org.glassfish.jersey.internal.guava;

import java.util.Collection;
import java.util.SortedMap;
import java.util.SortedSet;

abstract class AbstractSortedKeySortedSetMultimap<K, V> extends AbstractSortedSetMultimap<K, V> {
   AbstractSortedKeySortedSetMultimap(SortedMap<K, Collection<V>> map) {
      super(map);
   }

   public SortedMap<K, Collection<V>> asMap() {
      return (SortedMap<K, Collection<V>>)super.asMap();
   }

   SortedMap<K, Collection<V>> backingMap() {
      return (SortedMap<K, Collection<V>>)super.backingMap();
   }

   public SortedSet<K> keySet() {
      return (SortedSet<K>)super.keySet();
   }
}
