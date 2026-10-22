package org.eclipse.jetty.util.compression;

import java.io.Closeable;
import org.eclipse.jetty.util.ConcurrentPool;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.Pool;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

@ManagedObject
public abstract class CompressionPool<T> extends ContainerLifeCycle {
   public static final int DEFAULT_CAPACITY = 1024;
   private int _capacity;
   private Pool<CompressionPool<T>.Entry> _pool;

   public CompressionPool(int capacity) {
      this._capacity = capacity;
   }

   public int getCapacity() {
      return this._capacity;
   }

   public void setCapacity(int capacity) {
      if (this.isStarted()) {
         throw new IllegalStateException("Already Started");
      } else {
         this._capacity = capacity;
      }
   }

   public Pool<CompressionPool<T>.Entry> getPool() {
      return this._pool;
   }

   protected abstract T newPooled();

   protected abstract void end(T var1);

   protected abstract void reset(T var1);

   public CompressionPool<T>.Entry acquire() {
      CompressionPool<T>.Entry entry = null;
      if (this._pool != null) {
         Pool.Entry<CompressionPool<T>.Entry> acquiredEntry = this._pool.acquire(e -> new CompressionPool.Entry(this.newPooled(), e));
         if (acquiredEntry != null) {
            entry = acquiredEntry.getPooled();
         }
      }

      return entry == null ? new CompressionPool.Entry(this.newPooled()) : entry;
   }

   public void release(CompressionPool<T>.Entry entry) {
      entry.release();
   }

   @Override
   protected void doStart() throws Exception {
      if (this._capacity > 0) {
         this._pool = new ConcurrentPool<>(ConcurrentPool.StrategyType.THREAD_ID, this._capacity, true);
         this.addBean(this._pool);
      }

      super.doStart();
   }

   @Override
   public void doStop() throws Exception {
      super.doStop();
      if (this._pool != null) {
         this.removeBean(this._pool);
         this._pool.terminate().stream().map(Pool.Entry::getPooled).forEach(IO::close);
      }
   }

   @Override
   public String toString() {
      return String.format(
         "%s@%x{%s,size=%d,capacity=%s}",
         this.getClass().getSimpleName(),
         this.hashCode(),
         this.getState(),
         this._pool == null ? -1 : this._pool.size(),
         this._capacity
      );
   }

   public class Entry implements Closeable {
      private final T _value;
      private final Pool.Entry<CompressionPool<T>.Entry> _entry;

      Entry(T value) {
         this(value, null);
      }

      Entry(T value, Pool.Entry<CompressionPool<T>.Entry> entry) {
         this._value = value;
         this._entry = entry;
      }

      public T get() {
         return this._value;
      }

      public void release() {
         CompressionPool.this.reset(this._value);
         if (this._entry != null) {
            if (!this._entry.release() && this._entry.remove()) {
               this.close();
            }
         } else {
            this.close();
         }
      }

      @Override
      public void close() {
         CompressionPool.this.end(this._value);
      }
   }
}
