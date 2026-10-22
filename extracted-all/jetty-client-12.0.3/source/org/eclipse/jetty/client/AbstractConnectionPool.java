package org.eclipse.jetty.client;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.eclipse.jetty.client.transport.HttpDestination;
import org.eclipse.jetty.util.Attachable;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.Pool;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.Pool.Entry;
import org.eclipse.jetty.util.Pool.Factory;
import org.eclipse.jetty.util.Promise.Completable;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.thread.Sweeper.Sweepable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public abstract class AbstractConnectionPool extends ContainerLifeCycle implements ConnectionPool, Dumpable, Sweepable {
   private static final Logger LOG = LoggerFactory.getLogger(AbstractConnectionPool.class);
   private final AtomicInteger pending = new AtomicInteger();
   private final HttpDestination destination;
   private final org.eclipse.jetty.util.Pool.Factory<Connection> poolFactory;
   private Pool<Connection> pool;
   private boolean maximizeConnections;
   private volatile long maxDurationNanos;
   private volatile int maxUsage;
   private volatile int initialMaxMultiplex;

   protected AbstractConnectionPool(Destination destination, org.eclipse.jetty.util.Pool.Factory<Connection> poolFactory, int initialMaxMultiplex) {
      this.destination = (HttpDestination)destination;
      this.poolFactory = poolFactory;
      this.initialMaxMultiplex = initialMaxMultiplex;
   }

   protected void doStart() throws Exception {
      this.pool = this.poolFactory.wrap(this.poolFactory.newPool());
      this.addBean(this.pool);
      super.doStart();
   }

   protected void doStop() throws Exception {
      super.doStop();
      this.removeBean(this.pool);
      this.pool.terminate().forEach(this::close);
      this.pool = null;
   }

   @Override
   public CompletableFuture<Void> preCreateConnections(int connectionCount) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Pre-creating connections {}/{}", connectionCount, this.getMaxConnectionCount());
      }

      List<CompletableFuture<?>> futures = new ArrayList<>();

      for (int i = 0; i < connectionCount; i++) {
         Entry<Connection> entry = this.pool.reserve();
         if (entry == null) {
            break;
         }

         this.pending.incrementAndGet();
         Completable<Connection> future = new AbstractConnectionPool.FutureConnection(entry);
         futures.add(future);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Pre-creating connection {}/{} at {}", new Object[]{futures.size(), this.getMaxConnectionCount(), entry});
         }

         this.destination.newConnection(future);
      }

      return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
   }

   @ManagedAttribute("The maximum duration in milliseconds a connection can be used for before it gets closed")
   public long getMaxDuration() {
      return TimeUnit.NANOSECONDS.toMillis(this.maxDurationNanos);
   }

   public void setMaxDuration(long maxDurationInMs) {
      this.maxDurationNanos = TimeUnit.MILLISECONDS.toNanos(maxDurationInMs);
   }

   protected int getInitialMaxMultiplex() {
      return this.initialMaxMultiplex;
   }

   protected void setInitialMaxMultiplex(int initialMaxMultiplex) {
      this.initialMaxMultiplex = initialMaxMultiplex;
   }

   @ManagedAttribute("The maximum amount of times a connection is used before it gets closed")
   public int getMaxUsage() {
      return this.maxUsage;
   }

   public void setMaxUsage(int maxUsage) {
      this.maxUsage = maxUsage;
   }

   @ManagedAttribute(
      value = "The number of active connections",
      readonly = true
   )
   public int getActiveConnectionCount() {
      Pool<Connection> pool = this.pool;
      return pool == null ? 0 : pool.getInUseCount();
   }

   @ManagedAttribute(
      value = "The number of idle connections",
      readonly = true
   )
   public int getIdleConnectionCount() {
      Pool<Connection> pool = this.pool;
      return pool == null ? 0 : pool.getIdleCount();
   }

   @ManagedAttribute(
      value = "The max number of connections",
      readonly = true
   )
   public int getMaxConnectionCount() {
      Pool<Connection> pool = this.pool;
      return pool == null ? 0 : pool.getMaxSize();
   }

   @ManagedAttribute(
      value = "The number of connections",
      readonly = true
   )
   public int getConnectionCount() {
      Pool<Connection> pool = this.pool;
      return pool == null ? 0 : pool.size();
   }

   @ManagedAttribute(
      value = "The number of pending connections",
      readonly = true
   )
   public int getPendingConnectionCount() {
      return this.pending.get();
   }

   @Override
   public boolean isEmpty() {
      Pool<Connection> pool = this.pool;
      return pool == null || pool.size() == 0;
   }

   @ManagedAttribute("Whether the pool tries to maximize the number of connections used")
   public boolean isMaximizeConnections() {
      return this.maximizeConnections;
   }

   public void setMaximizeConnections(boolean maximizeConnections) {
      this.maximizeConnections = maximizeConnections;
   }

   @Override
   public Connection acquire(boolean create) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Acquiring create={} on {}", create, this);
      }

      Connection connection = this.activate();
      if (connection == null) {
         this.tryCreate(create);
         connection = this.activate();
      }

      return connection;
   }

   protected void tryCreate(boolean create) {
      int connectionCount = this.getConnectionCount();
      if (LOG.isDebugEnabled()) {
         LOG.debug(
            "Try creating connection {}/{} with {} pending", new Object[]{connectionCount, this.getMaxConnectionCount(), this.getPendingConnectionCount()}
         );
      }

      int multiplexed = this.getInitialMaxMultiplex();

      int pending;
      do {
         pending = this.pending.get();
         int supply = pending * multiplexed;
         int demand = this.destination.getQueuedRequestCount() + (create ? 1 : 0);
         boolean tryCreate = this.isMaximizeConnections() || supply < demand;
         if (LOG.isDebugEnabled()) {
            LOG.debug("Try creating({}) connection, pending/demand/supply: {}/{}/{}, result={}", new Object[]{create, pending, demand, supply, tryCreate});
         }

         if (!tryCreate) {
            return;
         }
      } while (!this.pending.compareAndSet(pending, pending + 1));

      Entry<Connection> entry = this.pool.reserve();
      if (entry == null) {
         this.pending.decrementAndGet();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Not creating connection as pool {} is full, pending: {}", this.pool, this.pending);
         }
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Creating connection {}/{} at {}", new Object[]{connectionCount, this.getMaxConnectionCount(), entry});
         }

         Promise<Connection> future = new AbstractConnectionPool.FutureConnection(entry);
         this.destination.newConnection(future);
      }
   }

   @Override
   public boolean accept(Connection connection) {
      if (connection instanceof Attachable attachable) {
         Entry<Connection> entry = this.pool.reserve();
         if (entry == null) {
            return false;
         } else {
            if (LOG.isDebugEnabled()) {
               LOG.debug("onCreating {} {}", entry, connection);
            }

            attachable.setAttachment(new AbstractConnectionPool.EntryHolder(entry));
            this.onCreated(connection);
            entry.enable(connection, false);
            this.idle(connection, false);
            return true;
         }
      } else {
         throw new IllegalArgumentException("Invalid connection object: " + connection);
      }
   }

   protected void proceed() {
      this.destination.succeeded();
   }

   protected Connection activate() {
      while (true) {
         Entry<Connection> entry = this.pool.acquire();
         if (entry != null) {
            Connection connection = (Connection)entry.getPooled();
            long maxDurationNanos = this.maxDurationNanos;
            if (maxDurationNanos > 0L) {
               AbstractConnectionPool.EntryHolder holder = (AbstractConnectionPool.EntryHolder)((Attachable)connection).getAttachment();
               if (holder.isExpired(maxDurationNanos)) {
                  boolean canClose = this.remove(connection);
                  if (canClose) {
                     IO.close(connection);
                  }

                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Connection removed{}: expired {} {}", new Object[]{canClose ? " and closed" : "", entry, this.pool});
                  }
                  continue;
               }
            }

            int maxUsage = this.getMaxUsage();
            if (connection instanceof ConnectionPool.MaxUsable maxUsable) {
               maxUsage = maxUsable.getMaxUsage();
            }

            if (maxUsage > 0) {
               AbstractConnectionPool.EntryHolder holder = (AbstractConnectionPool.EntryHolder)((Attachable)connection).getAttachment();
               if (!holder.use(maxUsage)) {
                  boolean canClosex = this.remove(connection);
                  if (canClosex) {
                     IO.close(connection);
                  }

                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Connection removed{}: over used {} {}", new Object[]{canClosex ? " and closed" : "", entry, this.pool});
                  }
                  continue;
               }
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("Activated {} {}", entry, this.pool);
            }

            this.acquired(connection);
            return connection;
         }

         return null;
      }
   }

   @Override
   public boolean isActive(Connection connection) {
      if (connection instanceof Attachable attachable) {
         AbstractConnectionPool.EntryHolder holder = (AbstractConnectionPool.EntryHolder)attachable.getAttachment();
         return holder == null ? false : !holder.entry.isIdle();
      } else {
         throw new IllegalArgumentException("Invalid connection object: " + connection);
      }
   }

   @Override
   public boolean release(Connection connection) {
      if (!this.deactivate(connection)) {
         return false;
      } else {
         this.released(connection);
         return this.idle(connection, this.isStopped());
      }
   }

   protected boolean deactivate(Connection connection) {
      if (connection instanceof Attachable attachable) {
         AbstractConnectionPool.EntryHolder holder = (AbstractConnectionPool.EntryHolder)attachable.getAttachment();
         if (holder == null) {
            return true;
         } else {
            long maxDurationNanos = this.maxDurationNanos;
            if (maxDurationNanos > 0L && holder.isExpired(maxDurationNanos)) {
               return !this.remove(connection);
            } else {
               int maxUsage = this.getMaxUsage();
               if (connection instanceof ConnectionPool.MaxUsable maxUsable) {
                  maxUsage = maxUsable.getMaxUsage();
               }

               if (maxUsage > 0 && holder.isOverUsed(maxUsage)) {
                  return !this.remove(connection);
               } else {
                  boolean reusable = holder.entry.release();
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Released ({}) {} {}", new Object[]{reusable, holder.entry, this.pool});
                  }

                  return reusable ? true : !this.remove(connection);
               }
            }
         }
      } else {
         throw new IllegalArgumentException("Invalid connection object: " + connection);
      }
   }

   @Override
   public boolean remove(Connection connection) {
      if (connection instanceof Attachable attachable) {
         AbstractConnectionPool.EntryHolder holder = (AbstractConnectionPool.EntryHolder)attachable.getAttachment();
         if (holder == null) {
            return false;
         } else {
            boolean removed = holder.entry.remove();
            if (removed) {
               attachable.setAttachment(null);
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("Removed ({}) {} {}", new Object[]{removed, holder.entry, this.pool});
            }

            if (removed) {
               this.released(connection);
               this.removed(connection);
            }

            return removed;
         }
      } else {
         throw new IllegalArgumentException("Invalid connection object: " + connection);
      }
   }

   protected void onCreated(Connection connection) {
   }

   protected boolean idle(Connection connection, boolean close) {
      return !close;
   }

   protected void acquired(Connection connection) {
   }

   protected void released(Connection connection) {
   }

   protected void removed(Connection connection) {
   }

   Collection<Connection> getIdleConnections() {
      return this.pool
         .stream()
         .filter(Entry::isIdle)
         .filter(entry -> !entry.isTerminated())
         .<Connection>map(Entry::getPooled)
         .collect(Collectors.toCollection(ArrayDeque::new));
   }

   Collection<Connection> getActiveConnections() {
      return this.pool
         .stream()
         .filter(entry -> !entry.isIdle())
         .filter(entry -> !entry.isTerminated())
         .<Connection>map(Entry::getPooled)
         .collect(Collectors.toList());
   }

   private void close(Entry<Connection> entry) {
      assert this.pool.isTerminated();

      Connection connection = (Connection)entry.getPooled();

      while (true) {
         if (entry.remove()) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Removed terminated entry {}", entry);
            }

            this.removed(connection);
            IO.close(connection);
         }

         if (!entry.isInUse()) {
            return;
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("Entry {} still in use, removing it again", entry);
         }
      }
   }

   public void dump(Appendable out, String indent) throws IOException {
      Dumpable.dumpObjects(out, indent, this, new Object[0]);
   }

   public boolean sweep() {
      Pool<Connection> pool = this.pool;
      if (pool != null) {
         pool.stream()
            .<Connection>map(Entry::getPooled)
            .filter(connection -> connection instanceof Sweepable)
            .forEach(
               connection -> {
                  if (((Sweepable)connection).sweep()) {
                     boolean removed = this.remove(connection);
                     LOG.warn(
                        "Connection swept: {}{}{} from active connections{}{}",
                        new Object[]{connection, System.lineSeparator(), removed ? "Removed" : "Not removed", System.lineSeparator(), this.dump()}
                     );
                  }
               }
            );
      }

      return false;
   }

   @Override
   public String toString() {
      return String.format(
         "%s@%x[s=%s,c=%d/%d/%d,a=%d,i=%d,q=%d,p=%s]",
         this.getClass().getSimpleName(),
         this.hashCode(),
         this.getState(),
         this.getPendingConnectionCount(),
         this.getConnectionCount(),
         this.getMaxConnectionCount(),
         this.getActiveConnectionCount(),
         this.getIdleConnectionCount(),
         this.destination.getQueuedRequestCount(),
         this.pool
      );
   }

   private static class EntryHolder {
      private final Entry<Connection> entry;
      private final long creationNanoTime = NanoTime.now();
      private final AtomicInteger usage = new AtomicInteger();

      private EntryHolder(Entry<Connection> entry) {
         this.entry = Objects.requireNonNull(entry);
      }

      private boolean isExpired(long timeoutNanos) {
         return NanoTime.since(this.creationNanoTime) >= timeoutNanos;
      }

      private boolean use(int maxUsage) {
         int current;
         do {
            current = this.usage.get();
            if (current >= maxUsage) {
               return false;
            }
         } while (!this.usage.compareAndSet(current, current + 1));

         return true;
      }

      public boolean isOverUsed(int maxUsage) {
         return this.usage.get() >= maxUsage;
      }
   }

   private class FutureConnection extends Completable<Connection> {
      private final Entry<Connection> reserved;

      public FutureConnection(Entry<Connection> reserved) {
         this.reserved = reserved;
      }

      public void succeeded(Connection connection) {
         if (AbstractConnectionPool.LOG.isDebugEnabled()) {
            AbstractConnectionPool.LOG.debug("Connection creation succeeded {}: {}", this.reserved, connection);
         }

         if (connection instanceof Attachable) {
            ((Attachable)connection).setAttachment(new AbstractConnectionPool.EntryHolder(this.reserved));
            AbstractConnectionPool.this.onCreated(connection);
            AbstractConnectionPool.this.pending.decrementAndGet();
            this.reserved.enable(connection, false);
            AbstractConnectionPool.this.idle(connection, false);
            this.complete(null);
            AbstractConnectionPool.this.proceed();
         } else {
            this.failed(new IllegalArgumentException("Invalid connection object: " + connection));
         }
      }

      public void failed(Throwable x) {
         if (AbstractConnectionPool.LOG.isDebugEnabled()) {
            AbstractConnectionPool.LOG.debug("Connection creation failed {}", this.reserved, x);
         }

         AbstractConnectionPool.this.pending.decrementAndGet();
         this.reserved.remove();
         this.completeExceptionally(x);
         AbstractConnectionPool.this.destination.failed(x);
      }
   }
}
