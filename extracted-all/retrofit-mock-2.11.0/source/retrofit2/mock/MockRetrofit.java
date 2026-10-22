package retrofit2.mock;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import retrofit2.Retrofit;

public final class MockRetrofit {
   private final Retrofit retrofit;
   private final NetworkBehavior behavior;
   private final ExecutorService executor;

   MockRetrofit(Retrofit retrofit, NetworkBehavior behavior, ExecutorService executor) {
      this.retrofit = retrofit;
      this.behavior = behavior;
      this.executor = executor;
   }

   public Retrofit retrofit() {
      return this.retrofit;
   }

   public NetworkBehavior networkBehavior() {
      return this.behavior;
   }

   public Executor backgroundExecutor() {
      return this.executor;
   }

   public <T> BehaviorDelegate<T> create(Class<T> service) {
      return new BehaviorDelegate<>(this.retrofit, this.behavior, this.executor, service);
   }

   public static final class Builder {
      private final Retrofit retrofit;
      @Nullable
      private NetworkBehavior behavior;
      @Nullable
      private ExecutorService executor;

      public Builder(Retrofit retrofit) {
         if (retrofit == null) {
            throw new NullPointerException("retrofit == null");
         } else {
            this.retrofit = retrofit;
         }
      }

      public MockRetrofit.Builder networkBehavior(NetworkBehavior behavior) {
         if (behavior == null) {
            throw new NullPointerException("behavior == null");
         } else {
            this.behavior = behavior;
            return this;
         }
      }

      public MockRetrofit.Builder backgroundExecutor(ExecutorService executor) {
         if (executor == null) {
            throw new NullPointerException("executor == null");
         } else {
            this.executor = executor;
            return this;
         }
      }

      public MockRetrofit build() {
         if (this.behavior == null) {
            this.behavior = NetworkBehavior.create();
         }

         if (this.executor == null) {
            this.executor = Executors.newCachedThreadPool();
         }

         return new MockRetrofit(this.retrofit, this.behavior, this.executor);
      }
   }
}
