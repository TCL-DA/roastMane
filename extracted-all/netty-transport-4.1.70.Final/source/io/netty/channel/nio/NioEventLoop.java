package io.netty.channel.nio;

import io.netty.channel.ChannelException;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopException;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.IntSupplier;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReflectionUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

public final class NioEventLoop extends SingleThreadEventLoop {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioEventLoop.class);
   private static final int CLEANUP_INTERVAL = 256;
   private static final boolean DISABLE_KEY_SET_OPTIMIZATION = SystemPropertyUtil.getBoolean("io.netty.noKeySetOptimization", false);
   private static final int MIN_PREMATURE_SELECTOR_RETURNS = 3;
   private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;
   private final IntSupplier selectNowSupplier = new IntSupplier() {
      public int get() throws Exception {
         return NioEventLoop.this.selectNow();
      }
   };
   private Selector selector;
   private Selector unwrappedSelector;
   private SelectedSelectionKeySet selectedKeys;
   private final SelectorProvider provider;
   private static final long AWAKE = -1L;
   private static final long NONE = Long.MAX_VALUE;
   private final AtomicLong nextWakeupNanos = new AtomicLong(-1L);
   private final SelectStrategy selectStrategy;
   private volatile int ioRatio = 50;
   private int cancelledKeys;
   private boolean needsToSelectAgain;

   NioEventLoop(
      NioEventLoopGroup parent,
      Executor executor,
      SelectorProvider selectorProvider,
      SelectStrategy strategy,
      RejectedExecutionHandler rejectedExecutionHandler,
      EventLoopTaskQueueFactory taskQueueFactory,
      EventLoopTaskQueueFactory tailTaskQueueFactory
   ) {
      super(parent, executor, false, newTaskQueue(taskQueueFactory), newTaskQueue(tailTaskQueueFactory), rejectedExecutionHandler);
      this.provider = (SelectorProvider)ObjectUtil.checkNotNull(selectorProvider, "selectorProvider");
      this.selectStrategy = (SelectStrategy)ObjectUtil.checkNotNull(strategy, "selectStrategy");
      NioEventLoop.SelectorTuple selectorTuple = this.openSelector();
      this.selector = selectorTuple.selector;
      this.unwrappedSelector = selectorTuple.unwrappedSelector;
   }

   private static Queue<Runnable> newTaskQueue(EventLoopTaskQueueFactory queueFactory) {
      return queueFactory == null ? newTaskQueue0(DEFAULT_MAX_PENDING_TASKS) : queueFactory.newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
   }

   private NioEventLoop.SelectorTuple openSelector() {
      final Selector unwrappedSelector;
      try {
         unwrappedSelector = this.provider.openSelector();
      } catch (IOException var7) {
         throw new ChannelException("failed to open a new selector", var7);
      }

      if (DISABLE_KEY_SET_OPTIMIZATION) {
         return new NioEventLoop.SelectorTuple(unwrappedSelector);
      } else {
         Object maybeSelectorImplClass = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               try {
                  return Class.forName("sun.nio.ch.SelectorImpl", false, PlatformDependent.getSystemClassLoader());
               } catch (Throwable var2) {
                  return var2;
               }
            }
         });
         if (maybeSelectorImplClass instanceof Class && ((Class)maybeSelectorImplClass).isAssignableFrom(unwrappedSelector.getClass())) {
            final Class<?> selectorImplClass = (Class<?>)maybeSelectorImplClass;
            final SelectedSelectionKeySet selectedKeySet = new SelectedSelectionKeySet();
            Object maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     Field selectedKeysField = selectorImplClass.getDeclaredField("selectedKeys");
                     Field publicSelectedKeysField = selectorImplClass.getDeclaredField("publicSelectedKeys");
                     if (PlatformDependent.javaVersion() >= 9 && PlatformDependent.hasUnsafe()) {
                        long selectedKeysFieldOffset = PlatformDependent.objectFieldOffset(selectedKeysField);
                        long publicSelectedKeysFieldOffset = PlatformDependent.objectFieldOffset(publicSelectedKeysField);
                        if (selectedKeysFieldOffset != -1L && publicSelectedKeysFieldOffset != -1L) {
                           PlatformDependent.putObject(unwrappedSelector, selectedKeysFieldOffset, selectedKeySet);
                           PlatformDependent.putObject(unwrappedSelector, publicSelectedKeysFieldOffset, selectedKeySet);
                           return null;
                        }
                     }

                     Throwable cause = ReflectionUtil.trySetAccessible(selectedKeysField, true);
                     if (cause != null) {
                        return cause;
                     } else {
                        cause = ReflectionUtil.trySetAccessible(publicSelectedKeysField, true);
                        if (cause != null) {
                           return cause;
                        } else {
                           selectedKeysField.set(unwrappedSelector, selectedKeySet);
                           publicSelectedKeysField.set(unwrappedSelector, selectedKeySet);
                           return null;
                        }
                     }
                  } catch (NoSuchFieldException var7) {
                     return var7;
                  } catch (IllegalAccessException var8x) {
                     return var8x;
                  }
               }
            });
            if (maybeException instanceof Exception) {
               this.selectedKeys = null;
               Exception e = (Exception)maybeException;
               logger.trace("failed to instrument a special java.util.Set into: {}", unwrappedSelector, e);
               return new NioEventLoop.SelectorTuple(unwrappedSelector);
            } else {
               this.selectedKeys = selectedKeySet;
               logger.trace("instrumented a special java.util.Set into: {}", unwrappedSelector);
               return new NioEventLoop.SelectorTuple(unwrappedSelector, new SelectedSelectionKeySetSelector(unwrappedSelector, selectedKeySet));
            }
         } else {
            if (maybeSelectorImplClass instanceof Throwable) {
               Throwable t = (Throwable)maybeSelectorImplClass;
               logger.trace("failed to instrument a special java.util.Set into: {}", unwrappedSelector, t);
            }

            return new NioEventLoop.SelectorTuple(unwrappedSelector);
         }
      }
   }

   public SelectorProvider selectorProvider() {
      return this.provider;
   }

   protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
      return newTaskQueue0(maxPendingTasks);
   }

   private static Queue<Runnable> newTaskQueue0(int maxPendingTasks) {
      return maxPendingTasks == Integer.MAX_VALUE ? PlatformDependent.newMpscQueue() : PlatformDependent.newMpscQueue(maxPendingTasks);
   }

   public void register(final SelectableChannel ch, final int interestOps, final NioTask<?> task) {
      ObjectUtil.checkNotNull(ch, "ch");
      if (interestOps == 0) {
         throw new IllegalArgumentException("interestOps must be non-zero.");
      } else if ((interestOps & ~ch.validOps()) != 0) {
         throw new IllegalArgumentException("invalid interestOps: " + interestOps + "(validOps: " + ch.validOps() + ')');
      } else {
         ObjectUtil.checkNotNull(task, "task");
         if (this.isShutdown()) {
            throw new IllegalStateException("event loop shut down");
         } else {
            if (this.inEventLoop()) {
               this.register0(ch, interestOps, task);
            } else {
               try {
                  this.submit(new Runnable() {
                     @Override
                     public void run() {
                        NioEventLoop.this.register0(ch, interestOps, task);
                     }
                  }).sync();
               } catch (InterruptedException var5) {
                  Thread.currentThread().interrupt();
               }
            }
         }
      }
   }

   private void register0(SelectableChannel ch, int interestOps, NioTask<?> task) {
      try {
         ch.register(this.unwrappedSelector, interestOps, task);
      } catch (Exception var5) {
         throw new EventLoopException("failed to register a channel", var5);
      }
   }

   public int getIoRatio() {
      return this.ioRatio;
   }

   public void setIoRatio(int ioRatio) {
      if (ioRatio > 0 && ioRatio <= 100) {
         this.ioRatio = ioRatio;
      } else {
         throw new IllegalArgumentException("ioRatio: " + ioRatio + " (expected: 0 < ioRatio <= 100)");
      }
   }

   public void rebuildSelector() {
      if (!this.inEventLoop()) {
         this.execute(new Runnable() {
            @Override
            public void run() {
               NioEventLoop.this.rebuildSelector0();
            }
         });
      } else {
         this.rebuildSelector0();
      }
   }

   @Override
   public int registeredChannels() {
      return this.selector.keys().size() - this.cancelledKeys;
   }

   private void rebuildSelector0() {
      Selector oldSelector = this.selector;
      if (oldSelector != null) {
         NioEventLoop.SelectorTuple newSelectorTuple;
         try {
            newSelectorTuple = this.openSelector();
         } catch (Exception var9) {
            logger.warn("Failed to create a new Selector.", var9);
            return;
         }

         int nChannels = 0;

         for (SelectionKey key : oldSelector.keys()) {
            Object a = key.attachment();

            try {
               if (key.isValid() && key.channel().keyFor(newSelectorTuple.unwrappedSelector) == null) {
                  int interestOps = key.interestOps();
                  key.cancel();
                  SelectionKey newKey = key.channel().register(newSelectorTuple.unwrappedSelector, interestOps, a);
                  if (a instanceof AbstractNioChannel) {
                     ((AbstractNioChannel)a).selectionKey = newKey;
                  }

                  nChannels++;
               }
            } catch (Exception var11) {
               logger.warn("Failed to re-register a Channel to the new Selector.", var11);
               if (a instanceof AbstractNioChannel) {
                  AbstractNioChannel ch = (AbstractNioChannel)a;
                  ch.unsafe().close(ch.unsafe().voidPromise());
               } else {
                  NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
                  invokeChannelUnregistered(task, key, var11);
               }
            }
         }

         this.selector = newSelectorTuple.selector;
         this.unwrappedSelector = newSelectorTuple.unwrappedSelector;

         try {
            oldSelector.close();
         } catch (Throwable var10) {
            if (logger.isWarnEnabled()) {
               logger.warn("Failed to close the old Selector.", var10);
            }
         }

         if (logger.isInfoEnabled()) {
            logger.info("Migrated " + nChannels + " channel(s) to the new Selector.");
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void run() {
      int selectCnt = 0;

      while (true) {
         label508:
         while (true) {
            label506:
            while (true) {
               label504:
               while (true) {
                  while (true) {
                     boolean ranTasks;
                     boolean var34;
                     try {
                        label512: {
                           int t;
                           try {
                              var34 = false;
                              var34 = true;
                              var34 = false;
                              t = this.selectStrategy.calculateStrategy(this.selectNowSupplier, this.hasTasks());
                              switch (t) {
                                 case -3:
                                 case -1:
                                    long curDeadlineNanos = this.nextScheduledTaskDeadlineNanos();
                                    if (curDeadlineNanos == -1L) {
                                       curDeadlineNanos = Long.MAX_VALUE;
                                    }

                                    this.nextWakeupNanos.set(curDeadlineNanos);

                                    try {
                                       if (!this.hasTasks()) {
                                          t = this.select(curDeadlineNanos);
                                       }
                                       break;
                                    } finally {
                                       this.nextWakeupNanos.lazySet(-1L);
                                    }
                                 case -2:
                                    break label508;
                              }
                           } catch (IOException var110) {
                              this.rebuildSelector0();
                              selectCnt = 0;
                              handleLoopException(var110);
                              var34 = false;
                              break label512;
                           }

                           selectCnt++;
                           this.cancelledKeys = 0;
                           this.needsToSelectAgain = false;
                           int ioRatio = this.ioRatio;
                           if (ioRatio == 100) {
                              try {
                                 if (t > 0) {
                                    this.processSelectedKeys();
                                 }
                              } finally {
                                 ranTasks = this.runAllTasks();
                              }
                           } else if (t > 0) {
                              long ioStartTime = System.nanoTime();

                              try {
                                 this.processSelectedKeys();
                              } finally {
                                 long ioTime = System.nanoTime() - ioStartTime;
                                 ranTasks = this.runAllTasks(ioTime * (long)(100 - ioRatio) / (long)ioRatio);
                              }
                           } else {
                              ranTasks = this.runAllTasks(0L);
                           }

                           if (ranTasks == false && t <= 0) {
                              if (this.unexpectedSelectorWakeup(selectCnt)) {
                                 selectCnt = 0;
                                 var34 = false;
                              } else {
                                 var34 = false;
                              }
                              break label506;
                           }

                           if (selectCnt > 3 && logger.isDebugEnabled()) {
                              logger.debug("Selector.select() returned prematurely {} times in a row for Selector {}.", selectCnt - 1, this.selector);
                           }

                           selectCnt = 0;
                           var34 = false;
                           break label506;
                        }
                     } catch (CancelledKeyException var111) {
                        if (logger.isDebugEnabled()) {
                           logger.debug(CancelledKeyException.class.getSimpleName() + " raised by a Selector {} - JDK bug?", this.selector, var111);
                           var34 = false;
                        } else {
                           var34 = false;
                        }
                        break;
                     } catch (Error var112) {
                        throw var112;
                     } catch (Throwable var113) {
                        handleLoopException(var113);
                        var34 = false;
                        break label504;
                     } finally {
                        if (var34) {
                           try {
                              if (this.isShuttingDown()) {
                                 this.closeAll();
                                 if (this.confirmShutdown()) {
                                    return;
                                 }
                              }
                           } catch (Error var95) {
                              throw var95;
                           } catch (Throwable var96) {
                              handleLoopException(var96);
                           }
                        }
                     }

                     try {
                        if (this.isShuttingDown()) {
                           this.closeAll();
                           if (this.confirmShutdown()) {
                              return;
                           }
                        }
                     } catch (Error var107) {
                        throw var107;
                     } catch (Throwable var108) {
                        ranTasks = (boolean)var108;
                        handleLoopException(var108);
                     }
                  }

                  try {
                     if (this.isShuttingDown()) {
                        this.closeAll();
                        if (this.confirmShutdown()) {
                           return;
                        }
                     }
                  } catch (Error var101) {
                     throw var101;
                  } catch (Throwable var102) {
                     handleLoopException(var102);
                  }
               }

               try {
                  if (this.isShuttingDown()) {
                     this.closeAll();
                     if (this.confirmShutdown()) {
                        return;
                     }
                  }
               } catch (Error var99) {
                  throw var99;
               } catch (Throwable var100) {
                  handleLoopException(var100);
               }
            }

            try {
               if (this.isShuttingDown()) {
                  this.closeAll();
                  if (this.confirmShutdown()) {
                     return;
                  }
               }
            } catch (Error var103) {
               throw var103;
            } catch (Throwable var104) {
               handleLoopException(var104);
            }
         }

         try {
            if (this.isShuttingDown()) {
               this.closeAll();
               if (this.confirmShutdown()) {
                  return;
               }
            }
         } catch (Error var97) {
            throw var97;
         } catch (Throwable var98) {
            handleLoopException(var98);
         }
      }
   }

   private boolean unexpectedSelectorWakeup(int selectCnt) {
      if (Thread.interrupted()) {
         if (logger.isDebugEnabled()) {
            logger.debug(
               "Selector.select() returned prematurely because Thread.currentThread().interrupt() was called. Use NioEventLoop.shutdownGracefully() to shutdown the NioEventLoop."
            );
         }

         return true;
      } else if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
         logger.warn("Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.", selectCnt, this.selector);
         this.rebuildSelector();
         return true;
      } else {
         return false;
      }
   }

   private static void handleLoopException(Throwable t) {
      logger.warn("Unexpected exception in the selector loop.", t);

      try {
         Thread.sleep(1000L);
      } catch (InterruptedException var2) {
      }
   }

   private void processSelectedKeys() {
      if (this.selectedKeys != null) {
         this.processSelectedKeysOptimized();
      } else {
         this.processSelectedKeysPlain(this.selector.selectedKeys());
      }
   }

   protected void cleanup() {
      try {
         this.selector.close();
      } catch (IOException var2) {
         logger.warn("Failed to close a selector.", var2);
      }
   }

   void cancel(SelectionKey key) {
      key.cancel();
      this.cancelledKeys++;
      if (this.cancelledKeys >= 256) {
         this.cancelledKeys = 0;
         this.needsToSelectAgain = true;
      }
   }

   private void processSelectedKeysPlain(Set<SelectionKey> selectedKeys) {
      if (!selectedKeys.isEmpty()) {
         Iterator<SelectionKey> i = selectedKeys.iterator();

         while (true) {
            SelectionKey k = i.next();
            Object a = k.attachment();
            i.remove();
            if (a instanceof AbstractNioChannel) {
               this.processSelectedKey(k, (AbstractNioChannel)a);
            } else {
               NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
               processSelectedKey(k, task);
            }

            if (!i.hasNext()) {
               break;
            }

            if (this.needsToSelectAgain) {
               this.selectAgain();
               selectedKeys = this.selector.selectedKeys();
               if (selectedKeys.isEmpty()) {
                  break;
               }

               i = selectedKeys.iterator();
            }
         }
      }
   }

   private void processSelectedKeysOptimized() {
      for (int i = 0; i < this.selectedKeys.size; i++) {
         SelectionKey k = this.selectedKeys.keys[i];
         this.selectedKeys.keys[i] = null;
         Object a = k.attachment();
         if (a instanceof AbstractNioChannel) {
            this.processSelectedKey(k, (AbstractNioChannel)a);
         } else {
            NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
            processSelectedKey(k, task);
         }

         if (this.needsToSelectAgain) {
            this.selectedKeys.reset(i + 1);
            this.selectAgain();
            i = -1;
         }
      }
   }

   private void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
      AbstractNioChannel.NioUnsafe unsafe = ch.unsafe();
      if (!k.isValid()) {
         EventLoop eventLoop;
         try {
            eventLoop = ch.eventLoop();
         } catch (Throwable var6) {
            return;
         }

         if (eventLoop == this) {
            unsafe.close(unsafe.voidPromise());
         }
      } else {
         try {
            int readyOps = k.readyOps();
            if ((readyOps & 8) != 0) {
               int ops = k.interestOps();
               ops &= -9;
               k.interestOps(ops);
               unsafe.finishConnect();
            }

            if ((readyOps & 4) != 0) {
               ch.unsafe().forceFlush();
            }

            if ((readyOps & 17) != 0 || readyOps == 0) {
               unsafe.read();
            }
         } catch (CancelledKeyException var7) {
            unsafe.close(unsafe.voidPromise());
         }
      }
   }

   private static void processSelectedKey(SelectionKey k, NioTask<SelectableChannel> task) {
      int state = 0;

      try {
         task.channelReady(k.channel(), k);
         state = 1;
      } catch (Exception var7) {
         k.cancel();
         invokeChannelUnregistered(task, k, var7);
         state = 2;
      } finally {
         switch (state) {
            case 0:
               k.cancel();
               invokeChannelUnregistered(task, k, null);
               break;
            case 1:
               if (!k.isValid()) {
                  invokeChannelUnregistered(task, k, null);
               }
         }
      }
   }

   private void closeAll() {
      this.selectAgain();
      Set<SelectionKey> keys = this.selector.keys();
      Collection<AbstractNioChannel> channels = new ArrayList<>(keys.size());

      for (SelectionKey k : keys) {
         Object a = k.attachment();
         if (a instanceof AbstractNioChannel) {
            channels.add((AbstractNioChannel)a);
         } else {
            k.cancel();
            NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
            invokeChannelUnregistered(task, k, null);
         }
      }

      for (AbstractNioChannel ch : channels) {
         ch.unsafe().close(ch.unsafe().voidPromise());
      }
   }

   private static void invokeChannelUnregistered(NioTask<SelectableChannel> task, SelectionKey k, Throwable cause) {
      try {
         task.channelUnregistered(k.channel(), cause);
      } catch (Exception var4) {
         logger.warn("Unexpected exception while running NioTask.channelUnregistered()", var4);
      }
   }

   protected void wakeup(boolean inEventLoop) {
      if (!inEventLoop && this.nextWakeupNanos.getAndSet(-1L) != -1L) {
         this.selector.wakeup();
      }
   }

   protected boolean beforeScheduledTaskSubmitted(long deadlineNanos) {
      return deadlineNanos < this.nextWakeupNanos.get();
   }

   protected boolean afterScheduledTaskSubmitted(long deadlineNanos) {
      return deadlineNanos < this.nextWakeupNanos.get();
   }

   Selector unwrappedSelector() {
      return this.unwrappedSelector;
   }

   int selectNow() throws IOException {
      return this.selector.selectNow();
   }

   private int select(long deadlineNanos) throws IOException {
      if (deadlineNanos == Long.MAX_VALUE) {
         return this.selector.select();
      } else {
         long timeoutMillis = deadlineToDelayNanos(deadlineNanos + 995000L) / 1000000L;
         return timeoutMillis <= 0L ? this.selector.selectNow() : this.selector.select(timeoutMillis);
      }
   }

   private void selectAgain() {
      this.needsToSelectAgain = false;

      try {
         this.selector.selectNow();
      } catch (Throwable var2) {
         logger.warn("Failed to update SelectionKeys.", var2);
      }
   }

   static {
      String key = "sun.nio.ch.bugLevel";
      String bugLevel = SystemPropertyUtil.get("sun.nio.ch.bugLevel");
      if (bugLevel == null) {
         try {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  System.setProperty("sun.nio.ch.bugLevel", "");
                  return null;
               }
            });
         } catch (SecurityException var3) {
            logger.debug("Unable to get/set System Property: sun.nio.ch.bugLevel", var3);
         }
      }

      int selectorAutoRebuildThreshold = SystemPropertyUtil.getInt("io.netty.selectorAutoRebuildThreshold", 512);
      if (selectorAutoRebuildThreshold < 3) {
         selectorAutoRebuildThreshold = 0;
      }

      SELECTOR_AUTO_REBUILD_THRESHOLD = selectorAutoRebuildThreshold;
      if (logger.isDebugEnabled()) {
         logger.debug("-Dio.netty.noKeySetOptimization: {}", DISABLE_KEY_SET_OPTIMIZATION);
         logger.debug("-Dio.netty.selectorAutoRebuildThreshold: {}", SELECTOR_AUTO_REBUILD_THRESHOLD);
      }
   }

   private static final class SelectorTuple {
      final Selector unwrappedSelector;
      final Selector selector;

      SelectorTuple(Selector unwrappedSelector) {
         this.unwrappedSelector = unwrappedSelector;
         this.selector = unwrappedSelector;
      }

      SelectorTuple(Selector unwrappedSelector, Selector selector) {
         this.unwrappedSelector = unwrappedSelector;
         this.selector = selector;
      }
   }
}
