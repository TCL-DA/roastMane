package org.eclipse.jetty.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.AutoLock.WithCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputStreamResponseListener implements Response.Listener {
   private static final Logger LOG = LoggerFactory.getLogger(InputStreamResponseListener.class);
   private static final InputStreamResponseListener.ChunkCallback EOF = new InputStreamResponseListener.ChunkCallback(Chunk.EOF, () -> {
   }, x -> {
   });
   private final WithCondition lock = new WithCondition();
   private final CountDownLatch responseLatch = new CountDownLatch(1);
   private final CountDownLatch resultLatch = new CountDownLatch(1);
   private final AtomicReference<InputStream> stream = new AtomicReference<>();
   private final Queue<InputStreamResponseListener.ChunkCallback> chunkCallbacks = new ArrayDeque<>();
   private Response response;
   private Result result;
   private Throwable failure;
   private boolean closed;

   @Override
   public void onHeaders(Response response) {
      AutoLock ignored = this.lock.lock();

      try {
         this.response = response;
         this.responseLatch.countDown();
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
   public void onContent(Response response, Chunk chunk, Runnable demander) {
      if (!chunk.hasRemaining()) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Skipped empty chunk {}", chunk);
         }

         demander.run();
      } else {
         WithCondition l = this.lock.lock();

         label56: {
            try {
               if (!this.closed) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Queueing chunk {}", chunk);
                  }

                  chunk.retain();
                  this.chunkCallbacks.add(new InputStreamResponseListener.ChunkCallback(chunk, demander, response::abort));
                  l.signalAll();
                  break label56;
               }
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

            if (LOG.isDebugEnabled()) {
               LOG.debug("InputStream closed, ignored chunk {}", chunk);
            }

            response.abort(new AsynchronousCloseException());
            return;
         }

         if (l != null) {
            l.close();
         }
      }
   }

   @Override
   public void onSuccess(Response response) {
      WithCondition l = this.lock.lock();

      try {
         if (!this.closed) {
            this.chunkCallbacks.add(EOF);
         }

         l.signalAll();
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

      if (LOG.isDebugEnabled()) {
         LOG.debug("End of content");
      }
   }

   @Override
   public void onFailure(Response response, Throwable failure) {
      WithCondition l = this.lock.lock();

      label44: {
         List<InputStreamResponseListener.ChunkCallback> chunkCallbacks;
         try {
            if (this.failure != null) {
               break label44;
            }

            this.failure = failure;
            chunkCallbacks = this.drain();
            l.signalAll();
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

         if (LOG.isDebugEnabled()) {
            LOG.debug("Content failure", failure);
         }

         chunkCallbacks.forEach(chunkCallback -> chunkCallback.releaseAndFail(failure));
         return;
      }

      if (l != null) {
         l.close();
      }
   }

   @Override
   public void onComplete(Result result) {
      Throwable failure = result.getFailure();
      List<InputStreamResponseListener.ChunkCallback> chunkCallbacks = Collections.emptyList();
      WithCondition l = this.lock.lock();

      try {
         this.result = result;
         if (result.isFailed() && this.failure == null) {
            this.failure = failure;
            chunkCallbacks = this.drain();
         }

         this.responseLatch.countDown();
         this.resultLatch.countDown();
         l.signalAll();
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

      if (LOG.isDebugEnabled()) {
         if (failure == null) {
            LOG.debug("Result success");
         } else {
            LOG.debug("Result failure", failure);
         }
      }

      chunkCallbacks.forEach(t -> t.releaseAndFail(failure));
   }

   public Response get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
      boolean expired = !this.responseLatch.await(timeout, unit);
      if (expired) {
         throw new TimeoutException();
      } else {
         AutoLock ignored = this.lock.lock();

         Response var6;
         try {
            if (this.response == null) {
               throw new ExecutionException(this.failure);
            }

            var6 = this.response;
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

         return var6;
      }
   }

   public Result await(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
      boolean expired = !this.resultLatch.await(timeout, unit);
      if (expired) {
         throw new TimeoutException();
      } else {
         AutoLock ignored = this.lock.lock();

         Result var6;
         try {
            var6 = this.result;
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

         return var6;
      }
   }

   public InputStream getInputStream() {
      InputStream result = new InputStreamResponseListener.Input();
      if (this.stream.compareAndSet(null, result)) {
         return result;
      } else {
         result = InputStream.nullInputStream();
         IO.close(result);
         return result;
      }
   }

   private List<InputStreamResponseListener.ChunkCallback> drain() {
      List<InputStreamResponseListener.ChunkCallback> failures = new ArrayList<>();
      AutoLock ignored = this.lock.lock();

      try {
         while (true) {
            InputStreamResponseListener.ChunkCallback chunkCallback = this.chunkCallbacks.peek();
            if (chunkCallback == null || chunkCallback == EOF) {
               break;
            }

            failures.add(chunkCallback);
            this.chunkCallbacks.poll();
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

      return failures;
   }

   private static record ChunkCallback(Chunk chunk, Runnable success, Consumer<Throwable> throwableConsumer) {
      private void releaseAndSucceed() {
         this.chunk.release();
         this.success.run();
      }

      private void releaseAndFail(Throwable x) {
         this.chunk.release();
         this.throwableConsumer.accept(x);
      }
   }

   private class Input extends InputStream {
      @Override
      public int read() throws IOException {
         byte[] tmp = new byte[1];
         int read = this.read(tmp);
         return read < 0 ? read : tmp[0] & 0xFF;
      }

      @Override
      public int read(byte[] b, int offset, int length) throws IOException {
         try {
            WithCondition l = InputStreamResponseListener.this.lock.lock();

            byte var12;
            label69: {
               int result;
               InputStreamResponseListener.ChunkCallback chunkCallback;
               try {
                  while (true) {
                     chunkCallback = InputStreamResponseListener.this.chunkCallbacks.peek();
                     if (chunkCallback == InputStreamResponseListener.EOF) {
                        var12 = -1;
                        break label69;
                     }

                     if (chunkCallback != null) {
                        ByteBuffer buffer = chunkCallback.chunk().getByteBuffer();
                        result = Math.min(buffer.remaining(), length);
                        buffer.get(b, offset, result);
                        if (!buffer.hasRemaining()) {
                           InputStreamResponseListener.this.chunkCallbacks.poll();
                        } else {
                           chunkCallback = null;
                        }
                        break;
                     }

                     if (InputStreamResponseListener.this.failure != null) {
                        throw new IOException(InputStreamResponseListener.this.failure);
                     }

                     if (InputStreamResponseListener.this.closed) {
                        throw new AsynchronousCloseException();
                     }

                     l.await();
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

               if (chunkCallback != null) {
                  chunkCallback.releaseAndSucceed();
               }

               return result;
            }

            if (l != null) {
               l.close();
            }

            return var12;
         } catch (InterruptedException var11) {
            throw new InterruptedIOException();
         }
      }

      @Override
      public void close() throws IOException {
         WithCondition l = InputStreamResponseListener.this.lock.lock();

         label48: {
            List<InputStreamResponseListener.ChunkCallback> chunkCallbacks;
            try {
               if (InputStreamResponseListener.this.closed) {
                  break label48;
               }

               InputStreamResponseListener.this.closed = true;
               chunkCallbacks = InputStreamResponseListener.this.drain();
               l.signalAll();
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

            if (InputStreamResponseListener.LOG.isDebugEnabled()) {
               InputStreamResponseListener.LOG.debug("InputStream close");
            }

            if (!chunkCallbacks.isEmpty()) {
               Throwable failure = new AsynchronousCloseException();
               chunkCallbacks.forEach(t -> t.releaseAndFail(failure));
            }

            super.close();
            return;
         }

         if (l != null) {
            l.close();
         }
      }
   }
}
