package org.eclipse.jetty.util.compression;

import java.util.zip.Inflater;
import org.eclipse.jetty.util.component.Container;
import org.eclipse.jetty.util.thread.ThreadPool;

public class InflaterPool extends CompressionPool<Inflater> {
   private final boolean nowrap;

   public InflaterPool(int capacity, boolean nowrap) {
      super(capacity);
      this.nowrap = nowrap;
   }

   protected Inflater newPooled() {
      return new Inflater(this.nowrap);
   }

   protected void end(Inflater inflater) {
      inflater.end();
   }

   protected void reset(Inflater inflater) {
      inflater.reset();
   }

   public static InflaterPool ensurePool(Container container) {
      InflaterPool pool = container.getBean(InflaterPool.class);
      if (pool != null) {
         return pool;
      } else {
         int capacity = 1024;
         ThreadPool.SizedThreadPool threadPool = container.getBean(ThreadPool.SizedThreadPool.class);
         if (threadPool != null) {
            capacity = threadPool.getMaxThreads();
         }

         pool = new InflaterPool(capacity, true);
         container.addBean(pool, true);
         return pool;
      }
   }
}
