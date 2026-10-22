package org.eclipse.jetty.util;

import java.util.concurrent.CompletableFuture;

public abstract class CompletableTask<T> extends CompletableFuture<T> implements Runnable {
   public CompletableTask<T> start() {
      this.run();
      return this;
   }
}
