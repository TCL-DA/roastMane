package com.segment.analytics.internal;

import com.google.gson.Gson;
import com.segment.analytics.Callback;
import com.segment.analytics.Log;
import com.segment.analytics.http.SegmentService;
import com.segment.analytics.http.UploadResponse;
import com.segment.analytics.messages.Batch;
import com.segment.analytics.messages.Message;
import com.segment.backo.Backo;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Response;

public class AnalyticsClient {
   private static final Map<String, ?> CONTEXT;
   private static final int BATCH_MAX_SIZE = 512000;
   private static final int MSG_MAX_SIZE = 32768;
   private static final Charset ENCODING = StandardCharsets.UTF_8;
   private Gson gsonInstance;
   private static final String instanceId = UUID.randomUUID().toString();
   private final BlockingQueue<Message> messageQueue;
   private final HttpUrl uploadUrl;
   private final SegmentService service;
   private final int size;
   private final int maximumRetries;
   private final int maximumQueueByteSize;
   private int currentQueueSizeInBytes;
   private final Log log;
   private final List<Callback> callbacks;
   private final ExecutorService networkExecutor;
   private final ExecutorService looperExecutor;
   private final ScheduledExecutorService flushScheduler;
   private final AtomicBoolean isShutDown;
   private final String writeKey;

   public static AnalyticsClient create(
      HttpUrl uploadUrl,
      SegmentService segmentService,
      int queueCapacity,
      int flushQueueSize,
      long flushIntervalInMillis,
      int maximumRetries,
      int maximumQueueSizeInBytes,
      Log log,
      ThreadFactory threadFactory,
      ExecutorService networkExecutor,
      List<Callback> callbacks,
      String writeKey,
      Gson gsonInstance
   ) {
      return new AnalyticsClient(
         new LinkedBlockingQueue<>(queueCapacity),
         uploadUrl,
         segmentService,
         flushQueueSize,
         flushIntervalInMillis,
         maximumRetries,
         maximumQueueSizeInBytes,
         log,
         threadFactory,
         networkExecutor,
         callbacks,
         new AtomicBoolean(false),
         writeKey,
         gsonInstance
      );
   }

   public AnalyticsClient(
      BlockingQueue<Message> messageQueue,
      HttpUrl uploadUrl,
      SegmentService service,
      int maxQueueSize,
      long flushIntervalInMillis,
      int maximumRetries,
      int maximumQueueSizeInBytes,
      Log log,
      ThreadFactory threadFactory,
      ExecutorService networkExecutor,
      List<Callback> callbacks,
      AtomicBoolean isShutDown,
      String writeKey,
      Gson gsonInstance
   ) {
      this.messageQueue = messageQueue;
      this.uploadUrl = uploadUrl;
      this.service = service;
      this.size = maxQueueSize;
      this.maximumRetries = maximumRetries;
      this.maximumQueueByteSize = maximumQueueSizeInBytes;
      this.log = log;
      this.callbacks = callbacks;
      this.looperExecutor = Executors.newSingleThreadExecutor(threadFactory);
      this.networkExecutor = networkExecutor;
      this.isShutDown = isShutDown;
      this.writeKey = writeKey;
      this.gsonInstance = gsonInstance;
      this.currentQueueSizeInBytes = 0;
      if (!isShutDown.get()) {
         this.looperExecutor.submit(new AnalyticsClient.Looper());
      }

      this.flushScheduler = Executors.newScheduledThreadPool(1, threadFactory);
      this.flushScheduler.scheduleAtFixedRate(new Runnable() {
         @Override
         public void run() {
            AnalyticsClient.this.flush();
         }
      }, flushIntervalInMillis, flushIntervalInMillis, TimeUnit.MILLISECONDS);
   }

   public int messageSizeInBytes(Message message) {
      String stringifiedMessage = this.gsonInstance.toJson(message);
      return stringifiedMessage.getBytes(ENCODING).length;
   }

   private Boolean isBackPressuredAfterSize(int incomingSize) {
      int POISON_BYTE_SIZE = this.messageSizeInBytes(FlushMessage.POISON);
      int sizeAfterAdd = this.currentQueueSizeInBytes + incomingSize + POISON_BYTE_SIZE;
      return (double)sizeAfterAdd >= (double)Math.min(this.maximumQueueByteSize, 512000) * 0.9;
   }

   public boolean offer(Message message) {
      return this.messageQueue.offer(message);
   }

