package retrofit2;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import okhttp3.Request;
import okio.Timeout;

final class DefaultCallAdapterFactory extends CallAdapter.Factory {
   @Nullable
   private final Executor callbackExecutor;

   DefaultCallAdapterFactory(@Nullable Executor callbackExecutor) {
      this.callbackExecutor = callbackExecutor;
   }

   @Nullable
   @Override
   public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
      if (getRawType(returnType) != Call.class) {
         return null;
      } else if (!(returnType instanceof ParameterizedType)) {
         throw new IllegalArgumentException("Call return type must be parameterized as Call<Foo> or Call<? extends Foo>");
      } else {
         final Type responseType = Utils.getParameterUpperBound(0, (ParameterizedType)returnType);
         final Executor executor = Utils.isAnnotationPresent(annotations, SkipCallbackExecutor.class) ? null : this.callbackExecutor;
         return new CallAdapter<Object, Call<?>>() {
            @Override
            public Type responseType() {
               return responseType;
            }

            public Call<Object> adapt(Call<Object> call) {
               return (Call<Object>)(executor == null ? call : new DefaultCallAdapterFactory.ExecutorCallbackCall<>(executor, call));
            }
         };
      }
   }

   static final class ExecutorCallbackCall<T> implements Call<T> {
      final Executor callbackExecutor;
      final Call<T> delegate;

      ExecutorCallbackCall(Executor callbackExecutor, Call<T> delegate) {
         this.callbackExecutor = callbackExecutor;
         this.delegate = delegate;
      }

      @Override
      public void enqueue(final Callback<T> callback) {
         Objects.requireNonNull(callback, "callback == null");
         this.delegate.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
               ExecutorCallbackCall.this.callbackExecutor.execute(() -> {
                  if (ExecutorCallbackCall.this.delegate.isCanceled()) {
                     callback.onFailure(ExecutorCallbackCall.this, new IOException("Canceled"));
                  } else {
                     callback.onResponse(ExecutorCallbackCall.this, response);
                  }
               });
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
               ExecutorCallbackCall.this.callbackExecutor.execute(() -> callback.onFailure(ExecutorCallbackCall.this, t));
            }
         });
      }

      @Override
      public boolean isExecuted() {
         return this.delegate.isExecuted();
      }

      @Override
      public Response<T> execute() throws IOException {
         return this.delegate.execute();
      }

      @Override
      public void cancel() {
         this.delegate.cancel();
      }

      @Override
      public boolean isCanceled() {
         return this.delegate.isCanceled();
      }

      @Override
      public Call<T> clone() {
         return new DefaultCallAdapterFactory.ExecutorCallbackCall<>(this.callbackExecutor, this.delegate.clone());
      }

      @Override
      public Request request() {
         return this.delegate.request();
      }

      @Override
      public Timeout timeout() {
         return this.delegate.timeout();
      }
   }
}
