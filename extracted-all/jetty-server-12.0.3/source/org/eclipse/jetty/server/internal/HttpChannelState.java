package org.eclipse.jetty.server.internal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.Trailers;
import org.eclipse.jetty.http.UriCompliance;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.http.MetaData.Request;
import org.eclipse.jetty.http.MultiPartFormData.Parts;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.server.Components;
import org.eclipse.jetty.server.ConnectionMetaData;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Context;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpStream;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Session;
import org.eclipse.jetty.server.TunnelSupport;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.ExceptionUtil;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.Attributes.Lazy;
import org.eclipse.jetty.util.Attributes.Mapped;
import org.eclipse.jetty.util.Callback.Completable;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.SerializedInvoker;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.eclipse.jetty.util.thread.Invocable.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpChannelState implements HttpChannel, Components {
   private static final Logger LOG = LoggerFactory.getLogger(HttpChannelState.class);
   private static final Throwable DO_NOT_SEND = new Throwable("No Send");
   private static final HttpField SERVER_VERSION = new ResponseHttpFields.PersistentPreEncodedHttpField(HttpHeader.SERVER, HttpConfiguration.SERVER_VERSION);
   private static final HttpField POWERED_BY = new ResponseHttpFields.PersistentPreEncodedHttpField(HttpHeader.X_POWERED_BY, HttpConfiguration.SERVER_VERSION);
   private final AutoLock _lock = new AutoLock();
   private final HttpChannelState.HandlerInvoker _handlerInvoker = new HttpChannelState.HandlerInvoker();
   private final ConnectionMetaData _connectionMetaData;
   private final SerializedInvoker _serializedInvoker;
   private final ResponseHttpFields _responseHeaders = new ResponseHttpFields();
   private Thread _handling;
   private boolean _handled;
   private HttpChannelState.StreamSendState _streamSendState = HttpChannelState.StreamSendState.SENDING;
   private boolean _callbackCompleted = false;
   private HttpChannelState.ChannelRequest _request;
   private HttpChannelState.ChannelResponse _response;
   private long _oldIdleTimeout;
   private HttpStream _stream;
   private long _committedContentLength = -1L;
   private Runnable _onContentAvailable;
   private Predicate<TimeoutException> _onIdleTimeout;
   private Chunk _failure;
   private Consumer<Throwable> _onFailure;
   private Throwable _callbackFailure;
   private Attributes _cache;

   public HttpChannelState(ConnectionMetaData connectionMetaData) {
      this._connectionMetaData = connectionMetaData;
      this._serializedInvoker = new HttpChannelState.HttpChannelSerializedInvoker();
   }

   @Override
   public void recycle() {
      AutoLock ignored = this._lock.lock();

      try {
         if (LOG.isDebugEnabled()) {
            LOG.debug("recycling {}", this);
         }

         this._request._httpChannelState = null;
         this._responseHeaders.recycle();
         this._handling = null;
         this._handled = false;
         this._streamSendState = HttpChannelState.StreamSendState.SENDING;
         this._callbackCompleted = false;
         this._request = null;
         this._response = null;
         this._oldIdleTimeout = 0L;
         this._stream = null;
         this._committedContentLength = -1L;
         this._onContentAvailable = null;
         this._onIdleTimeout = null;
         this._failure = null;
         this._onFailure = null;
         this._callbackFailure = null;
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
   }

   public HttpConfiguration getHttpConfiguration() {
      return this._connectionMetaData.getHttpConfiguration();
   }

   public HttpStream getHttpStream() {
      AutoLock ignored = this._lock.lock();

      HttpStream var2;
      try {
         var2 = this._stream;
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
   public void setHttpStream(HttpStream stream) {
      AutoLock ignored = this._lock.lock();

      try {
         this._stream = stream;
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
   }

   public Server getServer() {
      return this._connectionMetaData.getConnector().getServer();
   }

   @Override
   public ConnectionMetaData getConnectionMetaData() {
      return this._connectionMetaData;
   }

   public Connection getConnection() {
      return this._connectionMetaData.getConnection();
   }

   public Connector getConnector() {
      return this._connectionMetaData.getConnector();
   }

   public EndPoint getEndPoint() {
      return this.getConnection().getEndPoint();
   }

   @Override
   public ByteBufferPool getByteBufferPool() {
      return this.getConnectionMetaData().getConnector().getByteBufferPool();
   }

   @Override
   public Scheduler getScheduler() {
      return this.getServer().getScheduler();
   }

   @Override
   public ThreadPool getThreadPool() {
      return this.getServer().getThreadPool();
   }

   @Override
   public Attributes getCache() {
      if (this._cache == null) {
         if (this.getConnectionMetaData().isPersistent()) {
            this._cache = new Mapped(new HashMap());
         } else {
            this._cache = Attributes.NULL;
         }
      }

      return this._cache;
   }

   @Override
   public Runnable onRequest(Request request) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onRequest {} {}", request, this);
      }

      AutoLock ignored = this._lock.lock();

      HttpChannelState.HandlerInvoker var7;
      try {
         if (this._stream == null) {
            throw new IllegalStateException("No HttpStream");
         }

         if (this._request != null) {
            throw new IllegalStateException("duplicate request");
         }

         this._request = new HttpChannelState.ChannelRequest(this, request);
         this._response = new HttpChannelState.ChannelResponse(this._request);
         HttpConfiguration httpConfiguration = this.getHttpConfiguration();
         Mutable responseHeaders = this._response.getHeaders();
         if (httpConfiguration.getSendServerVersion()) {
            responseHeaders.add(SERVER_VERSION);
         }

         if (httpConfiguration.getSendXPoweredBy()) {
            responseHeaders.add(POWERED_BY);
         }

         if (httpConfiguration.getSendDateHeader()) {
            responseHeaders.add(this.getConnectionMetaData().getConnector().getServer().getDateField());
         }

         long idleTO = httpConfiguration.getIdleTimeout();
         this._oldIdleTimeout = this._stream.getIdleTimeout();
         if (idleTO >= 0L && this._oldIdleTimeout != idleTO) {
            this._stream.setIdleTimeout(idleTO);
         }

         var7 = this._handlerInvoker;
      } catch (Throwable var9) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (ignored != null) {
         ignored.close();
      }

      return var7;
   }

   @Override
   public org.eclipse.jetty.server.Request getRequest() {
      AutoLock ignored = this._lock.lock();

      HttpChannelState.ChannelRequest var2;
      try {
         var2 = this._request;
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

   public Response getResponse() {
      AutoLock ignored = this._lock.lock();

      HttpChannelState.ChannelResponse var2;
      try {
         var2 = this._response;
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
   public boolean isRequestHandled() {
      AutoLock ignored = this._lock.lock();

      boolean var2;
      try {
         var2 = this._handling != null || this._handled;
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
   public Runnable onContentAvailable() {
      AutoLock ignored = this._lock.lock();

      Object var3;
      label43: {
         Runnable onContent;
         try {
            if (this._request == null) {
               var3 = null;
               break label43;
            }

            onContent = this._onContentAvailable;
            this._onContentAvailable = null;
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

         return this._serializedInvoker.offer(onContent);
      }

      if (ignored != null) {
         ignored.close();
      }

      return (Runnable)var3;
   }

   public InvocationType getInvocationType() {
      AutoLock ignored = this._lock.lock();

      Object var3;
      label43: {
         Runnable onContent;
         try {
            if (this._request == null) {
               var3 = null;
               break label43;
            }

            onContent = this._onContentAvailable;
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

         return Invocable.getInvocationType(onContent);
      }

      if (ignored != null) {
         ignored.close();
      }

      return (InvocationType)var3;
   }

   @Override
   public Runnable onIdleTimeout(TimeoutException t) {
      AutoLock ignored = this._lock.lock();

      Runnable var11;
      label82: {
         Runnable var7;
         label83: {
            Runnable var10;
            label84: {
               try {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("onIdleTimeout {}", this, t);
                  }

                  if (this._failure == null) {
                     Runnable invokeOnContentAvailable = this._onContentAvailable;
                     this._onContentAvailable = null;
                     Runnable invokeWriteFailure = this._response.lockedFailWrite(t);
                     if (invokeOnContentAvailable != null || invokeWriteFailure != null) {
                        this._failure = Chunk.from(t, true);
                     }

                     if (invokeOnContentAvailable != null || invokeWriteFailure != null) {
                        var10 = this._serializedInvoker.offer(new Runnable[]{invokeOnContentAvailable, invokeWriteFailure});
                        break label84;
                     }

                     Predicate<TimeoutException> onIdleTimeout = this._onIdleTimeout;
                     if (onIdleTimeout != null) {
                        var11 = this._serializedInvoker.offer(() -> {
                           if (onIdleTimeout.test(t)) {
                              Runnable task = this.onFailure(t);
                              if (task != null) {
                                 task.run();
                              }
                           }
                        });
                        break label82;
                     }

                     HttpChannelState.ChannelRequest request = this._request;
                     if (this._onFailure == null && request != null) {
                        this._failure = Chunk.from(t, true);
                        var7 = () -> request._callback.failed(t);
                        break label83;
                     }
                  }
               } catch (Throwable var9) {
                  if (ignored != null) {
                     try {
                        ignored.close();
                     } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                     }
                  }

                  throw var9;
               }

               if (ignored != null) {
                  ignored.close();
               }

               return this.onFailure(t);
            }

            if (ignored != null) {
               ignored.close();
            }

            return var10;
         }

         if (ignored != null) {
            ignored.close();
         }

         return var7;
      }

      if (ignored != null) {
         ignored.close();
      }

      return var11;
   }

   @Override
   public Runnable onFailure(Throwable x) {
      AutoLock ignored = this._lock.lock();

      Runnable invokeOnContentAvailable;
      label73: {
         HttpStream stream;
         Runnable task;
         try {
            if (LOG.isDebugEnabled()) {
               LOG.debug("onFailure {}", this, x);
            }

            if (this._stream == null) {
               invokeOnContentAvailable = null;
               break label73;
            }

            stream = this._stream;
            if (this._request == null) {
               Request errorRequest = new Request("GET", HttpURI.from("/"), HttpVersion.HTTP_1_0, HttpFields.EMPTY);
               this._request = new HttpChannelState.ChannelRequest(this, errorRequest);
               this._response = new HttpChannelState.ChannelResponse(this._request);
            }

            if (this._failure == null) {
               this._failure = Chunk.from(x, true);
            } else if (ExceptionUtil.areNotAssociated(this._failure.getFailure(), x) && this._failure.getFailure().getClass() != x.getClass()) {
               this._failure.getFailure().addSuppressed(x);
            }

            if (!this._handled && this._handling == null) {
               task = () -> this._request._callback.failed(x);
            } else {
               invokeOnContentAvailable = this._onContentAvailable;
               this._onContentAvailable = null;
               Runnable invokeWriteFailure = this._response.lockedFailWrite(x);
               HttpChannelState.ChannelRequest request = this._request;
               Runnable invokeOnFailureListeners = () -> {
                  AutoLock ignore = this._lock.lock();

                  Consumer<Throwable> onFailure;
                  try {
                     onFailure = this._onFailure;
                  } catch (Throwable var11x) {
                     if (ignore != null) {
                        try {
                           ignore.close();
                        } catch (Throwable var9x) {
                           var11x.addSuppressed(var9x);
                        }
                     }

                     throw var11x;
                  }

                  if (ignore != null) {
                     ignore.close();
                  }

                  try {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("invokeListeners {} {}", new Object[]{this, onFailure, x});
                     }

                     if (onFailure != null) {
                        onFailure.accept(x);
                     }
                  } catch (Throwable var10x) {
                     ExceptionUtil.addSuppressedIfNotAssociated(x, var10x);
                  }

                  if (invokeOnContentAvailable == null && invokeWriteFailure == null && onFailure == null) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("failing callback in {}", this, x);
                     }

                     request._callback.failed(x);
                  }
               };
               task = this._serializedInvoker.offer(new Runnable[]{invokeOnContentAvailable, invokeWriteFailure, invokeOnFailureListeners});
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

         Throwable unconsumed = stream.consumeAvailable();
         if (unconsumed != null && LOG.isDebugEnabled()) {
            LOG.debug("consuming content during error {}", unconsumed.toString());
         }

         return task;
      }

      if (ignored != null) {
         ignored.close();
      }

      return invokeOnContentAvailable;
   }

   public void addHttpStreamWrapper(Function<HttpStream, HttpStream> onStreamEvent) {
      while (true) {
         AutoLock ignored = this._lock.lock();

         HttpStream stream;
         try {
            stream = this._stream;
         } catch (Throwable var9) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (ignored != null) {
            ignored.close();
         }

         if (this._stream == null) {
            throw new IllegalStateException("No active stream");
         }

         HttpStream combined = onStreamEvent.apply(stream);
         if (combined == null) {
            throw new IllegalArgumentException("Cannot remove stream");
         }

         AutoLock ignoredx = this._lock.lock();

         label69: {
            try {
               if (this._stream != stream) {
                  break label69;
               }

               this._stream = combined;
            } catch (Throwable var10) {
               if (ignoredx != null) {
                  try {
                     ignoredx.close();
                  } catch (Throwable var7) {
                     var10.addSuppressed(var7);
                  }
               }

               throw var10;
            }

            if (ignoredx != null) {
               ignoredx.close();
            }

            return;
         }

         if (ignoredx != null) {
            ignoredx.close();
         }
      }
   }

   private void resetResponse() {
      AutoLock ignored = this._lock.lock();

      try {
         if (this._responseHeaders.isCommitted()) {
            throw new IllegalStateException("response committed");
         }

         this._responseHeaders.clear();
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
   }

   private Throwable lockedStreamSend(boolean last, long length) {
      assert this._lock.isHeldByCurrentThread();

      return (Throwable)(switch (this._streamSendState) {
         case SENDING -> {
            this._streamSendState = last ? HttpChannelState.StreamSendState.LAST_SENDING : HttpChannelState.StreamSendState.SENDING;
            yield null;
         }
         case LAST_SENDING, LAST_COMPLETE -> length > 0L ? new IllegalStateException("last already written") : DO_NOT_SEND;
      });
   }

   private void lockedStreamSendCompleted(boolean success) {
      assert this._lock.isHeldByCurrentThread();

      if (this._streamSendState == HttpChannelState.StreamSendState.LAST_SENDING) {
         this._streamSendState = success ? HttpChannelState.StreamSendState.LAST_COMPLETE : HttpChannelState.StreamSendState.SENDING;
      }
   }

   private boolean lockedIsLastStreamSendCompleted() {
      assert this._lock.isHeldByCurrentThread();

      return this._streamSendState == HttpChannelState.StreamSendState.LAST_COMPLETE;
   }

   private boolean lockedLastStreamSend() {
      assert this._lock.isHeldByCurrentThread();

      if (this._streamSendState != HttpChannelState.StreamSendState.SENDING) {
         return false;
      } else {
         this._streamSendState = HttpChannelState.StreamSendState.LAST_SENDING;
         return true;
      }
   }

   @Override
   public String toString() {
      AutoLock ignored = this._lock.lock();

      String var2;
      try {
         var2 = String.format(
            "%s@%x{handling=%s, handled=%b, send=%s, completed=%b, request=%s}",
            this.getClass().getSimpleName(),
            this.hashCode(),
            this._handling,
            this._handled,
            this._streamSendState,
            this._callbackCompleted,
            this._request
         );
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

   private static class ChannelCallback implements Callback {
      private final HttpChannelState.ChannelRequest _request;
      private Throwable _completedBy;

      private ChannelCallback(HttpChannelState.ChannelRequest request) {
         this._request = request;
      }

      public void succeeded() {
         Throwable failure = null;
         org.eclipse.jetty.http.MetaData.Response responseMetaData = null;
         HttpChannelState.ErrorResponse errorResponse = null;
         AutoLock ignored = this._request._lock.lock();

         label125: {
            HttpStream stream;
            boolean needLastStreamSend;
            HttpChannelState httpChannelState;
            HttpChannelState.ChannelRequest request;
            boolean completeStream;
            try {
               request = this._request;
               httpChannelState = this._request._httpChannelState;
               HttpChannelState.ChannelResponse response = httpChannelState._response;
               stream = httpChannelState._stream;
               if (httpChannelState._onContentAvailable != null) {
                  throw new IllegalStateException("demand pending");
               }

               if (response.lockedIsWriting()) {
                  throw new IllegalStateException("write pending");
               }

               if (this.lockedCompleteCallback()) {
                  break label125;
               }

               assert httpChannelState._callbackFailure == null;

               needLastStreamSend = httpChannelState.lockedLastStreamSend();
               completeStream = !needLastStreamSend && httpChannelState._handling == null && httpChannelState.lockedIsLastStreamSendCompleted();
               if (httpChannelState._responseHeaders.commit()) {
                  responseMetaData = response.lockedPrepareResponse(httpChannelState, true);
               }

               long totalWritten = response._contentBytesWritten;
               long committedContentLength = httpChannelState._committedContentLength;
               if (committedContentLength >= 0L
                  && committedContentLength != totalWritten
                  && (totalWritten != 0L || !HttpMethod.HEAD.is(this._request.getMethod()))) {
                  failure = new IOException("content-length %d != %d written".formatted(committedContentLength, totalWritten));
               }

               Throwable unconsumed = stream.consumeAvailable();
               if (HttpChannelState.LOG.isDebugEnabled()) {
                  HttpChannelState.LOG.debug("consumeAvailable: {} {} ", unconsumed == null, httpChannelState);
               }

               if (unconsumed != null && httpChannelState.getConnectionMetaData().isPersistent()) {
                  failure = ExceptionUtil.combine(failure, unconsumed);
               }

               if (failure != null) {
                  httpChannelState._callbackFailure = failure;
                  if (!stream.isCommitted()) {
                     errorResponse = new HttpChannelState.ErrorResponse(request);
                  } else {
                     completeStream = true;
                  }
               }
            } catch (Throwable var17) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var16) {
                     var17.addSuppressed(var16);
                  }
               }

               throw var17;
            }

            if (ignored != null) {
               ignored.close();
            }

            if (HttpChannelState.LOG.isDebugEnabled()) {
               HttpChannelState.LOG.debug("succeeded: failure={} needLastStreamSend={} {}", new Object[]{failure, needLastStreamSend, this});
            }

            if (errorResponse != null) {
               Response.writeError(request, errorResponse, new HttpChannelState.ErrorCallback(request, errorResponse, stream, failure), failure);
            } else if (needLastStreamSend) {
               stream.send(this._request._metaData, responseMetaData, true, null, httpChannelState._handlerInvoker);
            } else if (completeStream) {
               httpChannelState._handlerInvoker.completeStream(stream, failure);
            } else if (HttpChannelState.LOG.isDebugEnabled()) {
               HttpChannelState.LOG.debug("No action on succeeded {}", this);
            }

            return;
         }

         if (ignored != null) {
            ignored.close();
         }
      }

      public void failed(Throwable failure) {
         HttpChannelState.ErrorResponse errorResponse = null;
         AutoLock ignored = this._request._lock.lock();

         label61: {
            HttpStream stream;
            HttpChannelState.ChannelRequest request;
            try {
               if (this.lockedCompleteCallback()) {
                  break label61;
               }

               HttpChannelState httpChannelState = this._request._httpChannelState;
               stream = httpChannelState._stream;
               request = this._request;

               assert httpChannelState._callbackFailure == null;

               httpChannelState._callbackFailure = failure;
               Throwable unconsumed = stream.consumeAvailable();
               ExceptionUtil.addSuppressedIfNotAssociated(failure, unconsumed);
               if (HttpChannelState.LOG.isDebugEnabled()) {
                  HttpChannelState.LOG
                     .debug(
                        "failed stream.isCommitted={}, response.isCommitted={} {}",
                        new Object[]{httpChannelState._stream.isCommitted(), httpChannelState._response.isCommitted(), this}
                     );
               }

               if (!stream.isCommitted()) {
                  errorResponse = new HttpChannelState.ErrorResponse(request);
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

            if (errorResponse != null) {
               Response.writeError(request, errorResponse, new HttpChannelState.ErrorCallback(request, errorResponse, stream, failure), failure);
            } else {
               this._request.getHttpChannelState()._handlerInvoker.failed(failure);
            }

            return;
         }

         if (ignored != null) {
            ignored.close();
         }
      }

      private boolean lockedCompleteCallback() {
         assert this._request._lock.isHeldByCurrentThread();

         HttpChannelState httpChannelState = this._request._httpChannelState;
         if (httpChannelState == null) {
            if (HttpChannelState.LOG.isDebugEnabled()) {
               HttpChannelState.LOG.debug("already recycled after completion {} by", this._request, this._completedBy);
            }

            return true;
         } else if (httpChannelState._callbackCompleted) {
            if (HttpChannelState.LOG.isDebugEnabled()) {
               HttpChannelState.LOG.debug("already completed {} by", this._request, this._completedBy);
               HttpChannelState.LOG.debug("Second complete", new Throwable("second complete"));
            }

            return true;
         } else {
            if (HttpChannelState.LOG.isDebugEnabled()) {
               this._completedBy = new Throwable(Thread.currentThread().getName());
            }

            httpChannelState._callbackCompleted = true;
            return false;
         }
      }

      public InvocationType getInvocationType() {
         return this._request.getHttpStream().getInvocationType();
      }
   }

   public static class ChannelRequest implements Attributes, org.eclipse.jetty.server.Request {
      private final long _headersNanoTime = NanoTime.now();
      private final HttpChannelState.ChannelCallback _callback = new HttpChannelState.ChannelCallback(this);
      private final String _id;
      private final ConnectionMetaData _connectionMetaData;
      private final Request _metaData;
      private final AutoLock _lock;
      private final LongAdder _contentBytesRead = new LongAdder();
      private final Attributes _attributes = new Lazy();
      private HttpChannelState _httpChannelState;
      private org.eclipse.jetty.server.Request _loggedRequest;
      private HttpFields _trailers;

      ChannelRequest(HttpChannelState httpChannelState, Request metaData) {
         this._httpChannelState = Objects.requireNonNull(httpChannelState);
         this._id = httpChannelState.getHttpStream().getId();
         this._connectionMetaData = httpChannelState.getConnectionMetaData();
         this._metaData = Objects.requireNonNull(metaData);
         this._lock = httpChannelState._lock;
      }

      public void setLoggedRequest(org.eclipse.jetty.server.Request request) {
         this._loggedRequest = request;
      }

      public org.eclipse.jetty.server.Request getLoggedRequest() {
         return (org.eclipse.jetty.server.Request)(this._loggedRequest == null ? this : this._loggedRequest);
      }

      HttpStream getHttpStream() {
         return this.getHttpChannelState()._stream;
      }

      public long getContentBytesRead() {
         return this._contentBytesRead.longValue();
      }

      public Object getAttribute(String name) {
         if (name.startsWith("org.eclipse.jetty")) {
            if (Server.class.getName().equals(name)) {
               return this.getConnectionMetaData().getConnector().getServer();
            }

            if (HttpChannelState.class.getName().equals(name)) {
               return this.getHttpChannelState();
            }

            if (HttpConnection.class.getName().equals(name) && this.getConnectionMetaData().getConnection() instanceof HttpConnection) {
               return this.getConnectionMetaData().getConnection();
            }
         }

         return this._attributes.getAttribute(name);
      }

      public Object removeAttribute(String name) {
         return this._attributes.removeAttribute(name);
      }

      public Object setAttribute(String name, Object attribute) {
         return !Server.class.getName().equals(name) && !HttpChannelState.class.getName().equals(name) && !HttpConnection.class.getName().equals(name)
            ? this._attributes.setAttribute(name, attribute)
            : null;
      }

      public Set<String> getAttributeNameSet() {
         return this._attributes.getAttributeNameSet();
      }

      public void clearAttributes() {
         this._attributes.clearAttributes();
      }

      @Override
      public String getId() {
         return this._id;
      }

      @Override
      public Components getComponents() {
         return this.getHttpChannelState();
      }

      @Override
      public ConnectionMetaData getConnectionMetaData() {
         return this._connectionMetaData;
      }

      private HttpChannelState getHttpChannelState() {
         AutoLock ignore = this._lock.lock();

         HttpChannelState var2;
         try {
            var2 = this.lockedGetHttpChannelState();
         } catch (Throwable var5) {
            if (ignore != null) {
               try {
                  ignore.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (ignore != null) {
            ignore.close();
         }

         return var2;
      }

      private HttpChannelState lockedGetHttpChannelState() {
         assert this._lock.isHeldByCurrentThread();

         if (this._httpChannelState == null) {
            throw new IllegalStateException("channel already completed");
         } else {
            return this._httpChannelState;
         }
      }

      @Override
      public String getMethod() {
         return this._metaData.getMethod();
      }

      @Override
      public HttpURI getHttpURI() {
         return this._metaData.getHttpURI();
      }

      @Override
      public Context getContext() {
         return this.getConnectionMetaData().getConnector().getServer().getContext();
      }

      @Override
      public HttpFields getHeaders() {
         return this._metaData.getHttpFields();
      }

      @Override
      public HttpFields getTrailers() {
         return this._trailers;
      }

      @Override
      public long getBeginNanoTime() {
         return this._metaData.getBeginNanoTime();
      }

      @Override
      public long getHeadersNanoTime() {
         return this._headersNanoTime;
      }

      @Override
      public boolean isSecure() {
         return HttpScheme.HTTPS.is(this.getHttpURI().getScheme());
      }

      public long getLength() {
         return this._metaData.getContentLength();
      }

      @Override
      public Chunk read() {
         AutoLock ignored = this._lock.lock();

         Chunk var5;
         label57: {
            HttpStream stream;
            try {
               HttpChannelState httpChannel = this.lockedGetHttpChannelState();
               Chunk error = httpChannel._failure;
               httpChannel._failure = Chunk.next(error);
               if (error != null) {
                  var5 = error;
                  break label57;
               }

               stream = httpChannel._stream;
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

            Chunk chunk = stream.read();
            if (HttpChannelState.LOG.isDebugEnabled()) {
               HttpChannelState.LOG.debug("read {}", chunk);
            }

            if (chunk != null && chunk.hasRemaining()) {
               this._contentBytesRead.add((long)chunk.getByteBuffer().remaining());
            }

            if (chunk instanceof Trailers trailers) {
               this._trailers = trailers.getTrailers();
            }

            return chunk;
         }

         if (ignored != null) {
            ignored.close();
         }

         return var5;
      }

      @Override
      public boolean consumeAvailable() {
         AutoLock ignored = this._lock.lock();

         HttpStream stream;
         try {
            HttpChannelState httpChannel = this.lockedGetHttpChannelState();
            stream = httpChannel._stream;
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

         return stream.consumeAvailable() == null;
      }

      @Override
      public void demand(Runnable demandCallback) {
         AutoLock ignored = this._lock.lock();

         boolean error;
         HttpStream stream;
         try {
            HttpChannelState httpChannel = this.lockedGetHttpChannelState();
            if (HttpChannelState.LOG.isDebugEnabled()) {
               HttpChannelState.LOG.debug("demand {}", httpChannel);
            }

            error = httpChannel._failure != null;
            if (!error) {
               if (httpChannel._onContentAvailable != null) {
                  throw new IllegalArgumentException("demand pending");
               }

               httpChannel._onContentAvailable = demandCallback;
            }

            stream = httpChannel._stream;
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

         if (error) {
            this.getHttpChannelState()._serializedInvoker.run(demandCallback);
         } else {
            stream.demand();
         }
      }

      public void fail(Throwable failure) {
         Runnable runnable = this._httpChannelState.onFailure(failure);
         if (runnable != null) {
            this.getContext().execute(runnable);
         }
      }

      @Override
      public void push(Request resource) {
         this.getHttpStream().push(resource);
      }

      @Override
      public void addIdleTimeoutListener(Predicate<TimeoutException> onIdleTimeout) {
         AutoLock ignored = this._lock.lock();

         label43: {
            try {
               HttpChannelState httpChannel = this.lockedGetHttpChannelState();
               if (httpChannel._failure != null) {
                  break label43;
               }

               if (httpChannel._onIdleTimeout == null) {
                  httpChannel._onIdleTimeout = onIdleTimeout;
               } else {
                  Predicate<TimeoutException> previous = httpChannel._onIdleTimeout;
                  httpChannel._onIdleTimeout = throwable -> !previous.test(throwable) ? onIdleTimeout.test(throwable) : true;
               }
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

            return;
         }

         if (ignored != null) {
            ignored.close();
         }
      }

      @Override
      public void addFailureListener(Consumer<Throwable> onFailure) {
         AutoLock ignored = this._lock.lock();

         label43: {
            try {
               HttpChannelState httpChannel = this.lockedGetHttpChannelState();
               if (httpChannel._failure != null) {
                  break label43;
               }

               if (httpChannel._onFailure == null) {
                  httpChannel._onFailure = onFailure;
               } else {
                  Consumer<Throwable> previous = httpChannel._onFailure;
                  httpChannel._onFailure = throwable -> {
                     try {
                        previous.accept(throwable);
                     } catch (Throwable var7) {
                        ExceptionUtil.addSuppressedIfNotAssociated(throwable, var7);
                     } finally {
                        onFailure.accept(throwable);
                     }
                  };
               }
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

            return;
         }

         if (ignored != null) {
            ignored.close();
         }
      }

      @Override
      public TunnelSupport getTunnelSupport() {
         return this.getHttpStream().getTunnelSupport();
      }

      @Override
      public void addHttpStreamWrapper(Function<HttpStream, HttpStream> wrapper) {
         this.getHttpChannelState().addHttpStreamWrapper(wrapper);
      }

      @Override
      public Session getSession(boolean create) {
         return null;
      }

      @Override
      public String toString() {
         return String.format("%s@%x %s %s", this.getMethod(), this.hashCode(), this.getHttpURI(), this._metaData.getHttpVersion());
      }
   }

   public static class ChannelResponse implements Response, Callback {
      private final HttpChannelState.ChannelRequest _request;
      private final ResponseHttpFields _httpFields;
      protected int _status;
      private long _contentBytesWritten;
      private Supplier<HttpFields> _trailers;
      private Callback _writeCallback;

      private ChannelResponse(HttpChannelState.ChannelRequest request) {
         this._request = request;
         this._httpFields = this.getResponseHttpFields(this._request.lockedGetHttpChannelState());
      }

      protected ResponseHttpFields getResponseHttpFields(HttpChannelState httpChannelState) {
         return httpChannelState._responseHeaders;
      }

      protected ResponseHttpFields getResponseHttpFields() {
         return this._httpFields;
      }

      private boolean lockedIsWriting() {
         assert this._request._lock.isHeldByCurrentThread();

         return this._writeCallback != null;
      }

      private Runnable lockedFailWrite(Throwable x) {
         assert this._request._lock.isHeldByCurrentThread();

         Callback writeCallback = this._writeCallback;
         this._writeCallback = null;
         return writeCallback == null ? null : () -> writeCallback.failed(x);
      }

      public long getContentBytesWritten() {
         return this._contentBytesWritten;
      }

      @Override
      public org.eclipse.jetty.server.Request getRequest() {
         return this._request;
      }

      @Override
      public int getStatus() {
         return this._status;
      }

      @Override
      public void setStatus(int code) {
         if (!this.isCommitted()) {
            this._status = code;
         }
      }

      @Override
      public Mutable getHeaders() {
         return this._httpFields;
      }

      @Override
      public Supplier<HttpFields> getTrailersSupplier() {
         AutoLock ignored = this._request._lock.lock();

         Supplier var2;
         try {
            var2 = this._trailers;
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
      public void setTrailersSupplier(Supplier<HttpFields> trailers) {
         AutoLock ignored = this._request._lock.lock();

         try {
            this._trailers = trailers;
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
      }

      @Override
      public void write(boolean last, ByteBuffer content, Callback callback) {
         long length = (long)BufferUtil.length(content);
         HttpStream stream = null;
         org.eclipse.jetty.http.MetaData.Response responseMetaData = null;
         AutoLock ignored = this._request._lock.lock();

         Throwable failure;
         try {
            HttpChannelState httpChannelState = this._request.lockedGetHttpChannelState();
            long committedContentLength = httpChannelState._committedContentLength;
            long totalWritten = this._contentBytesWritten + length;
            long contentLength = committedContentLength >= 0L ? committedContentLength : this.getHeaders().getLongField(HttpHeader.CONTENT_LENGTH);
            if (this._writeCallback != null) {
               failure = new IllegalStateException("write pending");
            } else {
               failure = this.getFailure(httpChannelState);
               if (failure == null && contentLength >= 0L && totalWritten != contentLength) {
                  String lengthError = null;
                  if (totalWritten > contentLength) {
                     lengthError = "written %d > %d content-length";
                  } else if (last && (totalWritten != 0L || !HttpMethod.HEAD.is(this._request.getMethod()))) {
                     lengthError = "written %d < %d content-length";
                  }

                  if (lengthError != null) {
                     String message = lengthError.formatted(totalWritten, contentLength);
                     if (HttpChannelState.LOG.isDebugEnabled()) {
                        HttpChannelState.LOG.debug("fail {} {}", callback, message);
                     }

                     failure = new IOException(message);
                  }
               }
            }

            if (failure == null) {
               failure = httpChannelState.lockedStreamSend(last, length);
            }

            if (failure == HttpChannelState.DO_NOT_SEND) {
               httpChannelState._serializedInvoker.run(callback::succeeded);
            } else if (failure != null) {
               Throwable throwable = failure;
               httpChannelState._serializedInvoker.run(() -> callback.failed(throwable));
            } else {
               this._writeCallback = callback;
               this._contentBytesWritten = totalWritten;
               stream = httpChannelState._stream;
               if (this._httpFields.commit()) {
                  responseMetaData = this.lockedPrepareResponse(httpChannelState, last);
               }
            }
         } catch (Throwable var20) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var19) {
                  var20.addSuppressed(var19);
               }
            }

            throw var20;
         }

         if (ignored != null) {
            ignored.close();
         }

         if (failure == null) {
            if (HttpChannelState.LOG.isDebugEnabled()) {
               HttpChannelState.LOG.debug("writing last={} {} {}", new Object[]{last, BufferUtil.toDetailString(content), this});
            }

            stream.send(this._request._metaData, responseMetaData, last, content, this);
         }
      }

      protected Throwable getFailure(HttpChannelState httpChannelState) {
         Chunk failure = httpChannelState._failure;
         return failure == null ? null : failure.getFailure();
      }

      public void succeeded() {
         if (HttpChannelState.LOG.isDebugEnabled()) {
            HttpChannelState.LOG.debug("write succeeded {}", this);
         }

         AutoLock ignored = this._request._lock.lock();

         Callback callback;
         HttpChannelState httpChannel;
         try {
            httpChannel = this._request.lockedGetHttpChannelState();
            callback = this._writeCallback;
            this._writeCallback = null;
            httpChannel.lockedStreamSendCompleted(true);
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

         if (callback != null) {
            httpChannel._serializedInvoker.run(callback::succeeded);
         }
      }

      public void failed(Throwable x) {
         if (HttpChannelState.LOG.isDebugEnabled()) {
            HttpChannelState.LOG.debug("write failed {}", this, x);
         }

         AutoLock ignored = this._request._lock.lock();

         Callback callback;
         HttpChannelState httpChannel;
         try {
            httpChannel = this._request.lockedGetHttpChannelState();
            callback = this._writeCallback;
            this._writeCallback = null;
            httpChannel.lockedStreamSendCompleted(false);
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

         if (callback != null) {
            httpChannel._serializedInvoker.run(() -> callback.failed(x));
         }
      }

      public InvocationType getInvocationType() {
         return Invocable.getInvocationType(this._writeCallback);
      }

      @Override
      public boolean isCommitted() {
         return this._httpFields.isCommitted();
      }

      @Override
      public boolean hasLastWrite() {
         AutoLock ignored = this._request._lock.lock();

         boolean var6;
         label47: {
            try {
               if (this._request._httpChannelState == null) {
                  var6 = true;
                  break label47;
               }

               var6 = this._request._httpChannelState._streamSendState != HttpChannelState.StreamSendState.SENDING;
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

            return var6;
         }

         if (ignored != null) {
            ignored.close();
         }

         return var6;
      }

      @Override
      public boolean isCompletedSuccessfully() {
         AutoLock ignored = this._request._lock.lock();

         boolean var6;
         label50: {
            try {
               if (this._request._httpChannelState == null) {
                  var6 = false;
                  break label50;
               }

               var6 = this._request._httpChannelState._callbackCompleted && this._request._httpChannelState._callbackFailure == null;
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

            return var6;
         }

         if (ignored != null) {
            ignored.close();
         }

         return var6;
      }

      @Override
      public void reset() {
         this._status = 0;
         this._trailers = null;
         this._contentBytesWritten = 0L;
         this._request.getHttpChannelState().resetResponse();
      }

      @Override
      public CompletableFuture<Void> writeInterim(int status, HttpFields headers) {
         Completable completable = new Completable();
         if (HttpStatus.isInterim(status)) {
            HttpChannelState channel = this._request.getHttpChannelState();
            HttpVersion version = channel.getConnectionMetaData().getHttpVersion();
            org.eclipse.jetty.http.MetaData.Response response = new org.eclipse.jetty.http.MetaData.Response(status, null, version, headers);
            channel._stream.send(this._request._metaData, response, false, null, completable);
         } else {
            completable.failed(new IllegalArgumentException("Invalid interim status code: " + status));
         }

         return completable;
      }

      org.eclipse.jetty.http.MetaData.Response lockedPrepareResponse(HttpChannelState httpChannel, boolean last) {
         assert this._request._lock.isHeldByCurrentThread();

         if (this._status == 0) {
            this._status = 200;
         }

         Mutable mutableHeaders = this._httpFields.getMutableHttpFields();
         httpChannel._committedContentLength = mutableHeaders.getLongField(HttpHeader.CONTENT_LENGTH);
         if (last && httpChannel._committedContentLength < 0L) {
            httpChannel._committedContentLength = this._contentBytesWritten;
            mutableHeaders.put(HttpHeader.CONTENT_LENGTH, httpChannel._committedContentLength);
         }

         httpChannel._stream.prepareResponse(mutableHeaders);
         return new org.eclipse.jetty.http.MetaData.Response(
            this._status,
            null,
            httpChannel.getConnectionMetaData().getHttpVersion(),
            this._httpFields,
            httpChannel._committedContentLength,
            this.getTrailersSupplier()
         );
      }

      @Override
      public String toString() {
         return "%s@%x{%s,%s}".formatted(this.getClass().getSimpleName(), this.hashCode(), this.getStatus(), this.getRequest());
      }
   }

   private static class ErrorCallback implements Callback {
      private final HttpChannelState.ChannelRequest _request;
      private final HttpChannelState.ErrorResponse _errorResponse;
      private final HttpStream _stream;
      private final Throwable _failure;

      public ErrorCallback(HttpChannelState.ChannelRequest request, HttpChannelState.ErrorResponse response, HttpStream stream, Throwable failure) {
         this._request = request;
         this._errorResponse = response;
         this._stream = stream;
         this._failure = failure;
      }

      public void succeeded() {
         if (HttpChannelState.LOG.isDebugEnabled()) {
            HttpChannelState.LOG.debug("ErrorWrite succeeded: {}", this);
         }

         org.eclipse.jetty.http.MetaData.Response responseMetaData = null;
         AutoLock ignored = this._request._lock.lock();

         boolean needLastWrite;
         HttpChannelState httpChannelState;
         Throwable failure;
         try {
            httpChannelState = this._request.getHttpChannelState();
            failure = this._failure;
            needLastWrite = httpChannelState.lockedLastStreamSend();
            if (needLastWrite && this._errorResponse.getResponseHttpFields().commit()) {
               responseMetaData = this._errorResponse.lockedPrepareResponse(httpChannelState, true);
            }
         } catch (Throwable var9) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (ignored != null) {
            ignored.close();
         }

         if (needLastWrite) {
            this._stream
               .send(this._request._metaData, responseMetaData, true, null, Callback.from(() -> httpChannelState._handlerInvoker.failed(failure), x -> {
                  ExceptionUtil.addSuppressedIfNotAssociated(failure, x);
                  httpChannelState._handlerInvoker.failed(failure);
               }));
         } else {
            httpChannelState._handlerInvoker.failed(failure);
         }
      }

      public void failed(Throwable x) {
         if (HttpChannelState.LOG.isDebugEnabled()) {
            HttpChannelState.LOG.debug("ErrorWrite failed: {}", this, x);
         }

         AutoLock ignored = this._request._lock.lock();

         Throwable failure;
         HttpChannelState httpChannelState;
         try {
            failure = this._failure;
            httpChannelState = this._request.lockedGetHttpChannelState();
            httpChannelState._response._status = this._errorResponse._status;
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

         if (ExceptionUtil.areNotAssociated(failure, x)) {
            failure.addSuppressed(x);
         }

         httpChannelState._handlerInvoker.failed(failure);
      }

      @Override
      public String toString() {
         return "%s@%x".formatted(this.getClass().getSimpleName(), this.hashCode());
      }
   }

   private static class ErrorResponse extends HttpChannelState.ChannelResponse {
      public ErrorResponse(HttpChannelState.ChannelRequest request) {
         super(request);
         this._status = 500;
      }

      @Override
      protected Throwable getFailure(HttpChannelState httpChannelState) {
         return null;
      }

      @Override
      protected ResponseHttpFields getResponseHttpFields(HttpChannelState httpChannelState) {
         httpChannelState._committedContentLength = -1L;
         HttpFields original = super.getResponseHttpFields(httpChannelState);
         ResponseHttpFields httpFields = new ResponseHttpFields();

         for (HttpField field : original) {
            HttpHeader header = field.getHeader();
            if (header == HttpHeader.SERVER || header == HttpHeader.DATE) {
               httpFields.add(field);
            }
         }

         return httpFields;
      }

      @Override
      org.eclipse.jetty.http.MetaData.Response lockedPrepareResponse(HttpChannelState httpChannelState, boolean last) {
         assert httpChannelState._request._lock.isHeldByCurrentThread();

         org.eclipse.jetty.http.MetaData.Response httpFields = super.lockedPrepareResponse(httpChannelState, last);
         httpChannelState._response._status = this._status;
         Mutable originalResponseFields = httpChannelState._responseHeaders.getMutableHttpFields();
         originalResponseFields.clear();
         originalResponseFields.add(this.getResponseHttpFields());
         return httpFields;
      }
   }

   private class HandlerInvoker implements Task, Callback {
      public void run() {
         AutoLock ignored = HttpChannelState.this._lock.lock();

         HttpChannelState.ChannelRequest request;
         HttpChannelState.ChannelResponse response;
         try {
            assert HttpChannelState.this._handling == null && !HttpChannelState.this._handled;

            HttpChannelState.this._handling = Thread.currentThread();
            request = HttpChannelState.this._request;
            response = HttpChannelState.this._response;
         } catch (Throwable var16) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var13) {
                  var16.addSuppressed(var13);
               }
            }

            throw var16;
         }

         if (ignored != null) {
            ignored.close();
         }

         if (HttpChannelState.LOG.isDebugEnabled()) {
            HttpChannelState.LOG.debug("invoking handler in {}", HttpChannelState.this);
         }

         Server server = HttpChannelState.this._connectionMetaData.getConnector().getServer();

         try {
            String pathInContext = org.eclipse.jetty.server.Request.getPathInContext(request);
            if (pathInContext != null && !pathInContext.startsWith("/")) {
               String method = request.getMethod();
               if (!HttpMethod.PRI.is(method) && !HttpMethod.CONNECT.is(method) && !HttpMethod.OPTIONS.is(method)) {
                  throw new BadMessageException("Bad URI path");
               }
            }

            HttpURI uri = request.getHttpURI();
            if (uri.hasViolations()) {
               String badMessage = UriCompliance.checkUriCompliance(
                  HttpChannelState.this.getConnectionMetaData().getHttpConfiguration().getUriCompliance(), uri
               );
               if (badMessage != null) {
                  throw new BadMessageException(badMessage);
               }
            }

            HttpConfiguration configuration = HttpChannelState.this.getHttpConfiguration();
            org.eclipse.jetty.server.Request customized = request;
            Mutable responseHeaders = response.getHeaders();

            for (HttpConfiguration.Customizer customizer : configuration.getCustomizers()) {
               org.eclipse.jetty.server.Request next = customizer.customize(customized, responseHeaders);
               customized = next == null ? customized : next;
            }

            if (customized != request && server.getRequestLog() != null) {
               request.setLoggedRequest(customized);
            }

            if (!server.handle(customized, response, request._callback)) {
               Response.writeError(customized, response, request._callback, 404);
            }
         } catch (Throwable var15) {
            request._callback.failed(var15);
         }

         AutoLock ignoredx = HttpChannelState.this._lock.lock();

         HttpStream stream;
         Throwable failure;
         boolean completeStream;
         boolean callbackCompleted;
         try {
            stream = HttpChannelState.this._stream;
            HttpChannelState.this._handling = null;
            HttpChannelState.this._handled = true;
            failure = HttpChannelState.this._callbackFailure;
            callbackCompleted = HttpChannelState.this._callbackCompleted;
            boolean lastStreamSendComplete = HttpChannelState.this.lockedIsLastStreamSendCompleted();
            completeStream = callbackCompleted && lastStreamSendComplete;
            if (HttpChannelState.LOG.isDebugEnabled()) {
               HttpChannelState.LOG
                  .debug(
                     "handler invoked: completeStream={} failure={} callbackCompleted={} {}",
                     new Object[]{completeStream, failure, callbackCompleted, HttpChannelState.this}
                  );
            }
         } catch (Throwable var14) {
            if (ignoredx != null) {
               try {
                  ignoredx.close();
               } catch (Throwable var12) {
                  var14.addSuppressed(var12);
               }
            }

            throw var14;
         }

         if (ignoredx != null) {
            ignoredx.close();
         }

         if (HttpChannelState.LOG.isDebugEnabled()) {
            HttpChannelState.LOG
               .debug("stream={}, failure={}, callbackCompleted={}, completeStream={}", new Object[]{stream, failure, callbackCompleted, completeStream});
         }

         if (completeStream) {
            if (HttpChannelState.LOG.isDebugEnabled()) {
               HttpChannelState.LOG.debug("completeStream({}, {})", stream, Objects.toString(failure));
            }

            this.completeStream(stream, failure);
         }
      }

      public void succeeded() {
         AutoLock ignored = HttpChannelState.this._lock.lock();

         HttpStream stream;
         boolean completeStream;
         try {
            assert HttpChannelState.this._callbackCompleted;

            HttpChannelState.this._streamSendState = HttpChannelState.StreamSendState.LAST_COMPLETE;
            completeStream = HttpChannelState.this._handling == null;
            stream = HttpChannelState.this._stream;
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

         if (completeStream) {
            this.completeStream(stream, null);
         }
      }

      public void failed(Throwable failure) {
         AutoLock ignored = HttpChannelState.this._lock.lock();

         HttpStream stream;
         boolean completeStream;
         try {
            assert HttpChannelState.this._callbackCompleted;

            HttpChannelState.this._streamSendState = HttpChannelState.StreamSendState.LAST_COMPLETE;
            completeStream = HttpChannelState.this._handling == null;
            stream = HttpChannelState.this._stream;
            failure = HttpChannelState.this._callbackFailure = ExceptionUtil.combine(HttpChannelState.this._callbackFailure, failure);
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

         if (completeStream) {
            this.completeStream(stream, failure);
         }
      }

      private void completeStream(HttpStream stream, Throwable failure) {
         try {
            RequestLog requestLog = HttpChannelState.this.getServer().getRequestLog();
            if (requestLog != null) {
               if (HttpChannelState.LOG.isDebugEnabled()) {
                  HttpChannelState.LOG.debug("logging {}", HttpChannelState.this);
               }

               requestLog.log(HttpChannelState.this._request.getLoggedRequest(), HttpChannelState.this._response);
            }

            Parts parts = (Parts)HttpChannelState.this._request.getAttribute(Parts.class.getName());
            if (parts != null) {
               parts.close();
            }

            long idleTO = HttpChannelState.this.getHttpConfiguration().getIdleTimeout();
            if (idleTO > 0L && HttpChannelState.this._oldIdleTimeout != idleTO) {
               stream.setIdleTimeout(HttpChannelState.this._oldIdleTimeout);
            }
         } finally {
            if (failure == null) {
               stream.succeeded();
            } else {
               stream.failed(failure);
            }
         }
      }

      public InvocationType getInvocationType() {
         return HttpChannelState.this.getConnectionMetaData().getConnector().getServer().getInvocationType();
      }
   }

   private class HttpChannelSerializedInvoker extends SerializedInvoker {
      protected void onError(Runnable task, Throwable failure) {
         AutoLock ignore = HttpChannelState.this._lock.lock();

         HttpChannelState.ChannelRequest request;
         Chunk error;
         boolean callbackCompleted;
         try {
            callbackCompleted = HttpChannelState.this._callbackCompleted;
            request = HttpChannelState.this._request;
            error = HttpChannelState.this._request == null ? null : HttpChannelState.this._failure;
         } catch (Throwable var11) {
            if (ignore != null) {
               try {
                  ignore.close();
               } catch (Throwable var9) {
                  var11.addSuppressed(var9);
               }
            }

            throw var11;
         }

         if (ignore != null) {
            ignore.close();
         }

         if (request == null || callbackCompleted) {
            super.onError(task, failure);
         } else if (error == null) {
            try {
               request._callback.failed(failure);
            } catch (Throwable var10) {
               ExceptionUtil.addSuppressedIfNotAssociated(failure, var10);
               super.onError(task, failure);
            }
         } else {
            Throwable cause = error.getFailure();
            if (ExceptionUtil.areNotAssociated(cause, failure)) {
               error.getFailure().addSuppressed(failure);
            }
         }
      }
   }

   private static enum StreamSendState {
      SENDING,
      LAST_SENDING,
      LAST_COMPLETE;
   }
}
