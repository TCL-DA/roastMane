package org.eclipse.jetty.util;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jetty.util.thread.AutoLock;

public class LockedPool<P> extends Pool.Wrapper<P> {
   private final AutoLock lock = new AutoLock();
   private final LockedPool.Tracker<P> tracker;

   public LockedPool(Pool<P> pool) {
      this(pool, LockedPool.Tracker.noTracker());
   }

   public LockedPool(Pool<P> pool, LockedPool.Tracker<P> tracker) {
      super(pool);
      this.tracker = tracker;
   }

   @Override
   public Pool.Entry<P> reserve() {
      LockedPool.LockedEntry var3;
      try (AutoLock ignored = this.lock.lock()) {
         Pool.Entry<P> entry = super.reserve();
         if (entry == null) {
            return null;
         }

         var3 = new LockedPool.LockedEntry(entry);
      }

      return var3;
   }

   @Override
   public Pool.Entry<P> acquire() {
      LockedPool.LockedEntry var4;
      try (AutoLock ignored = this.lock.lock()) {
         Pool.Entry<P> entry = super.acquire();
         if (entry == null) {
            return null;
         }

         LockedPool<P>.LockedEntry lockedEntry = new LockedPool.LockedEntry(entry);
         this.tracker.acquired(this.getWrapped(), entry);
         var4 = lockedEntry;
      }

      return var4;
   }

   @Override
   public Pool.Entry<P> acquire(Function<Pool.Entry<P>, P> creator) {
      LockedPool.LockedEntry var5;
      try (AutoLock ignored = this.lock.lock()) {
         Pool.Entry<P> entry = super.acquire(creator);
         if (entry == null) {
            return null;
         }

         LockedPool<P>.LockedEntry lockedEntry = new LockedPool.LockedEntry(entry);
         this.tracker.acquired(this.getWrapped(), entry);
         var5 = lockedEntry;
      }

      return var5;
   }

   @Override
   public boolean isTerminated() {
      boolean var2;
      try (AutoLock ignored = this.lock.lock()) {
         var2 = super.isTerminated();
      }

      return var2;
   }

   @Override
   public Collection<Pool.Entry<P>> terminate() {
      Collection var3;
      try (AutoLock ignored = this.lock.lock()) {
         Collection<Pool.Entry<P>> result = super.terminate();
         this.tracker.terminated(this.getWrapped(), result);
         var3 = result.stream().map(x$0 -> new LockedPool.LockedEntry((Pool.Entry<P>)x$0)).collect(Collectors.toList());
      }

      return var3;
   }

   @Override
   public int size() {
      int var2;
      try (AutoLock ignored = this.lock.lock()) {
         var2 = super.size();
      }

      return var2;
   }

   @Override
   public int getMaxSize() {
      int var2;
      try (AutoLock ignored = this.lock.lock()) {
         var2 = super.getMaxSize();
      }

      return var2;
   }

   @Override
   public Stream<Pool.Entry<P>> stream() {
      Stream var2;
      try (AutoLock ignored = this.lock.lock()) {
         var2 = super.stream().map(x$0 -> new LockedPool.LockedEntry((Pool.Entry<P>)x$0));
      }

      return var2;
   }

   public interface Factory<F> extends Pool.Factory<F> {
      @Override
      default Pool<F> wrap(Pool<F> pool) {
         return new LockedPool<>(pool);
      }
   }

   private class LockedEntry extends Pool.Entry.Wrapper<P> {
      public LockedEntry(Pool.Entry<P> wrapped) {
         super(wrapped);
      }

      @Override
      public boolean release() {
         boolean var3;
         try (AutoLock ignored = LockedPool.this.lock.lock()) {
            boolean released = super.release();
            if (released) {
               LockedPool.this.tracker.released(LockedPool.this.getWrapped(), this.getWrapped());
            }

            var3 = released;
         }

         return var3;
      }

      @Override
      public boolean remove() {
         boolean var3;
         try (AutoLock ignored = LockedPool.this.lock.lock()) {
            boolean removed = super.remove();
            if (removed) {
               LockedPool.this.tracker.removed(LockedPool.this.getWrapped(), this.getWrapped());
            }

            var3 = removed;
         }

         return var3;
      }
   }

   public interface Tracker<T> {
      static <S> LockedPool.Tracker<S> noTracker() {
         class NoTracker implements LockedPool.Tracker<S> {
            private static final LockedPool.Tracker<?> INSTANCE = new NoTracker();
         }

         return (LockedPool.Tracker<S>)NoTracker.INSTANCE;
      }

      default void acquired(Pool<T> pool, Pool.Entry<T> entry) {
      }

      default void released(Pool<T> pool, Pool.Entry<T> entry) {
      }

      default void removed(Pool<T> pool, Pool.Entry<T> entry) {
      }

      default void terminated(Pool<T> pool, Collection<Pool.Entry<T>> entries) {
      }
   }
}
