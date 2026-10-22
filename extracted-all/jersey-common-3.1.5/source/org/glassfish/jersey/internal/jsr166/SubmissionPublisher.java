package org.glassfish.jersey.internal.jsr166;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class SubmissionPublisher<T> implements SubmittableFlowPublisher<T> {
   private final java.util.concurrent.SubmissionPublisher<T> publisher;

   public SubmissionPublisher(Executor executor, int maxBufferCapacity, BiConsumer<? super Flow.Subscriber<? super T>, ? super Throwable> handler) {
      this.publisher = new java.util.concurrent.SubmissionPublisher<>(executor, maxBufferCapacity, convertConsumer(handler));
   }

   public SubmissionPublisher(Executor executor, int maxBufferCapacity) {
      this.publisher = new java.util.concurrent.SubmissionPublisher<>(executor, maxBufferCapacity);
   }

   public SubmissionPublisher() {
      this.publisher = new java.util.concurrent.SubmissionPublisher<>();
   }

   @Override
   public CompletableFuture<Void> consume(Consumer<? super T> consumer) {
      return this.publisher.consume(consumer);
   }

   @Override
   public void close() {
      this.publisher.close();
   }

   @Override
   public void closeExceptionally(Throwable error) {
      this.publisher.closeExceptionally(error);
   }

   @Override
   public long estimateMinimumDemand() {
      return this.publisher.estimateMinimumDemand();
   }

   @Override
   public int estimateMaximumLag() {
      return this.publisher.estimateMaximumLag();
   }

   @Override
   public Throwable getClosedException() {
      return this.publisher.getClosedException();
   }

   @Override
   public int getMaxBufferCapacity() {
      return this.publisher.getMaxBufferCapacity();
   }

   @Override
   public int offer(T item, long timeout, TimeUnit unit, BiPredicate<Flow.Subscriber<? super T>, ? super T> onDrop) {
      return this.publisher.offer(item, timeout, unit, convertPredicate(onDrop));
   }

   @Override
   public int offer(T item, BiPredicate<Flow.Subscriber<? super T>, ? super T> onDrop) {
      return this.publisher.offer(item, convertPredicate(onDrop));
   }

   @Override
   public int submit(T item) {
      return this.publisher.submit(item);
   }

   @Override
   public void subscribe(Flow.Subscriber<? super T> subscriber) {
      this.publisher.subscribe(convertSubscriber(subscriber));
   }

   private static <T> BiConsumer<? super Subscriber<? super T>, ? super Throwable> convertConsumer(
      final BiConsumer<? super Flow.Subscriber<? super T>, ? super Throwable> consumer
   ) {
      return new BiConsumer<Subscriber<? super T>, Throwable>() {
         public void accept(Subscriber<? super T> subscriber, Throwable throwable) {
            consumer.accept(SubmissionPublisher.convertSubscriber(subscriber), throwable);
         }
      };
   }

   private static <T> BiPredicate<Subscriber<? super T>, ? super T> convertPredicate(final BiPredicate<Flow.Subscriber<? super T>, ? super T> predicate) {
      return new BiPredicate<Subscriber<? super T>, T>() {
         public boolean test(Subscriber<? super T> subscriber, T t) {
            return predicate.test(SubmissionPublisher.convertSubscriber(subscriber), t);
         }
      };
   }

   private static <T> Flow.Subscriber<? super T> convertSubscriber(final Subscriber<? super T> subscriber) {
      return new Flow.Subscriber<T>() {
         @Override
         public void onSubscribe(Flow.Subscription subscription) {
            subscriber.onSubscribe(SubmissionPublisher.convertSubscription(subscription));
         }

         @Override
         public void onNext(T item) {
            subscriber.onNext(item);
         }

         @Override
         public void onError(Throwable throwable) {
            subscriber.onError(throwable);
         }

         @Override
         public void onComplete() {
            subscriber.onComplete();
         }
      };
   }

   private static <T> Subscriber<T> convertSubscriber(final Flow.Subscriber<T> subscriber) {
      return new Subscriber<T>() {
         @Override
         public void onSubscribe(Subscription subscription) {
            subscriber.onSubscribe(SubmissionPublisher.convertSubscription(subscription));
         }

         @Override
         public void onNext(T item) {
            subscriber.onNext(item);
         }

         @Override
         public void onError(Throwable throwable) {
            subscriber.onError(throwable);
         }

         @Override
         public void onComplete() {
            subscriber.onComplete();
         }
      };
   }

   private static Subscription convertSubscription(final Flow.Subscription subscription) {
      return new Subscription() {
         @Override
         public void request(long n) {
            subscription.request(n);
         }

         @Override
         public void cancel() {
            subscription.cancel();
         }
      };
   }

   private static Flow.Subscription convertSubscription(final Subscription subscription) {
      return new Flow.Subscription() {
         @Override
         public void request(long n) {
            subscription.request(n);
         }

         @Override
         public void cancel() {
            subscription.cancel();
         }
      };
   }
}
