package retrofit2;

import android.annotation.TargetApi;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

class BuiltInFactories {
   List<? extends CallAdapter.Factory> createDefaultCallAdapterFactories(@Nullable Executor callbackExecutor) {
      return Collections.singletonList(new DefaultCallAdapterFactory(callbackExecutor));
   }

   List<? extends Converter.Factory> createDefaultConverterFactories() {
      return Collections.emptyList();
   }

   @TargetApi(24)
   static final class Java8 extends BuiltInFactories {
      @Override
      List<? extends CallAdapter.Factory> createDefaultCallAdapterFactories(@Nullable Executor callbackExecutor) {
         return Arrays.asList(new CompletableFutureCallAdapterFactory(), new DefaultCallAdapterFactory(callbackExecutor));
      }

      @Override
      List<? extends Converter.Factory> createDefaultConverterFactories() {
         return Collections.singletonList(new OptionalConverterFactory());
      }
   }
}
