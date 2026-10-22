package org.eclipse.parsson;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.eclipse.parsson.api.BufferPool;

class BufferPoolImpl implements BufferPool {
   private volatile WeakReference<ConcurrentLinkedQueue<char[]>> queue;

   @Override
   public final char[] take() {
      char[] t = this.getQueue().poll();
      return t == null ? new char[4096] : t;
   }

   private ConcurrentLinkedQueue<char[]> getQueue() {
      WeakReference<ConcurrentLinkedQueue<char[]>> q = this.queue;
      if (q != null) {
         ConcurrentLinkedQueue<char[]> d = q.get();
         if (d != null) {
            return d;
         }
      }

      ConcurrentLinkedQueue<char[]> d = new ConcurrentLinkedQueue<>();
      this.queue = new WeakReference<>(d);
      return d;
   }

   @Override
   public final void recycle(char[] t) {
      this.getQueue().offer(t);
   }
}
