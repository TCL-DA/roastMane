package org.glassfish.hk2.utilities.general;

import java.util.Map.Entry;
import org.glassfish.hk2.utilities.cache.CacheKeyFilter;

public interface WeakHashClock<K, V> {
   void put(K var1, V var2);

   V get(K var1);

   V remove(K var1);

   void releaseMatching(CacheKeyFilter<K> var1);

   int size();

   Entry<K, V> next();

   void clear();

   void clearStaleReferences();

   boolean hasWeakKeys();
}
