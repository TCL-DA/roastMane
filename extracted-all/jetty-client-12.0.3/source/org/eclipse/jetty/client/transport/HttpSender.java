package org.eclipse.jetty.client.transport;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Result;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.io.Content.Source;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IteratingCallback;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.IteratingCallback.Action;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpSender {
   private static final Logger LOG = LoggerFactory.getLogger(HttpSender.class);
   private final HttpSender.ContentSender contentSender = new HttpSender.ContentSender();
   private final AtomicReference<HttpSender.RequestState> requestState = new AtomicReference<>(HttpSender.RequestState.QUEUED);
   private final AtomicReference<Throwable> failure = new AtomicReference<>();
   private final HttpChannel channel;

   protected HttpSender(HttpChannel channel) {
      this.channel = channel;
   }

   protected HttpChannel getHttpChannel() {
      return this.channel;
   }

   protected HttpExchange getHttpExchange() {
      return this.channel.getHttpExchange();
   }

   public boolean isFailed() {
      return this.requestState.get() == HttpSender.RequestState.FAILURE;
   }

   public void send(HttpExchange exchange) {
      if (this.queuedToBegin(exchange)) {
         if (this.beginToHeaders(exchange)) {
            this.contentSender.iterate();
         }
      }
   }

   protected boolean expects100Continue(Request request) {
      return request.getHeaders().contains(HttpHeader.EXPECT, HttpHeaderValue.CONTINUE.asString());
   }

   protected boolean queuedToBegin(HttpExchange exchange) {
      if (!this.updateRequestState(HttpSender.RequestState.QUEUED, HttpSender.RequestState.TRANSIENT)) {
         return false;
      } else {
         HttpRequest request = exchange.getRequest();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Request begin {}", request);
         }

         request.notifyBegin();
         this.contentSender.exchange = exchange;
         this.contentSender.expect100 = this.expects100Continue(request);
         if (this.updateRequestState(HttpSender.RequestState.TRANSIENT, HttpSender.RequestState.BEGIN)) {
            return true;
         } else {
            this.abortRequest(exchange);
            return false;
         }
      }
   }

   protected boolean beginToHeaders(HttpExchange exchange) {
      if (!this.updateRequestState(HttpSender.RequestState.BEGIN, HttpSender.RequestState.TRANSIENT)) {
         return false;
      } else {
         HttpRequest request = exchange.getRequest();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Request headers {}{}{}", new Object[]{request, System.lineSeparator(), request.getHeaders().toString().trim()});
         }

         request.notifyHeaders();
         if (this.updateRequestState(HttpSender.RequestState.TRANSIENT, HttpSender.RequestState.HEADERS)) {
            return true;
         } else {
            this.abortRequest(exchange);
            return false;
         }
      }
   }

   protected boolean headersToCommit(HttpExchange exchange) {
      if (!this.updateRequestState(HttpSender.RequestState.HEADERS, HttpSender.RequestState.TRANSIENT)) {
         return false;
      } else {
         HttpRequest request = exchange.getRequest();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Request committed {}", request);
         }

         request.notifyCommit();
         if (this.updateRequestState(HttpSender.RequestState.TRANSIENT, HttpSender.RequestState.COMMIT)) {
            return true;
         } else {
            this.abortRequest(exchange);
            return false;
         }
      }
   }

   protected boolean someToContent(HttpExchange exchange, ByteBuffer content) {
      HttpSender.RequestState current = this.requestState.get();

      return switch (current) {
         case COMMIT, CONTENT -> {
            if (!this.updateRequestState(current, HttpSender.RequestState.TRANSIENT)) {
               yield false;
            } else {
               HttpRequest request = exchange.getRequest();
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Request content {}{}{}", new Object[]{request, System.lineSeparator(), BufferUtil.toDetailString(content)});
               }

               request.notifyContent(content);
               if (this.updateRequestState(HttpSender.RequestState.TRANSIENT, HttpSender.RequestState.CONTENT)) {
                  yield true;
               } else {
                  this.abortRequest(exchange);
                  yield false;
               }
            }
         }
         default -> false;
      };
   }

   protected boolean someToSuccess(HttpExchange exchange) {
      HttpSender.RequestState current = this.requestState.get();

      return switch (current) {
         case COMMIT, CONTENT -> {
            if (!exchange.requestComplete(null)) {
               yield false;
            } else {
               this.requestState.set(HttpSender.RequestState.QUEUED);
               this.reset();
               HttpRequest request = exchange.getRequest();
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Request success {}", request);
               }

               request.notifySuccess();
               Result result = exchange.terminateRequest();
               this.terminateRequest(exchange, null, result);
               yield true;
            }
         }
         default -> false;
      };
   }

   private void anyToFailure(Throwable failure) {
      HttpExchange exchange = this.getHttpExchange();
      if (exchange != null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Request failure {}", exchange.getRequest(), failure);
         }

         if (exchange.requestComplete(failure)) {
            this.executeAbort(exchange, failure);
         }
      }
   }

   private void executeAbort(HttpExchange exchange, Throwable failure) {
      try {
         Executor executor = this.getHttpChannel().getHttpDestination().getHttpClient().getExecutor();
         executor.execute(() -> this.abort(exchange, failure, Promise.noop()));
      } catch (RejectedExecutionException var4) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Exchange aborted {}", exchange, var4);
         }

         this.abort(exchange, failure, Promise.noop());
      }
   }

   private void abortRequest(HttpExchange exchange) {
      Throwable failure = this.failure.get();
      HttpRequest request = exchange.getRequest();
      Source content = request.getBody();
      if (content != null) {
         content.fail(failure);
      }

      this.dispose();
      if (LOG.isDebugEnabled()) {
         LOG.debug("Request abort {} {} on {}: {}", new Object[]{request, exchange, this.getHttpChannel(), failure});
      }

      request.notifyFailure(failure);
      Result result = exchange.terminateRequest();
      this.terminateRequest(exchange, failure, result);
   }

   private void terminateRequest(HttpExchange exchange, Throwable failure, Result result) {
      HttpRequest request = exchange.getRequest();
      if (LOG.isDebugEnabled()) {
         LOG.debug("Terminating request {}", request);
      }

      if (result == null) {
         if (failure != null && exchange.responseComplete(failure)) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Response failure from request {} {}", request, exchange);
            }

            this.getHttpChannel().abortResponse(exchange, failure, Promise.noop());
         }
      } else {
         result = this.channel.exchangeTerminating(exchange, result);
         HttpDestination destination = this.getHttpChannel().getHttpDestination();
         boolean ordered = destination.getHttpClient().isStrictEventOrdering();
         if (!ordered) {
            this.channel.exchangeTerminated(exchange, result);
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("Request/Response {}: {}", failure == null ? "succeeded" : "failed", result);
         }

         exchange.getConversation().getResponseListeners().notifyComplete(result);
         if (ordered) {
            this.channel.exchangeTerminated(exchange, result);
         }
      }
   }

   protected abstract void sendHeaders(HttpExchange var1, ByteBuffer var2, boolean var3, Callback var4);

   protected abstract void sendContent(HttpExchange var1, ByteBuffer var2, boolean var3, Callback var4);

   protected void reset() {
      this.contentSender.reset();
   }

   protected void dispose() {
   }

   public void proceed(HttpExchange exchange, Throwable failure) {
      if (this.contentSender.expect100) {
         this.contentSender.expect100 = false;
         if (failure == null) {
            this.contentSender.iterate();
         } else {
            this.anyToFailure(failure);
         }
      }
   }

   public void abort(HttpExchange exchange, Throwable failure, Promise<Boolean> promise) {
      this.failure.compareAndSet(null, failure);

      HttpSender.RequestState current;
      do {
         current = this.requestState.get();
         if (current == HttpSender.RequestState.FAILURE) {
            promise.succeeded(false);
            return;
         }
      } while (!this.updateRequestState(current, HttpSender.RequestState.FAILURE));

      boolean abort = current != HttpSender.RequestState.TRANSIENT;
      if (abort) {
         this.abortRequest(exchange);
         promise.succeeded(true);
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Concurrent failure: request termination skipped, performed by helpers");
         }

         promise.succeeded(false);
      }
   }

   private boolean updateRequestState(HttpSender.RequestState from, HttpSender.RequestState to) {
      boolean updated = this.requestState.compareAndSet(from, to);
      if (!updated && LOG.isDebugEnabled()) {
         LOG.debug("RequestState update failed: {} -> {}: {}", new Object[]{from, to, this.requestState.get()});
      }

      return updated;
   }

   protected String relativize(String path) {
      try {
         String result = path;
         URI uri = URI.create(path);
         if (uri.isAbsolute()) {
            result = uri.getPath();
         }

         return result.isEmpty() ? "/" : result;
      } catch (Throwable var4) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Could not relativize {}", path);
         }

         return path;
      }
   }

   @Override
   public String toString() {
      return String.format("%s@%x(req=%s,failure=%s)", this.getClass().getSimpleName(), this.hashCode(), this.requestState, this.failure);
   }

   private class ContentSender extends IteratingCallback {
      private HttpExchange exchange;
      private Chunk chunk;
      private ByteBuffer contentBuffer;
      private boolean expect100;
      private boolean committed;
      private boolean success;
      private boolean complete;

      public boolean reset() {
         this.exchange = null;
         this.chunk = null;
         this.contentBuffer = null;
         this.expect100 = false;
         this.committed = false;
         this.success = false;
         this.complete = false;
         return super.reset();
      }

      protected Action process() throws Throwable {
         if (this.complete) {
            if (this.success) {
               HttpSender.this.someToSuccess(this.exchange);
            }

            return Action.IDLE;
         } else {
            HttpRequest request = this.exchange.getRequest();
            Source content = request.getBody();
            if (this.expect100) {
               if (this.committed) {
                  return Action.IDLE;
               }

               this.chunk = null;
            } else {
               this.chunk = content.read();
            }

            if (HttpSender.LOG.isDebugEnabled()) {
               HttpSender.LOG.debug("Content {} for {}", this.chunk, request);
            }

            if (this.chunk == null) {
               if (this.committed) {
                  content.demand(this::iterate);
                  return Action.IDLE;
               }

               this.chunk = Chunk.EMPTY;
            }

            if (Chunk.isFailure(this.chunk)) {
               throw this.chunk.getFailure();
            } else {
               ByteBuffer buffer = this.chunk.getByteBuffer();
               this.contentBuffer = buffer.asReadOnlyBuffer();
               boolean last = this.chunk.isLast();
               if (this.committed) {
                  HttpSender.this.sendContent(this.exchange, buffer, last, this);
               } else {
                  HttpSender.this.sendHeaders(this.exchange, buffer, last, this);
               }

               return Action.SCHEDULED;
            }
         }
      }

      public void succeeded() {
         boolean proceed = true;
         if (this.committed) {
            if (this.contentBuffer.hasRemaining()) {
               proceed = HttpSender.this.someToContent(this.exchange, this.contentBuffer);
            }
         } else {
            this.committed = true;
            if (HttpSender.this.headersToCommit(this.exchange)) {
               if (this.contentBuffer.hasRemaining()) {
                  proceed = HttpSender.this.someToContent(this.exchange, this.contentBuffer);
               }
            } else {
               proceed = false;
            }
         }

         boolean last = this.chunk.isLast();
         this.chunk.release();
         this.chunk = null;
         if (proceed) {
            if (last) {
               this.success = true;
               this.complete = true;
            } else if (this.expect100 && HttpSender.LOG.isDebugEnabled()) {
               HttpSender.LOG.debug("Expecting 100 Continue for {}", this.exchange.getRequest());
            }
         } else {
            this.complete = true;
         }

         super.succeeded();
      }

      public void failed(Throwable x) {
         if (this.chunk != null) {
            this.chunk.release();
            this.chunk = null;
         }

         HttpRequest request = this.exchange.getRequest();
         Source content = request.getBody();
         if (content != null) {
            content.fail(x);
         }

         HttpSender.this.anyToFailure(x);
         super.failed(x);
      }

      public InvocationType getInvocationType() {
         return InvocationType.NON_BLOCKING;
      }
   }

   private static enum RequestState {
      TRANSIENT,
      QUEUED,
      BEGIN,
      HEADERS,
      COMMIT,
      CONTENT,
      FAILURE;
   }
}
