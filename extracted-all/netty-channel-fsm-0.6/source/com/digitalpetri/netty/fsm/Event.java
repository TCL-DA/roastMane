package com.digitalpetri.netty.fsm;

import io.netty.channel.Channel;
import java.util.concurrent.CompletableFuture;

public interface Event {
   public static class ChannelIdle implements Event {
      @Override
      public String toString() {
         return this.getClass().getSimpleName();
      }
   }

   public static class ChannelInactive implements Event {
      @Override
      public String toString() {
         return this.getClass().getSimpleName();
      }
   }

   public static class Connect implements Event {
      final CompletableFuture<Channel> channelFuture = new CompletableFuture<>();

      @Override
      public String toString() {
         return this.getClass().getSimpleName();
      }
   }

   public static class ConnectFailure implements Event {
      final Throwable failure;

      public ConnectFailure(Throwable failure) {
         this.failure = failure;
      }

      @Override
      public String toString() {
         return this.getClass().getSimpleName();
      }
   }

   public static class ConnectSuccess implements Event {
      final Channel channel;

      public ConnectSuccess(Channel channel) {
         this.channel = channel;
      }

      @Override
      public String toString() {
         return this.getClass().getSimpleName();
      }
   }

   public static class Disconnect implements Event {
      final CompletableFuture<Void> disconnectFuture = new CompletableFuture<>();

      @Override
      public String toString() {
         return this.getClass().getSimpleName();
      }
   }

   public static class DisconnectSuccess implements Event {
      @Override
      public String toString() {
         return this.getClass().getSimpleName();
      }
   }

   public static class GetChannel implements Event {
      final CompletableFuture<Channel> channelFuture = new CompletableFuture<>();
      final boolean waitForReconnect;

      GetChannel() {
         this(true);
      }

      GetChannel(boolean waitForReconnect) {
         this.waitForReconnect = waitForReconnect;
      }

      @Override
      public String toString() {
         return this.getClass().getSimpleName();
      }
   }

   public static class KeepAliveFailure implements Event {
      final Throwable failure;

      KeepAliveFailure(Throwable failure) {
         this.failure = failure;
      }

      @Override
      public String toString() {
         return this.getClass().getSimpleName();
      }
   }

   public static class ReconnectDelayElapsed implements Event {
      @Override
      public String toString() {
         return this.getClass().getSimpleName();
      }
   }
}
