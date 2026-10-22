package org.eclipse.jetty.server.handler;

import java.time.Duration;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.io.CyclicTimeouts;
import org.eclipse.jetty.io.CyclicTimeouts.Expirable;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpStream;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.ProcessorUtils;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ThreadPool.SizedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public class QoSHandler extends ConditionalHandler.Abstract {
   private static final Logger LOG = LoggerFactory.getLogger(QoSHandler.class);
   private static final String EXPIRED_ATTRIBUTE_NAME = QoSHandler.class.getName() + ".expired";
   private final AtomicInteger state = new AtomicInteger();
   private final Map<Integer, Queue<QoSHandler.Entry>> queues = new ConcurrentHashMap<>();
   private final Set<Integer> priorities = new ConcurrentSkipListSet<>(Comparator.reverseOrder());
   private CyclicTimeouts<QoSHandler.Entry> timeouts;
   private int maxRequests;
   private Duration maxSuspend = Duration.ZERO;

   public QoSHandler() {
      this(null);
   }

   public QoSHandler(Handler handler) {
      super(false, handler);
   }

   @ManagedAttribute(
      value = "The maximum number of concurrent requests",
      readonly = true
   )
   public int getMaxRequestCount() {
      return this.maxRequests;
   }

   public void setMaxRequestCount(int maxRequests) {
      if (this.isStarted()) {
         throw new IllegalStateException("Cannot change maxRequests: " + this);
      } else {
         this.maxRequests = maxRequests;
      }
   }

   public Duration getMaxSuspend() {
      return this.maxSuspend;
   }

   public void setMaxSuspend(Duration maxSuspend) {
      if (maxSuspend.isNegative()) {
         throw new IllegalArgumentException("Invalid maxSuspend duration");
      } else {
         this.maxSuspend = maxSuspend;
      }
   }

   @ManagedAttribute("The number of suspended requests")
   public long getSuspendedRequestCount() {
      int permits = this.state.get();
      return (long)Math.max(0, -permits);
   }

   @Override
   protected void doStart() throws Exception {
      this.timeouts = new QoSHandler.Timeouts(this.getServer().getScheduler());
      this.addBean(this.timeouts);
      int maxRequests = this.getMaxRequestCount();
      if (maxRequests <= 0) {
         if (this.getServer().getThreadPool() instanceof SizedThreadPool sized) {
            maxRequests = sized.getMaxThreads() / 2;
         } else {
            maxRequests = ProcessorUtils.availableProcessors();
         }

         this.setMaxRequestCount(maxRequests);
      }

      this.state.set(maxRequests);
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} initialized maxRequests={}", this, maxRequests);
      }

      super.doStart();
   }

   @Override
   protected void doStop() throws Exception {
      super.doStop();
      this.removeBean(this.timeouts);
      this.timeouts.destroy();
   }

   @Override
   public boolean onConditionsMet(Request request, Response response, Callback callback) throws Exception {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} handling {}", this, request);
      }

      int permits = this.state.getAndDecrement();
      if (permits > 0) {
         return this.handleWithPermit(request, response, callback);
      } else {
         if (request.getAttribute(EXPIRED_ATTRIBUTE_NAME) != null) {
            this.state.getAndIncrement();
            notAvailable(response, callback);
         } else {
            this.suspend(request, response, callback);
         }

         return true;
      }
   }

   @Override
   protected boolean onConditionsNotMet(Request request, Response response, Callback callback) throws Exception {
      return this.nextHandler(request, response, callback);
   }

   private static void notAvailable(Response response, Callback callback) {
      response.setStatus(503);
      if (response.isCommitted()) {
         callback.failed(new IllegalStateException("Response already committed"));
      } else {
         response.write(true, null, callback);
      }
   }

   protected int getPriority(Request request) {
      return 0;
   }

   protected void failSuspended(Request request, Response response, Callback callback, int status, Throwable failure) {
      Response.writeError(request, response, callback, status, null, failure);
   }

   private boolean handleWithPermit(Request request, Response response, Callback callback) throws Exception {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} forwarding {}", this, request);
      }

      request.addHttpStreamWrapper(stream -> new QoSHandler.Resumer(stream, request));
      return this.nextHandler(request, response, callback);
   }

   private void suspend(Request request, Response response, Callback callback) {
      int priority = Math.max(0, this.getPriority(request));
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} suspending priority={} {}", new Object[]{this, priority, request});
      }

      QoSHandler.Entry entry = new QoSHandler.Entry(request, response, callback, priority);
      this.queues.compute(priority, (k, v) -> {
         if (v == null) {
            this.priorities.add(priority);
            v = new ConcurrentLinkedQueue<>();
         }

         v.offer(entry);
         return v;
      });
      this.timeouts.schedule(entry);
   }

   private void resume() {
      int permits = this.state.getAndIncrement();
      if (permits >= 0) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} no suspended requests to resume", this);
         }
      } else {
         while (!this.resumeSuspended()) {
            Thread.onSpinWait();
         }
      }
   }

   private boolean resumeSuspended() {
      for (Integer priority : this.priorities) {
         Queue<QoSHandler.Entry> queue = this.queues.get(priority);
         if (queue == null) {
            return false;
         }

         QoSHandler.Entry entry = queue.poll();
         if (entry != null) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("{} resuming {}", this, entry.request);
            }

            this.getServer().getThreadPool().execute(entry);
            return true;
         }
      }

      return false;
   }

   private class Entry implements Expirable, Runnable {
      private final Request request;
      private final Response response;
      private final Callback callback;
      private final int priority;
      private final long expireNanoTime;

      private Entry(Request request, Response response, Callback callback, int priority) {
         this.request = request;
         this.response = response;
         this.callback = callback;
         this.priority = priority;
         Duration maxSuspend = QoSHandler.this.getMaxSuspend();
         long suspendNanos = NanoTime.now() + maxSuspend.toNanos();
         if (suspendNanos == Long.MAX_VALUE) {
            suspendNanos--;
         }

         this.expireNanoTime = maxSuspend.isZero() ? Long.MAX_VALUE : suspendNanos;
      }

      public long getExpireNanoTime() {
         return this.expireNanoTime;
      }

      private void expire() {
         boolean removed = QoSHandler.this.queues.get(this.priority).remove(this);
         if (removed) {
            QoSHandler.this.state.getAndIncrement();
            if (QoSHandler.LOG.isDebugEnabled()) {
               QoSHandler.LOG.debug("{} timeout {}", QoSHandler.this, this.request);
            }

            this.request.setAttribute(QoSHandler.EXPIRED_ATTRIBUTE_NAME, true);
            QoSHandler.this.failSuspended(this.request, this.response, this.callback, 503, new TimeoutException());
         }
      }

      @Override
      public void run() {
         try {
            boolean handled = QoSHandler.this.handleWithPermit(this.request, this.response, this.callback);
            if (QoSHandler.LOG.isDebugEnabled()) {
               QoSHandler.LOG.debug("{} handled={} {}", new Object[]{QoSHandler.this, handled, this.request});
            }

            if (!handled) {
               QoSHandler.this.failSuspended(this.request, this.response, this.callback, 404, null);
            }
         } catch (Throwable var2) {
            if (QoSHandler.LOG.isDebugEnabled()) {
               QoSHandler.LOG.debug("{} failed {}", new Object[]{QoSHandler.this, this.request, var2});
            }

            QoSHandler.this.failSuspended(this.request, this.response, this.callback, 500, var2);
         }
      }
   }

   private class Resumer extends HttpStream.Wrapper {
      private final Request request;

      private Resumer(HttpStream wrapped, Request request) {
         super(wrapped);
         this.request = request;
      }

      @Override
      public void succeeded() {
         if (QoSHandler.LOG.isDebugEnabled()) {
            QoSHandler.LOG.debug("{} succeeded {}", QoSHandler.this, this.request);
         }

         QoSHandler.this.resume();
      }

      @Override
      public void failed(Throwable x) {
         if (QoSHandler.LOG.isDebugEnabled()) {
            QoSHandler.LOG.debug("{} failed {}", new Object[]{QoSHandler.this, this.request, x});
         }

         QoSHandler.this.resume();
      }
   }

   private class Timeouts extends CyclicTimeouts<QoSHandler.Entry> {
      private Timeouts(Scheduler scheduler) {
         super(scheduler);
      }

      protected Iterator<QoSHandler.Entry> iterator() {
         return QoSHandler.this.queues.values().stream().flatMap(java.util.Collection::stream).iterator();
      }

      protected boolean onExpired(QoSHandler.Entry entry) {
         entry.expire();
         return false;
      }
   }
}
