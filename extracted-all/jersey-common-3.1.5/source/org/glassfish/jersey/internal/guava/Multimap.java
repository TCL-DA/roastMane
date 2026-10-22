package org.glassfish.jersey.internal.guava;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public interface Multimap<K, V> {
   int size();

   boolean containsKey(Object var1);

   boolean containsValue(Object var1);

   boolean containsEntry(Object var1, Object var2);

   boolean put(K var1, V var2);

   boolean remove(Object var1, Object var2);

   boolean putAll(K var1, Iterable<? extends V> var2);

   Collection<V> removeAll(Object var1);

   void clear();

   Collection<V> get(K var1);

   Set<K> keySet();

   Collection<V> values();

   Collection<Entry<K, V>> entries();

   Map<K, Collection<V>> asMap();

   @Override
   boolean equals(Object var1);

   @Override
   int hashCode();
}
