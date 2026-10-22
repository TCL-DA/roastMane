package com.digitalpetri.netty.fsm;

import com.digitalpetri.strictmachine.Fsm;
import com.digitalpetri.strictmachine.FsmContext;
import com.digitalpetri.strictmachine.FsmContext.Key;
import io.netty.channel.Channel;
import java.util.concurrent.CompletableFuture;

public class ChannelFsm {
   private final Fsm<State, Event> fsm;
   static final Key<ChannelFsm.ConnectFuture> KEY_CF = new Key("connectFuture", ChannelFsm.ConnectFuture.class);
   static final Key<ChannelFsm.DisconnectFuture> KEY_DF = new Key("disconnectFuture", ChannelFsm.DisconnectFuture.class);
   static final Key<Long> KEY_RD = new Key("reconnectDelay", Long.class);
   static final Key<Scheduler.Cancellable> KEY_RDF = new Key("reconnectDelayCancellable", Scheduler.Cancellable.class);

   ChannelFsm(Fsm<State, Event> fsm) {
      this.fsm = fsm;
   }

   Fsm<State, Event> getFsm() {
      return this.fsm;
   }

   public CompletableFuture<Channel> connect() {
      Event.Connect connect = new Event.Connect();
      this.fsm.fireEvent(connect);
      return connect.channelFuture;
   }

   public CompletableFuture<Void> disconnect() {
      Event.Disconnect disconnect = new Event.Disconnect();
      this.fsm.fireEvent(disconnect);
      return disconnect.disconnectFuture;
   }

   public CompletableFuture<Channel> getChannel() {
      return this.getChannel(true);
   }

   public CompletableFuture<Channel> getChannel(boolean waitForReconnect) {
      CompletableFuture<Channel> future = (CompletableFuture<Channel>)this.fsm.getFromContext(ctx -> {
         State state = (State)ctx.currentState();
         if (state == State.Connected) {
            ChannelFsm.ConnectFuture cf = (ChannelFsm.ConnectFuture)KEY_CF.get(ctx);

            assert cf != null;

            return cf.future;
         } else {
            return null;
         }
      });
      if (future != null) {
         return future;
      } else {
         Event.GetChannel getChannel = new Event.GetChannel(waitForReconnect);
         this.fsm.fireEvent(getChannel);
         return getChannel.channelFuture;
      }
   }

   public State getState() {
      return (State)this.fsm.getFromContext(FsmContext::currentState);
   }

   static class ConnectFuture {
      final CompletableFuture<Channel> future = new CompletableFuture<>();
   }

   static class DisconnectFuture {
      final CompletableFuture<Void> future = new CompletableFuture<>();
   }
}
