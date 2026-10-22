package org.eclipse.jetty.server.handler.gzip;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.WritePendingException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import org.eclipse.jetty.http.CompressedContentFormat;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IteratingNestedCallback;
import org.eclipse.jetty.util.IteratingCallback.Action;
import org.eclipse.jetty.util.compression.CompressionPool;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GzipResponseAndCallback extends Response.Wrapper implements Callback, Invocable {
   public static Logger LOG = LoggerFactory.getLogger(GzipResponseAndCallback.class);
   private static final byte OS_UNKNOWN = -1;
   private static final byte[] GZIP_HEADER = new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 0, -1};
   public static final int GZIP_TRAILER_SIZE = 8;
   public static final HttpField VARY_ACCEPT_ENCODING = new PreEncodedHttpField(HttpHeader.VARY, HttpHeader.ACCEPT_ENCODING.asString());
   private final AtomicReference<GzipResponseAndCallback.GZState> _state = new AtomicReference<>(GzipResponseAndCallback.GZState.MIGHT_COMPRESS);
   private final CRC32 _crc = new CRC32();
   private final Callback _callback;
   private final GzipFactory _factory;
   private final HttpField _vary;
   private final int _bufferSize;
   private final boolean _syncFlush;
   private CompressionPool<Deflater>.Entry _deflaterEntry;
   private RetainableByteBuffer _buffer;

   public GzipResponseAndCallback(GzipHandler handler, Request request, Response response, Callback callback) {
      super(request, response);
      this._callback = callback;
      this._factory = handler;
      this._vary = handler.getVary();
      this._bufferSize = Math.max(GZIP_HEADER.length + 8, request.getConnectionMetaData().getHttpConfiguration().getOutputBufferSize());
      this._syncFlush = handler.isSyncFlush();
   }

   public void succeeded() {
      try {
         this.write(true, null, Callback.NOOP);
         this._callback.succeeded();
      } finally {
         if (this.getRequest() instanceof GzipRequest gzipRequest) {
            gzipRequest.destroy();
         }
      }
   }

   public void failed(Throwable x) {
      try {
         this._callback.failed(x);
      } finally {
         if (this.getRequest() instanceof GzipRequest gzipRequest) {
            gzipRequest.destroy();
         }
      }
   }

   public InvocationType getInvocationType() {
      return this._callback.getInvocationType();
   }

   @Override
   public void write(boolean last, ByteBuffer content, Callback callback) {
      switch ((GzipResponseAndCallback.GZState)this._state.get()) {
         case MIGHT_COMPRESS:
            this.commit(last, callback, content);
            break;
         case NOT_COMPRESSING:
            super.write(last, content, callback);
            break;
         case COMMITTING:
            callback.failed(new WritePendingException());
            break;
         case COMPRESSING:
            this.gzip(last, callback, content);
            break;
         default:
            if (BufferUtil.isEmpty(content)) {
               callback.succeeded();
            } else {
               callback.failed(new IllegalStateException("state=" + this._state.get()));
            }
      }
   }

   private void addTrailer(ByteBuffer outputBuffer) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("addTrailer: _crc={}, _totalIn={})", this._crc.getValue(), ((Deflater)this._deflaterEntry.get()).getTotalIn());
      }

      outputBuffer.putInt((int)this._crc.getValue());
      outputBuffer.putInt(((Deflater)this._deflaterEntry.get()).getTotalIn());
   }

   private void gzip(boolean complete, Callback callback, ByteBuffer content) {
      if (content == null && !complete) {
         callback.succeeded();
      } else {
         new GzipResponseAndCallback.GzipBufferCB(complete, callback, content).iterate();
      }
   }

   protected void commit(boolean last, Callback callback, ByteBuffer content) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("commit(last={}, callback={}, content={})", new Object[]{last, callback, BufferUtil.toDetailString(content)});
      }

      Request request = this.getRequest();
      int sc = this.getStatus();
      if (sc <= 0 || sc >= 200 && sc != 204 && sc != 205 && sc < 300) {
         String ct = this.getHeaders().get(HttpHeader.CONTENT_TYPE);
         if (ct != null) {
            String baseType = HttpField.getValueParameters(ct, null);
            if (!this._factory.isMimeTypeDeflatable(baseType)) {
               LOG.debug("{} exclude by mimeType {}", this, ct);
               this.noCompression();
               super.write(last, content, callback);
               return;
            }
         }

         Mutable fields = this.getHeaders();
         String ce = fields.get(HttpHeader.CONTENT_ENCODING);
         if (ce != null) {
            LOG.debug("{} exclude by content-encoding {}", this, ce);
            this.noCompression();
            super.write(last, content, callback);
         } else {
            if (this._state.compareAndSet(GzipResponseAndCallback.GZState.MIGHT_COMPRESS, GzipResponseAndCallback.GZState.COMMITTING)) {
               if (this._vary != null) {
                  fields.ensureField(this._vary);
               }

               long contentLength = this.getHeaders().getLongField(HttpHeader.CONTENT_LENGTH);
               if (contentLength < 0L && last) {
                  contentLength = (long)BufferUtil.length(content);
               }

               this._deflaterEntry = this._factory.getDeflaterEntry(request, contentLength);
               if (this._deflaterEntry == null) {
                  LOG.debug("{} exclude no deflater", this);
                  this._state.set(GzipResponseAndCallback.GZState.NOT_COMPRESSING);
                  super.write(last, content, callback);
                  return;
               }

               fields.put(CompressedContentFormat.GZIP.getContentEncoding());
               this._crc.reset();
               this.getHeaders().remove(HttpHeader.CONTENT_LENGTH);
               String etag = fields.get(HttpHeader.ETAG);
               if (etag != null) {
                  fields.put(HttpHeader.ETAG, this.etagGzip(etag));
               }

               LOG.debug("{} compressing {}", this, this._deflaterEntry);
               this._state.set(GzipResponseAndCallback.GZState.COMPRESSING);
               if (BufferUtil.isEmpty(content)) {
                  super.write(last, content, callback);
               } else {
                  this.gzip(last, callback, content);
               }
            } else {
               callback.failed(new WritePendingException());
            }
         }
      } else {
         LOG.debug("{} exclude by status {}", this, sc);
         this.noCompression();
         if (sc == 304) {
            String requestEtags = (String)request.getAttribute("o.e.j.s.h.gzip.GzipHandler.etag");
            String responseEtag = this.getHeaders().get(HttpHeader.ETAG);
            if (requestEtags != null && responseEtag != null) {
               String responseEtagGzip = this.etagGzip(responseEtag);
               if (requestEtags.contains(responseEtagGzip)) {
                  this.getHeaders().put(HttpHeader.ETAG, responseEtagGzip);
               }

               if (this._vary != null) {
                  this.getHeaders().ensureField(this._vary);
               }
            }
         }

         super.write(last, content, callback);
      }
   }

   private String etagGzip(String etag) {
      return CompressedContentFormat.GZIP.etag(etag);
   }

   public void noCompression() {
      while (true) {
         switch ((GzipResponseAndCallback.GZState)this._state.get()) {
            case MIGHT_COMPRESS:
               if (!this._state.compareAndSet(GzipResponseAndCallback.GZState.MIGHT_COMPRESS, GzipResponseAndCallback.GZState.NOT_COMPRESSING)) {
                  break;
               }

               return;
            case NOT_COMPRESSING:
               return;
            default:
               throw new IllegalStateException(this._state.get().toString());
         }
      }
   }

   private static enum GZState {
      MIGHT_COMPRESS,
      NOT_COMPRESSING,
      COMMITTING,
      COMPRESSING,
      FINISHING,
      FINISHED;
   }

   private class GzipBufferCB extends IteratingNestedCallback {
      private final ByteBuffer _content;
      private final boolean _last;

      public GzipBufferCB(boolean complete, Callback callback, ByteBuffer content) {
         super(callback);
         this._content = content;
         this._last = complete;
         if (this._content != null) {
            GzipResponseAndCallback.this._crc.update(this._content.slice());
            Deflater deflater = (Deflater)GzipResponseAndCallback.this._deflaterEntry.get();
            deflater.setInput(this._content);
         }

         if (GzipResponseAndCallback.LOG.isDebugEnabled()) {
            GzipResponseAndCallback.LOG
               .debug("GzipBufferCB(complete={}, callback={}, content={})", new Object[]{complete, callback, BufferUtil.toDetailString(content)});
         }
      }

      protected void onCompleteFailure(Throwable x) {
         if (GzipResponseAndCallback.this._deflaterEntry != null) {
            GzipResponseAndCallback.this._deflaterEntry.release();
            GzipResponseAndCallback.this._deflaterEntry = null;
         }

         super.onCompleteFailure(x);
      }

      protected Action process() throws Exception {
         if (GzipResponseAndCallback.LOG.isDebugEnabled()) {
            GzipResponseAndCallback.LOG
               .debug(
                  "GzipBufferCB.process(): _last={}, _buffer={}, _content={}",
                  new Object[]{this._last, GzipResponseAndCallback.this._buffer, BufferUtil.toDetailString(this._content)}
               );
         }

         GzipResponseAndCallback.GZState gzstate = GzipResponseAndCallback.this._state.get();
         if (gzstate == GzipResponseAndCallback.GZState.FINISHED) {
            this.cleanup();
            return Action.SUCCEEDED;
         } else {
            if (GzipResponseAndCallback.this._buffer == null) {
               GzipResponseAndCallback.this._buffer = GzipResponseAndCallback.this.getRequest()
                  .getComponents()
                  .getByteBufferPool()
                  .acquire(GzipResponseAndCallback.this._bufferSize, false);
               ByteBuffer byteBuffer = GzipResponseAndCallback.this._buffer.getByteBuffer();
               byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
               BufferUtil.flipToFill(byteBuffer);
               byteBuffer.put(GzipResponseAndCallback.GZIP_HEADER, 0, GzipResponseAndCallback.GZIP_HEADER.length);
            } else {
               BufferUtil.clearToFill(GzipResponseAndCallback.this._buffer.getByteBuffer());
            }

            Deflater deflater = (Deflater)GzipResponseAndCallback.this._deflaterEntry.get();

            return switch (gzstate) {
               case COMPRESSING -> this.compressing(deflater, GzipResponseAndCallback.this._buffer.getByteBuffer());
               case FINISHING -> this.finishing(deflater, GzipResponseAndCallback.this._buffer.getByteBuffer());
               default -> throw new IllegalStateException("Unexpected state [" + GzipResponseAndCallback.this._state.get() + "]");
            };
         }
      }

      private void cleanup() {
         if (GzipResponseAndCallback.this._deflaterEntry != null) {
            GzipResponseAndCallback.this._deflaterEntry.release();
            GzipResponseAndCallback.this._deflaterEntry = null;
         }

         if (GzipResponseAndCallback.this._buffer != null) {
            GzipResponseAndCallback.this._buffer.release();
            GzipResponseAndCallback.this._buffer = null;
         }
      }

      private int getFlushMode() {
         return GzipResponseAndCallback.this._syncFlush ? 2 : 0;
      }

      private Action compressing(Deflater deflater, ByteBuffer outputBuffer) {
         if (GzipResponseAndCallback.LOG.isDebugEnabled()) {
            GzipResponseAndCallback.LOG.debug("compressing() deflater={}, outputBuffer={}", deflater, BufferUtil.toDetailString(outputBuffer));
         }

         if (!deflater.finished() && !deflater.needsInput()) {
            int len = deflater.deflate(outputBuffer, this.getFlushMode());
            if (len > 0) {
               BufferUtil.flipToFlush(outputBuffer, 0);
               this.write(false, outputBuffer);
               return Action.SCHEDULED;
            }
         }

         if (this._last) {
            GzipResponseAndCallback.this._state.set(GzipResponseAndCallback.GZState.FINISHING);
            deflater.finish();
            return this.finishing(deflater, outputBuffer);
         } else {
            BufferUtil.flipToFlush(outputBuffer, 0);
            if (outputBuffer.hasRemaining()) {
               this.write(false, outputBuffer);
               return Action.SCHEDULED;
            } else if (BufferUtil.isEmpty(this._content)) {
               return Action.SUCCEEDED;
            } else {
               throw new AssertionError("No progress on deflate made for " + this);
            }
         }
      }

      private Action finishing(Deflater deflater, ByteBuffer outputBuffer) {
         if (GzipResponseAndCallback.LOG.isDebugEnabled()) {
            GzipResponseAndCallback.LOG.debug("finishing() deflater={}, outputBuffer={}", deflater, BufferUtil.toDetailString(outputBuffer));
         }

         if (!deflater.finished()) {
            int len = deflater.deflate(outputBuffer, this.getFlushMode());
            if (deflater.finished() && outputBuffer.remaining() >= 8) {
               GzipResponseAndCallback.this._state.set(GzipResponseAndCallback.GZState.FINISHED);
               GzipResponseAndCallback.this.addTrailer(outputBuffer);
               BufferUtil.flipToFlush(outputBuffer, 0);
               this.write(true, outputBuffer);
               return Action.SCHEDULED;
            } else if (len > 0) {
               BufferUtil.flipToFlush(outputBuffer, 0);
               this.write(false, outputBuffer);
               return Action.SCHEDULED;
            } else {
               throw new AssertionError("No progress on deflate made for " + this);
            }
         } else {
            GzipResponseAndCallback.this._state.set(GzipResponseAndCallback.GZState.FINISHED);
            GzipResponseAndCallback.this.addTrailer(outputBuffer);
            BufferUtil.flipToFlush(outputBuffer, 0);
            this.write(true, outputBuffer);
            return Action.SCHEDULED;
         }
      }

      private void write(boolean last, ByteBuffer outputBuffer) {
         if (GzipResponseAndCallback.LOG.isDebugEnabled()) {
            GzipResponseAndCallback.LOG.debug("write() last={}, outputBuffer={}", last, BufferUtil.toDetailString(outputBuffer));
         }

         GzipResponseAndCallback.super.write(last, outputBuffer, this);
      }

      public String toString() {
         return String.format(
            "%s[content=%s last=%b buffer=%s deflate=%s %s]",
            super.toString(),
            BufferUtil.toDetailString(this._content),
            this._last,
            GzipResponseAndCallback.this._buffer,
            GzipResponseAndCallback.this._deflaterEntry,
            GzipResponseAndCallback.this._state.get()
         );
      }
   }
}
