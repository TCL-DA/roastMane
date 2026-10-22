package org.eclipse.jetty.io.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;
import org.eclipse.jetty.util.Pool;
import org.eclipse.jetty.util.Pool.Entry;

public class CompoundPool<P> implements Pool<P> {
   private final Pool<P> primaryPool;
   private final Pool<P> secondaryPool;

   public CompoundPool(Pool<P> primaryPool, Pool<P> secondaryPool) {
      this.primaryPool = primaryPool;
      this.secondaryPool = secondaryPool;
   }

   public Entry<P> reserve() {
      Entry<P> primary = this.primaryPool.reserve();
      return primary != null ? primary : this.secondaryPool.reserve();
   }

   public Entry<P> acquire() {
      Entry<P> primary = this.primaryPool.acquire();
      return primary != null ? primary : this.secondaryPool.acquire();
   }

   public boolean isTerminated() {
      return this.primaryPool.isTerminated();
   }

   public Collection<Entry<P>> terminate() {
      Collection<Entry<P>> entries = new ArrayList<>();
      entries.addAll(this.primaryPool.terminate());
      entries.addAll(this.secondaryPool.terminate());
      return entries;
   }

   public int size() {
      return this.primaryPool.size() + this.secondaryPool.size();
   }

   public int getMaxSize() {
      return this.primaryPool.getMaxSize() + this.secondaryPool.getMaxSize();
   }

   public Stream<Entry<P>> stream() {
      return Stream.concat(this.primaryPool.stream(), this.secondaryPool.stream());
   }
}
