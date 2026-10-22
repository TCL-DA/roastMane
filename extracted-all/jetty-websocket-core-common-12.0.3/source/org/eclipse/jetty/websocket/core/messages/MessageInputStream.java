package org.eclipse.jetty.websocket.core.messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.AutoLock.WithCondition;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageInputStream extends InputStream implements MessageSink {
   private static final Logger LOG = LoggerFactory.getLogger(MessageInputStream.class);
   private static final MessageInputStream.Entry EOF = new MessageInputStream.Entry(null, Callback.NOOP);
   private static final MessageInputStream.Entry CLOSED = new MessageInputStream.Entry(null, Callback.NOOP);
   private static final MessageInputStream.Entry FAILED = new MessageInputStream.Entry(null, Callback.NOOP);
   private final WithCondition lock = new WithCondition();
   private final ArrayDeque<MessageInputStream.Entry> buffers = new ArrayDeque<>();
   private final CoreSession session;
   private MessageInputStream.Entry currentEntry;
   private Throwable failure;
   private boolean closed;
   private long timeoutMs = -1L;

   public MessageInputStream(CoreSession session) {
      this.session = session;
   }

   @Override
   public void accept(Frame frame, Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("accepting {}", frame);
      }

      if (!frame.isFin() && !frame.hasPayload()) {
         callback.succeeded();
         this.session.demand();
      } else {
         Runnable action = null;
         WithCondition l = this.lock.lock();

         try {
            if (this.failure != null) {
               Throwable cause = this.failure;
               action = () -> callback.failed(cause);
            } else if (this.closed) {
               action = callback::succeeded;
            } else {
               this.buffers.offer(new MessageInputStream.Entry(frame, callback));
               if (frame.isFin()) {
                  this.buffers.offer(EOF);
               }
            }

            l.signal();
         } catch (Throwable var8) {
            if (l != null) {
               try {
                  l.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (l != null) {
            l.close();
         }

         if (action != null) {
            action.run();
         }
      }
   }

   @Override
   public int read() throws IOException {
      byte[] buf = new byte[1];

      int len;
      do {
         len = this.read(buf, 0, 1);
         if (len < 0) {
            return -1;
         }
      } while (len <= 0);

      return buf[0] & 0xFF;
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException {
      ByteBuffer buffer = ByteBuffer.wrap(b, off, len).slice();
      BufferUtil.clear(buffer);
      return this.read(buffer);
   }

   public int read(ByteBuffer buffer) throws IOException {
      MessageInputStream.Entry currentEntry = this.getCurrentEntry();
      if (LOG.isDebugEnabled()) {
         LOG.debug("currentEntry = {}", currentEntry);
      }

      if (currentEntry == FAILED) {
         throw IO.rethrow(this.getFailure());
      } else if (currentEntry == CLOSED) {
         throw new IOException("Closed");
      } else if (currentEntry == EOF) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Read EOF");
         }

         return -1;
      } else {
         ByteBuffer payload = currentEntry.frame.getPayload();
         if (currentEntry.frame.isFin() && !payload.hasRemaining()) {
            this.succeedCurrentEntry();
            return this.read(buffer);
         } else {
            int length = BufferUtil.append(buffer, payload);
            if (!payload.hasRemaining()) {
               this.succeedCurrentEntry();
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("filled {} bytes from {}", length, currentEntry);
            }

            return length;
         }
      }
   }

   @Override
   public void fail(Throwable failure) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("fail()", failure);
      }

      ArrayList<MessageInputStream.Entry> entries = new ArrayList<>();
      WithCondition l = this.lock.lock();

      label44: {
         try {
            if (this.failure != null) {
               break label44;
            }

            this.failure = failure;
            this.drainInto(entries);
            this.buffers.offer(FAILED);
            l.signal();
         } catch (Throwable var7) {
            if (l != null) {
               try {
                  l.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (l != null) {
            l.close();
         }

         entries.forEach(e -> e.callback.failed(failure));
         return;
      }

      if (l != null) {
         l.close();
      }
   }

   @Override
   public void close() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("close()");
      }

      ArrayList<MessageInputStream.Entry> entries = new ArrayList<>();
      WithCondition l = this.lock.lock();

      label44: {
         try {
            if (this.closed) {
               break label44;
            }

            this.closed = true;
            this.drainInto(entries);
            this.buffers.offer(CLOSED);
            l.signal();
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

         entries.forEach(e -> e.callback.succeeded());
         return;
      }

      if (l != null) {
         l.close();
      }
   }

   private void drainInto(ArrayList<MessageInputStream.Entry> entries) {
      assert this.lock.isHeldByCurrentThread();

      if (this.currentEntry != null) {
         entries.add(this.currentEntry);
         this.currentEntry = null;
      }

      entries.addAll(this.buffers);
      this.buffers.clear();
   }

   public void setTimeout(long timeoutMs) {
      this.timeoutMs = timeoutMs;
   }

   private void succeedCurrentEntry() {
      AutoLock ignored = this.lock.lock();

      MessageInputStream.Entry current;
      try {
         current = this.currentEntry;
         this.currentEntry = null;
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

      if (current != null) {
         current.callback.succeeded();
         if (!current.frame.isFin()) {
            this.session.demand();
         }
      }
   }

   private MessageInputStream.Entry getCurrentEntry() throws IOException {
      try {
         WithCondition l = this.lock.lock();

         MessageInputStream.Entry var9;
         label63: {
            MessageInputStream.Entry var5;
            try {
               if (this.currentEntry != null) {
                  var9 = this.currentEntry;
                  break label63;
               }

               long timeout = this.timeoutMs;
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Waiting {} ms to read", timeout);
               }

               while (true) {
                  MessageInputStream.Entry result = this.buffers.poll();
                  if (result != null) {
                     var5 = this.currentEntry = result;
                     break;
                  }

                  if (timeout < 0L) {
                     l.await();
                  } else if (!l.await(timeout, TimeUnit.MILLISECONDS)) {
                     throw new IOException(String.format("Read timeout: %,dms expired", timeout));
                  }
               }
            } catch (Throwable var7) {
               if (l != null) {
                  try {
                     l.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (l != null) {
               l.close();
            }

            return var5;
         }

         if (l != null) {
            l.close();
         }

         return var9;
      } catch (InterruptedException var8) {
         this.close();
         throw new InterruptedIOException();
      }
   }

   private Throwable getFailure() {
      AutoLock ignored = this.lock.lock();

      Throwable var2;
      try {
         var2 = this.failure;
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

   private static record Entry(Frame frame, Callback callback) {
   }
}
