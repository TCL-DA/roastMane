package org.eclipse.jetty.io;

import java.io.Closeable;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.eclipse.jetty.util.statistic.SampleStatistic;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.ExecutionStrategy;
import org.eclipse.jetty.util.thread.ExecutionStrategy.Producer;
import org.eclipse.jetty.util.thread.Scheduler.Task;
import org.eclipse.jetty.util.thread.strategy.AdaptiveExecutionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedSelector extends ContainerLifeCycle implements Dumpable {
   private static final Logger LOG = LoggerFactory.getLogger(ManagedSelector.class);
   private static final boolean FORCE_SELECT_NOW;
   private final AutoLock _lock = new AutoLock();
   private final AtomicBoolean _started = new AtomicBoolean(false);
   private boolean _selecting;
   private final SelectorManager _selectorManager;
   private final int _id;
   private final ExecutionStrategy _strategy;
   private Selector _selector;
   private Deque<ManagedSelector.SelectorUpdate> _updates = new ArrayDeque<>();
   private Deque<ManagedSelector.SelectorUpdate> _updateable = new ArrayDeque<>();
   private final SampleStatistic _keyStats = new SampleStatistic();

   public ManagedSelector(SelectorManager selectorManager, int id) {
      this._selectorManager = selectorManager;
      this._id = id;
      ManagedSelector.SelectorProducer producer = new ManagedSelector.SelectorProducer();
      Executor executor = selectorManager.getExecutor();
      this._strategy = new AdaptiveExecutionStrategy(producer, executor);
      this.addBean(this._strategy, true);
   }

   public Selector getSelector() {
      return this._selector;
   }

   protected void doStart() throws Exception {
      super.doStart();
      this._selector = this._selectorManager.newSelector();
      this._selectorManager.execute(this._strategy::produce);
      ManagedSelector.Start start = new ManagedSelector.Start();
      this.submit(start);
      start._started.await();
   }

   protected void doStop() throws Exception {
      if (this._started.compareAndSet(true, false) && this._selector != null) {
         ManagedSelector.CloseConnections closeConnections = new ManagedSelector.CloseConnections(this);
         this.submit(closeConnections);
         closeConnections._complete.await();
         ManagedSelector.StopSelector stopSelector = new ManagedSelector.StopSelector();
         this.submit(stopSelector);
         stopSelector._stopped.await();
      }

      super.doStop();
   }

   @ManagedAttribute(
      value = "Total number of keys",
      readonly = true
   )
   public int getTotalKeys() {
      Selector selector = this._selector;
      return selector == null ? 0 : selector.keys().size();
   }

   @ManagedAttribute(
      value = "Average number of selected keys",
      readonly = true
   )
   public double getAverageSelectedKeys() {
      return this._keyStats.getMean();
   }

   @ManagedAttribute(
      value = "Maximum number of selected keys",
      readonly = true
   )
   public long getMaxSelectedKeys() {
      return this._keyStats.getMax();
   }

   @ManagedAttribute(
      value = "Total number of select() calls",
      readonly = true
   )
   public long getSelectCount() {
      return this._keyStats.getCount();
   }

   @ManagedOperation(
      value = "Resets the statistics",
      impact = "ACTION"
   )
   public void resetStats() {
      this._keyStats.reset();
   }

   protected int nioSelect(Selector selector, boolean now) throws IOException {
      return now ? selector.selectNow() : selector.select();
   }

   protected int select(Selector selector) throws IOException {
      try {
         int selected = this.nioSelect(selector, false);
         if (selected == 0) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Selector {} woken with none selected", selector);
            }

            if (Thread.interrupted() && !this.isRunning()) {
               throw new ClosedSelectorException();
            }

            if (FORCE_SELECT_NOW) {
               selected = this.nioSelect(selector, true);
            }
         }

         return selected;
      } catch (ClosedSelectorException var3) {
         throw var3;
      } catch (Throwable var4) {
         this.handleSelectFailure(selector, var4);
         return 0;
      }
   }

   protected void handleSelectFailure(Selector selector, Throwable failure) throws IOException {
      LOG.info("Caught select() failure, trying to recover: {}", failure.toString());
      if (LOG.isDebugEnabled()) {
         LOG.debug("", failure);
      }

      Selector newSelector = this._selectorManager.newSelector();

      for (SelectionKey oldKey : selector.keys()) {
         SelectableChannel channel = oldKey.channel();
         int interestOps = safeInterestOps(oldKey);
         if (interestOps >= 0) {
            try {
               Object attachment = oldKey.attachment();
               SelectionKey newKey = channel.register(newSelector, interestOps, attachment);
               if (attachment instanceof ManagedSelector.Selectable) {
                  ((ManagedSelector.Selectable)attachment).replaceKey(newKey);
               }

               oldKey.cancel();
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Transferred {} iOps={} att={}", new Object[]{channel, interestOps, attachment});
               }
            } catch (Throwable var10) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Could not transfer {}", channel, var10);
               }

               IO.close(channel);
            }
         } else {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Invalid interestOps for {}", channel);
            }

            IO.close(channel);
         }
      }

      IO.close(selector);
      this._selector = newSelector;
   }

   protected void onSelectFailed(Throwable cause) {
   }

   public int size() {
      Selector s = this._selector;
      if (s == null) {
         return 0;
      } else {
         Set<SelectionKey> keys = s.keys();
         return keys == null ? 0 : keys.size();
      }
   }

   public void submit(ManagedSelector.SelectorUpdate update) {
      this.submit(update, false);
   }

   private void submit(ManagedSelector.SelectorUpdate update, boolean lazy) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Queued change lazy={} {} on {}", new Object[]{lazy, update, this});
      }

      Selector selector = null;
      AutoLock l = this._lock.lock();

      try {
         this._updates.offer(update);
         if (this._selecting && !lazy) {
            selector = this._selector;
            this._selecting = false;
         }
      } catch (Throwable var8) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (l != null) {
         l.close();
      }

      if (selector != null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Wakeup on submit {}", this);
         }

         selector.wakeup();
      }
   }

   private void wakeup() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Wakeup {}", this);
      }

      Selector selector = null;
      AutoLock l = this._lock.lock();

      try {
         if (this._selecting) {
            selector = this._selector;
            this._selecting = false;
         }
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

      if (selector != null) {
         selector.wakeup();
      }
   }

   private void execute(Runnable task) {
      try {
         this._selectorManager.execute(task);
      } catch (RejectedExecutionException var3) {
         if (task instanceof Closeable) {
            IO.close((Closeable)task);
         }
      }
   }

   private void processConnect(SelectionKey key, ManagedSelector.Connect connect) {
      SelectableChannel channel = key.channel();

      try {
         key.attach(connect.attachment);
         boolean connected = this._selectorManager.doFinishConnect(channel);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Connected {} {}", connected, channel);
         }

         if (!connected) {
            throw new ConnectException();
         }

         if (!connect.timeout.cancel()) {
            throw new SocketTimeoutException("Concurrent Connect Timeout");
         }

         key.interestOps(0);
         this.execute(new ManagedSelector.CreateEndPoint(connect, key));
      } catch (Throwable var5) {
         connect.failed(var5);
      }
   }

   protected void endPointOpened(EndPoint endPoint) {
      this._selectorManager.endPointOpened(endPoint);
   }

   protected void endPointClosed(EndPoint endPoint) {
      this._selectorManager.endPointClosed(endPoint);
   }

   void createEndPoint(SelectableChannel channel, SelectionKey selectionKey) throws IOException {
      EndPoint endPoint = this._selectorManager.newEndPoint(channel, this, selectionKey);
      Object context = selectionKey.attachment();
      Connection connection = this._selectorManager.newConnection(channel, endPoint, context);
      endPoint.setConnection(connection);
      this.submit(selector -> {
         SelectionKey key = selectionKey;
         if (selectionKey.selector() != selector) {
            key = channel.keyFor(selector);
            if (key != null && endPoint instanceof ManagedSelector.Selectable) {
               ((ManagedSelector.Selectable)endPoint).replaceKey(key);
            }
         }

         if (key != null) {
            key.attach(endPoint);
         }
      }, true);
      endPoint.onOpen();
      this.endPointOpened(endPoint);
      this._selectorManager.connectionOpened(connection, context);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Created {}", endPoint);
      }
   }

   void destroyEndPoint(EndPoint endPoint, Throwable cause) {
      this.wakeup();
      this.execute(new ManagedSelector.DestroyEndPoint(endPoint, cause));
   }

   private int getActionSize() {
      AutoLock l = this._lock.lock();

      int var2;
      try {
         var2 = this._updates.size();
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   static int safeReadyOps(SelectionKey selectionKey) {
      try {
         return selectionKey.readyOps();
      } catch (Throwable var2) {
         LOG.trace("IGNORED", var2);
         return -1;
      }
   }

   static int safeInterestOps(SelectionKey selectionKey) {
      try {
         return selectionKey.interestOps();
      } catch (Throwable var2) {
         LOG.trace("IGNORED", var2);
         return -1;
      }
   }

   public void dump(Appendable out, String indent) throws IOException {
      Selector selector = this._selector;
      if (selector != null && selector.isOpen()) {
         ManagedSelector.DumpKeys dump = new ManagedSelector.DumpKeys();
         String updatesAt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now());
         AutoLock l = this._lock.lock();

         List<ManagedSelector.SelectorUpdate> updates;
         try {
            updates = new ArrayList<>(this._updates);
            this._updates.addFirst(dump);
            this._selecting = false;
         } catch (Throwable var12) {
            if (l != null) {
               try {
                  l.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }
            }

            throw var12;
         }

         if (l != null) {
            l.close();
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("wakeup on dump {}", this);
         }

         selector.wakeup();
         List<String> keys = dump.get(5L, TimeUnit.SECONDS);
         String keysAt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now());
         if (keys == null) {
            keys = Collections.singletonList("No dump keys retrieved");
         }

         this.dumpObjects(
            out, indent, new Object[]{new DumpableCollection("updates @ " + updatesAt, updates), new DumpableCollection("keys @ " + keysAt, keys)}
         );
      } else {
         this.dumpObjects(out, indent, new Object[0]);
      }
   }

   public String toString() {
      Selector selector = this._selector;
      return String.format(
         "%s id=%s keys=%d selected=%d updates=%d",
         super.toString(),
         this._id,
         selector != null && selector.isOpen() ? selector.keys().size() : -1,
         selector != null && selector.isOpen() ? selector.selectedKeys().size() : -1,
         this.getActionSize()
      );
   }

   static {
      String property = System.getProperty("org.eclipse.jetty.io.forceSelectNow");
      if (property != null) {
         FORCE_SELECT_NOW = Boolean.parseBoolean(property);
      } else {
         property = System.getProperty("os.name");
         FORCE_SELECT_NOW = property != null && property.toLowerCase(Locale.ENGLISH).contains("windows");
      }
   }

   class Accept implements ManagedSelector.SelectorUpdate, Runnable, Closeable {
      private final SelectableChannel channel;
      private final Object attachment;
      private SelectionKey key;

      Accept(SelectableChannel channel, Object attachment) {
         this.channel = channel;
         this.attachment = attachment;
         ManagedSelector.this._selectorManager.onAccepting(channel);
      }

      @Override
      public void close() {
         if (ManagedSelector.LOG.isDebugEnabled()) {
            ManagedSelector.LOG.debug("closed accept of {}", this.channel);
         }

         IO.close(this.channel);
      }

      @Override
      public void update(Selector selector) {
         try {
            this.key = this.channel.register(selector, 0, this.attachment);
            ManagedSelector.this.execute(this);
         } catch (Throwable var3) {
            IO.close(this.channel);
            ManagedSelector.this._selectorManager.onAcceptFailed(this.channel, var3);
            if (ManagedSelector.LOG.isDebugEnabled()) {
               ManagedSelector.LOG.debug("Could not register channel after accept {}", this.channel, var3);
            }
         }
      }

      @Override
      public void run() {
         try {
            ManagedSelector.this.createEndPoint(this.channel, this.key);
            ManagedSelector.this._selectorManager.onAccepted(this.channel);
         } catch (Throwable var2) {
            this.failed(var2);
         }
      }

      protected void failed(Throwable failure) {
         IO.close(this.channel);
         if (ManagedSelector.LOG.isDebugEnabled()) {
            ManagedSelector.LOG.warn("Could not accept {}", this.channel, failure);
         } else {
            ManagedSelector.LOG.warn("Could not accept {}: {}", this.channel, String.valueOf(failure));
         }

         ManagedSelector.this._selectorManager.onAcceptFailed(this.channel, failure);
      }

      @Override
      public String toString() {
         return String.format("%s@%x[%s]", this.getClass().getSimpleName(), this.hashCode(), this.channel);
      }
   }

   class Acceptor implements ManagedSelector.SelectorUpdate, ManagedSelector.Selectable, Closeable {
      private final SelectableChannel _channel;
      private SelectionKey _key;

      Acceptor(SelectableChannel channel) {
         this._channel = channel;
      }

      @Override
      public void update(Selector selector) {
         try {
            this._key = this._channel.register(selector, 16, this);
            if (ManagedSelector.LOG.isDebugEnabled()) {
               ManagedSelector.LOG.debug("{} acceptor={}", this, this._channel);
            }
         } catch (Throwable var3) {
            IO.close(this._channel);
            ManagedSelector.LOG.warn("Unable to register OP_ACCEPT on selector for {}", this._channel, var3);
         }
      }

      @Override
      public Runnable onSelected() {
         SelectableChannel channel = null;

         try {
            while (true) {
               channel = ManagedSelector.this._selectorManager.doAccept(this._channel);
               if (channel == null) {
                  break;
               }

               ManagedSelector.this._selectorManager.accepted(channel);
            }
         } catch (Throwable var3) {
            ManagedSelector.LOG.warn("Accept failed for channel {}", channel, var3);
            IO.close(channel);
         }

         return null;
      }

      @Override
      public void updateKey() {
      }

      @Override
      public void replaceKey(SelectionKey newKey) {
         this._key = newKey;
      }

      @Override
      public void close() throws IOException {
         ManagedSelector.this.submit(selector -> this._key.cancel());
      }
   }

   private class CloseConnections implements ManagedSelector.SelectorUpdate {
      private final Set<Closeable> _closed;
      private final CountDownLatch _complete;

      private CloseConnections(final ManagedSelector param1) {
         this(var1, null);
      }

      private CloseConnections(final Set<Closeable> param1, Set closed) {
         this.this$0 = var1;
         this._complete = new CountDownLatch(1);
         this._closed = closed;
      }

      @Override
      public void update(Selector selector) {
         try {
            if (ManagedSelector.LOG.isDebugEnabled()) {
               ManagedSelector.LOG.debug("Closing {} connections on {}", selector.keys().size(), this.this$0);
            }

            for (SelectionKey key : selector.keys()) {
               if (key != null && key.isValid()) {
                  Closeable closeable = null;
                  if (key.attachment() instanceof EndPoint endPoint) {
                     Connection connection = endPoint.getConnection();
                     closeable = Objects.requireNonNullElse(connection, endPoint);
                  }

                  if (closeable != null) {
                     if (this._closed == null) {
                        IO.close(closeable);
                     } else if (!this._closed.contains(closeable)) {
                        this._closed.add(closeable);
                        IO.close(closeable);
                     }
                  }
               }
            }
         } finally {
            this._complete.countDown();
         }
      }
   }

   class Connect implements ManagedSelector.SelectorUpdate, Runnable {
      private final AtomicBoolean failed = new AtomicBoolean();
      private final SelectableChannel channel;
      private final Object attachment;
      private final Task timeout;

      Connect(SelectableChannel channel, Object attachment) {
         this.channel = channel;
         this.attachment = attachment;
         long timeout = ManagedSelector.this._selectorManager.getConnectTimeout();
         if (timeout > 0L) {
            this.timeout = ManagedSelector.this._selectorManager.getScheduler().schedule(this, timeout, TimeUnit.MILLISECONDS);
         } else {
            this.timeout = null;
         }
      }

      @Override
      public void update(Selector selector) {
         try {
            this.channel.register(selector, 8, this);
         } catch (Throwable var3) {
            this.failed(var3);
         }
      }

      @Override
      public void run() {
         if (ManagedSelector.this._selectorManager.isConnectionPending(this.channel)) {
            if (ManagedSelector.LOG.isDebugEnabled()) {
               ManagedSelector.LOG.debug("Channel {} timed out while connecting, closing it", this.channel);
            }

            this.failed(new SocketTimeoutException("Connect Timeout"));
         }
      }

      public void failed(Throwable failure) {
         if (this.failed.compareAndSet(false, true)) {
            if (this.timeout != null) {
               this.timeout.cancel();
            }

            IO.close(this.channel);
            ManagedSelector.this._selectorManager.connectionFailed(this.channel, failure, this.attachment);
         }
      }

      @Override
      public String toString() {
         return String.format("Connect@%x{%s,%s}", this.hashCode(), this.channel, this.attachment);
      }
   }

   private final class CreateEndPoint implements Runnable {
      private final ManagedSelector.Connect _connect;
      private final SelectionKey _key;

      private CreateEndPoint(ManagedSelector.Connect connect, SelectionKey key) {
         this._connect = connect;
         this._key = key;
      }

      @Override
      public void run() {
         try {
            ManagedSelector.this.createEndPoint(this._connect.channel, this._key);
         } catch (Throwable var2) {
            IO.close(this._connect.channel);
            ManagedSelector.LOG.warn("Could not create EndPoint {}: {}", this._connect.channel, String.valueOf(var2));
            if (ManagedSelector.LOG.isDebugEnabled()) {
               ManagedSelector.LOG.debug("", var2);
            }

            this._connect.failed(var2);
         }
      }

      @Override
      public String toString() {
         return String.format("%s@%x{%s}", this.getClass().getSimpleName(), this.hashCode(), this._connect);
      }
   }

   private class DestroyEndPoint implements Runnable, Closeable {
      private final EndPoint endPoint;
      private final Throwable cause;

      private DestroyEndPoint(EndPoint endPoint, Throwable cause) {
         this.endPoint = endPoint;
         this.cause = cause;
      }

      @Override
      public void run() {
         if (ManagedSelector.LOG.isDebugEnabled()) {
            ManagedSelector.LOG.debug("Destroyed {}", this.endPoint);
         }

         Connection connection = this.endPoint.getConnection();
         if (connection != null) {
            ManagedSelector.this._selectorManager.connectionClosed(connection, this.cause);
         }

         ManagedSelector.this.endPointClosed(this.endPoint);
      }

      @Override
      public void close() {
         this.run();
      }
   }

   private static class DumpKeys implements ManagedSelector.SelectorUpdate {
      private final CountDownLatch latch = new CountDownLatch(1);
      private List<String> keys;

      @Override
      public void update(Selector selector) {
         Set<SelectionKey> selectorKeys = selector.keys();
         List<String> list = new ArrayList<>(selectorKeys.size());

         for (SelectionKey key : selectorKeys) {
            if (key != null) {
               list.add(String.format("SelectionKey@%x{i=%d}->%s", key.hashCode(), ManagedSelector.safeInterestOps(key), key.attachment()));
            }
         }

         this.keys = list;
         this.latch.countDown();
      }

      public List<String> get(long timeout, TimeUnit unit) {
         try {
            this.latch.await(timeout, unit);
         } catch (InterruptedException var5) {
            ManagedSelector.LOG.trace("IGNORED", var5);
         }

         return this.keys;
      }
   }

   public interface Selectable {
      Runnable onSelected();

      void updateKey();

      void replaceKey(SelectionKey var1);
   }

   private class SelectorProducer implements Producer {
      private Set<SelectionKey> _keys = Collections.emptySet();
      private Iterator<SelectionKey> _cursor = Collections.emptyIterator();

      public Runnable produce() {
         do {
            Runnable task = this.processSelected();
            if (task != null) {
               return task;
            }

            this.processUpdates();
            this.updateKeys();
         } while (this.select());

         return null;
      }

      private void processUpdates() {
         AutoLock l = ManagedSelector.this._lock.lock();

         try {
            Deque<ManagedSelector.SelectorUpdate> updates = ManagedSelector.this._updates;
            ManagedSelector.this._updates = ManagedSelector.this._updateable;
            ManagedSelector.this._updateable = updates;
         } catch (Throwable var9) {
            if (l != null) {
               try {
                  l.close();
               } catch (Throwable var7) {
                  var9.addSuppressed(var7);
               }
            }

            throw var9;
         }

         if (l != null) {
            l.close();
         }

         if (ManagedSelector.LOG.isDebugEnabled()) {
            ManagedSelector.LOG.debug("updateable {}", ManagedSelector.this._updateable.size());
         }

         Selector selector = ManagedSelector.this._selector;

         while (true) {
            ManagedSelector.SelectorUpdate update = ManagedSelector.this._updateable.pollFirst();
            if (update == null || selector == null) {
               AutoLock lx = ManagedSelector.this._lock.lock();

               try {
                  updates = ManagedSelector.this._updates.size();
                  ManagedSelector.this._selecting = updates == 0;
                  if (ManagedSelector.this._selecting) {
                     selector = null;
                  }
               } catch (Throwable var10) {
                  if (lx != null) {
                     try {
                        lx.close();
                     } catch (Throwable var6) {
                        var10.addSuppressed(var6);
                     }
                  }

                  throw var10;
               }

               if (lx != null) {
                  lx.close();
               }

               if (ManagedSelector.LOG.isDebugEnabled()) {
                  ManagedSelector.LOG.debug("updates {}", updates);
               }

               if (selector != null) {
                  if (ManagedSelector.LOG.isDebugEnabled()) {
                     ManagedSelector.LOG.debug("wakeup on updates {}", this);
                  }

                  selector.wakeup();
               }

               return;
            }

            try {
               if (ManagedSelector.LOG.isDebugEnabled()) {
                  ManagedSelector.LOG.debug("update {}", update);
               }

               update.update(selector);
            } catch (Throwable var8) {
               ManagedSelector.LOG.warn("Cannot update selector {}", ManagedSelector.this, var8);
            }
         }
      }

      private boolean select() {
         try {
            Selector selector = ManagedSelector.this._selector;
            if (selector != null) {
               if (ManagedSelector.LOG.isDebugEnabled()) {
                  ManagedSelector.LOG.debug("Selector {} waiting with {} keys", selector, selector.keys().size());
               }

               int selected = ManagedSelector.this.select(selector);
               selector = ManagedSelector.this._selector;
               if (selector != null) {
                  if (ManagedSelector.LOG.isDebugEnabled()) {
                     ManagedSelector.LOG
                        .debug(
                           "Selector {} woken up from select, {}/{}/{} selected",
                           new Object[]{selector, selected, selector.selectedKeys().size(), selector.keys().size()}
                        );
                  }

                  AutoLock l = ManagedSelector.this._lock.lock();

                  int updates;
                  try {
                     ManagedSelector.this._selecting = false;
                     updates = ManagedSelector.this._updates.size();
                  } catch (Throwable var8) {
                     if (l != null) {
                        try {
                           l.close();
                        } catch (Throwable var7) {
                           var8.addSuppressed(var7);
                        }
                     }

                     throw var8;
                  }

                  if (l != null) {
                     l.close();
                  }

                  this._keys = selector.selectedKeys();
                  int selectedKeys = this._keys.size();
                  if (selectedKeys > 0) {
                     ManagedSelector.this._keyStats.record((long)selectedKeys);
                  }

                  this._cursor = selectedKeys > 0 ? this._keys.iterator() : Collections.emptyIterator();
                  if (ManagedSelector.LOG.isDebugEnabled()) {
                     ManagedSelector.LOG.debug("Selector {} processing {} keys, {} updates", new Object[]{selector, selectedKeys, updates});
                  }

                  return true;
               }
            }
         } catch (Throwable var9) {
            IO.close(ManagedSelector.this._selector);
            ManagedSelector.this._selector = null;
            if (ManagedSelector.this.isRunning()) {
               ManagedSelector.LOG.warn("Fatal select() failure", var9);
               ManagedSelector.this.onSelectFailed(var9);
            } else if (ManagedSelector.LOG.isDebugEnabled()) {
               ManagedSelector.LOG.warn("select() failure", var9);
            } else {
               ManagedSelector.LOG.warn("select() failure {}", var9.toString());
            }
         }

         return false;
      }

      private Runnable processSelected() {
         while (this._cursor.hasNext()) {
            SelectionKey key = this._cursor.next();
            Object attachment = key.attachment();
            SelectableChannel channel = key.channel();
            if (!key.isValid()) {
               if (ManagedSelector.LOG.isDebugEnabled()) {
                  ManagedSelector.LOG.debug("Selector loop ignoring invalid key for channel {}", channel);
               }

               IO.close((Closeable)(attachment instanceof EndPoint ? (EndPoint)attachment : channel));
            } else {
               if (ManagedSelector.LOG.isDebugEnabled()) {
                  ManagedSelector.LOG.debug("selected {} {} {} ", new Object[]{ManagedSelector.safeReadyOps(key), key, attachment});
               }

               try {
                  if (attachment instanceof ManagedSelector.Selectable) {
                     Runnable task = ((ManagedSelector.Selectable)attachment).onSelected();
                     if (task != null) {
                        return task;
                     }
                  } else {
                     if (!key.isConnectable()) {
                        throw new IllegalStateException(
                           "key="
                              + key
                              + ", att="
                              + attachment
                              + ", iOps="
                              + ManagedSelector.safeInterestOps(key)
                              + ", rOps="
                              + ManagedSelector.safeReadyOps(key)
                        );
                     }

                     ManagedSelector.this.processConnect(key, (ManagedSelector.Connect)attachment);
                  }
               } catch (CancelledKeyException var5) {
                  if (ManagedSelector.LOG.isDebugEnabled()) {
                     ManagedSelector.LOG.debug("Ignoring cancelled key for channel {}", channel);
                  }

                  IO.close((Closeable)(attachment instanceof EndPoint ? (EndPoint)attachment : channel));
               } catch (Throwable var6) {
                  ManagedSelector.LOG.warn("Could not process key for channel {}", channel, var6);
                  IO.close((Closeable)(attachment instanceof EndPoint ? (EndPoint)attachment : channel));
               }
            }
         }

         return null;
      }

      private void updateKeys() {
         for (SelectionKey key : this._keys) {
            Object attachment = key.attachment();
            if (attachment instanceof ManagedSelector.Selectable) {
               ((ManagedSelector.Selectable)attachment).updateKey();
            }
         }

         this._keys.clear();
      }

      @Override
      public String toString() {
         return String.format("%s@%x", this.getClass().getSimpleName(), this.hashCode());
      }
   }

   public interface SelectorUpdate {
      void update(Selector var1);
   }

   private class Start implements ManagedSelector.SelectorUpdate {
      private final CountDownLatch _started = new CountDownLatch(1);

      @Override
      public void update(Selector selector) {
         ManagedSelector.this._started.set(true);
         this._started.countDown();
      }
   }

   private class StopSelector implements ManagedSelector.SelectorUpdate {
      private final CountDownLatch _stopped = new CountDownLatch(1);

      @Override
      public void update(Selector selector) {
         try {
            for (SelectionKey key : selector.keys()) {
               if (key != null) {
                  Object attachment = key.attachment();
                  if (attachment instanceof Closeable) {
                     IO.close((Closeable)attachment);
                  }
               }
            }

            ManagedSelector.this._selector = null;
            IO.close(selector);
         } finally {
            this._stopped.countDown();
         }
      }
   }
}
