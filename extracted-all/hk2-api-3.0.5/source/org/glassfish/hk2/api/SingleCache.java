package org.glassfish.hk2.api;

public interface SingleCache<T> {
   T getCache();

   boolean isCacheSet();

   void setCache(T var1);

   void releaseCache();
}
