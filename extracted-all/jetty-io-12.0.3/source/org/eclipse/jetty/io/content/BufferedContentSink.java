package org.eclipse.jetty.io.content;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritePendingException;
import org.eclipse.jetty.io.ByteBufferAggregator;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IteratingCallback;
import org.eclipse.jetty.util.Callback.Nested;
import org.eclipse.jetty.util.IteratingCallback.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BufferedContentSink implements Content.Sink {
   public static final ByteBuffer FLUSH_BUFFER = ByteBuffer.wrap(new byte[0]);
   private static final Logger LOG = LoggerFactory.getLogger(BufferedContentSink.class);
   private static final int START_BUFFER_SIZE = 1024;
   private final Content.Sink _delegate;
   private final ByteBufferPool _bufferPool;
   private final boolean _direct;
   private final int _maxBufferSize;
   private final int _maxAggregationSize;
   private final BufferedContentSink.Flusher _flusher;
   private ByteBufferAggregator _aggregator;
   private boolean _firstWrite = true;
   private boolean _lastWritten;

   public BufferedContentSink(Content.Sink delegate, ByteBufferPool bufferPool, boolean direct, int maxAggregationSize, int maxBufferSize) {
      if (maxBufferSize <= 0) {
         throw new IllegalArgumentException("maxBufferSize must be > 0, was: " + maxBufferSize);
      } else if (maxAggregationSize <= 0) {
         throw new IllegalArgumentException("maxAggregationSize must be > 0, was: " + maxAggregationSize);
      } else if (maxBufferSize < maxAggregationSize) {
         throw new IllegalArgumentException("maxBufferSize (" + maxBufferSize + ") must be >= maxAggregationSize (" + maxAggregationSize + ")");
      } else {
         this._delegate = delegate;
         this._bufferPool = (ByteBufferPool)(bufferPool == null ? new ByteBufferPool.NonPooling() : bufferPool);
         this._direct = direct;
         this._maxBufferSize = maxBufferSize;
         this._maxAggregationSize = maxAggregationSize;
         this._flusher = new BufferedContentSink.Flusher(delegate);
      }
   }

   @Override
   public void write(boolean last, ByteBuffer byteBuffer, Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("writing last={} {}", last, BufferUtil.toDetailString(byteBuffer));
      }

      if (this._lastWritten) {
         callback.failed(new IOException("complete"));
      } else {
         this._lastWritten = last;
         if (this._firstWrite) {
            this._firstWrite = false;
            if (last) {
               this._delegate.write(true, byteBuffer, callback);
               return;
            }
         }

         ByteBuffer current = byteBuffer != null ? byteBuffer : BufferUtil.EMPTY_BUFFER;
         if (current.remaining() <= this._maxAggregationSize) {
            if (this._aggregator == null) {
               this._aggregator = new ByteBufferAggregator(this._bufferPool, this._direct, Math.min(1024, this._maxBufferSize), this._maxBufferSize);
            }

            this.aggregateAndFlush(last, current, callback);
         } else {
            this.flush(last, current, callback);
         }
      }
   }

   public void flush(Callback callback) {
      this.flush(false, FLUSH_BUFFER, callback);
   }

   private void flush(final boolean last, final ByteBuffer currentBuffer, final Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("given buffer is greater than _maxBufferSize");
      }

      RetainableByteBuffer aggregatedBuffer = this._aggregator == null ? null : this._aggregator.takeRetainableByteBuffer();
      if (aggregatedBuffer == null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("nothing aggregated, flushing current buffer {}", currentBuffer);
         }

         this._flusher.offer(last, currentBuffer, callback);
      } else if (BufferUtil.hasContent(currentBuffer)) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("flushing aggregated buffer {}", aggregatedBuffer);
         }

         this._flusher.offer(false, aggregatedBuffer.getByteBuffer(), new Nested(Callback.from(aggregatedBuffer::release)) {
            public void succeeded() {
               super.succeeded();
               if (BufferedContentSink.LOG.isDebugEnabled()) {
                  BufferedContentSink.LOG.debug("succeeded writing aggregated buffer, flushing current buffer {}", currentBuffer);
               }

               BufferedContentSink.this._flusher.offer(last, currentBuffer, callback);
            }

            public void failed(Throwable x) {
               if (BufferedContentSink.LOG.isDebugEnabled()) {
                  BufferedContentSink.LOG.debug("failure writing aggregated buffer", x);
               }

               super.failed(x);
               callback.failed(x);
            }
         });
      } else {
         this._flusher.offer(false, aggregatedBuffer.getByteBuffer(), Callback.from(aggregatedBuffer::release, callback));
      }
   }

   private void aggregateAndFlush(final boolean last, final ByteBuffer currentBuffer, final Callback callback) {
      boolean full = this._aggregator.aggregate(currentBuffer);
      boolean empty = !currentBuffer.hasRemaining();
      boolean flush = full || currentBuffer == FLUSH_BUFFER;
      boolean complete = last && empty;
      if (LOG.isDebugEnabled()) {
         LOG.debug(
            "aggregated current buffer, full={}, complete={}, bytes left={}, aggregator={}",
            new Object[]{full, complete, currentBuffer.remaining(), this._aggregator}
         );
      }

      if (complete) {
         RetainableByteBuffer aggregatedBuffer = this._aggregator.takeRetainableByteBuffer();
         if (aggregatedBuffer != null) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("complete; writing aggregated buffer as the last one: {} bytes", aggregatedBuffer.remaining());
            }

            this._flusher.offer(true, aggregatedBuffer.getByteBuffer(), Callback.from(callback, aggregatedBuffer::release));
         } else {
            if (LOG.isDebugEnabled()) {
               LOG.debug("complete; no aggregated buffer, writing last empty buffer");
            }

            this._flusher.offer(true, BufferUtil.EMPTY_BUFFER, callback);
         }
      } else if (flush) {
         RetainableByteBuffer aggregatedBuffer = this._aggregator.takeRetainableByteBuffer();
         if (LOG.isDebugEnabled()) {
            LOG.debug("writing aggregated buffer: {} bytes, then {}", aggregatedBuffer.remaining(), currentBuffer.remaining());
         }

         if (BufferUtil.hasContent(currentBuffer)) {
            this._flusher
               .offer(
                  false,
                  aggregatedBuffer.getByteBuffer(),
                  new Nested(Callback.from(aggregatedBuffer::release)) {
                     public void succeeded() {
                        super.succeeded();
                        if (BufferedContentSink.LOG.isDebugEnabled()) {
                           BufferedContentSink.LOG
                              .debug(
                                 "written aggregated buffer, writing remaining of current: {} bytes{}", currentBuffer.remaining(), last ? " (last write)" : ""
                              );
                        }

                        if (last) {
                           BufferedContentSink.this._flusher.offer(true, currentBuffer, callback);
                        } else {
                           BufferedContentSink.this.aggregateAndFlush(false, currentBuffer, callback);
                        }
                     }

                     public void failed(Throwable x) {
                        if (BufferedContentSink.LOG.isDebugEnabled()) {
                           BufferedContentSink.LOG.debug("failure writing aggregated buffer", x);
                        }

                        super.failed(x);
                        callback.failed(x);
                     }
                  }
               );
         } else {
            this._flusher.offer(false, aggregatedBuffer.getByteBuffer(), Callback.from(aggregatedBuffer::release, callback));
         }
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("buffer fully aggregated, delaying writing - aggregator: {}", this._aggregator);
         }

         this._flusher.offer(callback);
      }
   }

   private static class Flusher extends IteratingCallback {
      private static final ByteBuffer COMPLETE_CALLBACK = BufferUtil.allocate(0);
      private final Content.Sink _sink;
      private boolean _last;
      private ByteBuffer _buffer;
      private Callback _callback;
      private boolean _lastWritten;

      Flusher(Content.Sink sink) {
         this._sink = sink;
      }

      void offer(Callback callback) {
         this.offer(false, COMPLETE_CALLBACK, callback);
      }

      void offer(boolean last, ByteBuffer byteBuffer, Callback callback) {
         if (this._callback != null) {
            throw new WritePendingException();
         } else {
            this._last = last;
            this._buffer = byteBuffer;
            this._callback = callback;
            this.iterate();
         }
      }

      protected Action process() {
         if (this._lastWritten) {
            return Action.SUCCEEDED;
         } else if (this._callback == null) {
            return Action.IDLE;
         } else {
            if (this._buffer != COMPLETE_CALLBACK) {
               this._lastWritten = this._last;
               this._sink.write(this._last, this._buffer, this);
            } else {
               this.succeeded();
            }

            return Action.SCHEDULED;
         }
      }

      public void succeeded() {
         this._buffer = null;
         Callback callback = this._callback;
         this._callback = null;
         callback.succeeded();
         super.succeeded();
      }

      protected void onCompleteFailure(Throwable cause) {
         this._buffer = null;
         this._callback.failed(cause);
      }
   }
}
