package org.glassfish.jersey.client;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.logging.Logger;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.spi.ThreadPoolExecutorProvider;

@ClientAsyncExecutor
class DefaultClientAsyncExecutorProvider extends ThreadPoolExecutorProvider {
   private static final Logger LOGGER = Logger.getLogger(DefaultClientAsyncExecutorProvider.class.getName());
   private final LazyValue<Integer> asyncThreadPoolSize;

   @Inject
   public DefaultClientAsyncExecutorProvider(@Named("ClientAsyncThreadPoolSize") final int poolSize) {
      super("jersey-client-async-executor");
      this.asyncThreadPoolSize = Values.lazy(new Value<Integer>() {
         public Integer get() {
            if (poolSize <= 0) {
               DefaultClientAsyncExecutorProvider.LOGGER.config(LocalizationMessages.IGNORED_ASYNC_THREADPOOL_SIZE(poolSize));
               return Integer.MAX_VALUE;
            } else {
               DefaultClientAsyncExecutorProvider.LOGGER.config(LocalizationMessages.USING_FIXED_ASYNC_THREADPOOL(poolSize));
               return poolSize;
            }
         }
      });
   }

   protected int getMaximumPoolSize() {
      return (Integer)this.asyncThreadPoolSize.get();
   }

   protected int getCorePoolSize() {
      Integer maximumPoolSize = this.getMaximumPoolSize();
      return maximumPoolSize != Integer.MAX_VALUE ? maximumPoolSize : 0;
   }
}
