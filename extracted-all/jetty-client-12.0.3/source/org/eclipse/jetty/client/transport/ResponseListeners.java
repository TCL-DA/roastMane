package org.eclipse.jetty.client.transport;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.client.ContentResponse;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.client.Result;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.io.Content.Source;
import org.eclipse.jetty.io.content.ByteBufferContentSource;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseListeners {
   private static final Logger LOG = LoggerFactory.getLogger(ResponseListeners.class);
   private Response.BeginListener beginListener;
   private Response.HeaderListener headerListener;
   private Response.HeadersListener headersListener;
   private Response.ContentSourceListener contentSourceListener;
   private Response.SuccessListener successListener;
   private Response.FailureListener failureListener;
   private Response.CompleteListener completeListener;

   public ResponseListeners() {
   }

   public ResponseListeners(Response.Listener listener) {
      this.beginListener = listener;
      this.headerListener = listener;
      this.headersListener = listener;
      this.contentSourceListener = listener;
      this.successListener = listener;
      this.failureListener = listener;
      this.completeListener = listener;
   }

   public ResponseListeners(ResponseListeners that) {
      this.beginListener = that.beginListener;
      this.headerListener = that.headerListener;
      this.headersListener = that.headersListener;
      this.contentSourceListener = that.contentSourceListener;
      this.successListener = that.successListener;
      this.failureListener = that.failureListener;
      this.completeListener = that.completeListener;
   }

   public boolean addBeginListener(Response.BeginListener listener) {
      if (listener == null) {
         return false;
      } else {
         Response.BeginListener existing = this.beginListener;
         this.beginListener = existing == null ? listener : response -> {
            notifyBegin(existing, response);
            notifyBegin(listener, response);
         };
         return true;
      }
   }

   public void notifyBegin(Response response) {
      notifyBegin(this.beginListener, response);
   }

   private static void notifyBegin(Response.BeginListener listener, Response response) {
      try {
         if (listener != null) {
            listener.onBegin(response);
         }
      } catch (Throwable var3) {
         LOG.info("Exception while notifying listener {}", listener, var3);
      }
   }

   public boolean addHeaderListener(Response.HeaderListener listener) {
      if (listener == null) {
         return false;
      } else {
         Response.HeaderListener existing = this.headerListener;
         this.headerListener = existing == null ? listener : (response, field) -> {
            boolean r1 = notifyHeader(existing, response, field);
            boolean r2 = notifyHeader(listener, response, field);
            return r1 && r2;
         };
         return true;
      }
   }

   public boolean notifyHeader(Response response, HttpField field) {
      return notifyHeader(this.headerListener, response, field);
   }

   private static boolean notifyHeader(Response.HeaderListener listener, Response response, HttpField field) {
      try {
         return listener != null ? listener.onHeader(response, field) : true;
      } catch (Throwable var4) {
         LOG.info("Exception while notifying listener {}", listener, var4);
         return false;
      }
   }

   public boolean addHeadersListener(Response.HeadersListener listener) {
      if (listener == null) {
         return false;
      } else {
         Response.HeadersListener existing = this.headersListener;
         this.headersListener = existing == null ? listener : response -> {
            notifyHeaders(existing, response);
            notifyHeaders(listener, response);
         };
         return true;
      }
   }

   public void notifyHeaders(Response response) {
      notifyHeaders(this.headersListener, response);
   }

   private static void notifyHeaders(Response.HeadersListener listener, Response response) {
      try {
         if (listener != null) {
            listener.onHeaders(response);
         }
      } catch (Throwable var3) {
         LOG.info("Exception while notifying listener {}", listener, var3);
      }
   }

   public boolean addContentSourceListener(Response.ContentSourceListener listener) {
      if (listener == null) {
         return false;
      } else {
         Response.ContentSourceListener existing = this.contentSourceListener;
         if (existing == null) {
            this.contentSourceListener = listener;
         } else if (existing instanceof ResponseListeners.ContentSourceDemultiplexer demultiplexer) {
            demultiplexer.addContentSourceListener(listener);
         } else {
            ResponseListeners.ContentSourceDemultiplexer demultiplexer = new ResponseListeners.ContentSourceDemultiplexer();
            demultiplexer.addContentSourceListener(existing);
            demultiplexer.addContentSourceListener(listener);
            this.contentSourceListener = demultiplexer;
         }

         return true;
      }
   }

   public boolean hasContentSourceListeners() {
      return this.contentSourceListener != null;
   }

   public void notifyContentSource(Response response, Source contentSource) {
      if (this.hasContentSourceListeners()) {
         if (this.contentSourceListener instanceof ResponseListeners.ContentSourceDemultiplexer demultiplexer) {
            notifyContentSource(demultiplexer, response, contentSource);
         } else {
            notifyContentSource(this.contentSourceListener, response, contentSource);
         }
      } else {
         notifyContentSource((r, c) -> consume(c), response, contentSource);
      }
   }

   private static void consume(Source contentSource) {
      Chunk chunk = contentSource.read();
      if (chunk != null) {
         chunk.release();
      }

      if (chunk == null || !chunk.isLast()) {
         contentSource.demand(() -> consume(contentSource));
      }
   }

   private static void notifyContentSource(Response.ContentSourceListener listener, Response response, Source contentSource) {
      try {
         if (listener != null) {
            listener.onContentSource(response, contentSource);
         }
      } catch (Throwable var4) {
         LOG.info("Exception while notifying listener {}", listener, var4);
      }
   }

   public boolean addSuccessListener(Response.SuccessListener listener) {
      if (listener == null) {
         return false;
      } else {
         Response.SuccessListener existing = this.successListener;
         this.successListener = existing == null ? listener : response -> {
            notifySuccess(existing, response);
            notifySuccess(listener, response);
         };
         return true;
      }
   }

   public void notifySuccess(Response response) {
      notifySuccess(this.successListener, response);
   }

   private static void notifySuccess(Response.SuccessListener listener, Response response) {
      try {
         if (listener != null) {
            listener.onSuccess(response);
         }
      } catch (Throwable var3) {
         LOG.info("Exception while notifying listener {}", listener, var3);
      }
   }

   public boolean addFailureListener(Response.FailureListener listener) {
      if (listener == null) {
         return false;
      } else {
         Response.FailureListener existing = this.failureListener;
         this.failureListener = existing == null ? listener : (response, failure) -> {
            notifyFailure(existing, response, failure);
            notifyFailure(listener, response, failure);
         };
         return true;
      }
   }

   public void notifyFailure(Response response, Throwable failure) {
      notifyFailure(this.failureListener, response, failure);
   }

   private static void notifyFailure(Response.FailureListener listener, Response response, Throwable failure) {
      try {
         if (listener != null) {
            listener.onFailure(response, failure);
         }
      } catch (Throwable var4) {
         LOG.info("Exception while notifying listener {}", listener, var4);
      }
   }

   public boolean addCompleteListener(Response.CompleteListener listener, boolean includeOtherEvents) {
      if (listener == null) {
         return false;
      } else {
         if (includeOtherEvents) {
            if (listener instanceof Response.BeginListener l) {
               this.addBeginListener(l);
            }

            if (listener instanceof Response.HeaderListener l) {
               this.addHeaderListener(l);
            }

            if (listener instanceof Response.HeadersListener l) {
               this.addHeadersListener(l);
            }

            if (listener instanceof Response.ContentSourceListener l) {
               this.addContentSourceListener(l);
            }

            if (listener instanceof Response.SuccessListener l) {
               this.addSuccessListener(l);
            }

            if (listener instanceof Response.FailureListener l) {
               this.addFailureListener(l);
            }
         }

         Response.CompleteListener existing = this.completeListener;
         this.completeListener = existing == null ? listener : result -> {
            notifyComplete(existing, result);
            notifyComplete(listener, result);
         };
         return true;
      }
   }

   public void notifyComplete(Result result) {
      notifyComplete(this.completeListener, result);
   }

   private static void notifyComplete(Response.CompleteListener listener, Result result) {
      try {
         if (listener != null) {
            listener.onComplete(result);
         }
      } catch (Throwable var3) {
         LOG.info("Exception while notifying listener {}", listener, var3);
      }
   }

   public boolean addListener(Response.Listener listener) {
      return this.addBeginListener(listener)
         | this.addHeaderListener(listener)
         | this.addHeadersListener(listener)
         | this.addContentSourceListener(listener)
         | this.addSuccessListener(listener)
         | this.addFailureListener(listener)
         | this.addCompleteListener(listener, false);
   }

   public boolean addResponseListeners(ResponseListeners listeners) {
      return this.addBeginListener(listeners.beginListener)
         | this.addHeaderListener(listeners.headerListener)
         | this.addHeadersListener(listeners.headersListener)
         | this.addContentSourceListener(listeners.contentSourceListener)
         | this.addSuccessListener(listeners.successListener)
         | this.addFailureListener(listeners.failureListener)
         | this.addCompleteListener(listeners.completeListener, false);
   }

   private void emitEvents(Response response) {
      notifyBegin(this.beginListener, response);
      Iterator<HttpField> iterator = response.getHeaders().iterator();

      while (iterator.hasNext()) {
         HttpField field = iterator.next();
         if (!notifyHeader(this.headerListener, response, field)) {
            iterator.remove();
         }
      }

      notifyHeaders(this.headersListener, response);
      if (response instanceof ContentResponse contentResponse) {
         byte[] content = contentResponse.getContent();
         if (content != null && content.length > 0) {
            ByteBufferContentSource byteBufferContentSource = new ByteBufferContentSource(new ByteBuffer[]{ByteBuffer.wrap(content)});
            notifyContentSource(this.contentSourceListener, response, byteBufferContentSource);
         }
      }
   }

   public void emitSuccess(Response response) {
      this.emitEvents(response);
      notifySuccess(this.successListener, response);
   }

   public void emitFailure(Response response, Throwable failure) {
      this.emitEvents(response);
      notifyFailure(this.failureListener, response, failure);
   }

   public void emitSuccessComplete(Result result) {
      this.emitSuccess(result.getResponse());
      notifyComplete(this.completeListener, result);
   }

   public void emitFailureComplete(Result result) {
      this.emitFailure(result.getResponse(), result.getFailure());
      notifyComplete(this.completeListener, result);
   }

   private static class ContentSourceDemultiplexer implements Response.ContentSourceListener {
      private static final Logger LOG = LoggerFactory.getLogger(ResponseListeners.ContentSourceDemultiplexer.class);
      private final AutoLock lock = new AutoLock();
      private final List<Response.ContentSourceListener> listeners = new ArrayList<>(2);
      private final List<ResponseListeners.ContentSourceDemultiplexer.ContentSource> contentSources = new ArrayList<>(2);
      private Source originalContentSource;

      private void addContentSourceListener(Response.ContentSourceListener listener) {
         this.listeners.add(listener);
      }

      @Override
      public void onContentSource(Response response, Source contentSource) {
         this.originalContentSource = contentSource;

         for (int i = 0; i < this.listeners.size(); i++) {
            Response.ContentSourceListener listener = this.listeners.get(i);
            ResponseListeners.ContentSourceDemultiplexer.ContentSource cs = new ResponseListeners.ContentSourceDemultiplexer.ContentSource(i);
            this.contentSources.add(cs);
            ResponseListeners.notifyContentSource(listener, response, cs);
         }
      }

      private ResponseListeners.ContentSourceDemultiplexer.Counters countStates() {
         assert this.lock.isHeldByCurrentThread();

         int demands = 0;
         int failures = 0;

         for (ResponseListeners.ContentSourceDemultiplexer.ContentSource contentSource : this.contentSources) {
            switch (contentSource.state) {
               case DEMANDED:
                  demands++;
                  break;
               case FAILED:
                  failures++;
            }
         }

         return new ResponseListeners.ContentSourceDemultiplexer.Counters(demands, failures);
      }

      private void resetDemands() {
         assert this.lock.isHeldByCurrentThread();

         for (ResponseListeners.ContentSourceDemultiplexer.ContentSource contentSource : this.contentSources) {
            if (contentSource.state == ResponseListeners.ContentSourceDemultiplexer.State.DEMANDED) {
               contentSource.state = ResponseListeners.ContentSourceDemultiplexer.State.IDLE;
            }
         }
      }

      private void onDemandCallback() {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Original content source's demand calling back");
         }

         Chunk chunk = this.originalContentSource.read();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Read from original content source {}", chunk);
         }

         if (chunk == null) {
            this.originalContentSource.demand(this::onDemandCallback);
         } else {
            for (ResponseListeners.ContentSourceDemultiplexer.ContentSource demultiplexerContentSource : this.contentSources) {
               demultiplexerContentSource.onChunk(chunk);
            }

            chunk.release();
         }
      }

      private void registerFailure(ResponseListeners.ContentSourceDemultiplexer.ContentSource contentSource, Throwable failure) {
         boolean processFail = false;
         boolean processDemand = false;
         AutoLock ignored = this.lock.lock();

         ResponseListeners.ContentSourceDemultiplexer.Counters counters;
         try {
            contentSource.state = ResponseListeners.ContentSourceDemultiplexer.State.FAILED;
            counters = this.countStates();
            if (counters.failures() == this.listeners.size()) {
               processFail = true;
            } else if (counters.total() == this.listeners.size()) {
               this.resetDemands();
               processDemand = true;
            }
         } catch (Throwable var10) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (ignored != null) {
            ignored.close();
         }

         if (processFail) {
            this.originalContentSource.fail(failure);
         } else if (processDemand) {
            this.originalContentSource.demand(this::onDemandCallback);
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("Registered failure on {}; {}", contentSource, counters);
         }
      }

      private void registerDemand(ResponseListeners.ContentSourceDemultiplexer.ContentSource contentSource) {
         boolean processDemand = false;
         AutoLock ignored = this.lock.lock();

         label52: {
            ResponseListeners.ContentSourceDemultiplexer.Counters counters;
            try {
               if (contentSource.state != ResponseListeners.ContentSourceDemultiplexer.State.IDLE) {
                  break label52;
               }

               contentSource.state = ResponseListeners.ContentSourceDemultiplexer.State.DEMANDED;
               counters = this.countStates();
               if (counters.total() == this.listeners.size()) {
                  this.resetDemands();
                  processDemand = true;
               }
            } catch (Throwable var8) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (ignored != null) {
               ignored.close();
            }

            if (processDemand) {
               this.originalContentSource.demand(this::onDemandCallback);
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("Registered demand on {}; {}", contentSource, counters);
            }

            return;
         }

         if (ignored != null) {
            ignored.close();
         }
      }

      private class ContentSource implements Source {
         private static final Chunk ALREADY_READ_CHUNK = new Chunk() {
            public ByteBuffer getByteBuffer() {
               throw new UnsupportedOperationException();
            }

            public boolean isLast() {
               throw new UnsupportedOperationException();
            }

            public boolean canRetain() {
               throw new UnsupportedOperationException();
            }

            public void retain() {
               throw new UnsupportedOperationException();
            }

            public boolean release() {
               throw new UnsupportedOperationException();
            }

            @Override
            public String toString() {
               return "AlreadyReadChunk";
            }
         };
         private final int index;
         private final AtomicReference<Runnable> demandCallbackRef = new AtomicReference<>();
         private volatile Chunk chunk;
         private volatile ResponseListeners.ContentSourceDemultiplexer.State state = ResponseListeners.ContentSourceDemultiplexer.State.IDLE;

         private ContentSource(int index) {
            this.index = index;
         }

         private void onChunk(Chunk chunk) {
            Chunk currentChunk = this.chunk;
            if (ResponseListeners.ContentSourceDemultiplexer.LOG.isDebugEnabled()) {
               ResponseListeners.ContentSourceDemultiplexer.LOG
                  .debug("Registering content in multiplexed content source #{} that contains {}", this.index, currentChunk);
            }

            if (currentChunk != null && currentChunk != ALREADY_READ_CHUNK) {
               if (!currentChunk.isLast()) {
                  throw new IllegalStateException("Cannot overwrite chunk");
               }
            } else {
               if (chunk.hasRemaining()) {
                  chunk = Chunk.asChunk(chunk.getByteBuffer().slice(), chunk.isLast(), chunk);
               }

               chunk.retain();
               this.chunk = chunk;
            }

            this.onDemandCallback();
         }

         private void onDemandCallback() {
            Runnable callback = this.demandCallbackRef.getAndSet(null);
            if (ResponseListeners.ContentSourceDemultiplexer.LOG.isDebugEnabled()) {
               ResponseListeners.ContentSourceDemultiplexer.LOG.debug("Content source #{} invoking demand callback {}", this.index, callback);
            }

            if (callback != null) {
               try {
                  callback.run();
               } catch (Throwable var3) {
                  this.fail(var3);
               }
            }
         }

         public Chunk read() {
            if (this.chunk == ALREADY_READ_CHUNK) {
               if (ResponseListeners.ContentSourceDemultiplexer.LOG.isDebugEnabled()) {
                  ResponseListeners.ContentSourceDemultiplexer.LOG.debug("Content source #{} already read current chunk", this.index);
               }

               return null;
            } else {
               Chunk result = this.chunk;
               if (result != null) {
                  this.chunk = result.isLast() ? Chunk.next(result) : ALREADY_READ_CHUNK;
               }

               if (ResponseListeners.ContentSourceDemultiplexer.LOG.isDebugEnabled()) {
                  ResponseListeners.ContentSourceDemultiplexer.LOG.debug("Content source #{} reading current chunk {}", this.index, result);
               }

               return result;
            }
         }

         public void demand(Runnable demandCallback) {
            if (!this.demandCallbackRef.compareAndSet(null, Objects.requireNonNull(demandCallback))) {
               throw new IllegalStateException();
            } else {
               Chunk currentChunk = this.chunk;
               if (ResponseListeners.ContentSourceDemultiplexer.LOG.isDebugEnabled()) {
                  ResponseListeners.ContentSourceDemultiplexer.LOG.debug("Content source #{} demand while current chunk is {}", this.index, currentChunk);
               }

               if (currentChunk != null && currentChunk != ALREADY_READ_CHUNK) {
                  this.onDemandCallback();
               } else {
                  ContentSourceDemultiplexer.this.registerDemand(this);
               }
            }
         }

         public void fail(Throwable failure) {
            Chunk currentChunk = this.chunk;
            if (ResponseListeners.ContentSourceDemultiplexer.LOG.isDebugEnabled()) {
               ResponseListeners.ContentSourceDemultiplexer.LOG.debug("Content source #{} fail while current chunk is {}", this.index, currentChunk);
            }

            if (!Chunk.isFailure(currentChunk)) {
               if (currentChunk != null && currentChunk != ALREADY_READ_CHUNK) {
                  currentChunk.release();
               }

               this.chunk = Chunk.from(failure);
               this.onDemandCallback();
               ContentSourceDemultiplexer.this.registerFailure(this, failure);
            }
         }

         @Override
         public String toString() {
            return "%s@%x[i=%d,d=%s,c=%s,s=%s]"
               .formatted(this.getClass().getSimpleName(), this.hashCode(), this.index, this.demandCallbackRef, this.chunk, this.state);
         }
      }

      private static record Counters(int demands, int failures) {
         public int total() {
            return this.demands + this.failures;
         }
      }

      static enum State {
         IDLE,
         DEMANDED,
         FAILED;
      }
   }
}
