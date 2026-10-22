package org.eclipse.jetty.websocket.core.messages;

import java.io.Closeable;
import java.lang.invoke.MethodHandle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.Callback.Completable;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.Frame;

public abstract class DispatchedMessageSink extends AbstractMessageSink {
   private final Executor executor;
   private volatile CompletableFuture<Void> dispatchComplete;
   private MessageSink typeSink;

   public DispatchedMessageSink(CoreSession session, MethodHandle methodHandle, boolean autoDemand) {
      super(session, methodHandle, autoDemand);
      if (!autoDemand) {
         throw new IllegalArgumentException("%s must be auto-demanding".formatted(this.getClass().getSimpleName()));
      } else {
         this.executor = session.getWebSocketComponents().getExecutor();
      }
   }

   public abstract MessageSink newMessageSink();

   @Override
   public void accept(Frame frame, Callback callback) {
      if (this.typeSink == null) {
         this.typeSink = this.newMessageSink();
         this.dispatchComplete = new CompletableFuture<>();
         this.executor.execute(() -> {
            try {
               this.getMethodHandle().invoke((MessageSink)this.typeSink);
               if (this.typeSink instanceof Closeable closeable) {
                  IO.close(closeable);
               }

               this.dispatchComplete.complete(null);
            } catch (Throwable var3x) {
               this.typeSink.fail(var3x);
               this.dispatchComplete.completeExceptionally(var3x);
            }
         });
      }

      Callback frameCallback = callback;
      if (frame.isFin()) {
         Completable frameComplete = Completable.from(callback);
         frameCallback = frameComplete;
         CompletableFuture.allOf(this.dispatchComplete, frameComplete).whenComplete((result, failure) -> {
            this.typeSink = null;
            this.dispatchComplete = null;
            if (failure == null) {
               this.autoDemand();
            }
         });
      }

      this.typeSink.accept(frame, frameCallback);
   }

   @Override
   public void fail(Throwable failure) {
      if (this.typeSink != null) {
         this.typeSink.fail(failure);
      }
   }

   public boolean isDispatched() {
      return this.dispatchComplete != null;
   }
}
