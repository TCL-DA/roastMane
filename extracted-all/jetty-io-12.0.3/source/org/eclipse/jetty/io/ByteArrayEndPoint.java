package org.eclipse.jetty.io;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteArrayEndPoint extends AbstractEndPoint {
   private static final Logger LOG = LoggerFactory.getLogger(ByteArrayEndPoint.class);
   private static final SocketAddress NO_SOCKET_ADDRESS = noSocketAddress();
   private static final int MAX_BUFFER_SIZE = 2147482623;
   private static final ByteBuffer EOF = BufferUtil.allocate(0);
   private final Runnable _runFillable = () -> this.getFillInterest().fillable();
   private final AutoLock _lock = new AutoLock();
   private final Condition _hasOutput = this._lock.newCondition();
   private final Queue<ByteBuffer> _inQ = new ArrayDeque<>();
   private final int _outputSize;
   private ByteBuffer _out;
   private boolean _growOutput;

   private static SocketAddress noSocketAddress() {
      try {
         return new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 0);
      } catch (Throwable var1) {
         throw new RuntimeIOException(var1);
      }
   }

   public ByteArrayEndPoint() {
      this(null, 0L, null, null);
   }

   public ByteArrayEndPoint(byte[] input, int outputSize) {
      this(null, 0L, input != null ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
   }

   public ByteArrayEndPoint(String input, int outputSize) {
      this(null, 0L, input != null ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
   }

   public ByteArrayEndPoint(Scheduler scheduler, long idleTimeoutMs) {
      this(scheduler, idleTimeoutMs, null, null);
   }

   public ByteArrayEndPoint(Scheduler timer, long idleTimeoutMs, byte[] input, int outputSize) {
      this(timer, idleTimeoutMs, input != null ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
   }

   public ByteArrayEndPoint(Scheduler timer, long idleTimeoutMs, String input, int outputSize) {
      this(timer, idleTimeoutMs, input != null ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
   }

   public ByteArrayEndPoint(Scheduler timer, long idleTimeoutMs, ByteBuffer input, ByteBuffer output) {
      super(timer);
      if (BufferUtil.hasContent(input)) {
         this.addInput(input);
      }

      this._outputSize = output == null ? 1024 : output.capacity();
      this._out = output == null ? BufferUtil.allocate(this._outputSize) : output;
      this.setIdleTimeout(idleTimeoutMs);
      this.onOpen();
   }

   @Override
   public SocketAddress getLocalSocketAddress() {
      return NO_SOCKET_ADDRESS;
   }

   @Override
   public SocketAddress getRemoteSocketAddress() {
      return NO_SOCKET_ADDRESS;
   }

   @Override
   public void doShutdownOutput() {
      super.doShutdownOutput();
      AutoLock l = this._lock.lock();

      try {
         this._hasOutput.signalAll();
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

   @Override
   public void doClose() {
      super.doClose();
      AutoLock l = this._lock.lock();

      try {
         this._hasOutput.signalAll();
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

   @Override
   protected void onIncompleteFlush() {
   }

   protected void execute(Runnable task) {
      new Thread(task, "BAEPoint-" + Integer.toHexString(this.hashCode())).start();
   }

   @Override
   protected void needsFillInterest() throws IOException {
      AutoLock lock = this._lock.lock();

      try {
         if (!this.isOpen()) {
            throw new ClosedChannelException();
         }

         ByteBuffer in = this._inQ.peek();
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} needsFillInterest EOF={} {}", new Object[]{this, in == EOF, BufferUtil.toDetailString(in)});
         }

         if (BufferUtil.hasContent(in) || isEOF(in)) {
            this.execute(this._runFillable);
         }
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
   }

   public void addInputEOF() {
      this.addInput((ByteBuffer)null);
   }

   public void addInput(ByteBuffer in) {
      boolean fillable = false;
      AutoLock lock = this._lock.lock();

      try {
         if (isEOF(this._inQ.peek())) {
            throw new RuntimeIOException(new EOFException());
         }

         boolean wasEmpty = this._inQ.isEmpty();
         if (in == null) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("{} addEOFAndRun=true", this);
            }

            this._inQ.add(EOF);
            fillable = true;
         }

         if (BufferUtil.hasContent(in)) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("{} addInputAndRun={} {}", new Object[]{this, wasEmpty, BufferUtil.toDetailString(in)});
            }

            this._inQ.add(in);
            fillable = wasEmpty;
         }
      } catch (Throwable var7) {
         if (lock != null) {
            try {
               lock.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (lock != null) {
         lock.close();
      }

      if (fillable) {
         this._runFillable.run();
      }
   }

   public void addInput(String s) {
      this.addInput(BufferUtil.toBuffer(s, StandardCharsets.UTF_8));
   }

   public void addInput(String s, Charset charset) {
      this.addInput(BufferUtil.toBuffer(s, charset));
   }

   public void addInputAndExecute(String s) {
      this.addInputAndExecute(BufferUtil.toBuffer(s, StandardCharsets.UTF_8));
   }

   public void addInputAndExecute(ByteBuffer in) {
      boolean fillable = false;
      AutoLock lock = this._lock.lock();

      try {
         if (isEOF(this._inQ.peek())) {
            throw new RuntimeIOException(new EOFException());
         }

         boolean wasEmpty = this._inQ.isEmpty();
         if (in == null) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("{} addEOFAndExecute=true", this);
            }

            this._inQ.add(EOF);
            fillable = true;
         }

         if (BufferUtil.hasContent(in)) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("{} addInputAndExecute={} {}", new Object[]{this, wasEmpty, BufferUtil.toDetailString(in)});
            }

            this._inQ.add(in);
            fillable = wasEmpty;
         }
      } catch (Throwable var7) {
         if (lock != null) {
            try {
               lock.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (lock != null) {
         lock.close();
      }

      if (fillable) {
         this.execute(this._runFillable);
      }
   }

   public ByteBuffer getOutput() {
      AutoLock lock = this._lock.lock();

      ByteBuffer var2;
      try {
         var2 = this._out;
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

   public String getOutputString() {
      return this.getOutputString(StandardCharsets.UTF_8);
   }

   public String getOutputString(Charset charset) {
      return BufferUtil.toString(this._out, charset);
   }

   public ByteBuffer takeOutput() {
      AutoLock lock = this._lock.lock();

      ByteBuffer b;
      try {
         b = this._out;
         this._out = BufferUtil.allocate(this._outputSize);
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

      this.getWriteFlusher().completeWrite();
      return b;
   }

   public ByteBuffer waitForOutput(long time, TimeUnit unit) throws InterruptedException {
      AutoLock l = this._lock.lock();

      Object var6;
      label49: {
         ByteBuffer b;
         try {
            while (BufferUtil.isEmpty(this._out) && !this.isOutputShutdown()) {
               if (!this._hasOutput.await(time, unit)) {
                  var6 = null;
                  break label49;
               }
            }

            b = this._out;
            this._out = BufferUtil.allocate(this._outputSize);
         } catch (Throwable var9) {
            if (l != null) {
               try {
                  l.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (l != null) {
            l.close();
         }

         this.getWriteFlusher().completeWrite();
         return b;
      }

      if (l != null) {
         l.close();
      }

      return (ByteBuffer)var6;
   }

   public String takeOutputString() {
      return this.takeOutputString(StandardCharsets.UTF_8);
   }

   public String takeOutputString(Charset charset) {
      ByteBuffer buffer = this.takeOutput();
      return BufferUtil.toString(buffer, charset);
   }

   public void setOutput(ByteBuffer out) {
      AutoLock lock = this._lock.lock();

      try {
         this._out = out;
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

      this.getWriteFlusher().completeWrite();
   }

   public boolean hasMore() {
      return this.getOutput().position() > 0;
   }

   @Override
   public int fill(ByteBuffer buffer) throws IOException {
      int filled = 0;
      AutoLock lock = this._lock.lock();

      byte var8;
      label67: {
         try {
            while (true) {
               if (!this.isOpen()) {
                  throw new EofException("CLOSED");
               }

               if (this.isInputShutdown()) {
                  var8 = -1;
                  break label67;
               }

               if (this._inQ.isEmpty()) {
                  break;
               }

               ByteBuffer in = this._inQ.peek();
               if (isEOF(in)) {
                  filled = -1;
                  break;
               } else if (!BufferUtil.hasContent(in)) {
                  this._inQ.poll();
               } else {
                  filled = BufferUtil.append(buffer, in);
                  if (BufferUtil.isEmpty(in)) {
                     this._inQ.poll();
                  }
                  break;
               }
            }
         } catch (Throwable var7) {
            if (lock != null) {
               try {
                  lock.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (lock != null) {
            lock.close();
         }

         if (filled > 0) {
            this.notIdle();
         } else if (filled < 0) {
            this.shutdownInput();
         }

         return filled;
      }

      if (lock != null) {
         lock.close();
      }

      return var8;
   }

   @Override
   public boolean flush(ByteBuffer... buffers) throws IOException {
      boolean flushed = true;
      AutoLock l = this._lock.lock();

      try {
         if (!this.isOpen()) {
            throw new IOException("CLOSED");
         }

         if (this.isOutputShutdown()) {
            throw new IOException("OSHUT");
         }

         boolean idle = true;

         for (ByteBuffer b : buffers) {
            if (BufferUtil.hasContent(b)) {
               if (this._growOutput && b.remaining() > BufferUtil.space(this._out)) {
                  BufferUtil.compact(this._out);
                  if (b.remaining() > BufferUtil.space(this._out) && this._out.capacity() < 2147482623) {
                     long newBufferCapacity = Math.min((long)((double)this._out.capacity() + (double)b.remaining() * 1.5), 2147482623L);
                     ByteBuffer n = BufferUtil.allocate(Math.toIntExact(newBufferCapacity));
                     BufferUtil.append(n, this._out);
                     this._out = n;
                  }
               }

               if (BufferUtil.append(this._out, b) > 0) {
                  idle = false;
               }

               if (BufferUtil.hasContent(b)) {
                  flushed = false;
                  break;
               }
            }
         }

         if (!idle) {
            this.notIdle();
            this._hasOutput.signalAll();
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

      return flushed;
   }

   @Override
   public void reset() {
      AutoLock l = this._lock.lock();

      try {
         this._inQ.clear();
         this._hasOutput.signalAll();
         BufferUtil.clear(this._out);
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

      super.reset();
   }

   @Override
   public Object getTransport() {
      return null;
   }

   public boolean isGrowOutput() {
      return this._growOutput;
   }

   public void setGrowOutput(boolean growOutput) {
      this._growOutput = growOutput;
   }

   @Override
   public String toString() {
      AutoLock lock = this._lock.lock();

      int q;
      ByteBuffer b;
      String o;
      try {
         q = this._inQ.size();
         b = this._inQ.peek();
         o = BufferUtil.toDetailString(this._out);
      } catch (Throwable var8) {
         if (lock != null) {
            try {
               lock.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (lock != null) {
         lock.close();
      }

      return String.format("%s[q=%d,q[0]=%s,o=%s]", super.toString(), q, b, o);
   }

   private static boolean isEOF(ByteBuffer buffer) {
      return buffer == EOF;
   }
}
