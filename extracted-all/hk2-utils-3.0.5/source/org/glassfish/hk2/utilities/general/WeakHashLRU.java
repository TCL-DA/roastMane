package org.glassfish.hk2.utilities.general;

import org.glassfish.hk2.utilities.cache.CacheKeyFilter;

public interface WeakHashLRU<K> {
   void add(K var1);

   boolean contains(K var1);

   boolean remove(K var1);

   void releaseMatching(CacheKeyFilter<K> var1);

   int size();

   K remove();

   void clear();

   void clearStaleReferences();
}
