package org.glassfish.jersey.internal.guava;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

abstract class AbstractSetMultimap<K, V> extends AbstractMapBasedMultimap<K, V> implements SetMultimap<K, V> {
   private static final long serialVersionUID = 7431625294878419160L;

   AbstractSetMultimap(Map<K, Collection<V>> map) {
      super(map);
   }

   abstract Set<V> createCollection();

   Set<V> createUnmodifiableEmptyCollection() {
      return Collections.emptySet();
   }

   @Override
   public Set<V> get(K key) {
      return (Set<V>)super.get(key);
   }

   @Override
   public Set<Entry<K, V>> entries() {
      return (Set<Entry<K, V>>)super.entries();
   }

   @Override
   public Set<V> removeAll(Object key) {
      return (Set<V>)super.removeAll(key);
   }

   @Override
   public Map<K, Collection<V>> asMap() {
      return super.asMap();
   }

   @Override
   public boolean put(K key, V value) {
      return super.put(key, value);
   }

   @Override
   public boolean equals(Object object) {
      return super.equals(object);
   }
}
