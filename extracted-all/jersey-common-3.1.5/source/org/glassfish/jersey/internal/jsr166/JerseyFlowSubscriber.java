package org.glassfish.jersey.internal.jsr166;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public interface JerseyFlowSubscriber<T> extends Flow.Subscriber<T>, Subscriber<T> {
   @Override
   default void onSubscribe(final Subscription subscription) {
      this.onSubscribe(new Flow.Subscription() {
         @Override
         public void request(long n) {
            subscription.request(n);
         }

         @Override
         public void cancel() {
            subscription.cancel();
         }
      });
   }
}
