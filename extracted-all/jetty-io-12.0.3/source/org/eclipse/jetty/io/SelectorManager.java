package org.eclipse.jetty.io;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;
import org.eclipse.jetty.util.ProcessorUtils;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ThreadPoolBudget;
import org.eclipse.jetty.util.thread.ThreadPool.SizedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPoolBudget.Lease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("Manager of the NIO Selectors")
public abstract class SelectorManager extends ContainerLifeCycle implements Dumpable {
   public static final int DEFAULT_CONNECT_TIMEOUT = 15000;
   protected static final Logger LOG = LoggerFactory.getLogger(SelectorManager.class);
   private final Executor executor;
   private final Scheduler scheduler;
   private final ManagedSelector[] _selectors;
   private final AtomicInteger _selectorIndex = new AtomicInteger();
   private final IntUnaryOperator _selectorIndexUpdate;
   private final List<SelectorManager.AcceptListener> _acceptListeners = new CopyOnWriteArrayList<>();
   private long _connectTimeout = 15000L;
   private Lease _lease;

   private static int defaultSelectors(Executor executor) {
      if (executor instanceof SizedThreadPool) {
         int threads = ((SizedThreadPool)executor).getMaxThreads();
         int cpus = ProcessorUtils.availableProcessors();
         return Math.max(1, Math.min(cpus / 2, threads / 16));
      } else {
         return Math.max(1, ProcessorUtils.availableProcessors() / 2);
      }
   }

   protected SelectorManager(Executor executor, Scheduler scheduler) {
      this(executor, scheduler, -1);
   }

   protected SelectorManager(Executor executor, Scheduler scheduler, int selectors) {
      if (selectors <= 0) {
         selectors = defaultSelectors(executor);
      }

      this.executor = executor;
      this.scheduler = scheduler;
      this._selectors = new ManagedSelector[selectors];
      this._selectorIndexUpdate = index -> (index + 1) % this._selectors.length;
   }

   @ManagedAttribute("The Executor")
   public Executor getExecutor() {
      return this.executor;
   }

   @ManagedAttribute("The Scheduler")
   public Scheduler getScheduler() {
      return this.scheduler;
   }

   @ManagedAttribute("The Connection timeout (ms)")
   public long getConnectTimeout() {
      return this._connectTimeout;
   }

   public void setConnectTimeout(long milliseconds) {
      this._connectTimeout = milliseconds;
   }

   protected void execute(Runnable task) {
      this.executor.execute(task);
   }

   @ManagedAttribute(
      value = "Total number of keys in all selectors",
      readonly = true
   )
   public int getTotalKeys() {
      int keys = 0;

      for (ManagedSelector selector : this._selectors) {
         if (selector != null) {
            keys += selector.getTotalKeys();
         }
      }

      return keys;
   }

   @ManagedAttribute("The number of NIO Selectors")
   public int getSelectorCount() {
      return this._selectors.length;
   }

   protected ManagedSelector chooseSelector() {
      return this._selectors[this._selectorIndex.updateAndGet(this._selectorIndexUpdate)];
   }

   public void connect(SelectableChannel channel, Object attachment) {
      ManagedSelector set = this.chooseSelector();
      if (set != null) {
         set.submit(set.new Connect(channel, attachment));
      }
   }

   public void accept(SelectableChannel channel) {
      this.accept(channel, null);
   }

   public void accept(SelectableChannel channel, Object attachment) {
      ManagedSelector selector = this.chooseSelector();
      selector.submit(selector.new Accept(channel, attachment));
   }

   public Closeable acceptor(SelectableChannel server) {
      ManagedSelector selector = this.chooseSelector();
      ManagedSelector.Acceptor acceptor = selector.new Acceptor(server);
      selector.submit(acceptor);
      return acceptor;
   }

   protected void accepted(SelectableChannel channel) throws IOException {
      throw new UnsupportedOperationException();
   }

   protected void doStart() throws Exception {
      this._lease = ThreadPoolBudget.leaseFrom(this.getExecutor(), this, this._selectors.length);

      for (int i = 0; i < this._selectors.length; i++) {
         ManagedSelector selector = this.newSelector(i);
         this._selectors[i] = selector;
         this.addBean(selector);
      }

      super.doStart();
   }

