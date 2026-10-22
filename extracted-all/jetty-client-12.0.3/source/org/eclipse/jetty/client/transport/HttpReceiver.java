package org.eclipse.jetty.client.transport;

import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.client.ContentDecoder;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.ProtocolHandler;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.client.Result;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.QuotedCSV;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.io.Content.Source;
import org.eclipse.jetty.io.content.ContentSourceTransformer;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.component.Destroyable;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.SerializedInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpReceiver {
   private static final Logger LOG = LoggerFactory.getLogger(HttpReceiver.class);
   private final SerializedInvoker invoker = new SerializedInvoker();
   private final HttpChannel channel;
   private HttpReceiver.ResponseState responseState = HttpReceiver.ResponseState.IDLE;
   private HttpReceiver.NotifiableContentSource contentSource;
   private Throwable failure;

   protected HttpReceiver(HttpChannel channel) {
      this.channel = channel;
   }

   protected abstract Chunk read(boolean var1);

   protected abstract void onInterim();

   protected abstract void failAndClose(Throwable var1);

   protected HttpChannel getHttpChannel() {
      return this.channel;
   }

   protected HttpExchange getHttpExchange() {
      return this.channel.getHttpExchange();
   }

   protected HttpDestination getHttpDestination() {
      return this.channel.getHttpDestination();
   }

   public boolean isFailed() {
      return this.responseState == HttpReceiver.ResponseState.FAILURE;
   }

   protected boolean hasContent() {
      return this.contentSource != null;
   }

   protected void responseBegin(HttpExchange exchange) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Invoking responseBegin for {} on {}", exchange, this);
      }

      this.invoker.run(() -> {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Executing responseBegin for {} on {}", exchange, this);
         }

         if (!exchange.isResponseComplete()) {
            this.responseState = HttpReceiver.ResponseState.BEGIN;
            HttpResponse response = exchange.getResponse();
            HttpConversation conversation = exchange.getConversation();
            HttpClient client = this.getHttpDestination().getHttpClient();
            ProtocolHandler protocolHandler = client.findProtocolHandler(exchange.getRequest(), response);
            Response.Listener handlerListener = null;
            if (protocolHandler != null) {
               handlerListener = protocolHandler.getResponseListener();
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Response {} found protocol handler {}", response, protocolHandler);
               }
            }

            conversation.updateResponseListeners(handlerListener);
            if (LOG.isDebugEnabled()) {
               LOG.debug("Response begin {}", response);
            }

            conversation.getResponseListeners().notifyBegin(response);
         }
      });
   }

   protected void responseHeader(HttpExchange exchange, HttpField field) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Invoking responseHeader for {} on {}", field, this);
      }

      this.invoker.run(() -> {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Executing responseHeader on {}", this);
         }

         if (!exchange.isResponseComplete()) {
            this.responseState = HttpReceiver.ResponseState.HEADER;
            HttpResponse response = exchange.getResponse();
            if (LOG.isDebugEnabled()) {
               LOG.debug("Notifying header {}", field);
            }

            boolean process = exchange.getConversation().getResponseListeners().notifyHeader(response, field);
            if (LOG.isDebugEnabled()) {
               LOG.debug("Header {} notified, {}processing needed", field, process ? "" : "no ");
            }

            if (process) {
               response.addHeader(field);
               HttpHeader fieldHeader = field.getHeader();
               if (fieldHeader != null) {
                  switch (fieldHeader) {
                     case SET_COOKIE:
                     case SET_COOKIE2:
                        URI uri = exchange.getRequest().getURI();
                        if (uri != null) {
                           this.storeCookie(uri, field);
                        }
                  }
               }
            }
         }
      });
   }

   protected void storeCookie(URI uri, HttpField field) {
      this.getHttpDestination().getHttpClient().putCookie(uri, field);
   }

   protected void responseHeaders(HttpExchange exchange) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Invoking responseHeaders on {}", this);
      }

      this.invoker.run(() -> {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Executing responseHeaders on {}", this);
         }

         if (!exchange.isResponseComplete()) {
            this.responseState = HttpReceiver.ResponseState.HEADERS;
            HttpResponse response = exchange.getResponse();
            HttpFields responseHeaders = response.getHeaders();
            if (LOG.isDebugEnabled()) {
               LOG.debug("Response headers {}{}{}", new Object[]{response, System.lineSeparator(), responseHeaders.toString().trim()});
            }

            ContentDecoder decoder = null;
            if (!HttpMethod.HEAD.is(exchange.getRequest().getMethod())) {
               String contentEncoding = responseHeaders.getLast(HttpHeader.CONTENT_ENCODING);
               if (contentEncoding != null) {
                  int comma = contentEncoding.indexOf(",");
                  if (comma > 0) {
                     List<String> values = new QuotedCSV(false, new String[]{contentEncoding}).getValues();
                     contentEncoding = values.get(values.size() - 1);
                  }
               }

               for (ContentDecoder.Factory factory : this.getHttpDestination().getHttpClient().getContentDecoderFactories()) {
                  if (factory.getEncoding().equalsIgnoreCase(contentEncoding)) {
                     decoder = factory.newContentDecoder();
                     decoder.beforeDecoding(response);
                     break;
                  }
               }
            }

            ResponseListeners responseListeners = exchange.getConversation().getResponseListeners();
            responseListeners.notifyHeaders(response);
            if (!exchange.isResponseComplete()) {
               if (HttpStatus.isInterim(response.getStatus())) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Interim response status {}, succeeding", response.getStatus());
                  }

                  this.responseSuccess(exchange, this::onInterim);
               } else {
                  this.responseState = HttpReceiver.ResponseState.CONTENT;
                  if (this.contentSource != null) {
                     throw new IllegalStateException();
                  } else {
                     this.contentSource = new HttpReceiver.ContentSource();
                     if (decoder != null) {
                        this.contentSource = new HttpReceiver.DecodingContentSource(this.contentSource, this.invoker, decoder, response);
                     }

                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Response content {} {}", response, this.contentSource);
                     }

                     responseListeners.notifyContentSource(response, this.contentSource);
                  }
               }
            }
         }
      });
   }

   protected void responseContentAvailable() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Response content available on {}", this);
      }

      this.contentSource.onDataAvailable();
   }

   protected void responseSuccess(HttpExchange exchange, Runnable afterSuccessTask) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Invoking responseSuccess on {}", this);
      }

      if (exchange.responseComplete(null)) {
         this.invoker.run(new Runnable[]{() -> {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Executing responseSuccess on {}", this);
            }

            this.responseState = HttpReceiver.ResponseState.IDLE;
            this.reset();
            HttpResponse response = exchange.getResponse();
            if (LOG.isDebugEnabled()) {
               LOG.debug("Response success {}", response);
            }

            exchange.getConversation().getResponseListeners().notifySuccess(response);
            if (!HttpStatus.isInterim(exchange.getResponse().getStatus())) {
               this.terminateResponse(exchange);
            }
         }, afterSuccessTask});
      }
   }

   protected void responseFailure(Throwable failure, Promise<Boolean> promise) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Failing with {} on {}", failure, this);
      }

      HttpExchange exchange = this.getHttpExchange();
      if (exchange != null && exchange.responseComplete(failure)) {
         this.abort(exchange, failure, promise);
      } else {
         promise.succeeded(false);
      }
   }

   private void terminateResponse(HttpExchange exchange) {
      Result result = exchange.terminateResponse();
      this.terminateResponse(exchange, result);
   }

   private void terminateResponse(HttpExchange exchange, Result result) {
      HttpResponse response = exchange.getResponse();
      if (LOG.isDebugEnabled()) {
         LOG.debug("Response complete {}, result: {}", response, result);
      }

      if (result != null) {
         result = this.channel.exchangeTerminating(exchange, result);
         boolean ordered = this.getHttpDestination().getHttpClient().isStrictEventOrdering();
         if (!ordered) {
            this.channel.exchangeTerminated(exchange, result);
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("Request/Response {}: {}", this.failure == null ? "succeeded" : "failed", result);
         }

         exchange.getConversation().getResponseListeners().notifyComplete(result);
         if (ordered) {
            this.channel.exchangeTerminated(exchange, result);
         }
      }
   }

   protected void reset() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Resetting {}", this);
      }

      this.cleanup();
   }

   protected void dispose() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Disposing {}", this);
      }

      this.cleanup();
   }

   private void cleanup() {
      if (this.contentSource != null) {
         this.contentSource.destroy();
      }

      this.contentSource = null;
   }

   public void abort(HttpExchange exchange, Throwable failure, Promise<Boolean> promise) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Invoking abort with {} on {}", failure, this);
      }

      if (!exchange.isResponseComplete()) {
         throw new IllegalStateException();
      } else {
         this.invoker.run(() -> {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Executing abort with {} on {}", failure, this);
            }

            if (this.responseState == HttpReceiver.ResponseState.FAILURE) {
               promise.succeeded(false);
            } else {
               this.responseState = HttpReceiver.ResponseState.FAILURE;
               this.failure = failure;
               if (this.contentSource != null) {
                  this.contentSource.error(failure);
               }

               this.dispose();
               HttpResponse response = exchange.getResponse();
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Response abort {} {} on {}", new Object[]{response, exchange, this.getHttpChannel(), failure});
               }

               exchange.getConversation().getResponseListeners().notifyFailure(response, failure);
               this.terminateResponse(exchange);
               promise.succeeded(true);
            }
         });
      }
   }

   @Override
   public String toString() {
      return String.format(
         "%s@%x(ex=%s,rsp=%s,failure=%s)", this.getClass().getSimpleName(), this.hashCode(), this.getHttpExchange(), this.responseState, this.failure
      );
   }

   private class ContentSource implements HttpReceiver.NotifiableContentSource {
      private static final Logger LOG = LoggerFactory.getLogger(HttpReceiver.ContentSource.class);
      private final AtomicReference<Runnable> demandCallbackRef = new AtomicReference<>();
      private final AutoLock lock = new AutoLock();
      private final Runnable processDemand = this::processDemand;
      private Chunk currentChunk;

      public Chunk read() {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Reading from {}", this);
         }

         AutoLock ignored = this.lock.lock();

         Chunk var12;
         label84: {
            try {
               Chunk current = this.currentChunk;
               this.currentChunk = Chunk.next(current);
               if (current != null) {
                  var12 = current;
                  break label84;
               }
            } catch (Throwable var8) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var6) {
                     var8.addSuppressed(var6);
                  }
               }

               throw var8;
            }

            if (ignored != null) {
               ignored.close();
            }

            Chunk var9 = HttpReceiver.this.read(false);
            ignored = this.lock.lock();

            label85: {
               try {
                  if (this.currentChunk != null) {
                     if (var9 != null) {
                        var9.release();
                     }

                     var12 = this.currentChunk;
                     break label85;
                  }

                  this.currentChunk = Chunk.next(var9);
                  var12 = var9;
               } catch (Throwable var7) {
                  if (ignored != null) {
                     try {
                        ignored.close();
                     } catch (Throwable var5) {
                        var7.addSuppressed(var5);
                     }
                  }

                  throw var7;
               }

               if (ignored != null) {
                  ignored.close();
               }

               return var12;
            }

            if (ignored != null) {
               ignored.close();
            }

            return var12;
         }

         if (ignored != null) {
            ignored.close();
         }

         return var12;
      }

      @Override
      public void onDataAvailable() {
         if (LOG.isDebugEnabled()) {
            LOG.debug("onDataAvailable on {}", this);
         }

         this.invokeDemandCallback(true);
      }

      public void demand(Runnable demandCallback) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Registering demand on {}", this);
         }

         if (demandCallback == null) {
            throw new IllegalArgumentException();
         } else if (!this.demandCallbackRef.compareAndSet(null, demandCallback)) {
            throw new IllegalStateException();
         } else {
            HttpReceiver.this.invoker.run(this.processDemand);
         }
      }

      private void processDemand() {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Processing demand on {}", this);
         }

         AutoLock ignored = this.lock.lock();

         Chunk current;
         try {
            current = this.currentChunk;
         } catch (Throwable var7) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (ignored != null) {
            ignored.close();
         }

         label61:
         if (current == null) {
            current = HttpReceiver.this.read(true);
            if (current == null) {
               return;
            }

            ignored = this.lock.lock();

            label70: {
               try {
                  if (this.currentChunk != null) {
                     current.release();
                     break label70;
                  }

                  this.currentChunk = current;
               } catch (Throwable var8) {
                  if (ignored != null) {
                     try {
                        ignored.close();
                     } catch (Throwable var5) {
                        var8.addSuppressed(var5);
                     }
                  }

                  throw var8;
               }

               if (ignored != null) {
                  ignored.close();
               }
               break label61;
            }

            if (ignored != null) {
               ignored.close();
            }

            return;
         }

         this.invokeDemandCallback(false);
      }

      private void invokeDemandCallback(boolean invoke) {
         Runnable demandCallback = this.demandCallbackRef.getAndSet(null);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Invoking demand callback on {}", this);
         }

         if (demandCallback != null) {
            try {
               if (invoke) {
                  HttpReceiver.this.invoker.run(demandCallback);
               } else {
                  demandCallback.run();
               }
            } catch (Throwable var4) {
               this.fail(var4);
            }
         }
      }

      public void fail(Throwable failure) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Failing {}", this);
         }

         boolean failed = this.error(failure);
         if (failed) {
            HttpReceiver.this.failAndClose(failure);
         }

         this.invokeDemandCallback(true);
      }

      @Override
      public boolean error(Throwable failure) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Erroring {}", this);
         }

         AutoLock ignored = this.lock.lock();

         boolean var3;
         label51: {
            try {
               if (Chunk.isFailure(this.currentChunk)) {
                  var3 = false;
                  break label51;
               }

               if (this.currentChunk != null) {
                  this.currentChunk.release();
               }

               this.currentChunk = Chunk.from(failure);
            } catch (Throwable var6) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (ignored != null) {
               ignored.close();
            }

            return true;
         }

         if (ignored != null) {
            ignored.close();
         }

         return var3;
      }

      private Chunk chunk() {
         AutoLock ignored = this.lock.lock();

         Chunk var2;
         try {
            var2 = this.currentChunk;
         } catch (Throwable var5) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (ignored != null) {
            ignored.close();
         }

         return var2;
      }

      @Override
      public String toString() {
         return String.format("%s@%x{c=%s,d=%s}", this.getClass().getSimpleName(), this.hashCode(), this.chunk(), this.demandCallbackRef);
      }
   }

   private static class DecodingContentSource extends ContentSourceTransformer implements HttpReceiver.NotifiableContentSource {
      private static final Logger LOG = LoggerFactory.getLogger(HttpReceiver.DecodingContentSource.class);
      private final ContentDecoder _decoder;
      private final Response _response;
      private volatile Chunk _chunk;

      private DecodingContentSource(HttpReceiver.NotifiableContentSource rawSource, SerializedInvoker invoker, ContentDecoder decoder, Response response) {
         super(rawSource, invoker);
         this._decoder = decoder;
         this._response = response;
      }

      protected HttpReceiver.NotifiableContentSource getContentSource() {
         return (HttpReceiver.NotifiableContentSource)super.getContentSource();
      }

      @Override
      public void onDataAvailable() {
         this.getContentSource().onDataAvailable();
      }

      protected Chunk transform(Chunk inputChunk) {
         while (true) {
            boolean retain = this._chunk == null;
            if (LOG.isDebugEnabled()) {
               LOG.debug("input: {}, chunk: {}, retain? {}", new Object[]{inputChunk, this._chunk, retain});
            }

            if (this._chunk == null) {
               this._chunk = inputChunk;
            }

            if (this._chunk == null) {
               return null;
            }

            if (Chunk.isFailure(this._chunk)) {
               return this._chunk;
            }

            if (retain) {
               this._chunk.retain();
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("decoding: {}", this._chunk);
            }

            RetainableByteBuffer decodedBuffer = this._decoder.decode(this._chunk.getByteBuffer());
            if (LOG.isDebugEnabled()) {
               LOG.debug("decoded: {}", decodedBuffer);
            }

            if (decodedBuffer != null && decodedBuffer.hasRemaining()) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("returning decoded content");
               }

               return Chunk.asChunk(decodedBuffer.getByteBuffer(), false, decodedBuffer);
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("decoding produced no content");
            }

            if (!this._chunk.hasRemaining()) {
               Chunk result = this._chunk.isLast() ? Chunk.EOF : null;
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Could not decode more from this chunk, releasing it, r={}", result);
               }

               this._chunk.release();
               this._chunk = null;
               return result;
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("retrying transformation");
            }
         }
      }

      @Override
      public boolean error(Throwable failure) {
         if (this._chunk != null) {
            this._chunk.release();
         }

         this._chunk = null;
         return this.getContentSource().error(failure);
      }

      @Override
      public void destroy() {
         this._decoder.afterDecoding(this._response);
         this.getContentSource().destroy();
      }
   }

   private interface NotifiableContentSource extends Source, Destroyable {
      boolean error(Throwable var1);

      void onDataAvailable();

      default void destroy() {
      }
   }

   private static enum ResponseState {
      IDLE,
      BEGIN,
      HEADER,
      HEADERS,
      CONTENT,
      FAILURE;
   }
}
