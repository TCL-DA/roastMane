package org.eclipse.jetty.util;

import java.util.concurrent.atomic.AtomicInteger;

public class CountingCallback extends Callback.Nested {
   private final AtomicInteger count;

   public CountingCallback(Callback callback, int count) {
      super(callback);
      if (count < 1) {
         throw new IllegalArgumentException();
      } else {
         this.count = new AtomicInteger(count);
      }
   }

   @Override
   public void succeeded() {
      int current;
      do {
         current = this.count.get();
         if (current == 0) {
            return;
         }
      } while (!this.count.compareAndSet(current, current - 1));

      if (current == 1) {
         super.succeeded();
      }
   }

   @Override
   public void failed(Throwable failure) {
      int current;
      do {
         current = this.count.get();
         if (current == 0) {
            return;
         }
      } while (!this.count.compareAndSet(current, 0));

      super.failed(failure);
   }

   @Override
   public String toString() {
      return String.format("%s@%x", this.getClass().getSimpleName(), this.hashCode());
   }
}
