package com.digitalpetri.netty.fsm;

import java.util.Map;
import java.util.concurrent.Executor;

public interface ChannelFsmConfig {
   boolean isLazy();

   boolean isPersistent();

   int getMaxIdleSeconds();

   int getMaxReconnectDelaySeconds();

   ChannelActions getChannelActions();

   Executor getExecutor();

   Scheduler getScheduler();

   String getLoggerName();

   Map<String, String> getLoggingContext();

   static ChannelFsmConfigBuilder newBuilder() {
      return new ChannelFsmConfigBuilder();
   }
}
