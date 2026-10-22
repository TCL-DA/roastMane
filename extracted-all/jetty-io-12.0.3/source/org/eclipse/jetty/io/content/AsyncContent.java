package org.eclipse.jetty.io.content;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.io.Retainable;
import org.eclipse.jetty.io.internal.ByteBufferChunk;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.SerializedInvoker;
import org.eclipse.jetty.util.thread.AutoLock.WithCondition;

public class AsyncContent implements Content.Sink, Content.Source, Closeable {
   private static final int UNDETERMINED_LENGTH = -2;
   private static final AsyncContent.AsyncChunk ASYNC_EOF = new AsyncContent.AsyncChunk(true, BufferUtil.EMPTY_BUFFER, Callback.NOOP) {
      @Override
      public String toString() {
         return "ASYNC_EOF";
      }
   };
   private final WithCondition lock = new WithCondition();
   private final SerializedInvoker invoker = new SerializedInvoker();
   private final Queue<Content.Chunk> chunks = new ArrayDeque<>();
   private Content.Chunk persistentFailure;
   private boolean readClosed;
   private boolean writeClosed;
   private Runnable demandCallback;
   private long length = -2L;

   @Override
   public void write(boolean last, ByteBuffer byteBuffer, Callback callback) {
      this.offer(new AsyncContent.AsyncChunk(last, byteBuffer, callback));
   }

