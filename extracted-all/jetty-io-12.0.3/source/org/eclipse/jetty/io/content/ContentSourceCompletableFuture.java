package org.eclipse.jetty.io.content;

import java.io.EOFException;
import java.util.concurrent.CompletableFuture;
import org.eclipse.jetty.io.Content;

public abstract class ContentSourceCompletableFuture<X> extends CompletableFuture<X> {
   private final Content.Source _content;

   public ContentSourceCompletableFuture(Content.Source content) {
      this._content = content;
   }

   public void parse() {
      while (true) {
         Content.Chunk chunk = this._content.read();
         if (chunk == null) {
            this._content.demand(this::parse);
            return;
         }

         if (Content.Chunk.isFailure(chunk)) {
            if (chunk.isLast() || !this.onTransientFailure(chunk.getFailure())) {
               this.completeExceptionally(chunk.getFailure());
               return;
            }
         } else {
            try {
               X x = this.parse(chunk);
               if (x != null) {
                  this.complete(x);
                  return;
               }
            } catch (Throwable var6) {
               this.completeExceptionally(var6);
               return;
            } finally {
               chunk.release();
            }

            if (chunk.isLast()) {
               this.completeExceptionally(new EOFException());
               return;
            }
         }
      }
   }

   protected abstract X parse(Content.Chunk var1) throws Throwable;

   protected boolean onTransientFailure(Throwable cause) {
      return false;
   }
}