   public void enqueue(Message message) {
      if (message != StopMessage.STOP && this.isShutDown.get()) {
         this.log.print(Log.Level.ERROR, "Attempt to enqueue a message when shutdown has been called %s.", message);
      } else {
         try {
            if (message != StopMessage.STOP && message != FlushMessage.POISON) {
               int messageByteSize = this.messageSizeInBytes(message);
               if (messageByteSize > 32768) {
                  this.log.print(Log.Level.ERROR, "Message was above individual limit. MessageId: %s", message.messageId());
                  throw new IllegalArgumentException("Message was above individual limit. MessageId: " + message.messageId());
               }

               if (this.isBackPressuredAfterSize(messageByteSize)) {
                  this.currentQueueSizeInBytes = messageByteSize;
                  this.messageQueue.put(FlushMessage.POISON);
                  this.messageQueue.put(message);
                  this.log.print(Log.Level.VERBOSE, "Maximum storage size has been hit Flushing...");
               } else {
                  this.messageQueue.put(message);
                  this.currentQueueSizeInBytes += messageByteSize;
               }
            } else {
               this.messageQueue.put(message);
            }
         } catch (InterruptedException var3) {
            this.log.print(Log.Level.ERROR, var3, "Interrupted while adding message %s.", message);
            Thread.currentThread().interrupt();
         }
      }
   }

   public void flush() {
      if (!this.isShutDown.get()) {
         this.enqueue(FlushMessage.POISON);
      }
   }

   public void shutdown() {
      if (this.isShutDown.compareAndSet(false, true)) {
         long start = System.currentTimeMillis();
         this.enqueue(StopMessage.STOP);
         this.flushScheduler.shutdownNow();
         this.shutdownAndWait(this.looperExecutor, "looper");
         this.shutdownAndWait(this.networkExecutor, "network");
         this.log.print(Log.Level.VERBOSE, "Analytics client shut down in %s ms", System.currentTimeMillis() - start);
      }
   }

   public void shutdownAndWait(ExecutorService executor, String name) {
      try {
         executor.shutdown();
         boolean executorTerminated = executor.awaitTermination(1L, TimeUnit.SECONDS);
         this.log.print(Log.Level.VERBOSE, "%s executor %s.", name, executorTerminated ? "terminated normally" : "timed out");
      } catch (InterruptedException var4) {
         this.log.print(Log.Level.ERROR, var4, "Interrupted while stopping %s executor.", name);
         Thread.currentThread().interrupt();
      }
   }

   static {
      Map<String, String> library = new LinkedHashMap<>();
      library.put("name", "analytics-java");
      library.put("version", AnalyticsVersion.get());
      Map<String, Object> context = new LinkedHashMap<>();
      context.put("library", Collections.unmodifiableMap(library));
      context.put("instanceId", instanceId);
      CONTEXT = Collections.unmodifiableMap(context);
   }

   static class BatchUploadTask implements Runnable {
      private static final Backo BACKO = Backo.builder().base(TimeUnit.SECONDS, 15L).cap(TimeUnit.HOURS, 1L).jitter(1).build();
      private final AnalyticsClient client;
      private final Backo backo;
      final Batch batch;
      private final int maxRetries;

      static AnalyticsClient.BatchUploadTask create(AnalyticsClient client, Batch batch, int maxRetries) {
         return new AnalyticsClient.BatchUploadTask(client, BACKO, batch, maxRetries);
      }

      BatchUploadTask(AnalyticsClient client, Backo backo, Batch batch, int maxRetries) {
         this.client = client;
         this.batch = batch;
         this.backo = backo;
         this.maxRetries = maxRetries;
      }

      private void notifyCallbacksWithException(Batch batch, Exception exception) {
         for (Message message : batch.batch()) {
            for (Callback callback : this.client.callbacks) {
               callback.failure(message, exception);
            }
         }
      }

      boolean upload() {
         this.client.log.print(Log.Level.VERBOSE, "Uploading batch %s.", this.batch.sequence());

         try {
            Call<UploadResponse> call = this.client.service.upload(this.client.uploadUrl, this.batch);
            Response<UploadResponse> response = call.execute();
            if (!response.isSuccessful()) {
               int status = response.code();
               if (is5xx(status)) {
                  this.client.log.print(Log.Level.DEBUG, "Could not upload batch %s due to server error. Retrying.", this.batch.sequence());
                  return true;
               } else if (status == 429) {
                  this.client.log.print(Log.Level.DEBUG, "Could not upload batch %s due to rate limiting. Retrying.", this.batch.sequence());
                  return true;
               } else {
                  this.client.log.print(Log.Level.DEBUG, "Could not upload batch %s. Giving up.", this.batch.sequence());
                  this.notifyCallbacksWithException(this.batch, new IOException(response.errorBody().string()));
                  return false;
               }
            } else {
               this.client.log.print(Log.Level.VERBOSE, "Uploaded batch %s.", this.batch.sequence());

               for (Message message : this.batch.batch()) {
                  for (Callback callback : this.client.callbacks) {
                     callback.success(message);
                  }
               }

               return false;
            }
         } catch (IOException var7) {
            this.client.log.print(Log.Level.DEBUG, var7, "Could not upload batch %s. Retrying.", this.batch.sequence());
            return true;
         } catch (Exception var8) {
            this.client.log.print(Log.Level.DEBUG, "Could not upload batch %s. Giving up.", this.batch.sequence());
            this.notifyCallbacksWithException(this.batch, var8);
            return false;
         }
      }

