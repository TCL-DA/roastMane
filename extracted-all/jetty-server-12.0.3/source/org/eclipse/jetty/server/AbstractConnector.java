package org.eclipse.jetty.server;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.stream.Collectors;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.ProcessorUtils;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.Graceful.Shutdown;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ThreadPoolBudget;
import org.eclipse.jetty.util.thread.ThreadPoolBudget.Lease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("Abstract implementation of the Connector Interface")
public abstract class AbstractConnector extends ContainerLifeCycle implements Connector, Dumpable {
   protected static final Logger LOG = LoggerFactory.getLogger(AbstractConnector.class);
   private final AutoLock _lock = new AutoLock();
   private final Condition _setAccepting = this._lock.newCondition();
   private final Map<String, ConnectionFactory> _factories = new LinkedHashMap<>();
   private final Server _server;
   private final Executor _executor;
   private final Scheduler _scheduler;
   private final ByteBufferPool _bufferPool;
   private final Thread[] _acceptors;
   private final Set<EndPoint> _endpoints = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private final Set<EndPoint> _immutableEndPoints = Collections.unmodifiableSet(this._endpoints);
   private Shutdown _shutdown;
   private long _idleTimeout = 30000L;
   private long _shutdownIdleTimeout = 1000L;
   private String _defaultProtocol;
   private ConnectionFactory _defaultConnectionFactory;
   private String _name;
   private int _acceptorPriorityDelta = -2;
   private boolean _accepting = true;
   private Lease _lease;

   public AbstractConnector(Server server, Executor executor, Scheduler scheduler, ByteBufferPool bufferPool, int acceptors, ConnectionFactory... factories) {
      this._server = Objects.requireNonNull(server);
      this._executor = (Executor)(executor != null ? executor : this._server.getThreadPool());
      this.addBean(this._executor, executor != null);
      this._scheduler = scheduler != null ? scheduler : this._server.getScheduler();
      this.addBean(this._scheduler, scheduler != null);
      this._bufferPool = bufferPool != null ? bufferPool : server.getByteBufferPool();
      this.addBean(this._bufferPool, bufferPool != null);

      for (ConnectionFactory factory : factories) {
         this.addConnectionFactory(factory);
      }

      int cores = ProcessorUtils.availableProcessors();
      if (acceptors < 0) {
         acceptors = Math.max(1, Math.min(4, cores / 8));
      }

      if (acceptors > cores) {
         LOG.warn("Acceptors should be <= availableProcessors: {} ", this);
      }

      this._acceptors = new Thread[acceptors];
   }

   @Override
   public Server getServer() {
      return this._server;
   }

   @Override
   public Executor getExecutor() {
      return this._executor;
   }

   @Override
   public ByteBufferPool getByteBufferPool() {
      return this._bufferPool;
   }

   @ManagedAttribute("The connection idle timeout in milliseconds")
   @Override
   public long getIdleTimeout() {
      return this._idleTimeout;
   }

   public void setIdleTimeout(long idleTimeout) {
      this._idleTimeout = idleTimeout;
      if (this._idleTimeout == 0L) {
         this._shutdownIdleTimeout = 0L;
      } else if (this._idleTimeout < this._shutdownIdleTimeout) {
         this._shutdownIdleTimeout = Math.min(1000L, this._idleTimeout);
      }
   }

   public void setShutdownIdleTimeout(long idle) {
      this._shutdownIdleTimeout = idle;
   }

   public long getShutdownIdleTimeout() {
      return this._shutdownIdleTimeout;
   }

   @ManagedAttribute("number of acceptor threads")
   public int getAcceptors() {
      return this._acceptors.length;
   }

