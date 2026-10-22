package org.eclipse.jetty.io.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.io.Retainable;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.SerializedInvoker;

public class ChunksContentSource implements Content.Source {
   private final AutoLock lock = new AutoLock();
   private final SerializedInvoker invoker = new SerializedInvoker();
   private final long length;
   private final Collection<Content.Chunk> chunks;
   private Iterator<Content.Chunk> iterator;
   private Content.Chunk terminated;
   private Runnable demandCallback;

   public ChunksContentSource(Collection<Content.Chunk> chunks) {
      chunks.forEach(Retainable::retain);
      this.chunks = chunks;
      this.length = chunks.stream().mapToLong(c -> (long)c.getByteBuffer().remaining()).sum();
   }

   public Collection<Content.Chunk> getChunks() {
      return this.chunks;
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
            Content.Chunk chunk;
            try {
               if (this.terminated != null) {
                  var8 = this.terminated;
                  break label68;
               }

               if (this.iterator == null) {
                  this.iterator = this.chunks.iterator();
               }

               if (!this.iterator.hasNext()) {
                  var8 = this.terminated = Content.Chunk.EOF;
                  break label69;
               }

               chunk = this.iterator.next();
               boolean last = !this.iterator.hasNext();
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

            return chunk;
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

      label43: {
         List<Content.Chunk> chunksToRelease;
         try {
            if (this.terminated != null) {
               break label43;
            }

            this.terminated = Content.Chunk.from(failure);
            if (this.iterator != null) {
               chunksToRelease = new ArrayList<>();
               this.iterator.forEachRemaining(chunksToRelease::add);
            } else {
               chunksToRelease = List.copyOf(this.chunks);
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

         chunksToRelease.forEach(Retainable::release);
         return;
      }

      if (ignored != null) {
         ignored.close();
      }
   }
}
