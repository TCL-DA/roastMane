package com.segment.analytics;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;

class Platform {
   static final String THREAD_NAME = "Analytics";
   private static final Platform PLATFORM = findPlatform();

   static Platform get() {
      return PLATFORM;
   }

   private static Platform findPlatform() {
      return new Platform();
   }

   OkHttpClient defaultClient() {
      return new Builder().connectTimeout(15L, TimeUnit.SECONDS).readTimeout(15L, TimeUnit.SECONDS).writeTimeout(15L, TimeUnit.SECONDS).build();
   }

   ExecutorService defaultNetworkExecutor() {
      return Executors.newSingleThreadExecutor(this.defaultThreadFactory());
   }

   ThreadFactory defaultThreadFactory() {
      return new ThreadFactory() {
         @Override
         public Thread newThread(final Runnable r) {
            return new Thread(new Runnable() {
               @Override
               public void run() {
                  Thread.currentThread().setPriority(1);
                  r.run();
               }
            }, "Analytics");
         }
      };
   }

   public long defaultFlushIntervalInMillis() {
      return 10000L;
   }

   public int defaultFlushQueueSize() {
      return 250;
   }
}
