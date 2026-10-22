package org.glassfish.hk2.utilities.cache;

public interface CacheKeyFilter<K> {
   boolean matches(K var1);
}
