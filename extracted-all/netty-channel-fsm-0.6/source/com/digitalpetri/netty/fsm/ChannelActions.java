package com.digitalpetri.netty.fsm;

import com.digitalpetri.strictmachine.FsmContext;
import io.netty.channel.Channel;
import java.util.concurrent.CompletableFuture;

public interface ChannelActions {
   CompletableFuture<Channel> connect(FsmContext<State, Event> var1);

   CompletableFuture<Void> disconnect(FsmContext<State, Event> var1, Channel var2);

   default CompletableFuture<Void> keepAlive(FsmContext<State, Event> ctx, Channel channel) {
      return CompletableFuture.completedFuture(null);
   }
}
