package org.eclipse.jetty.io.content;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.SerializedInvoker;

public class InputStreamContentSource implements Content.Source {
   private final AutoLock lock = new AutoLock();
   private final SerializedInvoker invoker = new SerializedInvoker();
   private final InputStream inputStream;
   private final ByteBufferPool bufferPool;
   private int bufferSize = 4096;
   private Runnable demandCallback;
   private Content.Chunk errorChunk;
   private boolean closed;

   public InputStreamContentSource(InputStream inputStream) {
      this(inputStream, null);
   }

   public InputStreamContentSource(InputStream inputStream, ByteBufferPool bufferPool) {
      this.inputStream = Objects.requireNonNull(inputStream);
      this.bufferPool = (ByteBufferPool)(bufferPool != null ? bufferPool : new ByteBufferPool.NonPooling());
   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public void setBufferSize(int bufferSize) {
      this.bufferSize = bufferSize;
   }

   @Override
   public Content.Chunk read() {
      AutoLock ignored = this.lock.lock();

      Content.Chunk var9;
      label64: {
         label65: {
            try {
               if (this.errorChunk != null) {
                  var9 = this.errorChunk;
                  break label64;
               }

               if (this.closed) {
                  var9 = Content.Chunk.EOF;
                  break label65;
               }
            } catch (Throwable var6) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var4) {
                     var6.addSuppressed(var4);
                  }
               }

               throw var6;
            }

            if (ignored != null) {
               ignored.close();
            }

            RetainableByteBuffer streamBuffer = this.bufferPool.acquire(this.getBufferSize(), false);

            try {
               ByteBuffer buffer = streamBuffer.getByteBuffer();
               int read = this.fillBufferFromInputStream(this.inputStream, buffer.array());
               if (read < 0) {
                  streamBuffer.release();
                  this.close();
                  return Content.Chunk.EOF;
               }

               buffer.limit(read);
               return Content.Chunk.asChunk(buffer, false, streamBuffer);
            } catch (Throwable var5) {
               streamBuffer.release();
               return this.failure(var5);
            }
         }

         if (ignored != null) {
            ignored.close();
         }

         return var9;
      }

      if (ignored != null) {
         ignored.close();
      }

      return var9;
   }

   protected int fillBufferFromInputStream(InputStream inputStream, byte[] buffer) throws IOException {
      return inputStream.read(buffer, 0, buffer.length);
   }

   private void close() {
      AutoLock ignored = this.lock.lock();

      try {
         this.closed = true;
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

      IO.close(this.inputStream);
   }

   @Override
   public void demand(Runnable demandCallback) {
      AutoLock ignored = this.lock.lock();

      try {
         if (this.demandCallback != null) {
            throw new IllegalStateException("demand pending");
         }

         this.demandCallback = demandCallback;
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

      this.invoker.run(this::invokeDemandCallback);
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
      this.failure(failure);
   }

   private Content.Chunk failure(Throwable failure) {
      AutoLock ignored = this.lock.lock();

      Content.Chunk error;
      try {
         error = this.errorChunk;
         if (error == null) {
            error = this.errorChunk = Content.Chunk.from(failure);
         }
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

      IO.close(this.inputStream);
      return error;
   }
}
