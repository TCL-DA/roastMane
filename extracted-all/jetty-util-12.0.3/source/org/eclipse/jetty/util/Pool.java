package org.eclipse.jetty.util;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject
public interface Pool<P> {
   Pool.Entry<P> reserve();

   Pool.Entry<P> acquire();

   default Pool.Entry<P> acquire(Function<Pool.Entry<P>, P> creator) {
      Pool.Entry<P> entry = this.acquire();
      if (entry != null) {
         return entry;
      } else {
         entry = this.reserve();
         if (entry == null) {
            return null;
         } else {
            P value;
            try {
               value = creator.apply(entry);
            } catch (Throwable var5) {
               entry.remove();
               throw var5;
            }

            if (value == null) {
               entry.remove();
               return null;
            } else {
               return entry.enable(value, true) ? entry : null;
            }
         }
      }
   }

   boolean isTerminated();

   Collection<Pool.Entry<P>> terminate();

   @ManagedAttribute("The number of entries")
   int size();

   @ManagedAttribute("The maximum number of entries")
   int getMaxSize();

   Stream<Pool.Entry<P>> stream();

   @ManagedAttribute("The number of reserved entries")
   default int getReservedCount() {
      return (int)this.stream().filter(Pool.Entry::isReserved).count();
   }

   @ManagedAttribute("The number of idle entries")
   default int getIdleCount() {
      return (int)this.stream().filter(Pool.Entry::isIdle).count();
   }

   @ManagedAttribute("The number of in-use entries")
   default int getInUseCount() {
      return (int)this.stream().filter(Pool.Entry::isInUse).count();
   }

   @ManagedAttribute("The number of terminated entries")
   default int getTerminatedCount() {
      return (int)this.stream().filter(Pool.Entry::isTerminated).count();
   }

   public interface Entry<E> {
      boolean enable(E var1, boolean var2);

      E getPooled();

      boolean release();

      boolean remove();

      boolean isReserved();

      boolean isIdle();

      boolean isInUse();

      boolean isTerminated();

      public static class Wrapper<W> implements Pool.Entry<W> {
         private final Pool.Entry<W> wrapped;

         public Wrapper(Pool.Entry<W> wrapped) {
            this.wrapped = wrapped;
         }

         public Pool.Entry<W> getWrapped() {
            return this.wrapped;
         }

         @Override
         public boolean enable(W pooled, boolean acquire) {
            return this.getWrapped().enable(pooled, acquire);
         }

         @Override
         public W getPooled() {
            return this.getWrapped().getPooled();
         }

         @Override
         public boolean release() {
            return this.getWrapped().release();
         }

         @Override
         public boolean remove() {
            return this.getWrapped().remove();
         }

         @Override
         public boolean isReserved() {
            return this.getWrapped().isReserved();
         }

         @Override
         public boolean isIdle() {
            return this.getWrapped().isIdle();
         }

         @Override
         public boolean isInUse() {
            return this.getWrapped().isInUse();
         }

         @Override
         public boolean isTerminated() {
            return this.getWrapped().isTerminated();
         }
      }
   }

   public interface Factory<F> {
      Pool<F> newPool();

      default Pool<F> wrap(Pool<F> pool) {
         return pool;
      }
   }

   public static class Wrapper<W> implements Pool<W> {
      private final Pool<W> wrapped;

      public Wrapper(Pool<W> wrapped) {
         this.wrapped = wrapped;
      }

      public Pool<W> getWrapped() {
         return this.wrapped;
      }

      @Override
      public Pool.Entry<W> reserve() {
         return this.getWrapped().reserve();
      }

      @Override
      public Pool.Entry<W> acquire() {
         return this.getWrapped().acquire();
      }

      @Override
      public Pool.Entry<W> acquire(Function<Pool.Entry<W>, W> creator) {
         return this.getWrapped().acquire(creator);
      }

      @Override
      public boolean isTerminated() {
         return this.getWrapped().isTerminated();
      }

      @Override
      public Collection<Pool.Entry<W>> terminate() {
         return this.getWrapped().terminate();
      }

      @Override
      public int size() {
         return this.getWrapped().size();
      }

      @Override
      public int getMaxSize() {
         return this.getWrapped().getMaxSize();
      }

      @Override
      public Stream<Pool.Entry<W>> stream() {
         return this.getWrapped().stream();
      }

      @Override
      public int getReservedCount() {
         return this.getWrapped().getReservedCount();
      }

      @Override
      public int getIdleCount() {
         return this.getWrapped().getIdleCount();
      }

      @Override
      public int getInUseCount() {
         return this.getWrapped().getInUseCount();
      }

      @Override
      public int getTerminatedCount() {
         return this.getWrapped().getTerminatedCount();
      }
   }
}
