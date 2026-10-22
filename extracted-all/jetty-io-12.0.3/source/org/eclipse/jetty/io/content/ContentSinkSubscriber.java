package org.eclipse.jetty.io.content;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.util.Callback;

public class ContentSinkSubscriber implements Subscriber<Content.Chunk> {
   private final Content.Sink sink;
   private final Callback callback;
   private Subscription subscription;

   public ContentSinkSubscriber(Content.Sink sink, Callback callback) {
      this.sink = sink;
      this.callback = callback;
   }

   @Override
   public void onSubscribe(Subscription subscription) {
      this.subscription = subscription;
      subscription.request(1L);
   }

   public void onNext(Content.Chunk chunk) {
      chunk.retain();
      this.sink.write(false, chunk.getByteBuffer(), Callback.from(() -> this.succeeded(chunk), x -> this.failed(chunk, x)));
   }

   private void succeeded(Content.Chunk chunk) {
      chunk.release();
      if (!chunk.isLast()) {
         this.subscription.request(1L);
      }
   }

   private void failed(Content.Chunk chunk, Throwable failure) {
      chunk.release();
      this.subscription.cancel();
      this.onError(failure);
   }

   @Override
   public void onError(Throwable failure) {
      this.callback.failed(failure);
   }

   @Override
   public void onComplete() {
      this.sink.write(true, null, this.callback);
   }
}
