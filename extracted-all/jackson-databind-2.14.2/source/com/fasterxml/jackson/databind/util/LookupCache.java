package com.fasterxml.jackson.databind.util;

public interface LookupCache<K, V> {
   int size();

   V get(Object var1);

   V put(K var1, V var2);

   V putIfAbsent(K var1, V var2);

   void clear();
}
