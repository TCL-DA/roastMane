package org.eclipse.jetty.io;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public interface Retainable {
   default boolean canRetain() {
      return false;
   }

   default void retain() {
   }

   default boolean release() {
      return true;
   }

   public static class ReferenceCounter implements Retainable {
      private final AtomicInteger references;

      public ReferenceCounter() {
         this(1);
      }

      protected ReferenceCounter(int initialCount) {
         this.references = new AtomicInteger(initialCount);
      }

      public int get() {
         return this.references.get();
      }

      public void acquire() {
         if (this.references.getAndUpdate(c -> c == 0 ? 1 : c) != 0) {
            throw new IllegalStateException("acquired while in use " + this);
         }
      }

      @Override
      public boolean canRetain() {
         return true;
      }

      @Override
      public void retain() {
         if (!this.tryRetain()) {
            throw new IllegalStateException("released " + this);
         }
      }

      public boolean tryRetain() {
         return this.references.getAndUpdate(c -> c == 0 ? 0 : c + 1) != 0;
      }

      @Override
      public boolean release() {
         int ref = this.references.updateAndGet(c -> {
            if (c == 0) {
               throw new IllegalStateException("already released " + this);
            } else {
               return c - 1;
            }
         });
         return ref == 0;
      }

      public boolean isRetained() {
         return this.references.get() > 1;
      }

      @Override
      public String toString() {
         return String.format("%s@%x[r=%d]", this.getClass().getSimpleName(), this.hashCode(), this.get());
      }
   }

   public static class Wrapper implements Retainable {
      private final Retainable wrapped;

      public Wrapper(Retainable wrapped) {
         this.wrapped = Objects.requireNonNull(wrapped);
      }

      public Retainable getWrapped() {
         return this.wrapped;
      }

      @Override
      public boolean canRetain() {
         return this.getWrapped().canRetain();
      }

      @Override
      public void retain() {
         this.getWrapped().retain();
      }

      @Override
      public boolean release() {
         return this.getWrapped().release();
      }

      @Override
      public String toString() {
         return "%s@%x[%s]".formatted(this.getClass().getSimpleName(), this.hashCode(), this.getWrapped());
      }
   }
}
