package org.eclipse.jetty.io.content;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.SerializedInvoker;

public class PathContentSource implements Content.Source {
   private final AutoLock lock = new AutoLock();
   private final SerializedInvoker invoker = new SerializedInvoker();
   private final Path path;
   private final long length;
   private final ByteBufferPool byteBufferPool;
   private int bufferSize = 4096;
   private boolean useDirectByteBuffers = true;
   private SeekableByteChannel channel;
   private long totalRead;
   private Runnable demandCallback;
   private Content.Chunk errorChunk;

   public PathContentSource(Path path) {
      this(path, null);
   }

   public PathContentSource(Path path, ByteBufferPool byteBufferPool) {
      try {
         if (!Files.isRegularFile(path)) {
            throw new NoSuchFileException(path.toString());
         } else if (!Files.isReadable(path)) {
            throw new AccessDeniedException(path.toString());
         } else {
            this.path = path;
            this.length = Files.size(path);
            this.byteBufferPool = (ByteBufferPool)(byteBufferPool != null ? byteBufferPool : new ByteBufferPool.NonPooling());
         }
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Path getPath() {
      return this.path;
   }

   @Override
   public long getLength() {
      return this.length;
   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public void setBufferSize(int bufferSize) {
      this.bufferSize = bufferSize;
   }

   public boolean isUseDirectByteBuffers() {
      return this.useDirectByteBuffers;
   }

   public void setUseDirectByteBuffers(boolean useDirectByteBuffers) {
      this.useDirectByteBuffers = useDirectByteBuffers;
   }

   @Override
   public Content.Chunk read() {
      AutoLock ignored = this.lock.lock();

      Content.Chunk read;
      label79: {
         Content.Chunk var11;
         label80: {
            SeekableByteChannel channel;
            try {
               if (this.errorChunk != null) {
                  var11 = this.errorChunk;
                  break label80;
               }

               if (this.channel == null) {
                  try {
                     this.channel = this.open();
                  } catch (Throwable var8) {
                     read = this.failure(var8);
                     break label79;
                  }
               }

               channel = this.channel;
            } catch (Throwable var9) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var6) {
                     var9.addSuppressed(var6);
                  }
               }

               throw var9;
            }

            if (ignored != null) {
               ignored.close();
            }

            if (!channel.isOpen()) {
               return Content.Chunk.EOF;
            }

            RetainableByteBuffer retainableByteBuffer = this.byteBufferPool.acquire(this.getBufferSize(), this.isUseDirectByteBuffers());
            ByteBuffer byteBuffer = retainableByteBuffer.getByteBuffer();

            try {
               BufferUtil.clearToFill(byteBuffer);
               read = this.read(channel, byteBuffer);
               BufferUtil.flipToFlush(byteBuffer, 0);
            } catch (Throwable var7) {
               retainableByteBuffer.release();
               return this.failure(var7);
            }

            if (read > 0) {
               this.totalRead += (long)read;
            }

            boolean last = this.isReadComplete(this.totalRead);
            if (last) {
               IO.close(channel);
            }

            return Content.Chunk.asChunk(byteBuffer, last, retainableByteBuffer);
         }

         if (ignored != null) {
            ignored.close();
         }

         return var11;
      }

      if (ignored != null) {
         ignored.close();
      }

      return read;
   }

   protected SeekableByteChannel open() throws IOException {
      return Files.newByteChannel(this.path, StandardOpenOption.READ);
   }

   protected int read(SeekableByteChannel channel, ByteBuffer byteBuffer) throws IOException {
      return channel.read(byteBuffer);
   }

   protected boolean isReadComplete(long read) {
      return read == this.getLength();
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

      Content.Chunk var3;
      try {
         if (this.errorChunk == null) {
            this.errorChunk = Content.Chunk.from(failure);
            IO.close(this.channel);
         }

         var3 = this.errorChunk;
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

      return var3;
   }

   @Override
   public boolean rewind() {
      AutoLock ignored = this.lock.lock();

      try {
         IO.close(this.channel);
         this.channel = null;
         this.totalRead = 0L;
         this.demandCallback = null;
         this.errorChunk = null;
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

      return true;
   }
}
