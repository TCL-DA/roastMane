package org.eclipse.jetty.server.handler;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.WritePendingException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.eclipse.jetty.http.HostPortHttpField;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.QuotedCSV;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadLimitHandler extends ConditionalHandler.Abstract {
   private static final Logger LOG = LoggerFactory.getLogger(ThreadLimitHandler.class);
   private final boolean _rfc7239;
   private final String _forwardedHeader;
   private final ConcurrentMap<String, ThreadLimitHandler.Remote> _remotes = new ConcurrentHashMap<>();
   private volatile boolean _enabled;
   private int _threadLimit = 10;

   public ThreadLimitHandler() {
      this(null, null, true);
   }

   public ThreadLimitHandler(@Name("forwardedHeader") String forwardedHeader) {
      this(null, forwardedHeader, HttpHeader.FORWARDED.is(forwardedHeader));
   }

   public ThreadLimitHandler(@Name("forwardedHeader") String forwardedHeader, @Name("rfc7239") boolean rfc7239) {
      this(null, forwardedHeader, rfc7239);
   }

   public ThreadLimitHandler(@Name("handler") Handler handler, @Name("forwardedHeader") String forwardedHeader, @Name("rfc7239") boolean rfc7239) {
      super(handler);
      this._rfc7239 = rfc7239;
      this._forwardedHeader = forwardedHeader;
      this._enabled = true;
   }

   @Override
   protected void doStart() throws Exception {
      super.doStart();
      LOG.info(String.format("ThreadLimitHandler enable=%b limit=%d", this._enabled, this._threadLimit));
   }

   @ManagedAttribute("true if this handler is enabled")
   public boolean isEnabled() {
      return this._enabled;
   }

   public void setEnabled(boolean enabled) {
      this._enabled = enabled;
      LOG.info(String.format("ThreadLimitHandler enable=%b limit=%d", this._enabled, this._threadLimit));
   }

   @ManagedAttribute("The maximum threads that can be dispatched per remote IP")
   public int getThreadLimit() {
      return this._threadLimit;
   }

   protected int getThreadLimit(String ip) {
      return this._threadLimit;
   }

   public void setThreadLimit(int threadLimit) {
      if (threadLimit <= 0) {
         throw new IllegalArgumentException("limit must be >0");
      } else {
         this._threadLimit = threadLimit;
      }
   }

   @ManagedOperation("Include IP in thread limits")
   public void include(String inetAddressPattern) {
      this.includeInetAddressPattern(new String[]{inetAddressPattern});
   }

   @ManagedOperation("Exclude IP from thread limits")
   public void exclude(String inetAddressPattern) {
      this.excludeInetAddressPattern(new String[]{inetAddressPattern});
   }

   @Override
   public boolean onConditionsMet(Request request, Response response, Callback callback) throws Exception {
      Handler next = this.getHandler();
      if (next == null) {
         return false;
      } else if (!this._enabled) {
         return next.handle(request, response, callback);
      } else {
         ThreadLimitHandler.Remote remote = this.getRemote(request);
         if (remote == null) {
            return next.handle(request, response, callback);
         } else {
            ThreadLimitHandler.LimitedRequest limitedRequest = new ThreadLimitHandler.LimitedRequest(remote, next, request, response, callback);
            limitedRequest.handle();
            return true;
         }
      }
   }

   @Override
   protected boolean onConditionsNotMet(Request request, Response response, Callback callback) throws Exception {
      return this.nextHandler(request, response, callback);
   }

   private ThreadLimitHandler.Remote getRemote(Request baseRequest) {
      String ip = this.getRemoteIP(baseRequest);
      LOG.debug("ip={}", ip);
      if (ip == null) {
         return null;
      } else {
         int limit = this.getThreadLimit(ip);
         if (limit <= 0) {
            return null;
         } else {
            ThreadLimitHandler.Remote remote = this._remotes.get(ip);
            if (remote == null) {
               ThreadLimitHandler.Remote r = new ThreadLimitHandler.Remote(baseRequest.getContext(), ip, limit);
               remote = this._remotes.putIfAbsent(ip, r);
               if (remote == null) {
                  remote = r;
               }
            }

            return remote;
         }
      }
   }

   protected String getRemoteIP(Request baseRequest) {
      if (this._forwardedHeader != null && !this._forwardedHeader.isEmpty()) {
         String remote = this._rfc7239 ? this.getForwarded(baseRequest) : this.getXForwardedFor(baseRequest);
         if (remote != null && !remote.isEmpty()) {
            return remote;
         }
      }

      if (baseRequest.getConnectionMetaData().getRemoteSocketAddress() instanceof InetSocketAddress inetAddr && inetAddr.getAddress() != null) {
         return inetAddr.getAddress().getHostAddress();
      }

      return null;
   }

   private String getForwarded(Request request) {
      ThreadLimitHandler.RFC7239 rfc7239 = new ThreadLimitHandler.RFC7239();

      for (HttpField field : request.getHeaders()) {
         if (this._forwardedHeader.equalsIgnoreCase(field.getName())) {
            rfc7239.addValue(field.getValue());
         }
      }

      return rfc7239.getFor() != null ? new HostPortHttpField(rfc7239.getFor()).getHost() : null;
   }

   private String getXForwardedFor(Request request) {
      String forwardedFor = null;

      for (HttpField field : request.getHeaders()) {
         if (this._forwardedHeader.equalsIgnoreCase(field.getName())) {
            forwardedFor = field.getValue();
         }
      }

      if (forwardedFor != null && !forwardedFor.isEmpty()) {
         int comma = forwardedFor.lastIndexOf(44);
         return comma >= 0 ? forwardedFor.substring(comma + 1).trim() : forwardedFor;
      } else {
         return null;
      }
   }

   private static class AllocatedPermit implements ThreadLimitHandler.Permit {
      private final ThreadLimitHandler.Remote _remote;

      private AllocatedPermit(ThreadLimitHandler.Remote remote) {
         this._remote = remote;
      }

      @Override
      public boolean isAllocated() {
         return true;
      }

      @Override
      public void whenAllocated(Consumer<ThreadLimitHandler.Permit> permitConsumer) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void release() {
         this._remote.release();
      }

      @Override
      public String toString() {
         return "AllocatedPermit:" + this._remote;
      }
   }

   private static class FuturePermit implements ThreadLimitHandler.Permit {
      private final CompletableFuture<ThreadLimitHandler.Permit> _future = new CompletableFuture<>();
      private final ThreadLimitHandler.Remote _remote;

      private FuturePermit(ThreadLimitHandler.Remote remote) {
         this._remote = remote;
      }

      @Override
      public boolean isAllocated() {
         return this._future.isDone();
      }

      @Override
      public void whenAllocated(Consumer<ThreadLimitHandler.Permit> permitConsumer) {
         this._future.thenAccept(permitConsumer);
      }

      void complete() {
         if (!this._future.complete(this)) {
            throw new IllegalStateException();
         }
      }

      @Override
      public void release() {
         this._remote.release();
      }
   }

   private static class LimitedRequest extends Request.Wrapper {
      private final ThreadLimitHandler.Remote _remote;
      private final Request.Handler _handler;
      private final ThreadLimitHandler.LimitedResponse _response;
      private final Callback _callback;
      private final AtomicReference<Runnable> _onContent = new AtomicReference<>();

      public LimitedRequest(ThreadLimitHandler.Remote remote, Request.Handler handler, Request request, Response response, Callback callback) {
         super(request);
         this._remote = remote;
         this._handler = Objects.requireNonNull(handler);
         this._response = new ThreadLimitHandler.LimitedResponse(this, response);
         this._callback = Objects.requireNonNull(callback);
      }

      protected Request.Handler getHandler() {
         return this._handler;
      }

      protected Response getResponse() {
         return this._response;
      }

      protected Callback getCallback() {
         return this._callback;
      }

      protected void handle() throws Exception {
         ThreadLimitHandler.Permit permit = this._remote.acquire();
         if (permit.isAllocated()) {
            if (ThreadLimitHandler.LOG.isDebugEnabled()) {
               ThreadLimitHandler.LOG.debug("Thread permitted {} {} {}", new Object[]{this._remote, this.getWrapped(), this._handler});
            }

            this.handle(permit);
         } else {
            if (ThreadLimitHandler.LOG.isDebugEnabled()) {
               ThreadLimitHandler.LOG.debug("Thread limited {} {} {}", new Object[]{this._remote, this.getWrapped(), this._handler});
            }

            permit.whenAllocated(this::handle);
         }
      }

      protected void handle(ThreadLimitHandler.Permit permit) {
         try {
            if (!this._handler.handle(this, this._response, this._callback)) {
               Response.writeError(this, this._response, this._callback, 404);
            }
         } catch (Throwable var6) {
            this._callback.failed(var6);
         } finally {
            permit.release();
         }
      }

      @Override
      public void demand(Runnable onContent) {
         if (!this._onContent.compareAndSet(null, Objects.requireNonNull(onContent))) {
            throw new IllegalStateException("Pending demand");
         } else {
            super.demand(this::onContent);
         }
      }

      private void onContent() {
         ThreadLimitHandler.Permit permit = this._remote.acquire();
         if (permit.isAllocated()) {
            this.onPermittedContent(permit);
         } else {
            permit.whenAllocated(this::onPermittedContent);
         }
      }

      private void onPermittedContent(ThreadLimitHandler.Permit permit) {
         try {
            Runnable onContent = this._onContent.getAndSet(null);
            onContent.run();
         } finally {
            permit.release();
         }
      }
   }

   private static class LimitedResponse extends Response.Wrapper implements Callback {
      private final ThreadLimitHandler.Remote _remote;
      private final AtomicReference<Callback> _writeCallback = new AtomicReference<>();

      public LimitedResponse(ThreadLimitHandler.LimitedRequest limitedRequest, Response response) {
         super(limitedRequest, response);
         this._remote = limitedRequest._remote;
      }

      @Override
      public void write(boolean last, ByteBuffer byteBuffer, Callback callback) {
         if (!this._writeCallback.compareAndSet(null, Objects.requireNonNull(callback))) {
            throw new WritePendingException();
         } else {
            super.write(last, byteBuffer, this);
         }
      }

      public void succeeded() {
         ThreadLimitHandler.Permit permit = this._remote.acquire();
         if (permit.isAllocated()) {
            this.permittedSuccess(permit);
         } else {
            permit.whenAllocated(this::permittedSuccess);
         }
      }

      private void permittedSuccess(ThreadLimitHandler.Permit permit) {
         try {
            this._writeCallback.getAndSet(null).succeeded();
         } finally {
            permit.release();
         }
      }

      public void failed(Throwable x) {
         ThreadLimitHandler.Permit permit = this._remote.acquire();
         if (permit.isAllocated()) {
            this.permittedFailure(permit, x);
         } else {
            permit.whenAllocated(p -> this.permittedFailure(p, x));
         }
      }

      private void permittedFailure(ThreadLimitHandler.Permit permit, Throwable x) {
         try {
            this._writeCallback.getAndSet(null).failed(x);
         } finally {
            permit.release();
         }
      }
   }

   private static class NoopPermit implements ThreadLimitHandler.Permit {
      @Override
      public boolean isAllocated() {
         return true;
      }

      @Override
      public void whenAllocated(Consumer<ThreadLimitHandler.Permit> permitConsumer) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void release() {
      }
   }

   private interface Permit {
      boolean isAllocated();

      void whenAllocated(Consumer<ThreadLimitHandler.Permit> var1);

      void release();
   }

   private static final class RFC7239 extends QuotedCSV {
      String _for;

      private RFC7239() {
         super(false, new String[0]);
      }

      String getFor() {
         return this._for;
      }

      protected void parsedParam(StringBuffer buffer, int valueLength, int paramName, int paramValue) {
         if (valueLength == 0 && paramValue > paramName) {
            String name = StringUtil.asciiToLowerCase(buffer.substring(paramName, paramValue - 1));
            if ("for".equalsIgnoreCase(name)) {
               String value = buffer.substring(paramValue);
               if ("unknown".equalsIgnoreCase(value)) {
                  this._for = null;
               } else {
                  this._for = value;
               }
            }
         }
      }
   }

   private static final class Remote {
      private final Executor _executor;
      private final String _ip;
      private final int _limit;
      private final AutoLock _lock = new AutoLock();
      private int _permits;
      private final Deque<ThreadLimitHandler.FuturePermit> _queue = new ArrayDeque<>();
      private final ThreadLimitHandler.Permit _permitted = new ThreadLimitHandler.AllocatedPermit(this);
      private final ThreadLocal<Boolean> _threadPermit = new ThreadLocal<>();
      private static final ThreadLimitHandler.Permit NOOP = new ThreadLimitHandler.NoopPermit();

      public Remote(Executor executor, String ip, int limit) {
         this._executor = executor;
         this._ip = ip;
         this._limit = limit;
      }

      ThreadLimitHandler.Permit acquire() {
         AutoLock lock = this._lock.lock();

         ThreadLimitHandler.Permit var7;
         label55: {
            label56: {
               ThreadLimitHandler.FuturePermit var3;
               try {
                  if (this._threadPermit.get() == Boolean.TRUE) {
                     var7 = NOOP;
                     break label55;
                  }

                  if (this._permits < this._limit) {
                     this._permits++;
                     this._threadPermit.set(Boolean.TRUE);
                     var7 = this._permitted;
                     break label56;
                  }

                  ThreadLimitHandler.FuturePermit futurePermit = new ThreadLimitHandler.FuturePermit(this);
                  this._queue.addLast(futurePermit);
                  var3 = futurePermit;
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

               return var3;
            }

            if (lock != null) {
               lock.close();
            }

            return var7;
         }

         if (lock != null) {
            lock.close();
         }

         return var7;
      }

      public void release() {
         AutoLock lock = this._lock.lock();

         ThreadLimitHandler.FuturePermit pending;
         try {
            this._permits--;
            this._threadPermit.set(Boolean.FALSE);
            pending = this._queue.pollFirst();
            if (pending != null) {
               this._permits++;
            }
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

         if (pending != null) {
            this._executor.execute(pending::complete);
         }
      }

      @Override
      public String toString() {
         AutoLock lock = this._lock.lock();

         String var2;
         try {
            var2 = String.format("R[ip=%s,p=%d,l=%d,q=%d]", this._ip, this._permits, this._limit, this._queue.size());
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
   }
}