   protected ManagedSelector newSelector(int id) {
      return new ManagedSelector(this, id);
   }

   protected Selector newSelector() throws IOException {
      return Selector.open();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void doStop() throws Exception {
      boolean var11 = false /* VF: Semaphore variable */;

      try {
         var11 = true;
         super.doStop();
         var11 = false;
      } finally {
         if (var11) {
            for (ManagedSelector selector : this._selectors) {
               if (selector != null) {
                  this.removeBean(selector);
               }
            }

            Arrays.fill(this._selectors, null);
            if (this._lease != null) {
               this._lease.close();
            }
         }
      }

      for (ManagedSelector selectorx : this._selectors) {
         if (selectorx != null) {
            this.removeBean(selectorx);
         }
      }

      Arrays.fill(this._selectors, null);
      if (this._lease != null) {
         this._lease.close();
      }
   }

   protected void endPointOpened(EndPoint endpoint) {
   }

   protected void endPointClosed(EndPoint endpoint) {
   }

   public void connectionOpened(Connection connection, Object context) {
      try {
         connection.onOpen();
      } catch (Throwable var4) {
         if (this.isRunning()) {
            LOG.warn("Exception while notifying connection {}", connection, var4);
         } else {
            LOG.debug("Exception while notifying connection {}", connection, var4);
         }

         throw var4;
      }
   }

   public void connectionClosed(Connection connection, Throwable cause) {
      try {
         connection.onClose(cause);
      } catch (Throwable var4) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Exception while notifying connection {}", connection, var4);
         }
      }
   }

   protected boolean doFinishConnect(SelectableChannel channel) throws IOException {
      return ((SocketChannel)channel).finishConnect();
   }

   protected boolean isConnectionPending(SelectableChannel channel) {
      return ((SocketChannel)channel).isConnectionPending();
   }

   protected SelectableChannel doAccept(SelectableChannel server) throws IOException {
      return ((ServerSocketChannel)server).accept();
   }

   protected void connectionFailed(SelectableChannel channel, Throwable ex, Object attachment) {
      LOG.warn(String.format("%s - %s", channel, attachment), ex);
   }

   protected abstract EndPoint newEndPoint(SelectableChannel var1, ManagedSelector var2, SelectionKey var3) throws IOException;

   public abstract Connection newConnection(SelectableChannel var1, EndPoint var2, Object var3) throws IOException;

   public boolean addEventListener(EventListener listener) {
      if (super.addEventListener(listener)) {
         if (listener instanceof SelectorManager.AcceptListener) {
            this._acceptListeners.add((SelectorManager.AcceptListener)listener);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean removeEventListener(EventListener listener) {
      if (super.removeEventListener(listener)) {
         if (listener instanceof SelectorManager.AcceptListener) {
            this._acceptListeners.remove(listener);
         }

         return true;
      } else {
         return false;
      }
   }

   protected void onAccepting(SelectableChannel channel) {
      for (SelectorManager.AcceptListener l : this._acceptListeners) {
         try {
            l.onAccepting(channel);
         } catch (Throwable var5) {
            LOG.warn("Failed to notify onAccepting on listener {}", l, var5);
         }
      }
   }

   protected void onAcceptFailed(SelectableChannel channel, Throwable cause) {
      for (SelectorManager.AcceptListener l : this._acceptListeners) {
         try {
            l.onAcceptFailed(channel, cause);
         } catch (Throwable var6) {
            LOG.warn("Failed to notify onAcceptFailed on listener {}", l, var6);
         }
      }
   }

   protected void onAccepted(SelectableChannel channel) {
      for (SelectorManager.AcceptListener l : this._acceptListeners) {
         try {
            l.onAccepted(channel);
         } catch (Throwable var5) {
            LOG.warn("Failed to notify onAccepted on listener {}", l, var5);
         }
      }
   }

   public String toString() {
      return String.format("%s@%x[keys=%d]", this.getClass().getSimpleName(), this.hashCode(), this.getTotalKeys());
   }

   public interface AcceptListener extends SelectorManager.SelectorManagerListener {
      default void onAccepting(SelectableChannel channel) {
      }

      default void onAcceptFailed(SelectableChannel channel, Throwable cause) {
      }

      default void onAccepted(SelectableChannel channel) {
      }
   }

   public interface SelectorManagerListener extends EventListener {
   }
}
