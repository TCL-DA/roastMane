package com.digitalpetri.netty.fsm;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ChannelFsmConfigBuilder {
   static int DEFAULT_MAX_RECONNECT_DELAY_SECONDS = 32;
   private boolean lazy = false;
   private boolean persistent = true;
   private int maxIdleSeconds = 15;
   private int maxReconnectDelaySeconds = DEFAULT_MAX_RECONNECT_DELAY_SECONDS;
   private ChannelActions channelActions;
   private Executor executor;
   private Scheduler scheduler;
   private String loggerName;
   private Map<String, String> loggingContext = Collections.emptyMap();

   public ChannelFsmConfigBuilder setLazy(boolean lazy) {
      this.lazy = lazy;
      return this;
   }

   public ChannelFsmConfigBuilder setPersistent(boolean persistent) {
      this.persistent = persistent;
      return this;
   }

   public ChannelFsmConfigBuilder setMaxIdleSeconds(int maxIdleSeconds) {
      this.maxIdleSeconds = maxIdleSeconds;
      return this;
   }

   public ChannelFsmConfigBuilder setMaxReconnectDelaySeconds(int maxReconnectDelaySeconds) {
      this.maxReconnectDelaySeconds = maxReconnectDelaySeconds;
      return this;
   }

   public ChannelFsmConfigBuilder setChannelActions(ChannelActions channelActions) {
      this.channelActions = channelActions;
      return this;
   }

   public ChannelFsmConfigBuilder setExecutor(Executor executor) {
      this.executor = executor;
      return this;
   }

   public ChannelFsmConfigBuilder setScheduler(Scheduler scheduler) {
      this.scheduler = scheduler;
      return this;
   }

   public ChannelFsmConfigBuilder setScheduler(ScheduledExecutorService scheduledExecutor) {
      this.scheduler = Scheduler.fromScheduledExecutor(scheduledExecutor);
      return this;
   }

   public ChannelFsmConfigBuilder setLoggerName(String loggerName) {
      this.loggerName = loggerName;
      return this;
   }

   public ChannelFsmConfigBuilder setLoggingContext(Map<String, String> loggingContext) {
      this.loggingContext = new ConcurrentHashMap<>(loggingContext);
      return this;
   }

   public ChannelFsmConfig build() {
      if (this.channelActions == null) {
         throw new IllegalArgumentException("channelActions must be non-null");
      } else {
         if (this.maxReconnectDelaySeconds < 1) {
            this.maxReconnectDelaySeconds = DEFAULT_MAX_RECONNECT_DELAY_SECONDS;
         }

         if (this.executor == null) {
            this.executor = ChannelFsmConfigBuilder.SharedExecutor.INSTANCE;
         }

         if (this.scheduler == null) {
            this.scheduler = ChannelFsmConfigBuilder.SharedScheduler.INSTANCE;
         }

         if (this.loggerName == null) {
            this.loggerName = ChannelFsm.class.getName();
         }

         return new ChannelFsmConfigBuilder.ChannelFsmConfigImpl(
            this.lazy,
            this.persistent,
            this.maxIdleSeconds,
            this.maxReconnectDelaySeconds,
            this.channelActions,
            this.executor,
            this.scheduler,
            this.loggerName,
            this.loggingContext
         );
      }
   }

   private static class ChannelFsmConfigImpl implements ChannelFsmConfig {
      private final boolean lazy;
      private final boolean persistent;
      private final int maxIdleSeconds;
      private final int maxReconnectDelaySeconds;
      private final ChannelActions channelActions;
      private final Executor executor;
      private final Scheduler scheduler;
      private final String loggerName;
      private final Map<String, String> loggingContext;

      ChannelFsmConfigImpl(
         boolean lazy,
         boolean persistent,
         int maxIdleSeconds,
         int maxReconnectDelaySeconds,
         ChannelActions channelActions,
         Executor executor,
         Scheduler scheduler,
         String loggerName,
         Map<String, String> loggingContext
      ) {
         this.lazy = lazy;
         this.persistent = persistent;
         this.maxIdleSeconds = maxIdleSeconds;
         this.maxReconnectDelaySeconds = maxReconnectDelaySeconds;
         this.channelActions = channelActions;
         this.executor = executor;
         this.scheduler = scheduler;
         this.loggerName = loggerName;
         this.loggingContext = loggingContext;
      }

      @Override
      public boolean isLazy() {
         return this.lazy;
      }

      @Override
      public boolean isPersistent() {
         return this.persistent;
      }

      @Override
      public int getMaxIdleSeconds() {
         return this.maxIdleSeconds;
      }

      @Override
      public int getMaxReconnectDelaySeconds() {
         return this.maxReconnectDelaySeconds;
      }

      @Override
      public ChannelActions getChannelActions() {
         return this.channelActions;
      }

      @Override
      public Executor getExecutor() {
         return this.executor;
      }

      @Override
      public Scheduler getScheduler() {
         return this.scheduler;
      }

      @Override
      public String getLoggerName() {
         return this.loggerName;
      }

      @Override
      public Map<String, String> getLoggingContext() {
         return this.loggingContext;
      }
   }

   private static class SharedExecutor {
      private static final ExecutorService INSTANCE = Executors.newSingleThreadExecutor();
   }

   private static class SharedScheduler {
      private static final Scheduler INSTANCE = Scheduler.fromScheduledExecutor(Executors.newSingleThreadScheduledExecutor());
   }
}
