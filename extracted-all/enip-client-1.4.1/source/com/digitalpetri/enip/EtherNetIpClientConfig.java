package com.digitalpetri.enip;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.util.HashedWheelTimer;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public class EtherNetIpClientConfig {
   private final String hostname;
   private final int port;
   private final int vendorId;
   private final int serialNumber;
   private final Duration timeout;
   private final Duration maxIdle;
   private final int maxReconnectDelaySeconds;
   private final boolean lazy;
   private final boolean persistent;
   private final boolean waitForReconnect;
   private final ExecutorService executor;
   private final ScheduledExecutorService scheduledExecutor;
   private final EventLoopGroup eventLoop;
   private final HashedWheelTimer wheelTimer;
   private final Consumer<Bootstrap> bootstrapConsumer;
   private final Map<String, String> loggingContext;

   public EtherNetIpClientConfig(
      String hostname,
      int port,
      int vendorId,
      int serialNumber,
      Duration timeout,
      Duration maxIdle,
      int maxReconnectDelaySeconds,
      boolean lazy,
      boolean persistent,
      boolean waitForReconnect,
      ExecutorService executor,
      ScheduledExecutorService scheduledExecutor,
      EventLoopGroup eventLoop,
      HashedWheelTimer wheelTimer,
      Consumer<Bootstrap> bootstrapConsumer,
      Map<String, String> loggingContext
   ) {
      this.hostname = hostname;
      this.port = port;
      this.vendorId = vendorId;
      this.serialNumber = serialNumber;
      this.timeout = timeout;
      this.maxIdle = maxIdle;
      this.maxReconnectDelaySeconds = maxReconnectDelaySeconds;
      this.lazy = lazy;
      this.persistent = persistent;
      this.waitForReconnect = waitForReconnect;
      this.executor = executor;
      this.scheduledExecutor = scheduledExecutor;
      this.eventLoop = eventLoop;
      this.wheelTimer = wheelTimer;
      this.bootstrapConsumer = bootstrapConsumer;
      this.loggingContext = loggingContext;
   }

   public String getHostname() {
      return this.hostname;
   }

   public int getPort() {
      return this.port;
   }

   public int getVendorId() {
      return this.vendorId;
   }

   public int getSerialNumber() {
      return this.serialNumber;
   }

   public Duration getTimeout() {
      return this.timeout;
   }

   public Duration getMaxIdle() {
      return this.maxIdle;
   }

   public int getMaxReconnectDelaySeconds() {
      return this.maxReconnectDelaySeconds;
   }

   public boolean isLazy() {
      return this.lazy;
   }

   public boolean isPersistent() {
      return this.persistent;
   }

   public boolean getWaitForReconnect() {
      return this.waitForReconnect;
   }

   public ExecutorService getExecutor() {
      return this.executor;
   }

   public ScheduledExecutorService getScheduledExecutor() {
      return this.scheduledExecutor;
   }

   public EventLoopGroup getEventLoop() {
      return this.eventLoop;
   }

   public HashedWheelTimer getWheelTimer() {
      return this.wheelTimer;
   }

   public Consumer<Bootstrap> getBootstrapConsumer() {
      return this.bootstrapConsumer;
   }

   public Map<String, String> getLoggingContext() {
      return this.loggingContext;
   }

   public static EtherNetIpClientConfig.Builder builder(String hostname) {
      return new EtherNetIpClientConfig.Builder().setHostname(hostname);
   }

   public static class Builder {
      private String hostname;
      private int port = 44818;
      private int vendorId = 0;
      private int serialNumber = 0;
      private Duration timeout = Duration.ofSeconds(5L);
      private Duration maxIdle = Duration.ofSeconds(15L);
      private int maxReconnectDelaySeconds = 16;
      private boolean lazy = true;
      private boolean persistent = true;
      private boolean waitForReconnect = true;
      private ExecutorService executor;
      private ScheduledExecutorService scheduledExecutor;
      private EventLoopGroup eventLoop;
      private HashedWheelTimer wheelTimer;
      private Consumer<Bootstrap> bootstrapConsumer = b -> {
      };
      private Map<String, String> loggingContext = new ConcurrentHashMap<>();

      public EtherNetIpClientConfig.Builder setHostname(String hostname) {
         this.hostname = hostname;
         return this;
      }

      public EtherNetIpClientConfig.Builder setPort(int port) {
         this.port = port;
         return this;
      }

      public EtherNetIpClientConfig.Builder setVendorId(int vendorId) {
         this.vendorId = vendorId;
         return this;
      }

      public EtherNetIpClientConfig.Builder setSerialNumber(int serialNumber) {
         this.serialNumber = serialNumber;
         return this;
      }

      public EtherNetIpClientConfig.Builder setTimeout(Duration timeout) {
         this.timeout = timeout;
         return this;
      }

      public EtherNetIpClientConfig.Builder setMaxIdle(Duration maxIdle) {
         this.maxIdle = maxIdle;
         return this;
      }

      public EtherNetIpClientConfig.Builder setMaxReconnectDelaySeconds(int maxReconnectDelaySeconds) {
         this.maxReconnectDelaySeconds = maxReconnectDelaySeconds;
         return this;
      }

      public EtherNetIpClientConfig.Builder setLazy(boolean lazy) {
         this.lazy = lazy;
         return this;
      }

      public EtherNetIpClientConfig.Builder setPersistent(boolean persistent) {
         this.persistent = persistent;
         return this;
      }

      public EtherNetIpClientConfig.Builder setWaitForReconnect(boolean waitForReconnect) {
         this.waitForReconnect = waitForReconnect;
         return this;
      }

      public EtherNetIpClientConfig.Builder setExecutor(ExecutorService executor) {
         this.executor = executor;
         return this;
      }

      public EtherNetIpClientConfig.Builder setScheduledExecutor(ScheduledExecutorService scheduledExecutor) {
         this.scheduledExecutor = scheduledExecutor;
         return this;
      }

      public EtherNetIpClientConfig.Builder setEventLoop(EventLoopGroup eventLoop) {
         this.eventLoop = eventLoop;
         return this;
      }

      public EtherNetIpClientConfig.Builder setWheelTimer(HashedWheelTimer wheelTimer) {
         this.wheelTimer = wheelTimer;
         return this;
      }

      public EtherNetIpClientConfig.Builder setBootstrapConsumer(Consumer<Bootstrap> bootstrapConsumer) {
         this.bootstrapConsumer = bootstrapConsumer;
         return this;
      }

      public EtherNetIpClientConfig.Builder setLoggingContext(Map<String, String> loggingContext) {
         this.loggingContext = new ConcurrentHashMap<>(loggingContext);
         return this;
      }

      public EtherNetIpClientConfig build() {
         if (this.executor == null) {
            this.executor = EtherNetIpShared.sharedExecutorService();
         }

         if (this.scheduledExecutor == null) {
            this.scheduledExecutor = EtherNetIpShared.sharedScheduledExecutor();
         }

         if (this.eventLoop == null) {
            this.eventLoop = EtherNetIpShared.sharedEventLoop();
         }

         if (this.wheelTimer == null) {
            this.wheelTimer = EtherNetIpShared.sharedWheelTimer();
         }

         return new EtherNetIpClientConfig(
            this.hostname,
            this.port,
            this.vendorId,
            this.serialNumber,
            this.timeout,
            this.maxIdle,
            this.maxReconnectDelaySeconds,
            this.lazy,
            this.persistent,
            this.waitForReconnect,
            this.executor,
            this.scheduledExecutor,
            this.eventLoop,
            this.wheelTimer,
            this.bootstrapConsumer,
            this.loggingContext
         );
      }
   }
}
