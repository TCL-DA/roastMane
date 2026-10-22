package org.glassfish.jersey.internal.guava;

public interface Cache<K, V> {
   V getIfPresent(Object var1);

   void put(K var1, V var2);
}
