package org.eclipse.jetty.websocket.core.messages;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.FutureCallback;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageOutputStream extends OutputStream {
   private static final Logger LOG = LoggerFactory.getLogger(MessageOutputStream.class);
   private final AutoLock lock = new AutoLock();
   private final CoreSession coreSession;
   private final int bufferSize;
   private final RetainableByteBuffer buffer;
   private long frameCount;
   private long bytesSent;
   private Callback callback;
   private boolean closed;
   private byte messageOpCode = 2;

   public MessageOutputStream(CoreSession coreSession, ByteBufferPool bufferPool) {
      this.coreSession = coreSession;
      this.bufferSize = coreSession.getOutputBufferSize();
      this.buffer = bufferPool.acquire(this.bufferSize, true);
   }

   void setMessageType(byte opcode) {
      this.messageOpCode = opcode;
   }

   @Override
   public void write(byte[] bytes, int off, int len) throws IOException {
      try {
         this.send(ByteBuffer.wrap(bytes, off, len));
      } catch (Throwable var5) {
         this.notifyFailure(var5);
         throw var5;
      }
   }

   @Override
   public void write(int b) throws IOException {
      try {
         this.send(ByteBuffer.wrap(new byte[]{(byte)b}));
      } catch (Throwable var3) {
         this.notifyFailure(var3);
         throw var3;
      }
   }

   public void write(ByteBuffer buffer) throws IOException {
      try {
         this.send(buffer);
      } catch (Throwable var3) {
         this.notifyFailure(var3);
         throw var3;
      }
   }

   @Override
   public void flush() throws IOException {
      try {
         this.flush(false);
      } catch (Throwable var2) {
         this.notifyFailure(var2);
         throw var2;
      }
   }

   private void flush(boolean fin) throws IOException {
      AutoLock ignored = this.lock.lock();

      try {
         if (this.closed) {
            throw new IOException("Stream is closed");
         }

         this.closed = fin;
         Frame frame = new Frame(this.frameCount == 0L ? this.messageOpCode : 0);
         frame.setPayload(this.buffer.getByteBuffer());
         frame.setFin(fin);
         int initialBufferSize = this.buffer.remaining();
         FutureCallback b = new FutureCallback();
         this.coreSession.sendFrame(frame, b, false);
         b.block();
         this.bytesSent += (long)initialBufferSize;
         this.frameCount++;

         try {
            assert this.buffer.remaining() == initialBufferSize;

            this.buffer.clear();
         } catch (Throwable var8) {
            var8.printStackTrace();
         }
      } catch (Throwable var9) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var7) {
               var9.addSuppressed(var7);
            }
         }

         throw var9;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   private void send(ByteBuffer data) throws IOException {
      AutoLock ignored = this.lock.lock();

      try {
         if (this.closed) {
            throw new IOException("Stream is closed");
         }

         while (data.hasRemaining()) {
            int bufferRemainingSpace = this.bufferSize - this.buffer.remaining();
            int copied = Math.min(bufferRemainingSpace, data.remaining());
            BufferUtil.append(this.buffer.getByteBuffer(), data.array(), data.arrayOffset() + data.position(), copied);
            data.position(data.position() + copied);
            if (data.hasRemaining()) {
               this.flush(false);
            }
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
   }

   @Override
   public void close() throws IOException {
      try {
         this.flush(true);
         this.buffer.release();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Stream closed, {} frames ({} bytes) sent", this.frameCount, this.bytesSent);
         }

         this.notifySuccess();
      } catch (Throwable var2) {
         this.notifyFailure(var2);
         throw var2;
      }
   }

   public void setCallback(Callback callback) {
      AutoLock ignored = this.lock.lock();

      try {
         this.callback = callback;
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

   private void notifySuccess() {
      AutoLock ignored = this.lock.lock();

      Callback callback;
      try {
         callback = this.callback;
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

      if (callback != null) {
         callback.succeeded();
      }
   }

   private void notifyFailure(Throwable failure) {
      AutoLock ignored = this.lock.lock();

      Callback callback;
      try {
         callback = this.callback;
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
         callback.failed(failure);
      }
   }
}
