package org.glassfish.jersey.internal.guava;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ListMultimap<K, V> extends Multimap<K, V> {
   List<V> get(K var1);

   List<V> removeAll(Object var1);

   @Override
   Map<K, Collection<V>> asMap();

   @Override
   boolean equals(Object var1);
}
