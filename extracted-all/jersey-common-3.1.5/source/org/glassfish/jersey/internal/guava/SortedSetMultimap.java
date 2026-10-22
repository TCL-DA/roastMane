package org.glassfish.jersey.internal.guava;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;

public interface SortedSetMultimap<K, V> extends SetMultimap<K, V> {
   SortedSet<V> get(K var1);

   SortedSet<V> removeAll(Object var1);

   @Override
   Map<K, Collection<V>> asMap();

   Comparator<? super V> valueComparator();
}
