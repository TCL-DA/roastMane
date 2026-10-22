package org.eclipse.jetty.io.internal;

import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IteratingNestedCallback;
import org.eclipse.jetty.util.IteratingCallback.Action;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentCopier extends IteratingNestedCallback {
   private static final Logger LOG = LoggerFactory.getLogger(ContentCopier.class);
   private final Content.Source source;
   private final Content.Sink sink;
   private final Content.Chunk.Processor chunkProcessor;
   private Content.Chunk current;
   private boolean terminated;

   public ContentCopier(Content.Source source, Content.Sink sink, Content.Chunk.Processor chunkProcessor, Callback callback) {
      super(callback);
      this.source = source;
      this.sink = sink;
      this.chunkProcessor = chunkProcessor;
   }

   public InvocationType getInvocationType() {
      return InvocationType.NON_BLOCKING;
   }

   protected Action process() throws Throwable {
      if (this.terminated) {
         return Action.SUCCEEDED;
      } else {
         this.current = this.source.read();
         if (this.current == null) {
            this.source.demand(this::iterate);
            return Action.IDLE;
         } else if (this.chunkProcessor != null && this.chunkProcessor.process(this.current, this)) {
            return Action.SCHEDULED;
         } else if (Content.Chunk.isFailure(this.current)) {
            if (this.current.isLast()) {
               throw this.current.getFailure();
            } else {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("ignored transient failure", this.current.getFailure());
               }

               this.succeeded();
               return Action.SCHEDULED;
            }
         } else {
            this.sink.write(this.current.isLast(), this.current.getByteBuffer(), this);
            return Action.SCHEDULED;
         }
      }
   }

   public void succeeded() {
      this.terminated = this.current.isLast();
      this.current.release();
      this.current = null;
      super.succeeded();
   }

   public void failed(Throwable x) {
      if (this.current != null) {
         this.current.release();
      }

      this.current = null;
      this.source.fail(x);
      super.failed(x);
   }
}