   protected void doStart() throws Exception {
      this.getConnectionFactories()
         .stream()
         .filter(ConnectionFactory.Configuring.class::isInstance)
         .map(ConnectionFactory.Configuring.class::cast)
         .forEach(configuring -> configuring.configure(this));
      this._shutdown = new Shutdown(this) {
         public boolean isShutdownDone() {
            if (!AbstractConnector.this._endpoints.isEmpty()) {
               return false;
            } else {
               for (Thread a : AbstractConnector.this._acceptors) {
                  if (a != null) {
                     return false;
                  }
               }

               return true;
            }
         }
      };
      if (this._defaultProtocol == null) {
         throw new IllegalStateException("No default protocol for " + this);
      } else {
         this._defaultConnectionFactory = this.getConnectionFactory(this._defaultProtocol);
         if (this._defaultConnectionFactory == null) {
            throw new IllegalStateException("No protocol factory for default protocol '" + this._defaultProtocol + "' in " + this);
         } else {
            SslConnectionFactory ssl = this.getConnectionFactory(SslConnectionFactory.class);
            if (ssl != null) {
               String next = ssl.getNextProtocol();
               ConnectionFactory cf = this.getConnectionFactory(next);
               if (cf == null) {
                  throw new IllegalStateException("No protocol factory for SSL next protocol: '" + next + "' in " + this);
               }
            }

            this._lease = ThreadPoolBudget.leaseFrom(this.getExecutor(), this, this._acceptors.length);
            super.doStart();

            for (int i = 0; i < this._acceptors.length; i++) {
               AbstractConnector.Acceptor a = new AbstractConnector.Acceptor(i);
               this.addBean(a);
               this.getExecutor().execute(a);
            }

            LOG.info("Started {}", this);
         }
      }
   }

