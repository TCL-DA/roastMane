package org.eclipse.jetty.websocket.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadPendingException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.LongAdder;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.Connection.UpgradeTo;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.websocket.core.exception.WebSocketTimeoutException;
import org.eclipse.jetty.websocket.core.internal.FrameFlusher;
import org.eclipse.jetty.websocket.core.internal.Generator;
import org.eclipse.jetty.websocket.core.internal.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketConnection extends AbstractConnection implements UpgradeTo, Dumpable, Runnable {
   private static final Logger LOG = LoggerFactory.getLogger(WebSocketConnection.class);
   private static final int MIN_BUFFER_SIZE = 28;
   private final AutoLock lock = new AutoLock();
   private final ByteBufferPool byteBufferPool;
   private final Generator generator;
   private final Parser parser;
   private final WebSocketCoreSession coreSession;
   private final WebSocketConnection.Flusher flusher;
   private final Random random;
   private WebSocketConnection.DemandState demand = WebSocketConnection.DemandState.NOT_DEMANDING;
   private boolean fillingAndParsing;
   private final LongAdder messagesIn = new LongAdder();
   private final LongAdder bytesIn = new LongAdder();
   private RetainableByteBuffer networkBuffer;
   private boolean useInputDirectByteBuffers;
   private boolean useOutputDirectByteBuffers;

   public WebSocketConnection(EndPoint endp, Executor executor, Scheduler scheduler, ByteBufferPool byteBufferPool, WebSocketCoreSession coreSession) {
      this(endp, executor, scheduler, byteBufferPool, coreSession, null);
   }

   public WebSocketConnection(
      EndPoint endp, Executor executor, Scheduler scheduler, ByteBufferPool byteBufferPool, WebSocketCoreSession coreSession, Random randomMask
   ) {
      super(endp, executor);
      Objects.requireNonNull(endp, "EndPoint");
      Objects.requireNonNull(coreSession, "Session");
      Objects.requireNonNull(executor, "Executor");
      Objects.requireNonNull(byteBufferPool, "ByteBufferPool");
      this.byteBufferPool = byteBufferPool;
      this.coreSession = coreSession;
      this.generator = new Generator();
      this.parser = new Parser(byteBufferPool, coreSession);
      this.flusher = new WebSocketConnection.Flusher(scheduler, coreSession.getOutputBufferSize(), this.generator, endp);
      this.setInputBufferSize(coreSession.getInputBufferSize());
      if (this.coreSession.getBehavior() == Behavior.CLIENT && randomMask == null) {
         randomMask = new SecureRandom();
      }

      this.random = randomMask;
   }

   public Executor getExecutor() {
      return super.getExecutor();
   }

   @Deprecated
   public InetSocketAddress getLocalAddress() {
      SocketAddress local = this.getLocalSocketAddress();
      return local instanceof InetSocketAddress ? (InetSocketAddress)local : null;
   }

   public SocketAddress getLocalSocketAddress() {
      return this.getEndPoint().getLocalSocketAddress();
   }

   @Deprecated
   public InetSocketAddress getRemoteAddress() {
      SocketAddress remote = this.getRemoteSocketAddress();
      return remote instanceof InetSocketAddress ? (InetSocketAddress)remote : null;
   }

   public SocketAddress getRemoteSocketAddress() {
      return this.getEndPoint().getRemoteSocketAddress();
   }

   public boolean isUseInputDirectByteBuffers() {
      return this.useInputDirectByteBuffers;
   }

   public void setWriteTimeout(long writeTimeout) {
      this.flusher.setIdleTimeout(writeTimeout);
   }

   public void setUseInputDirectByteBuffers(boolean useInputDirectByteBuffers) {
      this.useInputDirectByteBuffers = useInputDirectByteBuffers;
   }

   public boolean isUseOutputDirectByteBuffers() {
      return this.useOutputDirectByteBuffers;
   }

   public void setUseOutputDirectByteBuffers(boolean useOutputDirectByteBuffers) {
      this.useOutputDirectByteBuffers = useOutputDirectByteBuffers;
   }

   public void onClose(Throwable cause) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onClose() of physical connection");
      }

      if (!this.coreSession.isClosed()) {
         this.coreSession.onEof();
      }

      this.flusher.onClose(cause);
      AutoLock ignored = this.lock.lock();

      try {
         if (this.networkBuffer != null) {
            this.networkBuffer.clear();
            this.releaseNetworkBuffer();
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

      super.onClose(cause);
   }

   public boolean onIdleExpired(TimeoutException timeoutException) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onIdleExpired()");
      }

      this.coreSession.processHandlerError(new WebSocketTimeoutException("Connection Idle Timeout", timeoutException), Callback.NOOP);
      return true;
   }

   protected boolean onReadTimeout(TimeoutException timeout) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onReadTimeout()");
      }

      this.coreSession.processHandlerError(new WebSocketTimeoutException("Timeout on Read", timeout), Callback.NOOP);
      return false;
   }

   protected void onFrame(final Frame.Parsed frame) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onFrame({})", frame);
      }

      final RetainableByteBuffer referenced = frame.hasPayload() && !frame.isReleaseable() ? this.networkBuffer : null;
      if (referenced != null) {
         referenced.retain();
      }

      this.coreSession.onFrame(frame, new Callback() {
         public void succeeded() {
            if (WebSocketConnection.LOG.isDebugEnabled()) {
               WebSocketConnection.LOG.debug("succeeded onFrame({})", frame);
            }

            frame.close();
            if (referenced != null) {
               referenced.release();
            }
         }

         public void failed(Throwable cause) {
            if (WebSocketConnection.LOG.isDebugEnabled()) {
               WebSocketConnection.LOG.debug("failed onFrame({}) {}", frame, cause.toString());
            }

            frame.close();
            if (referenced != null) {
               referenced.release();
            }

            WebSocketConnection.this.coreSession.processHandlerError(cause, NOOP);
         }
      });
   }

   private void acquireNetworkBuffer() {
      AutoLock l = this.lock.lock();

      try {
         if (this.networkBuffer == null) {
            this.networkBuffer = this.newNetworkBuffer(this.getInputBufferSize());
         }
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
   }

   private void reacquireNetworkBuffer() {
      AutoLock l = this.lock.lock();

      try {
         if (this.networkBuffer == null) {
            throw new IllegalStateException();
         }

         if (this.networkBuffer.getByteBuffer().hasRemaining()) {
            throw new IllegalStateException();
         }

         this.networkBuffer.release();
         this.networkBuffer = this.newNetworkBuffer(this.getInputBufferSize());
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
   }

   private RetainableByteBuffer newNetworkBuffer(int capacity) {
      return this.byteBufferPool.acquire(capacity, this.isUseInputDirectByteBuffers());
   }

   private void releaseNetworkBuffer() {
      AutoLock l = this.lock.lock();

      try {
         if (this.networkBuffer == null) {
            throw new IllegalStateException();
         }

         if (this.networkBuffer.hasRemaining()) {
            throw new IllegalStateException();
         }

         this.networkBuffer.release();
         this.networkBuffer = null;
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
   }

   public void onFillable() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onFillable()");
      }

      this.fillAndParse();
   }

   @Override
   public void run() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("run()");
      }

      this.fillAndParse();
   }

   public void demand() {
      boolean fillAndParse = false;
      AutoLock l = this.lock.lock();

      try {
         if (LOG.isDebugEnabled()) {
            LOG.debug("demand {} d={} fp={} {}", new Object[]{this.demand, this.fillingAndParsing, this.networkBuffer, this});
         }

         if (this.demand != WebSocketConnection.DemandState.CANCELLED) {
            if (this.demand == WebSocketConnection.DemandState.DEMANDING) {
               throw new ReadPendingException();
            }

            this.demand = WebSocketConnection.DemandState.DEMANDING;
         }

         if (!this.fillingAndParsing) {
            this.fillingAndParsing = true;
            fillAndParse = true;
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

      if (fillAndParse) {
         this.getExecutor().execute(this);
      }
   }

   public boolean moreDemand() {
      AutoLock l = this.lock.lock();

      boolean var2;
      label59: {
         try {
            if (LOG.isDebugEnabled()) {
               LOG.debug("moreDemand? d={} fp={} {} {}", new Object[]{this.demand, this.fillingAndParsing, this.networkBuffer, this});
            }

            if (!this.fillingAndParsing) {
               throw new IllegalStateException();
            }

            switch (this.demand) {
               case DEMANDING:
               case CANCELLED:
                  var2 = true;
                  break;
               case NOT_DEMANDING:
                  this.fillingAndParsing = false;
                  if (!this.networkBuffer.hasRemaining()) {
                     this.releaseNetworkBuffer();
                  }

                  var2 = false;
                  break label59;
               default:
                  throw new IllegalStateException(this.demand.name());
            }
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

      if (l != null) {
         l.close();
      }

      return var2;
   }

   public boolean meetDemand() {
      AutoLock l = this.lock.lock();

      boolean var2;
      try {
         if (LOG.isDebugEnabled()) {
            LOG.debug("meetDemand d={} fp={} {} {}", new Object[]{this.demand, this.fillingAndParsing, this.networkBuffer, this});
         }

         if (this.demand == WebSocketConnection.DemandState.NOT_DEMANDING) {
            throw new IllegalStateException();
         }

         if (!this.fillingAndParsing) {
            throw new IllegalStateException();
         }

         if (this.demand != WebSocketConnection.DemandState.CANCELLED) {
            this.demand = WebSocketConnection.DemandState.NOT_DEMANDING;
         }

         var2 = true;
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

   public void cancelDemand() {
      AutoLock l = this.lock.lock();

      try {
         if (LOG.isDebugEnabled()) {
            LOG.debug("cancelDemand d={} fp={} {} {}", new Object[]{this.demand, this.fillingAndParsing, this.networkBuffer, this});
         }

         this.demand = WebSocketConnection.DemandState.CANCELLED;
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
   }

   private void fillAndParse() {
      this.acquireNetworkBuffer();

      try {
         while (true) {
            if (this.networkBuffer.hasRemaining()) {
               Frame.Parsed frame = this.parser.parse(this.networkBuffer.getByteBuffer());
               if (frame != null) {
                  this.messagesIn.increment();
                  if (this.meetDemand()) {
                     this.onFrame(frame);
                  }

                  if (!this.moreDemand()) {
                     return;
                  }
                  continue;
               }
            }

            assert !this.networkBuffer.hasRemaining();

            if (!this.getEndPoint().isOpen()) {
               this.releaseNetworkBuffer();
               return;
            }

            if (this.networkBuffer.isRetained()) {
               this.reacquireNetworkBuffer();
            }

            int filled = this.getEndPoint().fill(this.networkBuffer.getByteBuffer());
            if (LOG.isDebugEnabled()) {
               LOG.debug("endpointFill() filled={}: {}", filled, this.networkBuffer);
            }

            if (filled < 0) {
               this.releaseNetworkBuffer();
               this.coreSession.onEof();
               return;
            }

            if (filled == 0) {
               this.releaseNetworkBuffer();
               this.fillInterested();
               return;
            }

            this.bytesIn.add((long)filled);
         }
      } catch (Throwable var2) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Error during fillAndParse() {}", var2.toString());
         }

         if (this.networkBuffer != null) {
            BufferUtil.clear(this.networkBuffer.getByteBuffer());
            this.releaseNetworkBuffer();
         }

         this.coreSession.processConnectionError(var2, Callback.NOOP);
      }
   }

   protected void setInitialBuffer(ByteBuffer initialBuffer) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Set initial buffer - {}", BufferUtil.toDetailString(initialBuffer));
      }

      AutoLock l = this.lock.lock();

      try {
         this.networkBuffer = this.newNetworkBuffer(initialBuffer.remaining());
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

      ByteBuffer buffer = this.networkBuffer.getByteBuffer();
      BufferUtil.clearToFill(buffer);
      BufferUtil.put(initialBuffer, buffer);
      BufferUtil.flipToFlush(buffer, 0);
   }

   public void onOpen() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onOpen() {}", this);
      }

      super.onOpen();
      this.coreSession.onOpen();
   }

   public void setInputBufferSize(int inputBufferSize) {
      if (inputBufferSize < 28) {
         throw new IllegalArgumentException("Cannot have buffer size less than 28");
      } else {
         super.setInputBufferSize(inputBufferSize);
      }
   }

   public String dump() {
      return Dumpable.dump(this);
   }

   public void dump(Appendable out, String indent) throws IOException {
      Dumpable.dumpObjects(out, indent, this, new Object[0]);
   }

   public String toConnectionString() {
      return String.format(
         "%s@%x[%s,p=%s,f=%s,g=%s]",
         this.getClass().getSimpleName(),
         this.hashCode(),
         this.coreSession.getBehavior(),
         this.parser,
         this.flusher,
         this.generator
      );
   }

   public void onUpgradeTo(ByteBuffer buffer) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onUpgradeTo({})", BufferUtil.toDetailString(buffer));
      }

      this.setInitialBuffer(buffer);
   }

   public long getMessagesIn() {
      return this.messagesIn.longValue();
   }

   public long getBytesIn() {
      return this.bytesIn.longValue();
   }

   public long getMessagesOut() {
      return this.flusher.getMessagesOut();
   }

   public long getBytesOut() {
      return this.flusher.getBytesOut();
   }

   public void enqueueFrame(Frame frame, Callback callback, boolean batch) {
      if (this.coreSession.getBehavior() == Behavior.CLIENT) {
         byte[] mask = new byte[4];
         this.random.nextBytes(mask);
         frame.setMask(mask);
      }

      if (this.flusher.enqueue(frame, callback, batch)) {
         this.flusher.iterate();
      }
   }

   private static enum DemandState {
      DEMANDING,
      NOT_DEMANDING,
      CANCELLED;
   }

   private class Flusher extends FrameFlusher {
      private Flusher(Scheduler scheduler, int bufferSize, Generator generator, EndPoint endpoint) {
         super(WebSocketConnection.this.byteBufferPool, scheduler, generator, endpoint, bufferSize, 8);
         this.setUseDirectByteBuffers(WebSocketConnection.this.isUseOutputDirectByteBuffers());
      }

      @Override
      public void onCompleteFailure(Throwable x) {
         WebSocketConnection.this.coreSession.processConnectionError(x, NOOP);
         super.onCompleteFailure(x);
      }
   }
}