   private void offer(Content.Chunk chunk) {
      Throwable failure = null;
      boolean wasEmpty = false;
      AutoLock ignored = this.lock.lock();

      try {
         if (this.writeClosed) {
            failure = new IOException("closed");
         } else if (this.persistentFailure != null) {
            failure = this.persistentFailure.getFailure();
         } else {
            wasEmpty = this.chunks.isEmpty();
            this.chunks.offer(chunk);
            if (chunk.isLast()) {
               this.writeClosed = true;
               if (this.length == -2L) {
                  this.length = 0L;

                  for (Content.Chunk c : this.chunks) {
                     this.length = this.length + (long)c.remaining();
                  }
               }
            }
         }
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

      if (failure != null && chunk instanceof AsyncContent.AsyncChunk asyncChunk) {
         asyncChunk.failed(failure);
      }

      if (wasEmpty) {
         this.invoker.run(this::invokeDemandCallback);
      }
   }

   public void flush() throws IOException {
      try {
         WithCondition condition = this.lock.lock();

         label62: {
            try {
               while (true) {
                  if (this.persistentFailure != null) {
                     throw new IOException(this.persistentFailure.getFailure());
                  }

                  if (this.chunks.isEmpty()) {
                     break label62;
                  }

                  if (this.writeClosed && this.chunks.size() == 1) {
                     Content.Chunk chunk = this.chunks.peek();
                     if (chunk.isLast() && !chunk.hasRemaining()) {
                        break;
                     }
                  }

                  condition.await();
               }
            } catch (Throwable var5) {
               if (condition != null) {
                  try {
                     condition.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (condition != null) {
               condition.close();
            }

            return;
         }

         if (condition != null) {
            condition.close();
         }
      } catch (InterruptedException var6) {
         throw new InterruptedIOException();
      }
   }

   @Override
   public void close() {
      this.offer(ASYNC_EOF);
   }

   public boolean isClosed() {
      AutoLock ignored = this.lock.lock();

      boolean var2;
      try {
         var2 = this.writeClosed;
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
   public long getLength() {
      AutoLock ignored = this.lock.lock();

      long var2;
      try {
         var2 = this.length < 0L ? -1L : this.length;
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
   public Content.Chunk read() {
      WithCondition condition = this.lock.lock();

      Content.Chunk var9;
      label90: {
         label91: {
            label92: {
               Content.Chunk current;
               try {
                  if (this.length == -2L) {
                     this.length = -1L;
                  }

                  current = this.chunks.poll();
                  if (current == null) {
                     if (this.readClosed) {
                        var9 = Content.Chunk.EOF;
                        break label90;
                     }

                     if (this.persistentFailure != null) {
                        var9 = this.persistentFailure;
                        break label91;
                     }

                     var9 = null;
                     break label92;
                  }

                  this.readClosed = current.isLast();
                  if (this.chunks.isEmpty()) {
                     condition.signal();
                  }
               } catch (Throwable var6) {
                  if (condition != null) {
                     try {
                        condition.close();
                     } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                     }
                  }

                  throw var6;
               }

               if (condition != null) {
                  condition.close();
               }

               if (current.canRetain()) {
                  return current;
               }

               if (current instanceof AsyncContent.AsyncChunk asyncChunk) {
                  asyncChunk.succeeded();
               }

               if (Content.Chunk.isFailure(current)) {
                  return current;
               }

               return current.isLast() ? Content.Chunk.EOF : Content.Chunk.EMPTY;
            }

            if (condition != null) {
               condition.close();
            }

            return var9;
         }

         if (condition != null) {
            condition.close();
         }

         return var9;
      }

      if (condition != null) {
         condition.close();
      }

      return var9;
   }

   @Override
   public void demand(Runnable demandCallback) {
      AutoLock ignored = this.lock.lock();

      boolean invoke;
      try {
         if (this.demandCallback != null) {
            throw new IllegalStateException("demand pending");
         }

         this.demandCallback = Objects.requireNonNull(demandCallback);
         invoke = !this.chunks.isEmpty() || this.readClosed || this.persistentFailure != null;
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

      if (invoke) {
         this.invoker.run(this::invokeDemandCallback);
      }
   }

   private void invokeDemandCallback() {
      AutoLock ignored = this.lock.lock();

      Runnable demandCallback;
      try {
         demandCallback = this.demandCallback;
         this.demandCallback = null;
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

      if (demandCallback != null) {
         this.runDemandCallback(demandCallback);
      }
   }

   private void runDemandCallback(Runnable demandCallback) {
      try {
         demandCallback.run();
      } catch (Throwable var3) {
         this.fail(var3);
      }
   }

   @Override
   public void fail(Throwable failure) {
      WithCondition condition = this.lock.lock();

      label51: {
         label52: {
            List<Content.Chunk> drained;
            try {
               if (this.readClosed) {
                  break label51;
               }

               if (this.persistentFailure != null) {
                  break label52;
               }

               this.persistentFailure = Content.Chunk.from(failure);
               drained = List.copyOf(this.chunks);
               this.chunks.clear();
               condition.signal();
            } catch (Throwable var7) {
               if (condition != null) {
                  try {
                     condition.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (condition != null) {
               condition.close();
            }

            drained.forEach(c -> {
               if (c instanceof AsyncContent.AsyncChunk ac) {
                  ac.failed(failure);
               }
            });
            this.invoker.run(this::invokeDemandCallback);
            return;
         }

         if (condition != null) {
            condition.close();
         }

         return;
      }

      if (condition != null) {
         condition.close();
      }
   }

   @Override
   public void fail(Throwable failure, boolean last) {
      if (last) {
         this.fail(failure);
      } else {
         this.offer(Content.Chunk.from(failure, false));
      }
   }

   public int count() {
      AutoLock ignored = this.lock.lock();

      int var2;
      try {
         var2 = this.chunks.size();
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

   private static class AsyncChunk extends ByteBufferChunk implements Callback {
      private final Callback callback;
      private final Retainable.ReferenceCounter referenceCounter;

      public AsyncChunk(boolean last, ByteBuffer byteBuffer, Callback callback) {
         super(byteBuffer.hasRemaining() ? byteBuffer : BufferUtil.EMPTY_BUFFER, last);
         this.callback = callback;
         this.referenceCounter = this.getByteBuffer() == BufferUtil.EMPTY_BUFFER ? null : new Retainable.ReferenceCounter();
      }

      @Override
      public boolean canRetain() {
         return this.referenceCounter != null;
      }

      @Override
      public void retain() {
         if (this.canRetain()) {
            this.referenceCounter.retain();
         }
      }

      @Override
      public boolean release() {
         if (!this.canRetain()) {
            return true;
         } else {
            boolean released = this.referenceCounter.release();
            if (released) {
               this.succeeded();
            }

            return released;
         }
      }

      public void succeeded() {
         this.callback.succeeded();
      }

      public void failed(Throwable x) {
         this.callback.failed(x);
      }
   }
}
