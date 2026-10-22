package org.eclipse.jetty.io.internal;

import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.eclipse.jetty.util.thread.Invocable.Task;

public class ContentSourceConsumer implements Task {
   private final Content.Source source;
   private final Callback callback;

   public ContentSourceConsumer(Content.Source source, Callback callback) {
      this.source = source;
      this.callback = callback;
   }

   public void run() {
      Content.Chunk chunk;
      do {
         chunk = this.source.read();
         if (chunk == null) {
            this.source.demand(this);
            return;
         }

         if (Content.Chunk.isFailure(chunk)) {
            this.callback.failed(chunk.getFailure());
            return;
         }

         chunk.release();
      } while (!chunk.isLast());

      this.callback.succeeded();
   }

   public InvocationType getInvocationType() {
      return this.callback.getInvocationType();
   }
}
