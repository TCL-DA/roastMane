package org.glassfish.hk2.utilities.cache;

public interface WeakCARCache<K, V> {
   V compute(K var1);

   int getKeySize();

   int getValueSize();

   int getT1Size();

   int getT2Size();

   int getB1Size();

   int getB2Size();

   void clear();

   int getMaxSize();

   Computable<K, V> getComputable();

   boolean remove(K var1);

   void releaseMatching(CacheKeyFilter<K> var1);

   void clearStaleReferences();

   int getP();

   String dumpAllLists();

   double getHitRate();
}
