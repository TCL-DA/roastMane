package org.glassfish.jersey.internal.guava;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public interface LoadingCache<K, V> extends Cache<K, V>, Function<K, V> {
   V get(K var1) throws ExecutionException;

   @Deprecated
   @Override
   V apply(K var1);
}
