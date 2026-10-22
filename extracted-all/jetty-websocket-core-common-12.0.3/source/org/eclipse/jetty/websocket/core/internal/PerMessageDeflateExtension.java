package org.eclipse.jetty.websocket.core.internal;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.compression.CompressionPool;
import org.eclipse.jetty.websocket.core.AbstractExtension;
import org.eclipse.jetty.websocket.core.ExtensionConfig;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.WebSocketComponents;
import org.eclipse.jetty.websocket.core.exception.BadPayloadException;
import org.eclipse.jetty.websocket.core.exception.MessageTooLargeException;
import org.eclipse.jetty.websocket.core.exception.ProtocolException;
import org.eclipse.jetty.websocket.core.util.DemandChain;
import org.eclipse.jetty.websocket.core.util.DemandingFlusher;
import org.eclipse.jetty.websocket.core.util.TransformingFlusher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerMessageDeflateExtension extends AbstractExtension implements DemandChain {
   private static final byte[] TAIL_BYTES = new byte[]{0, 0, -1, -1};
   private static final ByteBuffer TAIL_BYTES_BUF = ByteBuffer.wrap(TAIL_BYTES);
   private static final Logger LOG = LoggerFactory.getLogger(PerMessageDeflateExtension.class);
   private static final int DEFAULT_BUF_SIZE = 8192;
   private final PerMessageDeflateExtension.OutgoingFlusher outgoingFlusher;
   private final PerMessageDeflateExtension.IncomingFlusher incomingFlusher;
   private CompressionPool<Deflater>.Entry deflaterHolder;
   private CompressionPool<Inflater>.Entry inflaterHolder;
   private boolean incomingCompressed;
   private ExtensionConfig configRequested;
   private ExtensionConfig configNegotiated;
   private int deflateBufferSize = 8192;
   private int inflateBufferSize = 8192;
   private boolean incomingContextTakeover = true;
   private boolean outgoingContextTakeover = true;

   public PerMessageDeflateExtension() {
      this.outgoingFlusher = new PerMessageDeflateExtension.OutgoingFlusher();
      this.incomingFlusher = new PerMessageDeflateExtension.IncomingFlusher();
   }

   @Override
   public String getName() {
      return "permessage-deflate";
   }

   @Override
   public boolean isRsv1User() {
      return true;
   }

   @Override
   public void sendFrame(Frame frame, Callback callback, boolean batch) {
      this.outgoingFlusher.sendFrame(frame, callback, batch);
   }

   @Override
   public void onFrame(Frame frame, Callback callback) {
      this.incomingFlusher.onFrame(frame, callback);
   }

   @Override
   public void init(ExtensionConfig config, WebSocketComponents components) {
      this.configRequested = new ExtensionConfig(config);
      Map<String, String> paramsNegotiated = new HashMap<>();

      for (String key : config.getParameterKeys()) {
         key = key.trim();
         switch (key) {
            case "client_max_window_bits":
            case "server_max_window_bits":
               break;
            case "client_no_context_takeover":
               paramsNegotiated.put("client_no_context_takeover", null);
               this.incomingContextTakeover = false;
               break;
            case "server_no_context_takeover":
               paramsNegotiated.put("server_no_context_takeover", null);
               this.outgoingContextTakeover = false;
               break;
            case "@deflate_buffer_size":
               this.deflateBufferSize = config.getParameter(key, 8192);
               break;
            case "@inflate_buffer_size":
               this.inflateBufferSize = config.getParameter(key, 8192);
               break;
            default:
               throw new IllegalArgumentException();
         }
      }

      this.configNegotiated = new ExtensionConfig(config.getName(), paramsNegotiated);
      LOG.debug(
         "config: outgoingContextTakover={}, incomingContextTakeover={} : {}", new Object[]{this.outgoingContextTakeover, this.incomingContextTakeover, this}
      );
      super.init(this.configNegotiated, components);
   }

   @Override
   public void close() {
      this.incomingFlusher.closeFlusher();
      this.outgoingFlusher.closeFlusher();
      this.releaseInflater();
      this.releaseDeflater();
   }

   private static String toDetail(Inflater inflater) {
      return String.format(
         "Inflater[finished=%b,read=%d,written=%d,remaining=%d,in=%d,out=%d]",
         inflater.finished(),
         inflater.getBytesRead(),
         inflater.getBytesWritten(),
         inflater.getRemaining(),
         inflater.getTotalIn(),
         inflater.getTotalOut()
      );
   }

   private static String toDetail(Deflater deflater) {
      return String.format(
         "Deflater[finished=%b,read=%d,written=%d,in=%d,out=%d]",
         deflater.finished(),
         deflater.getBytesRead(),
         deflater.getBytesWritten(),
         deflater.getTotalIn(),
         deflater.getTotalOut()
      );
   }

   public static boolean endsWithTail(ByteBuffer buf) {
      if (buf != null && buf.remaining() >= TAIL_BYTES.length) {
         int limit = buf.limit();

         for (int i = TAIL_BYTES.length; i > 0; i--) {
            if (buf.get(limit - i) != TAIL_BYTES[TAIL_BYTES.length - i]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public Deflater getDeflater() {
      if (this.deflaterHolder == null) {
         this.deflaterHolder = this.getDeflaterPool().acquire();
      }

      return (Deflater)this.deflaterHolder.get();
   }

   public Inflater getInflater() {
      if (this.inflaterHolder == null) {
         this.inflaterHolder = this.getInflaterPool().acquire();
      }

      return (Inflater)this.inflaterHolder.get();
   }

   public void releaseInflater() {
      if (this.inflaterHolder != null) {
         this.inflaterHolder.release();
         this.inflaterHolder = null;
      }
   }

   public void releaseDeflater() {
      if (this.deflaterHolder != null) {
         this.deflaterHolder.release();
         this.deflaterHolder = null;
      }
   }

   @Override
   public String toString() {
      return String.format(
         "%s[requested=\"%s\", negotiated=\"%s\"]",
         this.getClass().getSimpleName(),
         this.configRequested.getParameterizedName(),
         this.configNegotiated.getParameterizedName()
      );
   }

   @Override
   protected void nextIncomingFrame(Frame frame, Callback callback) {
      if (frame.isFin() && !this.incomingContextTakeover) {
         LOG.debug("Incoming Context Reset");
         this.releaseInflater();
      }

      super.nextIncomingFrame(frame, callback);
   }

   @Override
   protected void nextOutgoingFrame(Frame frame, Callback callback, boolean batch) {
      if (frame.isFin() && !this.outgoingContextTakeover) {
         LOG.debug("Outgoing Context Reset");
         this.releaseDeflater();
      }

      super.nextOutgoingFrame(frame, callback, batch);
   }

   @Override
   public void setNextDemand(DemandChain nextDemand) {
      this.incomingFlusher.setNextDemand(nextDemand);
   }

   @Override
   public void demand() {
      this.incomingFlusher.demand();
   }

   private class IncomingFlusher extends DemandingFlusher {
      private boolean _tailBytes;
      private AtomicReference<RetainableByteBuffer> _payloadRef;

      public IncomingFlusher() {
         super(PerMessageDeflateExtension.this::nextIncomingFrame);
      }

      @Override
      protected boolean handle(Frame frame, Callback callback, boolean first) {
         if (first) {
            if (frame.isControlFrame()) {
               this.emitFrame(frame, callback);
               return true;
            }

            switch (frame.getOpCode()) {
               case 0:
                  if (frame.isRsv1()) {
                     throw new ProtocolException("Invalid RSV1 set on permessage-deflate CONTINUATION frame");
                  }
                  break;
               case 1:
               case 2:
                  PerMessageDeflateExtension.this.incomingCompressed = frame.isRsv1();
            }

            if (!PerMessageDeflateExtension.this.incomingCompressed) {
               this.emitFrame(frame, callback);
               return true;
            }

            this._tailBytes = false;
            PerMessageDeflateExtension.this.getInflater().setInput(frame.getPayload().slice());
         }

         try {
            return this.inflate(frame, callback, first);
         } catch (DataFormatException var5) {
            throw new BadPayloadException(var5);
         }
      }

      private boolean inflate(Frame frame, Callback callback, boolean first) throws DataFormatException {
         long maxFrameSize = PerMessageDeflateExtension.this.getConfiguration().getMaxFrameSize();
         int bufferSize = maxFrameSize <= 0L
            ? PerMessageDeflateExtension.this.inflateBufferSize
            : (int)Math.min(maxFrameSize, (long)PerMessageDeflateExtension.this.inflateBufferSize);
         RetainableByteBuffer payload = PerMessageDeflateExtension.this.getByteBufferPool().acquire(bufferSize, false);
         this._payloadRef = new AtomicReference<>(payload);
         ByteBuffer byteBuffer = payload.getByteBuffer();
         payload.clear();
         Inflater inflater = PerMessageDeflateExtension.this.getInflater();
         boolean complete = false;

         while (true) {
            int decompressed = inflater.inflate(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), bufferSize - byteBuffer.position());
            byteBuffer.limit(byteBuffer.limit() + decompressed);
            if (PerMessageDeflateExtension.LOG.isDebugEnabled()) {
               PerMessageDeflateExtension.LOG.debug("Decompress: read {} {}", decompressed, PerMessageDeflateExtension.toDetail(inflater));
            }

            if (byteBuffer.limit() == bufferSize) {
               if (!PerMessageDeflateExtension.this.getConfiguration().isAutoFragment()) {
                  throw new MessageTooLargeException("Inflated payload exceeded the decompress buffer size");
               }
               break;
            }

            if (decompressed == 0) {
               if (this._tailBytes || !frame.isFin()) {
                  complete = true;
                  break;
               }

               inflater.setInput(PerMessageDeflateExtension.TAIL_BYTES_BUF.slice());
               this._tailBytes = true;
            }
         }

         Frame chunk = new Frame(first ? frame.getOpCode() : 0);
         chunk.setRsv1(false);
         chunk.setPayload(byteBuffer);
         chunk.setFin(frame.isFin() && complete);
         boolean completeCallback = complete;
         AtomicReference<RetainableByteBuffer> payloadRef = this._payloadRef;
         Callback payloadCallback = Callback.from(() -> {
            this.releasePayload(payloadRef);
            if (completeCallback) {
               callback.succeeded();
            }
         }, t -> {
            this.releasePayload(payloadRef);
            if (completeCallback) {
               callback.failed(t);
            }

            this.failFlusher(t);
         });
         this.emitFrame(chunk, payloadCallback);
         if (PerMessageDeflateExtension.LOG.isDebugEnabled()) {
            PerMessageDeflateExtension.LOG.debug("Decompress finished: {} {}", complete, chunk);
         }

         return complete;
      }

      @Override
      protected void onCompleteFailure(Throwable cause) {
         this.releasePayload(this._payloadRef);
         super.onCompleteFailure(cause);
      }

      private void releasePayload(AtomicReference<RetainableByteBuffer> reference) {
         if (reference != null) {
            RetainableByteBuffer buffer = reference.getAndSet(null);
            if (buffer != null) {
               buffer.release();
            }
         }
      }
   }

   private class OutgoingFlusher extends TransformingFlusher {
      private boolean _first;
      private Frame _frame;
      private boolean _batch;

      @Override
      protected boolean onFrame(Frame frame, Callback callback, boolean batch) {
         if (frame.isControlFrame()) {
            PerMessageDeflateExtension.this.nextOutgoingFrame(frame, callback, batch);
            return true;
         } else {
            this._first = true;
            this._frame = frame;
            this._batch = batch;
            PerMessageDeflateExtension.this.getDeflater().setInput(frame.getPayload().slice());
            callback.succeeded();
            return false;
         }
      }

      @Override
      protected boolean transform(Callback callback) {
         boolean finished = this.deflate(callback);
         this._first = false;
         return finished;
      }

      private boolean deflate(Callback callback) {
         long maxFrameSize = PerMessageDeflateExtension.this.getConfiguration().getMaxFrameSize();
         int bufferSize = maxFrameSize <= 0L
            ? PerMessageDeflateExtension.this.deflateBufferSize
            : (int)Math.min(maxFrameSize, (long)PerMessageDeflateExtension.this.deflateBufferSize);
         RetainableByteBuffer buffer = PerMessageDeflateExtension.this.getByteBufferPool().acquire(bufferSize, false);
         ByteBuffer byteBuffer = buffer.getByteBuffer();
         callback = Callback.from(callback, buffer::release);
         buffer.clear();
         boolean finished = false;
         Deflater deflater = PerMessageDeflateExtension.this.getDeflater();

         while (true) {
            int compressed = deflater.deflate(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), bufferSize - byteBuffer.position(), 2);
            byteBuffer.limit(byteBuffer.limit() + compressed);
            if (PerMessageDeflateExtension.LOG.isDebugEnabled()) {
               PerMessageDeflateExtension.LOG.debug("Compressed {} bytes {}", compressed, PerMessageDeflateExtension.toDetail(deflater));
            }

            if (byteBuffer.limit() == bufferSize) {
               if (!PerMessageDeflateExtension.this.getConfiguration().isAutoFragment()) {
                  throw new MessageTooLargeException("Deflated payload exceeded the compress buffer size");
               }
               break;
            }

            if (compressed == 0) {
               finished = true;
               break;
            }
         }

         ByteBuffer payload = byteBuffer;
         if (byteBuffer.hasRemaining()) {
            if (finished && this._frame.isFin() && PerMessageDeflateExtension.endsWithTail(byteBuffer)) {
               byteBuffer.limit(byteBuffer.limit() - PerMessageDeflateExtension.TAIL_BYTES.length);
               if (PerMessageDeflateExtension.LOG.isDebugEnabled()) {
                  PerMessageDeflateExtension.LOG.debug("payload (TAIL_DROP_FIN_ONLY) = {}", BufferUtil.toDetailString(byteBuffer));
               }
            }
         } else if (this._frame.isFin()) {
            payload = ByteBuffer.wrap(new byte[]{0});
         }

         if (PerMessageDeflateExtension.LOG.isDebugEnabled()) {
            PerMessageDeflateExtension.LOG.debug("Compressed {}: payload:{}", this._frame, payload.remaining());
         }

         Frame chunk = new Frame(this._first ? this._frame.getOpCode() : 0);
         chunk.setRsv1(this._first && this._frame.getOpCode() != 0);
         chunk.setPayload(payload);
         chunk.setFin(this._frame.isFin() && finished);
         PerMessageDeflateExtension.this.nextOutgoingFrame(chunk, callback, this._batch);
         return finished;
      }
   }
}