   protected void interruptAcceptors() {
      AutoLock lock = this._lock.lock();

      try {
         for (Thread thread : this._acceptors) {
            if (thread != null) {
               thread.interrupt();
            }
         }
      } catch (Throwable var7) {
         if (lock != null) {
            try {
               lock.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (lock != null) {
         lock.close();
      }
   }

   public CompletableFuture<Void> shutdown() {
      Shutdown shutdown = this._shutdown;
      if (shutdown == null) {
         return CompletableFuture.completedFuture(null);
      } else {
         CompletableFuture<Void> done = shutdown.shutdown();
         this.interruptAcceptors();

         for (EndPoint ep : this._endpoints) {
            ep.setIdleTimeout(this.getShutdownIdleTimeout());
         }

         return done;
      }
   }

   public boolean isShutdown() {
      Shutdown shutdown = this._shutdown;
      return shutdown == null || shutdown.isShutdown();
   }

   protected void doStop() throws Exception {
      if (this._lease != null) {
         this._lease.close();
      }

      this.interruptAcceptors();
      super.doStop();

      for (AbstractConnector.Acceptor a : this.getBeans(AbstractConnector.Acceptor.class)) {
         this.removeBean(a);
      }

      this._shutdown = null;
      LOG.info("Stopped {}", this);
   }

   public void join() throws InterruptedException {
      this.join(0L);
   }

   public void join(long timeout) throws InterruptedException {
      AutoLock lock = this._lock.lock();

      try {
         for (Thread thread : this._acceptors) {
            if (thread != null) {
               thread.join(timeout);
            }
         }
      } catch (Throwable var9) {
         if (lock != null) {
            try {
               lock.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (lock != null) {
         lock.close();
      }
   }

   protected abstract void accept(int var1) throws IOException, InterruptedException;

   public boolean isAccepting() {
      AutoLock lock = this._lock.lock();

      boolean var2;
      try {
         var2 = this._accepting;
      } catch (Throwable var5) {
         if (lock != null) {
            try {
               lock.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (lock != null) {
         lock.close();
      }

      return var2;
   }

   public void setAccepting(boolean accepting) {
      AutoLock l = this._lock.lock();

      try {
         this._accepting = accepting;
         this._setAccepting.signalAll();
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }
   }

   @Override
   public ConnectionFactory getConnectionFactory(String protocol) {
      AutoLock lock = this._lock.lock();

      ConnectionFactory var3;
      try {
         var3 = this._factories.get(StringUtil.asciiToLowerCase(protocol));
      } catch (Throwable var6) {
         if (lock != null) {
            try {
               lock.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (lock != null) {
         lock.close();
      }

      return var3;
   }

   @Override
   public <T> T getConnectionFactory(Class<T> factoryType) {
      AutoLock lock = this._lock.lock();

      ConnectionFactory var5;
      label49: {
         Object var8;
         try {
            for (ConnectionFactory f : this._factories.values()) {
               if (factoryType.isAssignableFrom(f.getClass())) {
                  var5 = f;
                  break label49;
               }
            }

            var8 = null;
         } catch (Throwable var7) {
            if (lock != null) {
               try {
                  lock.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (lock != null) {
            lock.close();
         }

         return (T)var8;
      }

      if (lock != null) {
         lock.close();
      }

      return (T)var5;
   }

   public void addConnectionFactory(ConnectionFactory factory) {
      if (this.isRunning()) {
         throw new IllegalStateException(this.getState());
      } else {
         Set<ConnectionFactory> toRemove = new HashSet<>();

         for (String key : factory.getProtocols()) {
            key = StringUtil.asciiToLowerCase(key);
            ConnectionFactory old = this._factories.remove(key);
            if (old != null) {
               if (old.getProtocol().equals(this._defaultProtocol)) {
                  this._defaultProtocol = null;
               }

               toRemove.add(old);
            }

            this._factories.put(key, factory);
         }

         for (ConnectionFactory f : this._factories.values()) {
            toRemove.remove(f);
         }

         for (ConnectionFactory old : toRemove) {
            this.removeBean(old);
            if (LOG.isDebugEnabled()) {
               LOG.debug("{} removed {}", this, old);
            }
         }

         this.addBean(factory);
         if (this._defaultProtocol == null) {
            this._defaultProtocol = factory.getProtocol();
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("{} added {}", this, factory);
         }
      }
   }

   public void addFirstConnectionFactory(ConnectionFactory factory) {
      if (this.isRunning()) {
         throw new IllegalStateException(this.getState());
      } else {
         List<ConnectionFactory> existings = new ArrayList<>(this._factories.values());
         this.clearConnectionFactories();
         this.addConnectionFactory(factory);

         for (ConnectionFactory existing : existings) {
            this.addConnectionFactory(existing);
         }
      }
   }

   public void addIfAbsentConnectionFactory(ConnectionFactory factory) {
      if (this.isRunning()) {
         throw new IllegalStateException(this.getState());
      } else {
         String key = StringUtil.asciiToLowerCase(factory.getProtocol());
         if (!this._factories.containsKey(key)) {
            this.addConnectionFactory(factory);
         }
      }
   }

   public ConnectionFactory removeConnectionFactory(String protocol) {
      if (this.isRunning()) {
         throw new IllegalStateException(this.getState());
      } else {
         ConnectionFactory factory = this._factories.remove(StringUtil.asciiToLowerCase(protocol));
         if (this._factories.isEmpty()) {
            this._defaultProtocol = null;
         }

         this.removeBean(factory);
         return factory;
      }
   }

   @Override
   public Collection<ConnectionFactory> getConnectionFactories() {
      return this._factories.values();
   }

   public void setConnectionFactories(Collection<ConnectionFactory> factories) {
      if (this.isRunning()) {
         throw new IllegalStateException(this.getState());
      } else {
         for (ConnectionFactory factory : new ArrayList<>(this._factories.values())) {
            this.removeConnectionFactory(factory.getProtocol());
         }

         for (ConnectionFactory factory : factories) {
            if (factory != null) {
               this.addConnectionFactory(factory);
            }
         }
      }
   }

   public void clearConnectionFactories() {
      if (this.isRunning()) {
         throw new IllegalStateException(this.getState());
      } else {
         this._factories.clear();
         this._defaultProtocol = null;
      }
   }

   @ManagedAttribute("The priority delta to apply to acceptor threads")
   public int getAcceptorPriorityDelta() {
      return this._acceptorPriorityDelta;
   }

   public void setAcceptorPriorityDelta(int acceptorPriorityDelta) {
      int old = this._acceptorPriorityDelta;
      this._acceptorPriorityDelta = acceptorPriorityDelta;
      if (old != acceptorPriorityDelta && this.isStarted()) {
         for (Thread thread : this._acceptors) {
            thread.setPriority(Math.max(1, Math.min(10, thread.getPriority() - old + acceptorPriorityDelta)));
         }
      }
   }

   @ManagedAttribute("Protocols supported by this connector")
   @Override
   public List<String> getProtocols() {
      return new ArrayList<>(this._factories.keySet());
   }

   @ManagedAttribute("This connector's default protocol")
   public String getDefaultProtocol() {
      return this._defaultProtocol;
   }

   public void setDefaultProtocol(String defaultProtocol) {
      this._defaultProtocol = StringUtil.asciiToLowerCase(defaultProtocol);
      if (this.isRunning()) {
         this._defaultConnectionFactory = this.getConnectionFactory(this._defaultProtocol);
      }
   }

   @Override
   public ConnectionFactory getDefaultConnectionFactory() {
      return this.isStarted() ? this._defaultConnectionFactory : this.getConnectionFactory(this._defaultProtocol);
   }

   protected boolean handleAcceptFailure(Throwable ex) {
      if (this.isRunning()) {
         if (ex instanceof InterruptedException) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Accept Interrupted", ex);
            }

            return true;
         } else if (ex instanceof ClosedByInterruptException) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Accept Closed by Interrupt", ex);
            }

            return false;
         } else {
            LOG.warn("Accept Failure", ex);

            try {
               Thread.sleep(1000L);
               return true;
            } catch (Throwable var3) {
               LOG.trace("IGNORED", var3);
               return false;
            }
         }
      } else {
         LOG.trace("IGNORED", ex);
         return false;
      }
   }

   @Override
   public Collection<EndPoint> getConnectedEndPoints() {
      return this._immutableEndPoints;
   }

   protected void onEndPointOpened(EndPoint endp) {
      this._endpoints.add(endp);
   }

   protected void onEndPointClosed(EndPoint endp) {
      this._endpoints.remove(endp);
      Shutdown shutdown = this._shutdown;
      if (shutdown != null) {
         shutdown.check();
      }
   }

   @Override
   public Scheduler getScheduler() {
      return this._scheduler;
   }

   @Override
   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   @Override
   public String toString() {
      return String.format(
         "%s@%x{%s, %s}",
         this._name == null ? this.getClass().getSimpleName() : this._name,
         this.hashCode(),
         this.getDefaultProtocol(),
         this.getProtocols().stream().collect(Collectors.joining(", ", "(", ")"))
      );
   }

   private class Acceptor implements Runnable {
      private final int _id;
      private String _name;

      private Acceptor(int id) {
         this._id = id;
      }

      @Override
      public void run() {
         Thread thread = Thread.currentThread();
         String name = thread.getName();
         this._name = String.format("%s-acceptor-%d@%x-%s", name, this._id, this.hashCode(), AbstractConnector.this.toString());
         thread.setName(this._name);
         int priority = thread.getPriority();
         if (AbstractConnector.this._acceptorPriorityDelta != 0) {
            thread.setPriority(Math.max(1, Math.min(10, priority + AbstractConnector.this._acceptorPriorityDelta)));
         }

         AutoLock l = AbstractConnector.this._lock.lock();

         try {
            AbstractConnector.this._acceptors[this._id] = thread;
         } catch (Throwable var26) {
            if (l != null) {
               try {
                  l.close();
               } catch (Throwable var23) {
                  var26.addSuppressed(var23);
               }
            }

            throw var26;
         }

         if (l != null) {
            l.close();
         }

         try {
            while (AbstractConnector.this.isRunning() && !AbstractConnector.this._shutdown.isShutdown()) {
               try {
                  l = AbstractConnector.this._lock.lock();

                  label208: {
                     try {
                        if (AbstractConnector.this._accepting || !AbstractConnector.this.isRunning()) {
                           break label208;
                        }

                        AbstractConnector.this._setAccepting.await();
                     } catch (Throwable var28) {
                        if (l != null) {
                           try {
                              l.close();
                           } catch (Throwable var24) {
                              var28.addSuppressed(var24);
                           }
                        }

                        throw var28;
                     }

                     if (l != null) {
                        l.close();
                     }
                     continue;
                  }

                  if (l != null) {
                     l.close();
                  }
               } catch (InterruptedException var29) {
                  continue;
               }

               try {
                  AbstractConnector.this.accept(this._id);
               } catch (Throwable var27) {
                  if (!AbstractConnector.this.handleAcceptFailure(var27)) {
                     break;
                  }
               }
            }
         } finally {
            thread.setName(name);
            if (AbstractConnector.this._acceptorPriorityDelta != 0) {
               thread.setPriority(priority);
            }

            AutoLock lx = AbstractConnector.this._lock.lock();

            try {
               AbstractConnector.this._acceptors[this._id] = null;
            } catch (Throwable var25) {
               if (lx != null) {
                  try {
                     lx.close();
                  } catch (Throwable var22) {
                     var25.addSuppressed(var22);
                  }
               }

               throw var25;
            }

            if (lx != null) {
               lx.close();
            }

            Shutdown shutdown = AbstractConnector.this._shutdown;
            if (shutdown != null) {
               shutdown.check();
            }
         }
      }

      @Override
      public String toString() {
         String name = this._name;
         return name == null ? String.format("acceptor-%d@%x", this._id, this.hashCode()) : name;
      }
   }
}
