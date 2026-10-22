package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public final class NonStickyEventExecutorGroup implements EventExecutorGroup {
   private final EventExecutorGroup group;
   private final int maxTaskExecutePerRun;

   public NonStickyEventExecutorGroup(EventExecutorGroup group) {
      this(group, 1024);
   }

   public NonStickyEventExecutorGroup(EventExecutorGroup group, int maxTaskExecutePerRun) {
      this.group = verify(group);
      this.maxTaskExecutePerRun = ObjectUtil.checkPositive(maxTaskExecutePerRun, "maxTaskExecutePerRun");
   }

   private static EventExecutorGroup verify(EventExecutorGroup group) {
      for (EventExecutor executor : ObjectUtil.checkNotNull(group, "group")) {
         if (executor instanceof OrderedEventExecutor) {
            throw new IllegalArgumentException("EventExecutorGroup " + group + " contains OrderedEventExecutors: " + executor);
         }
      }

      return group;
   }

   private NonStickyEventExecutorGroup.NonStickyOrderedEventExecutor newExecutor(EventExecutor executor) {
      return new NonStickyEventExecutorGroup.NonStickyOrderedEventExecutor(executor, this.maxTaskExecutePerRun);
   }

   @Override
   public boolean isShuttingDown() {
      return this.group.isShuttingDown();
   }

   @Override
   public Future<?> shutdownGracefully() {
      return this.group.shutdownGracefully();
   }

   @Override
   public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
      return this.group.shutdownGracefully(quietPeriod, timeout, unit);
   }

   @Override
   public Future<?> terminationFuture() {
      return this.group.terminationFuture();
   }

   @Override
   public void shutdown() {
      this.group.shutdown();
   }

   @Override
   public List<Runnable> shutdownNow() {
      return this.group.shutdownNow();
   }

   @Override
   public EventExecutor next() {
      return this.newExecutor(this.group.next());
   }

   @Override
   public Iterator<EventExecutor> iterator() {
      final Iterator<EventExecutor> itr = this.group.iterator();
      return new Iterator<EventExecutor>() {
         @Override
         public boolean hasNext() {
            return itr.hasNext();
         }

         public EventExecutor next() {
            return NonStickyEventExecutorGroup.this.newExecutor(itr.next());
         }

         @Override
         public void remove() {
            itr.remove();
         }
      };
   }

   @Override
   public Future<?> submit(Runnable task) {
      return this.group.submit(task);
   }

   @Override
   public <T> Future<T> submit(Runnable task, T result) {
      return this.group.submit(task, result);
   }

   @Override
   public <T> Future<T> submit(Callable<T> task) {
      return this.group.submit(task);
   }

   @Override
   public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      return this.group.schedule(command, delay, unit);
   }

   @Override
   public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
      return this.group.schedule(callable, delay, unit);
   }

   @Override
   public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      return this.group.scheduleAtFixedRate(command, initialDelay, period, unit);
   }

   @Override
   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      return this.group.scheduleWithFixedDelay(command, initialDelay, delay, unit);
   }

   @Override
   public boolean isShutdown() {
      return this.group.isShutdown();
   }

   @Override
   public boolean isTerminated() {
      return this.group.isTerminated();
   }

   @Override
   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      return this.group.awaitTermination(timeout, unit);
   }

   @Override
   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
      return this.group.invokeAll(tasks);
   }

   @Override
   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
      return this.group.invokeAll(tasks, timeout, unit);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
      return this.group.invokeAny(tasks);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return this.group.invokeAny(tasks, timeout, unit);
   }

   @Override
   public void execute(Runnable command) {
      this.group.execute(command);
   }

   private static final class NonStickyOrderedEventExecutor extends AbstractEventExecutor implements Runnable, OrderedEventExecutor {
      private final EventExecutor executor;
      private final Queue<Runnable> tasks = PlatformDependent.newMpscQueue();
      private static final int NONE = 0;
      private static final int SUBMITTED = 1;
      private static final int RUNNING = 2;
      private final AtomicInteger state = new AtomicInteger();
      private final int maxTaskExecutePerRun;

      NonStickyOrderedEventExecutor(EventExecutor executor, int maxTaskExecutePerRun) {
         super(executor);
         this.executor = executor;
         this.maxTaskExecutePerRun = maxTaskExecutePerRun;
      }

      @Override
      public void run() {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.RuntimeException: parsing failure!
         //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
         //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:184)
         //
         // Bytecode:
         // 00: aload 0
         // 01: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 04: bipush 1
         // 05: bipush 2
         // 06: invokevirtual java/util/concurrent/atomic/AtomicInteger.compareAndSet (II)Z
         // 09: ifne 0d
         // 0c: return
         // 0d: bipush 0
         // 0e: istore 1
         // 0f: iload 1
         // 10: aload 0
         // 11: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.maxTaskExecutePerRun I
         // 14: if_icmpge 35
         // 17: aload 0
         // 18: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.tasks Ljava/util/Queue;
         // 1b: invokeinterface java/util/Queue.poll ()Ljava/lang/Object; 1
         // 20: checkcast java/lang/Runnable
         // 23: astore 2
         // 24: aload 2
         // 25: ifnonnull 2b
         // 28: goto 35
         // 2b: aload 2
         // 2c: invokestatic io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.safeExecute (Ljava/lang/Runnable;)V
         // 2f: iinc 1 1
         // 32: goto 0f
         // 35: iload 1
         // 36: aload 0
         // 37: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.maxTaskExecutePerRun I
         // 3a: if_icmpne 5c
         // 3d: aload 0
         // 3e: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 41: bipush 1
         // 42: invokevirtual java/util/concurrent/atomic/AtomicInteger.set (I)V
         // 45: aload 0
         // 46: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.executor Lio/netty/util/concurrent/EventExecutor;
         // 49: aload 0
         // 4a: invokeinterface io/netty/util/concurrent/EventExecutor.execute (Ljava/lang/Runnable;)V 2
         // 4f: return
         // 50: astore 2
         // 51: aload 0
         // 52: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 55: bipush 2
         // 56: invokevirtual java/util/concurrent/atomic/AtomicInteger.set (I)V
         // 59: goto c9
         // 5c: aload 0
         // 5d: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 60: bipush 0
         // 61: invokevirtual java/util/concurrent/atomic/AtomicInteger.set (I)V
         // 64: aload 0
         // 65: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.tasks Ljava/util/Queue;
         // 68: invokeinterface java/util/Queue.isEmpty ()Z 1
         // 6d: ifne 7c
         // 70: aload 0
         // 71: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 74: bipush 0
         // 75: bipush 2
         // 76: invokevirtual java/util/concurrent/atomic/AtomicInteger.compareAndSet (II)Z
         // 79: ifne c9
         // 7c: return
         // 7d: astore 3
         // 7e: iload 1
         // 7f: aload 0
         // 80: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.maxTaskExecutePerRun I
         // 83: if_icmpne a6
         // 86: aload 0
         // 87: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 8a: bipush 1
         // 8b: invokevirtual java/util/concurrent/atomic/AtomicInteger.set (I)V
         // 8e: aload 0
         // 8f: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.executor Lio/netty/util/concurrent/EventExecutor;
         // 92: aload 0
         // 93: invokeinterface io/netty/util/concurrent/EventExecutor.execute (Ljava/lang/Runnable;)V 2
         // 98: return
         // 99: astore 4
         // 9b: aload 0
         // 9c: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 9f: bipush 2
         // a0: invokevirtual java/util/concurrent/atomic/AtomicInteger.set (I)V
         // a3: goto c7
         // a6: aload 0
         // a7: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // aa: bipush 0
         // ab: invokevirtual java/util/concurrent/atomic/AtomicInteger.set (I)V
         // ae: aload 0
         // af: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.tasks Ljava/util/Queue;
         // b2: invokeinterface java/util/Queue.isEmpty ()Z 1
         // b7: ifne c6
         // ba: aload 0
         // bb: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // be: bipush 0
         // bf: bipush 2
         // c0: invokevirtual java/util/concurrent/atomic/AtomicInteger.compareAndSet (II)Z
         // c3: ifne c7
         // c6: return
         // c7: aload 3
         // c8: athrow
         // c9: goto 0d
      }

      @Override
      public boolean inEventLoop(Thread thread) {
         return false;
      }

      @Override
      public boolean inEventLoop() {
         return false;
      }

      @Override
      public boolean isShuttingDown() {
         return this.executor.isShutdown();
      }

      @Override
      public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
         return this.executor.shutdownGracefully(quietPeriod, timeout, unit);
      }

      @Override
      public Future<?> terminationFuture() {
         return this.executor.terminationFuture();
      }

      @Override
      public void shutdown() {
         this.executor.shutdown();
      }

      @Override
      public boolean isShutdown() {
         return this.executor.isShutdown();
      }

      @Override
      public boolean isTerminated() {
         return this.executor.isTerminated();
      }

      @Override
      public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
         return this.executor.awaitTermination(timeout, unit);
      }

      @Override
      public void execute(Runnable command) {
         if (!this.tasks.offer(command)) {
            throw new RejectedExecutionException();
         } else {
            if (this.state.compareAndSet(0, 1)) {
               this.executor.execute(this);
            }
         }
      }
   }
}
