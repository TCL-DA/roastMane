package org.eclipse.jetty.client;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.client.internal.HttpContentResponse;

@Deprecated
public class FutureResponseListener extends BufferingResponseListener implements Future<ContentResponse> {
   private final AtomicBoolean cancelled = new AtomicBoolean();
   private final CountDownLatch latch = new CountDownLatch(1);
   private final Request request;
   private ContentResponse response;
   private Throwable failure;

   public FutureResponseListener(Request request) {
      this(request, 2097152);
   }

   public FutureResponseListener(Request request, int maxLength) {
      super(maxLength);
      this.request = request;
   }

   public Request getRequest() {
      return this.request;
   }

   @Override
   public void onComplete(Result result) {
      this.response = new HttpContentResponse(result.getResponse(), this.getContent(), this.getMediaType(), this.getEncoding());
      this.failure = result.getFailure();
      this.latch.countDown();
   }

   @Override
   public boolean cancel(boolean mayInterruptIfRunning) {
      if (this.cancelled.compareAndSet(false, true)) {
         this.request.abort(new CancellationException());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled.get();
   }

   @Override
   public boolean isDone() {
      return this.latch.getCount() == 0L || this.isCancelled();
   }

   public ContentResponse get() throws InterruptedException, ExecutionException {
      this.latch.await();
      return this.getResult();
   }

   public ContentResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      boolean expired = !this.latch.await(timeout, unit);
      if (expired) {
         throw new TimeoutException();
      } else {
         return this.getResult();
      }
   }

   private ContentResponse getResult() throws ExecutionException {
      if (this.isCancelled()) {
         throw (CancellationException)new CancellationException().initCause(this.failure);
      } else if (this.failure != null) {
         throw new ExecutionException(this.failure);
      } else {
         return this.response;
      }
   }
}
