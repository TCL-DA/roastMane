package retrofit2.mock;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class Calls {
   public static <T> Call<T> defer(Callable<Call<T>> callable) {
      return new Calls.DeferredCall<>(callable);
   }

   public static <T> Call<T> response(@Nullable T successValue) {
      return new Calls.FakeCall<>(Response.success(successValue), null);
   }

   public static <T> Call<T> response(Response<T> response) {
      return new Calls.FakeCall<>(response, null);
   }

   public static <T> Call<T> failure(IOException failure) {
      return new Calls.FakeCall<>(null, failure);
   }

   public static <T> Call<T> failure(Throwable failure) {
      return new Calls.FakeCall<>(null, failure);
   }

   private Calls() {
      throw new AssertionError("No instances.");
   }

   static final class DeferredCall<T> implements Call<T> {
      private final Callable<Call<T>> callable;
      @Nullable
      private Call<T> delegate;

      DeferredCall(Callable<Call<T>> callable) {
         this.callable = callable;
      }

      private synchronized Call<T> getDelegate() {
         Call<T> delegate = this.delegate;
         if (delegate == null) {
            try {
               delegate = this.callable.call();
            } catch (Exception var3) {
               delegate = Calls.failure(var3);
            }

            this.delegate = delegate;
         }

         return delegate;
      }

      public Response<T> execute() throws IOException {
         return this.getDelegate().execute();
      }

      public void enqueue(Callback<T> callback) {
         this.getDelegate().enqueue(callback);
      }

      public boolean isExecuted() {
         return this.getDelegate().isExecuted();
      }

      public void cancel() {
         this.getDelegate().cancel();
      }

      public boolean isCanceled() {
         return this.getDelegate().isCanceled();
      }

      public Call<T> clone() {
         return new Calls.DeferredCall<>(this.callable);
      }

      public Request request() {
         return this.getDelegate().request();
      }

      public Timeout timeout() {
         return this.getDelegate().timeout();
      }
   }

   static final class FakeCall<T> implements Call<T> {
      private final Response<T> response;
      private final Throwable error;
      private final AtomicBoolean canceled = new AtomicBoolean();
      private final AtomicBoolean executed = new AtomicBoolean();

      FakeCall(@Nullable Response<T> response, @Nullable Throwable error) {
         if (response == null == (error == null)) {
            throw new AssertionError("Only one of response or error can be set.");
         } else {
            this.response = response;
            this.error = error;
         }
      }

      public Response<T> execute() throws IOException {
         if (!this.executed.compareAndSet(false, true)) {
            throw new IllegalStateException("Already executed");
         } else if (this.canceled.get()) {
            throw new IOException("canceled");
         } else if (this.response != null) {
            return this.response;
         } else {
            throw (Error)sneakyThrow(this.error);
         }
      }

      private static <T extends Throwable> T sneakyThrow(Throwable t) throws T {
         throw t;
      }

      public void enqueue(Callback<T> callback) {
         if (callback == null) {
            throw new NullPointerException("callback == null");
         } else if (!this.executed.compareAndSet(false, true)) {
            throw new IllegalStateException("Already executed");
         } else {
            if (this.canceled.get()) {
               callback.onFailure(this, new IOException("canceled"));
            } else if (this.response != null) {
               callback.onResponse(this, this.response);
            } else {
               callback.onFailure(this, this.error);
            }
         }
      }

      public boolean isExecuted() {
         return this.executed.get();
      }

      public void cancel() {
         this.canceled.set(true);
      }

      public boolean isCanceled() {
         return this.canceled.get();
      }

      public Call<T> clone() {
         return new Calls.FakeCall<>(this.response, this.error);
      }

      public Request request() {
         return this.response != null ? this.response.raw().request() : new Builder().url("http://localhost").build();
      }

      public Timeout timeout() {
         return Timeout.NONE;
      }
   }
}
