package org.glassfish.jersey.internal.jsr166;

import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public class SubmissionPublisherFactory {
   public static <T> SubmittableFlowPublisher<T> createSubmissionPublisher() {
      return new SubmissionPublisher<>();
   }

   public static <T> SubmittableFlowPublisher<T> createSubmissionPublisher(Executor executor, int maxBufferCapacity) {
      return new SubmissionPublisher<>(executor, maxBufferCapacity);
   }

   public static <T> SubmittableFlowPublisher<T> createSubmissionPublisher(
      Executor executor, int maxBufferCapacity, BiConsumer<? super Flow.Subscriber<? super T>, ? super Throwable> handler
   ) {
      return new SubmissionPublisher<>(executor, maxBufferCapacity, handler);
   }
}
