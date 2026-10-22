package retrofit2.mock;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

final class BehaviorCall<T> implements Call<T> {
   final NetworkBehavior behavior;
   final ExecutorService backgroundExecutor;
   final Call<T> delegate;
   @Nullable
   private volatile Future<?> task;
   volatile boolean canceled;
   @GuardedBy("this")
   private boolean executed;

   BehaviorCall(NetworkBehavior behavior, ExecutorService backgroundExecutor, Call<T> delegate) {
      this.behavior = behavior;
      this.backgroundExecutor = backgroundExecutor;
      this.delegate = delegate;
   }

   public Call<T> clone() {
      return new BehaviorCall<>(this.behavior, this.backgroundExecutor, this.delegate.clone());
   }

   public Request request() {
      return this.delegate.request();
   }

   public Timeout timeout() {
      return this.delegate.timeout();
   }

   public void enqueue(final Callback<T> callback) {
      if (callback == null) {
         throw new NullPointerException("callback == null");
      } else {
         synchronized (this) {
            if (this.executed) {
               throw new IllegalStateException("Already executed");
            }

            this.executed = true;
         }

         this.task = this.backgroundExecutor.submit(new Runnable() {
            boolean delaySleep() {
               long sleepMs = BehaviorCall.this.behavior.calculateDelay(TimeUnit.MILLISECONDS);
               if (sleepMs > 0L) {
                  try {
                     Thread.sleep(sleepMs);
                  } catch (InterruptedException var4) {
                     callback.onFailure(BehaviorCall.this, new IOException("canceled", var4));
                     return false;
                  }
               }

               return true;
            }

            @Override
            public void run() {
               if (BehaviorCall.this.canceled) {
                  callback.onFailure(BehaviorCall.this, new IOException("canceled"));
               } else if (BehaviorCall.this.behavior.calculateIsFailure()) {
                  if (this.delaySleep()) {
                     callback.onFailure(BehaviorCall.this, BehaviorCall.this.behavior.failureException());
                  }
               } else if (BehaviorCall.this.behavior.calculateIsError()) {
                  if (this.delaySleep()) {
                     callback.onResponse(BehaviorCall.this, BehaviorCall.this.behavior.createErrorResponse());
                  }
               } else {
                  BehaviorCall.this.delegate.enqueue(new Callback<T>() {
                     public void onResponse(Call<T> call, Response<T> response) {
                        if (delaySleep()) {
                           callback.onResponse(call, response);
                        }
                     }

                     public void onFailure(Call<T> call, Throwable t) {
                        if (delaySleep()) {
                           callback.onFailure(call, t);
                        }
                     }
                  });
               }
            }
         });
      }
   }

   public synchronized boolean isExecuted() {
      return this.executed;
   }

   public Response<T> execute() throws IOException {
      final AtomicReference<Response<T>> responseRef = new AtomicReference<>();
      final AtomicReference<Throwable> failureRef = new AtomicReference<>();
      final CountDownLatch latch = new CountDownLatch(1);
      this.enqueue(new Callback<T>() {
         public void onResponse(Call<T> call, Response<T> response) {
            responseRef.set(response);
            latch.countDown();
         }

         public void onFailure(Call<T> call, Throwable t) {
            failureRef.set(t);
            latch.countDown();
         }
      });

      try {
         latch.await();
      } catch (InterruptedException var6) {
         throw new IOException("canceled", var6);
      }

      Response<T> response = responseRef.get();
      if (response != null) {
         return response;
      } else {
         Throwable failure = failureRef.get();
         if (failure instanceof RuntimeException) {
            throw (RuntimeException)failure;
         } else if (failure instanceof IOException) {
            throw (IOException)failure;
         } else {
            throw new RuntimeException(failure);
         }
      }
   }

   public void cancel() {
      this.canceled = true;
      Future<?> task = this.task;
      if (task != null) {
         task.cancel(true);
      }
   }

   public boolean isCanceled() {
      return this.canceled;
   }
}
