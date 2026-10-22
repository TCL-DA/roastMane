package org.glassfish.jersey.internal.guava;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public interface SetMultimap<K, V> extends Multimap<K, V> {
   Set<V> get(K var1);

   Set<V> removeAll(Object var1);

   Set<Entry<K, V>> entries();

   @Override
   Map<K, Collection<V>> asMap();

   @Override
   boolean equals(Object var1);
}
