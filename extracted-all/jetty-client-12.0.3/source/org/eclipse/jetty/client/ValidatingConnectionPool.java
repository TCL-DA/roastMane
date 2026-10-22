package org.eclipse.jetty.client;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.Scheduler.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidatingConnectionPool extends DuplexConnectionPool {
   private static final Logger LOG = LoggerFactory.getLogger(ValidatingConnectionPool.class);
   private final Scheduler scheduler;
   private final long timeout;
   private final Map<Connection, ValidatingConnectionPool.Holder> quarantine;

   public ValidatingConnectionPool(Destination destination, int maxConnections, Scheduler scheduler, long timeout) {
      super(destination, maxConnections);
      this.scheduler = scheduler;
      this.timeout = timeout;
      this.quarantine = new ConcurrentHashMap<>(maxConnections);
   }

   @ManagedAttribute(
      value = "The number of validating connections",
      readonly = true
   )
   public int getValidatingConnectionCount() {
      return this.quarantine.size();
   }

   @Override
   public boolean release(Connection connection) {
      ValidatingConnectionPool.Holder holder = new ValidatingConnectionPool.Holder(connection);
      holder.task = this.scheduler.schedule(holder, this.timeout, TimeUnit.MILLISECONDS);
      this.quarantine.put(connection, holder);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Validating for {}ms {}", this.timeout, connection);
      }

      this.released(connection);
      return true;
   }

   @Override
   public boolean remove(Connection connection) {
      ValidatingConnectionPool.Holder holder = this.quarantine.remove(connection);
      if (holder != null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Removed while validating {}", connection);
         }

         holder.cancel();
      }

      return super.remove(connection);
   }

   @Override
   public void dump(Appendable out, String indent) throws IOException {
      DumpableCollection toDump = new DumpableCollection("quarantine", this.quarantine.values());
      Dumpable.dumpObjects(out, indent, this, new Object[]{toDump});
   }

   @Override
   public String toString() {
      int size = this.quarantine.size();
      return String.format("%s[v=%d]", super.toString(), size);
   }

   private class Holder implements Runnable {
      private final long creationNanoTime = NanoTime.now();
      private final AtomicBoolean done = new AtomicBoolean();
      private final Connection connection;
      public Task task;

      public Holder(Connection connection) {
         this.connection = connection;
      }

      @Override
      public void run() {
         if (this.done.compareAndSet(false, true)) {
            boolean closed = ValidatingConnectionPool.this.isStopped();
            if (ValidatingConnectionPool.LOG.isDebugEnabled()) {
               ValidatingConnectionPool.LOG.debug("Validated {}", this.connection);
            }

            ValidatingConnectionPool.this.quarantine.remove(this.connection);
            if (!closed) {
               ValidatingConnectionPool.this.deactivate(this.connection);
            }

            ValidatingConnectionPool.this.idle(this.connection, closed);
            ValidatingConnectionPool.this.proceed();
         }
      }

      public boolean cancel() {
         if (this.done.compareAndSet(false, true)) {
            this.task.cancel();
            return true;
         } else {
            return false;
         }
      }

      @Override
      public String toString() {
         return String.format("%s[validationLeft=%dms]", this.connection, ValidatingConnectionPool.this.timeout - NanoTime.millisSince(this.creationNanoTime));
      }
   }
}
