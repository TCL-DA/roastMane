package org.glassfish.jersey.internal.guava;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

final class Uninterruptibles {
   private Uninterruptibles() {
   }

   public static <V> V getUninterruptibly(Future<V> future) throws ExecutionException {
      boolean interrupted = false;

      try {
         while (true) {
            try {
               return future.get();
            } catch (InterruptedException var6) {
               interrupted = true;
            }
         }
      } finally {
         if (interrupted) {
            Thread.currentThread().interrupt();
         }
      }
   }
}
