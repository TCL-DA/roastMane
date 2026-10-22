package org.glassfish.jersey.internal.guava;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public interface ListenableFuture<V> extends Future<V> {
   void addListener(Runnable var1, Executor var2);
}
