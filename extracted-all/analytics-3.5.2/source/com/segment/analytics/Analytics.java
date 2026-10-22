package com.segment.analytics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.segment.analytics.gson.AutoValueAdapterFactory;
import com.segment.analytics.gson.ISO8601DateAdapter;
import com.segment.analytics.http.SegmentService;
import com.segment.analytics.internal.AnalyticsClient;
import com.segment.analytics.internal.AnalyticsVersion;
import com.segment.analytics.messages.Message;
import com.segment.analytics.messages.MessageBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import okhttp3.logging.HttpLoggingInterceptor.Logger;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Analytics {
   private final AnalyticsClient client;
   private final List<MessageTransformer> messageTransformers;
   private final List<MessageInterceptor> messageInterceptors;
   private final Log log;

   Analytics(AnalyticsClient client, List<MessageTransformer> messageTransformers, List<MessageInterceptor> messageInterceptors, Log log) {
      this.client = client;
      this.messageTransformers = messageTransformers;
      this.messageInterceptors = messageInterceptors;
      this.log = log;
   }

   public static Analytics.Builder builder(String writeKey) {
      return new Analytics.Builder(writeKey);
   }

   public void enqueue(MessageBuilder builder) {
      Message message = this.buildMessage(builder);
      if (message != null) {
         this.client.enqueue(message);
      }
   }

   public boolean offer(MessageBuilder builder) {
      Message message = this.buildMessage(builder);
      return message == null ? false : this.client.offer(message);
   }

   public void flush() {
      this.client.flush();
   }

   public void shutdown() {
      this.client.shutdown();
   }

   private Message buildMessage(MessageBuilder builder) {
      for (MessageTransformer messageTransformer : this.messageTransformers) {
         boolean shouldContinue = messageTransformer.transform(builder);
         if (!shouldContinue) {
            this.log.print(Log.Level.VERBOSE, "Skipping message %s.", builder);
            return null;
         }
      }

      Message message = builder.build();

      for (MessageInterceptor messageInterceptor : this.messageInterceptors) {
         message = messageInterceptor.intercept(message);
         if (message == null) {
            this.log.print(Log.Level.VERBOSE, "Skipping message %s.", builder);
            return null;
         }
      }

      return message;
   }

   public static class Builder {
      private static final String DEFAULT_ENDPOINT = "https://api.segment.io";
      private static final String DEFAULT_PATH = "/v1/import/";
      private static final String DEFAULT_USER_AGENT = "analytics-java/" + AnalyticsVersion.get();
      private static final int MESSAGE_QUEUE_MAX_BYTE_SIZE = 512000;
      private final String writeKey;
      private OkHttpClient client;
      private Log log;
      public HttpUrl endpoint;
      public HttpUrl uploadURL;
      private String userAgent = DEFAULT_USER_AGENT;
      private List<MessageTransformer> messageTransformers;
      private List<MessageInterceptor> messageInterceptors;
      private ExecutorService networkExecutor;
      private ThreadFactory threadFactory;
      private int flushQueueSize;
      private int maximumFlushAttempts;
      private int maximumQueueSizeInBytes;
      private long flushIntervalInMillis;
      private List<Callback> callbacks;
      private int queueCapacity;
      private boolean forceTlsV1 = false;
      private GsonBuilder gsonBuilder;

      Builder(String writeKey) {
         if (writeKey != null && writeKey.trim().length() != 0) {
            this.writeKey = writeKey;
         } else {
            throw new NullPointerException("writeKey cannot be null or empty.");
         }
      }

      public Analytics.Builder client(OkHttpClient client) {
         if (client == null) {
            throw new NullPointerException("Null client");
         } else {
            this.client = client;
            return this;
         }
      }

      public Analytics.Builder log(Log log) {
         if (log == null) {
            throw new NullPointerException("Null log");
         } else {
            this.log = log;
            return this;
         }
      }

      public Analytics.Builder endpoint(String endpoint) {
         if (endpoint != null && endpoint.trim().length() != 0) {
            this.endpoint = HttpUrl.parse(endpoint + "/v1/import/");
            return this;
         } else {
            throw new NullPointerException("endpoint cannot be null or empty.");
         }
      }

      public Analytics.Builder setUploadURL(String uploadURL) {
         if (uploadURL != null && uploadURL.trim().length() != 0) {
            this.uploadURL = HttpUrl.parse(uploadURL);
            return this;
         } else {
            throw new NullPointerException("Upload URL cannot be null or empty.");
         }
      }

      public Analytics.Builder userAgent(String userAgent) {
         if (userAgent != null && userAgent.trim().length() != 0) {
            this.userAgent = userAgent;
            return this;
         } else {
            throw new NullPointerException("userAgent cannot be null or empty.");
         }
      }

      public Analytics.Builder messageTransformer(MessageTransformer transformer) {
         if (transformer == null) {
            throw new NullPointerException("Null transformer");
         } else {
            if (this.messageTransformers == null) {
               this.messageTransformers = new ArrayList<>();
            }

            if (this.messageTransformers.contains(transformer)) {
               throw new IllegalStateException("MessageTransformer is already registered.");
            } else {
               this.messageTransformers.add(transformer);
               return this;
            }
         }
      }

      public Analytics.Builder messageInterceptor(MessageInterceptor interceptor) {
         if (interceptor == null) {
            throw new NullPointerException("Null interceptor");
         } else {
            if (this.messageInterceptors == null) {
               this.messageInterceptors = new ArrayList<>();
            }

            if (this.messageInterceptors.contains(interceptor)) {
               throw new IllegalStateException("MessageInterceptor is already registered.");
            } else {
               this.messageInterceptors.add(interceptor);
               return this;
            }
         }
      }

      public Analytics.Builder queueCapacity(int capacity) {
         if (capacity <= 0) {
            throw new IllegalArgumentException("capacity should be positive.");
         } else {
            this.queueCapacity = capacity;
            return this;
         }
      }

      public Analytics.Builder gsonBuilder(GsonBuilder gsonBuilder) {
         if (gsonBuilder == null) {
            throw new NullPointerException("Null gsonBuilder");
         } else if (this.gsonBuilder != null) {
            throw new IllegalStateException("gsonBuilder is already registered.");
         } else {
            this.gsonBuilder = gsonBuilder;
            return this;
         }
      }

      public Analytics.Builder flushQueueSize(int flushQueueSize) {
         if (flushQueueSize < 1) {
            throw new IllegalArgumentException("flushQueueSize must not be less than 1.");
         } else {
            this.flushQueueSize = flushQueueSize;
            return this;
         }
      }

      public Analytics.Builder maximumQueueSizeInBytes(int bytes) {
         if (bytes < 1) {
            throw new IllegalArgumentException("maximumQueueSizeInBytes must not be less than 1.");
         } else {
            this.maximumQueueSizeInBytes = bytes;
            return this;
         }
      }

      public Analytics.Builder flushInterval(long flushInterval, TimeUnit unit) {
         long flushIntervalInMillis = unit.toMillis(flushInterval);
         if (flushIntervalInMillis < 1000L) {
            throw new IllegalArgumentException("flushInterval must not be less than 1 second.");
         } else {
            this.flushIntervalInMillis = flushIntervalInMillis;
            return this;
         }
      }

      public Analytics.Builder retries(int maximumRetries) {
         if (maximumRetries < 1) {
            throw new IllegalArgumentException("retries must be at least 1");
         } else {
            this.maximumFlushAttempts = maximumRetries;
            return this;
         }
      }

      public Analytics.Builder networkExecutor(ExecutorService networkExecutor) {
         if (networkExecutor == null) {
            throw new NullPointerException("Null networkExecutor");
         } else {
            this.networkExecutor = networkExecutor;
            return this;
         }
      }

      public Analytics.Builder threadFactory(ThreadFactory threadFactory) {
         if (threadFactory == null) {
            throw new NullPointerException("Null threadFactory");
         } else {
            this.threadFactory = threadFactory;
            return this;
         }
      }

      public Analytics.Builder callback(Callback callback) {
         if (callback == null) {
            throw new NullPointerException("Null callback");
         } else {
            if (this.callbacks == null) {
               this.callbacks = new ArrayList<>();
            }

            if (this.callbacks.contains(callback)) {
               throw new IllegalStateException("Callback is already registered.");
            } else {
               this.callbacks.add(callback);
               return this;
            }
         }
      }

      public Analytics.Builder plugin(Plugin plugin) {
         if (plugin == null) {
            throw new NullPointerException("Null plugin");
         } else {
            plugin.configure(this);
            return this;
         }
      }

      public Analytics.Builder forceTlsVersion1() {
         this.forceTlsV1 = true;
         return this;
      }

      public Analytics build() {
         if (this.gsonBuilder == null) {
            this.gsonBuilder = new GsonBuilder();
         }

         this.gsonBuilder.registerTypeAdapterFactory(new AutoValueAdapterFactory()).registerTypeAdapter(Date.class, new ISO8601DateAdapter());
         Gson gson = this.gsonBuilder.create();
         if (this.endpoint == null) {
            if (this.uploadURL != null) {
               this.endpoint = this.uploadURL;
            } else {
               this.endpoint = HttpUrl.parse("https://api.segment.io/v1/import/");
            }
         }

         if (this.client == null) {
            this.client = Platform.get().defaultClient();
         }

         if (this.log == null) {
            this.log = Log.NONE;
         }

         if (this.flushIntervalInMillis == 0L) {
            this.flushIntervalInMillis = Platform.get().defaultFlushIntervalInMillis();
         }

         if (this.queueCapacity == 0) {
            this.queueCapacity = Integer.MAX_VALUE;
         }

         if (this.flushQueueSize == 0) {
            this.flushQueueSize = Platform.get().defaultFlushQueueSize();
         }

         if (this.maximumQueueSizeInBytes == 0) {
            this.maximumQueueSizeInBytes = 512000;
         }

         if (this.maximumFlushAttempts == 0) {
            this.maximumFlushAttempts = 3;
         }

         if (this.messageTransformers == null) {
            this.messageTransformers = Collections.emptyList();
         } else {
            this.messageTransformers = Collections.unmodifiableList(this.messageTransformers);
         }

         if (this.messageInterceptors == null) {
            this.messageInterceptors = Collections.emptyList();
         } else {
            this.messageInterceptors = Collections.unmodifiableList(this.messageInterceptors);
         }

         if (this.networkExecutor == null) {
            this.networkExecutor = Platform.get().defaultNetworkExecutor();
         }

         if (this.threadFactory == null) {
            this.threadFactory = Platform.get().defaultThreadFactory();
         }

         if (this.callbacks == null) {
            this.callbacks = Collections.emptyList();
         } else {
            this.callbacks = Collections.unmodifiableList(this.callbacks);
         }

         HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new Logger() {
            public void log(String message) {
               Builder.this.log.print(Log.Level.VERBOSE, "%s", message);
            }
         });
         interceptor.setLevel(Level.BASIC);
         okhttp3.OkHttpClient.Builder builder = this.client
            .newBuilder()
            .addInterceptor(new AnalyticsRequestInterceptor(this.userAgent))
            .addInterceptor(interceptor);
         if (this.forceTlsV1) {
            ConnectionSpec connectionSpec = new okhttp3.ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
               .tlsVersions(new TlsVersion[]{TlsVersion.TLS_1_0, TlsVersion.TLS_1_1, TlsVersion.TLS_1_2, TlsVersion.TLS_1_3})
               .build();
            builder = builder.connectionSpecs(Arrays.asList(connectionSpec));
         }

         this.client = builder.build();
         Retrofit restAdapter = new retrofit2.Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("https://api.segment.io")
            .client(this.client)
            .build();
         SegmentService segmentService = (SegmentService)restAdapter.create(SegmentService.class);
         AnalyticsClient analyticsClient = AnalyticsClient.create(
            this.endpoint,
            segmentService,
            this.queueCapacity,
            this.flushQueueSize,
            this.flushIntervalInMillis,
            this.maximumFlushAttempts,
            this.maximumQueueSizeInBytes,
            this.log,
            this.threadFactory,
            this.networkExecutor,
            this.callbacks,
            this.writeKey,
            gson
         );
         return new Analytics(analyticsClient, this.messageTransformers, this.messageInterceptors, this.log);
      }
   }
}
