package org.eclipse.jetty.io.content;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.SerializedInvoker;

public class ByteBufferContentSource implements Content.Source {
   private final AutoLock lock = new AutoLock();
   private final SerializedInvoker invoker = new SerializedInvoker();
   private final long length;
   private final Collection<ByteBuffer> byteBuffers;
   private Iterator<ByteBuffer> iterator;
   private Content.Chunk terminated;
   private Runnable demandCallback;

   public ByteBufferContentSource(ByteBuffer... byteBuffers) {
      this(List.of(byteBuffers));
   }

   public ByteBufferContentSource(Collection<ByteBuffer> byteBuffers) {
      this.byteBuffers = byteBuffers;
      this.length = byteBuffers.stream().mapToLong(Buffer::remaining).sum();
   }

   public Collection<ByteBuffer> getByteBuffers() {
      return this.byteBuffers;
   }

   @Override
   public long getLength() {
      return this.length;
   }

   @Override
   public Content.Chunk read() {
      AutoLock ignored = this.lock.lock();

      Content.Chunk var8;
      label68: {
         label69: {
            ByteBuffer buffer;
            boolean last;
            try {
               if (this.terminated != null) {
                  var8 = this.terminated;
                  break label68;
               }

               if (this.iterator == null) {
                  this.iterator = this.byteBuffers.iterator();
               }

               if (!this.iterator.hasNext()) {
                  var8 = this.terminated = Content.Chunk.EOF;
                  break label69;
               }

               buffer = this.iterator.next().slice();
               last = !this.iterator.hasNext();
               if (last) {
                  this.terminated = Content.Chunk.EOF;
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

            return Content.Chunk.from(buffer, last);
         }

         if (ignored != null) {
            ignored.close();
         }

         return var8;
      }

      if (ignored != null) {
         ignored.close();
      }

      return var8;
   }

   @Override
   public boolean rewind() {
      AutoLock ignored = this.lock.lock();

      boolean var2;
      try {
         this.iterator = null;
         this.terminated = null;
         this.demandCallback = null;
         var2 = true;
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
      AutoLock ignored = this.lock.lock();

      label40: {
         try {
            if (this.terminated != null) {
               break label40;
            }

            this.terminated = Content.Chunk.from(failure);
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

         return;
      }

      if (ignored != null) {
         ignored.close();
      }
   }
}
