package org.glassfish.jersey.internal.jsr166;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public interface SubmittableFlowPublisher<T> extends Flow.Publisher<T>, AutoCloseable {
   CompletableFuture<Void> consume(Consumer<? super T> var1);

   @Override
   void close();

   void closeExceptionally(Throwable var1);

   long estimateMinimumDemand();

   int estimateMaximumLag();

   Throwable getClosedException();

   int getMaxBufferCapacity();

   int offer(T var1, long var2, TimeUnit var4, BiPredicate<Flow.Subscriber<? super T>, ? super T> var5);

   int offer(T var1, BiPredicate<Flow.Subscriber<? super T>, ? super T> var2);

   int submit(T var1);

   @Override
   void subscribe(Flow.Subscriber<? super T> var1);
}