      @Override
      public void run() {
         int attempt;
         for (attempt = 0; attempt <= this.maxRetries; attempt++) {
            boolean retry = this.upload();
            if (!retry) {
               return;
            }

            try {
               this.backo.sleep(attempt);
            } catch (InterruptedException var4) {
               this.client.log.print(Log.Level.DEBUG, "Thread interrupted while backing off for batch %s.", this.batch.sequence());
               return;
            }
         }

         this.client.log.print(Log.Level.ERROR, "Could not upload batch %s. Retries exhausted.", this.batch.sequence());
         this.notifyCallbacksWithException(this.batch, new IOException(Integer.toString(attempt) + " retries exhausted"));
      }

      private static boolean is5xx(int status) {
         return status >= 500 && status < 600;
      }
   }

   public static class BatchUtility {
      private static int getBatchDefaultSize(int contextSize, int currentMessageNumber) {
         int metadataExtraCharsSize = 1143;
         int commaNumber = currentMessageNumber - 1;
         return contextSize + metadataExtraCharsSize + commaNumber + String.valueOf(Integer.MAX_VALUE).length();
      }
   }

   class Looper implements Runnable {
      private boolean stop = false;

      public Looper() {
      }

      @Override
      public void run() {
         LinkedList<Message> messages = new LinkedList<>();
         AtomicInteger currentBatchSize = new AtomicInteger();
         boolean batchSizeLimitReached = false;
         int contextSize = AnalyticsClient.this.gsonInstance.toJson(AnalyticsClient.CONTEXT).getBytes(AnalyticsClient.ENCODING).length;

         try {
            while (!this.stop) {
               Message message = AnalyticsClient.this.messageQueue.take();
               if (message == StopMessage.STOP) {
                  AnalyticsClient.this.log.print(Log.Level.VERBOSE, "Stopping the Looper");
                  this.stop = true;
               } else if (message == FlushMessage.POISON) {
                  if (!messages.isEmpty()) {
                     AnalyticsClient.this.log.print(Log.Level.VERBOSE, "Flushing messages.");
                  }
               } else {
                  int defaultBatchSize = AnalyticsClient.BatchUtility.getBatchDefaultSize(contextSize, messages.size() + 1);
                  int msgSize = AnalyticsClient.this.messageSizeInBytes(message);
                  if (currentBatchSize.get() + msgSize + defaultBatchSize <= 512000) {
                     messages.add(message);
                     currentBatchSize.addAndGet(msgSize);
                  } else {
                     batchSizeLimitReached = true;
                  }
               }

               Boolean isBlockingSignal = message == FlushMessage.POISON || message == StopMessage.STOP;
               Boolean isOverflow = messages.size() >= AnalyticsClient.this.size;
               if (!messages.isEmpty() && (isOverflow || isBlockingSignal || batchSizeLimitReached)) {
                  Batch batch = Batch.create(AnalyticsClient.CONTEXT, new ArrayList<>(messages), AnalyticsClient.this.writeKey);
                  AnalyticsClient.this.log.print(Log.Level.VERBOSE, "Batching %s message(s) into batch %s.", batch.batch().size(), batch.sequence());
                  AnalyticsClient.this.networkExecutor
                     .submit(AnalyticsClient.BatchUploadTask.create(AnalyticsClient.this, batch, AnalyticsClient.this.maximumRetries));
                  currentBatchSize.set(0);
                  messages.clear();
                  if (batchSizeLimitReached) {
                     messages.add(message);
                  }

                  batchSizeLimitReached = false;
               }
            }
         } catch (InterruptedException var9) {
            AnalyticsClient.this.log.print(Log.Level.DEBUG, "Looper interrupted while polling for messages.");
            Thread.currentThread().interrupt();
         }

         AnalyticsClient.this.log.print(Log.Level.VERBOSE, "Looper stopped");
      }
   }
}
