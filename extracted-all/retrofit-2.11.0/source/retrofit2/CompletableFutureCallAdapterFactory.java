package retrofit2;

import android.annotation.TargetApi;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

@IgnoreJRERequirement
@TargetApi(24)
final class CompletableFutureCallAdapterFactory extends CallAdapter.Factory {
   @Nullable
   @Override
   public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
      if (getRawType(returnType) != CompletableFuture.class) {
         return null;
      } else if (!(returnType instanceof ParameterizedType)) {
         throw new IllegalStateException("CompletableFuture return type must be parameterized as CompletableFuture<Foo> or CompletableFuture<? extends Foo>");
      } else {
         Type innerType = getParameterUpperBound(0, (ParameterizedType)returnType);
         if (getRawType(innerType) != Response.class) {
            return new CompletableFutureCallAdapterFactory.BodyCallAdapter(innerType);
         } else if (!(innerType instanceof ParameterizedType)) {
            throw new IllegalStateException("Response must be parameterized as Response<Foo> or Response<? extends Foo>");
         } else {
            Type responseType = getParameterUpperBound(0, (ParameterizedType)innerType);
            return new CompletableFutureCallAdapterFactory.ResponseCallAdapter(responseType);
         }
      }
   }

   @IgnoreJRERequirement
   private static final class BodyCallAdapter<R> implements CallAdapter<R, CompletableFuture<R>> {
      private final Type responseType;

      BodyCallAdapter(Type responseType) {
         this.responseType = responseType;
      }

      @Override
      public Type responseType() {
         return this.responseType;
      }

      public CompletableFuture<R> adapt(Call<R> call) {
         CompletableFuture<R> future = new CompletableFutureCallAdapterFactory.CallCancelCompletableFuture<>(call);
         call.enqueue(new CompletableFutureCallAdapterFactory.BodyCallAdapter.BodyCallback(future));
         return future;
      }

      @IgnoreJRERequirement
      private class BodyCallback implements Callback<R> {
         private final CompletableFuture<R> future;

         public BodyCallback(CompletableFuture<R> future) {
            this.future = future;
         }

         @Override
         public void onResponse(Call<R> call, Response<R> response) {
            if (response.isSuccessful()) {
               this.future.complete(response.body());
            } else {
               this.future.completeExceptionally(new HttpException(response));
            }
         }

         @Override
         public void onFailure(Call<R> call, Throwable t) {
            this.future.completeExceptionally(t);
         }
      }
   }

   @IgnoreJRERequirement
   private static final class CallCancelCompletableFuture<T> extends CompletableFuture<T> {
      private final Call<?> call;

      CallCancelCompletableFuture(Call<?> call) {
         this.call = call;
      }

      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
         if (mayInterruptIfRunning) {
            this.call.cancel();
         }

         return super.cancel(mayInterruptIfRunning);
      }
   }

   @IgnoreJRERequirement
   private static final class ResponseCallAdapter<R> implements CallAdapter<R, CompletableFuture<Response<R>>> {
      private final Type responseType;

      ResponseCallAdapter(Type responseType) {
         this.responseType = responseType;
      }

      @Override
      public Type responseType() {
         return this.responseType;
      }

      public CompletableFuture<Response<R>> adapt(Call<R> call) {
         CompletableFuture<Response<R>> future = new CompletableFutureCallAdapterFactory.CallCancelCompletableFuture<>(call);
         call.enqueue(new CompletableFutureCallAdapterFactory.ResponseCallAdapter.ResponseCallback(future));
         return future;
      }

      @IgnoreJRERequirement
      private class ResponseCallback implements Callback<R> {
         private final CompletableFuture<Response<R>> future;

         public ResponseCallback(CompletableFuture<Response<R>> future) {
            this.future = future;
         }

         @Override
         public void onResponse(Call<R> call, Response<R> response) {
            this.future.complete(response);
         }

         @Override
         public void onFailure(Call<R> call, Throwable t) {
            this.future.completeExceptionally(t);
         }
      }
   }
}
