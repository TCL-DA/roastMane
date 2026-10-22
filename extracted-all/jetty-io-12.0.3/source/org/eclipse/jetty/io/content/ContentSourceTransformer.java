package org.eclipse.jetty.io.content;

import java.util.Objects;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.util.thread.SerializedInvoker;

public abstract class ContentSourceTransformer implements Content.Source {
   private final SerializedInvoker invoker;
   private final Content.Source rawSource;
   private Content.Chunk rawChunk;
   private Content.Chunk transformedChunk;
   private volatile boolean needsRawRead;
   private volatile Runnable demandCallback;

   protected ContentSourceTransformer(Content.Source rawSource) {
      this(rawSource, new SerializedInvoker());
   }

   protected ContentSourceTransformer(Content.Source rawSource, SerializedInvoker invoker) {
      this.rawSource = rawSource;
      this.invoker = invoker;
   }

   protected Content.Source getContentSource() {
      return this.rawSource;
   }

   @Override
   public Content.Chunk read() {
      while (true) {
         if (this.needsRawRead) {
            this.rawChunk = this.rawSource.read();
            this.needsRawRead = this.rawChunk == null;
            if (this.rawChunk == null) {
               return null;
            }
         }

         if (Content.Chunk.isFailure(this.rawChunk)) {
            return this.rawChunk;
         }

         if (Content.Chunk.isFailure(this.transformedChunk)) {
            return this.transformedChunk;
         }

         this.transformedChunk = this.process(this.rawChunk);
         if (this.rawChunk != null && this.rawChunk != this.transformedChunk) {
            this.rawChunk.release();
         }

         this.rawChunk = null;
         if (this.transformedChunk != null) {
            Content.Chunk result = this.transformedChunk;
            this.transformedChunk = Content.Chunk.next(result);
            return result;
         }

         this.needsRawRead = true;
      }
   }

   @Override
   public void demand(Runnable demandCallback) {
      this.demandCallback = Objects.requireNonNull(demandCallback);
      if (this.needsRawRead) {
         this.rawSource.demand(() -> this.invoker.run(this::invokeDemandCallback));
      } else {
         this.invoker.run(this::invokeDemandCallback);
      }
   }

   @Override
   public void fail(Throwable failure) {
      this.rawSource.fail(failure);
   }

   private void invokeDemandCallback() {
      Runnable demandCallback = this.demandCallback;
      this.demandCallback = null;
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

   private Content.Chunk process(Content.Chunk rawChunk) {
      try {
         return this.transform(rawChunk);
      } catch (Throwable var3) {
         this.fail(var3);
         return Content.Chunk.from(var3);
      }
   }

   protected abstract Content.Chunk transform(Content.Chunk var1);
}
