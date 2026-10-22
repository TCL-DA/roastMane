package org.glassfish.jersey.internal.jsr166;

public final class Flow {
   static final int DEFAULT_BUFFER_SIZE = 256;

   private Flow() {
   }

   public static int defaultBufferSize() {
      return 256;
   }

   public interface Processor<T, R> extends Flow.Subscriber<T>, Flow.Publisher<R> {
   }

   @FunctionalInterface
   public interface Publisher<T> {
      void subscribe(Flow.Subscriber<? super T> var1);
   }

   public interface Subscriber<T> {
      void onSubscribe(Flow.Subscription var1);

      void onNext(T var1);

      void onError(Throwable var1);

      void onComplete();
   }

   public interface Subscription {
      void request(long var1);

      void cancel();
   }
}
