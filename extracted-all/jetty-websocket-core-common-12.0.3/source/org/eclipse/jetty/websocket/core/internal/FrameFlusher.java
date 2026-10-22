package org.eclipse.jetty.websocket.core.internal;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.Retainable;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IteratingCallback;
import org.eclipse.jetty.util.StaticException;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.IteratingCallback.Action;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.websocket.core.CloseStatus;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.exception.WebSocketException;
import org.eclipse.jetty.websocket.core.exception.WebSocketWriteTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameFlusher extends IteratingCallback {
   public static final Frame FLUSH_FRAME = new Frame((byte)-1) {
      @Override
      public boolean isControlFrame() {
         return true;
      }
   };
   private static final Logger LOG = LoggerFactory.getLogger(FrameFlusher.class);
   private static final Throwable CLOSED_CHANNEL = new StaticException("Closed");
   private final AutoLock lock = new AutoLock();
   private final LongAdder messagesOut = new LongAdder();
   private final LongAdder bytesOut = new LongAdder();
   private final ByteBufferPool bufferPool;
   private final EndPoint endPoint;
   private final int bufferSize;
   private final Generator generator;
   private final int maxGather;
   private final Deque<FrameFlusher.Entry> queue = new ArrayDeque<>();
   private final List<ByteBuffer> buffers;
   private final Scheduler timeoutScheduler;
   private final List<FrameFlusher.Entry> entries;
   private final List<FrameFlusher.Entry> previousEntries;
   private final List<FrameFlusher.Entry> failedEntries;
   private List<RetainableByteBuffer> releasableBuffers = new ArrayList<>();
   private RetainableByteBuffer batchBuffer;
   private boolean canEnqueue = true;
   private boolean flushed = true;
   private Throwable closedCause;
   private long idleTimeout;
   private boolean useDirectByteBuffers;

   public FrameFlusher(ByteBufferPool bufferPool, Scheduler scheduler, Generator generator, EndPoint endPoint, int bufferSize, int maxGather) {
      this.bufferPool = bufferPool;
      this.endPoint = endPoint;
      this.bufferSize = bufferSize;
      this.generator = Objects.requireNonNull(generator);
      this.maxGather = maxGather;
      this.entries = new ArrayList<>(maxGather);
      this.previousEntries = new ArrayList<>(maxGather);
      this.failedEntries = new ArrayList<>(maxGather);
      this.buffers = new ArrayList<>(maxGather * 2 + 1);
      this.timeoutScheduler = scheduler;
   }

   public boolean isUseDirectByteBuffers() {
      return this.useDirectByteBuffers;
   }

   public void setUseDirectByteBuffers(boolean useDirectByteBuffers) {
      this.useDirectByteBuffers = useDirectByteBuffers;
   }

   public boolean enqueue(Frame frame, Callback callback, boolean batch) {
      FrameFlusher.Entry entry = new FrameFlusher.Entry(frame, callback, batch);
      byte opCode = frame.getOpCode();
      List<FrameFlusher.Entry> failedEntries = null;
      CloseStatus closeStatus = null;
      AutoLock l = this.lock.lock();

      Throwable dead;
      try {
         if (this.canEnqueue) {
            dead = this.closedCause;
            if (dead == null) {
               switch (opCode) {
                  case 8:
                     closeStatus = CloseStatus.getCloseStatus(frame);
                     if (closeStatus.isAbnormal()) {
                        failedEntries = new ArrayList<>(this.queue);
                        this.queue.clear();
                     }

                     this.queue.offerLast(entry);
                     this.canEnqueue = false;
                     break;
                  case 9:
                  case 10:
                     this.queue.offerFirst(entry);
                     break;
                  default:
                     this.queue.offerLast(entry);
               }

               if (this.idleTimeout > 0L && this.queue.size() == 1 && this.entries.isEmpty()) {
                  this.timeoutScheduler.schedule(this::timeoutExpired, this.idleTimeout, TimeUnit.MILLISECONDS);
               }
            }
         } else {
            dead = new ClosedChannelException();
         }
      } catch (Throwable var13) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var12) {
               var13.addSuppressed(var12);
            }
         }

         throw var13;
      }

      if (l != null) {
         l.close();
      }

      if (failedEntries != null) {
         WebSocketException failure = new WebSocketException(
            "Flusher received abnormal CloseFrame: " + CloseStatus.codeString(closeStatus.getCode()), closeStatus.getCause()
         );

         for (FrameFlusher.Entry e : failedEntries) {
            this.notifyCallbackFailure(e.callback, failure);
         }
      }

      if (dead == null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Enqueued {} to {}", entry, this);
         }

         return true;
      } else {
         this.notifyCallbackFailure(callback, dead);
         return false;
      }
   }

   public void onClose(Throwable cause) {
      AutoLock l = this.lock.lock();

      try {
         this.closedCause = cause == null ? CLOSED_CHANNEL : cause;
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }

      this.iterate();
   }

   protected Action process() throws Throwable {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Flushing {}", this);
      }

      boolean flush = false;
      Callback releasingCallback = this;
      AutoLock l = this.lock.lock();

      try {
         if (this.closedCause != null) {
            throw this.closedCause;
         }

         this.previousEntries.addAll(this.entries);
         this.entries.clear();
         if (this.flushed && this.batchBuffer != null) {
            this.batchBuffer.clear();
         }

         for (; !this.queue.isEmpty() && this.entries.size() <= this.maxGather; this.flushed = flush) {
            FrameFlusher.Entry entry = this.queue.poll();
            this.entries.add(entry);
            if (entry.frame == FLUSH_FRAME) {
               flush = true;
               break;
            }

            this.messagesOut.increment();
            int batchSpace = this.batchBuffer == null ? this.bufferSize : BufferUtil.space(this.batchBuffer.getByteBuffer());
            boolean batch = entry.batch
               && !entry.frame.isControlFrame()
               && entry.frame.getPayloadLength() < this.bufferSize / 4
               && batchSpace - 28 >= entry.frame.getPayloadLength();
            if (batch) {
               if (this.batchBuffer == null) {
                  this.batchBuffer = this.acquireBuffer(this.bufferSize);
                  this.buffers.add(this.batchBuffer.getByteBuffer());
               }

               this.generator.generateWholeFrame(entry.frame, this.batchBuffer.getByteBuffer());
            } else {
               if (this.batchBuffer != null && batchSpace >= 28) {
                  this.generator.generateHeader(entry.frame, this.batchBuffer.getByteBuffer());
               } else {
                  RetainableByteBuffer headerBuffer = this.acquireBuffer(28);
                  this.releasableBuffers.add(headerBuffer);
                  this.generator.generateHeader(entry.frame, headerBuffer.getByteBuffer());
                  this.buffers.add(headerBuffer.getByteBuffer());
               }

               ByteBuffer payload = entry.frame.getPayload();
               if (BufferUtil.hasContent(payload)) {
                  if (entry.frame.isMasked()) {
                     RetainableByteBuffer masked = this.acquireBuffer(entry.frame.getPayloadLength());
                     payload = masked.getByteBuffer();
                     this.releasableBuffers.add(masked);
                     this.generator.generatePayload(entry.frame, payload);
                  }

                  this.buffers.add(payload.slice());
               }

               flush = true;
            }
         }

         if (flush) {
            List<RetainableByteBuffer> callbackBuffers = this.releasableBuffers;
            this.releasableBuffers = new ArrayList<>();
            releasingCallback = Callback.from(releasingCallback, () -> callbackBuffers.forEach(Retainable::release));
         }
      } catch (Throwable var10) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (l != null) {
         l.close();
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("{} processed {} entries flush={} batch={}: {}", new Object[]{this, this.entries.size(), flush, this.batchBuffer, this.entries});
      }

      for (FrameFlusher.Entry entryx : this.previousEntries) {
         if (entryx.frame.getOpCode() == 8) {
            this.endPoint.shutdownOutput();
         }

         this.notifyCallbackSuccess(entryx.callback);
      }

      this.previousEntries.clear();
      if (this.entries.isEmpty()) {
         this.releaseAggregate();
         return Action.IDLE;
      } else {
         if (flush) {
            int i = 0;
            int bytes = 0;
            ByteBuffer[] bufferArray = new ByteBuffer[this.buffers.size()];

            for (ByteBuffer bb : this.buffers) {
               bytes += bb.limit() - bb.position();
               bufferArray[i++] = bb;
            }

            this.bytesOut.add((long)bytes);
            this.endPoint.write(releasingCallback, bufferArray);
            this.buffers.clear();
         } else {
            this.succeeded();
         }

         return Action.SCHEDULED;
      }
   }

   private RetainableByteBuffer acquireBuffer(int capacity) {
      return this.bufferPool.acquire(capacity, this.isUseDirectByteBuffers());
   }

   private int getQueueSize() {
      AutoLock l = this.lock.lock();

      int var2;
      try {
         var2 = this.queue.size();
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   public void timeoutExpired() {
      boolean failed = false;
      AutoLock l = this.lock.lock();

      label72: {
         try {
            if (this.closedCause != null) {
               break label72;
            }

            long currentTime = System.currentTimeMillis();
            long expiredIfCreatedBefore = currentTime - this.idleTimeout;
            long earliestEntry = currentTime;
            Iterator<FrameFlusher.Entry> iterator = TypeUtil.concat(this.entries.iterator(), this.queue.iterator());

            while (iterator.hasNext()) {
               FrameFlusher.Entry entry = iterator.next();
               if (entry.getTimeOfCreation() <= expiredIfCreatedBefore) {
                  LOG.warn("FrameFlusher write timeout on entry: {}", entry);
                  failed = true;
                  this.canEnqueue = false;
                  this.closedCause = new WebSocketWriteTimeoutException("FrameFlusher Write Timeout");
                  this.failedEntries.addAll(this.entries);
                  this.failedEntries.addAll(this.queue);
                  this.entries.clear();
                  this.queue.clear();
                  break;
               }

               if (entry.getTimeOfCreation() < earliestEntry) {
                  earliestEntry = entry.getTimeOfCreation();
               }
            }

            if (!failed && this.idleTimeout > 0L && (!this.entries.isEmpty() || !this.queue.isEmpty())) {
               long nextTimeout = earliestEntry + this.idleTimeout - currentTime;
               this.timeoutScheduler.schedule(this::timeoutExpired, nextTimeout, TimeUnit.MILLISECONDS);
            }
         } catch (Throwable var13) {
            if (l != null) {
               try {
                  l.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }
            }

            throw var13;
         }

         if (l != null) {
            l.close();
         }

         if (failed) {
            this.iterate();
         }

         return;
      }

      if (l != null) {
         l.close();
      }
   }

   public void onCompleteFailure(Throwable failure) {
      if (this.batchBuffer != null) {
         this.batchBuffer.clear();
      }

      this.releaseAggregate();
      AutoLock l = this.lock.lock();

      try {
         this.failedEntries.addAll(this.queue);
         this.queue.clear();
         this.failedEntries.addAll(this.entries);
         this.entries.clear();
         this.releasableBuffers.forEach(Retainable::release);
         this.releasableBuffers.clear();
         if (this.closedCause == null) {
            this.closedCause = failure;
         } else if (this.closedCause != failure) {
            this.closedCause.addSuppressed(failure);
         }
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }

      for (FrameFlusher.Entry entry : this.failedEntries) {
         this.notifyCallbackFailure(entry.callback, failure);
      }

      this.failedEntries.clear();
      this.endPoint.close(this.closedCause);
   }

   private void releaseAggregate() {
      if (this.batchBuffer != null && !this.batchBuffer.hasRemaining()) {
         this.batchBuffer.release();
         this.batchBuffer = null;
      }
   }

   protected void notifyCallbackSuccess(Callback callback) {
      try {
         if (callback != null) {
            callback.succeeded();
         }
      } catch (Throwable var3) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Exception while notifying success of callback {}", callback, var3);
         }
      }
   }

   protected void notifyCallbackFailure(Callback callback, Throwable failure) {
      try {
         if (callback != null) {
            callback.failed(failure);
         }
      } catch (Throwable var4) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Exception while notifying failure of callback {}", callback, var4);
         }
      }
   }

   public void setIdleTimeout(long idleTimeout) {
      this.idleTimeout = idleTimeout;
   }

   public long getIdleTimeout() {
      return this.idleTimeout;
   }

   public long getMessagesOut() {
      return this.messagesOut.longValue();
   }

   public long getBytesOut() {
      return this.bytesOut.longValue();
   }

   public String toString() {
      return String.format("%s[queueSize=%d,aggregate=%s]", super.toString(), this.getQueueSize(), this.batchBuffer);
   }

   private static class Entry extends FrameEntry {
      private final long timeOfCreation = System.currentTimeMillis();

      private Entry(Frame frame, Callback callback, boolean batch) {
         super(frame, callback, batch);
      }

      private long getTimeOfCreation() {
         return this.timeOfCreation;
      }

      @Override
      public String toString() {
         return String.format("%s{%s,%s,%b}", this.getClass().getSimpleName(), this.frame, this.callback, this.batch);
      }
   }
}
