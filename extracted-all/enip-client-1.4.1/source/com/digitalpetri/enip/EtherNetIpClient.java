package com.digitalpetri.enip;

import com.digitalpetri.enip.codec.EnipCodec;
import com.digitalpetri.enip.commands.Command;
import com.digitalpetri.enip.commands.CommandCode;
import com.digitalpetri.enip.commands.ListIdentity;
import com.digitalpetri.enip.commands.RegisterSession;
import com.digitalpetri.enip.commands.SendRRData;
import com.digitalpetri.enip.commands.SendUnitData;
import com.digitalpetri.enip.commands.UnRegisterSession;
import com.digitalpetri.enip.cpf.ConnectedDataItemResponse;
import com.digitalpetri.enip.cpf.CpfPacket;
import com.digitalpetri.enip.cpf.UnconnectedDataItemResponse;
import com.digitalpetri.enip.util.FutureUtils;
import com.digitalpetri.enip.util.IntUtil;
import com.digitalpetri.netty.fsm.ChannelActions;
import com.digitalpetri.netty.fsm.ChannelFsm;
import com.digitalpetri.netty.fsm.ChannelFsmConfig;
import com.digitalpetri.netty.fsm.ChannelFsmFactory;
import com.digitalpetri.netty.fsm.Event;
import com.digitalpetri.netty.fsm.State;
import com.digitalpetri.strictmachine.FsmContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.Timeout;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class EtherNetIpClient {
   private final Logger logger = LoggerFactory.getLogger(this.getClass());
   private final ExecutorService executor;
   private final Map<Long, EtherNetIpClient.PendingRequest<? extends Command>> pendingRequests = new ConcurrentHashMap<>();
   private final AtomicLong senderContext = new AtomicLong(0L);
   private volatile long sessionHandle;
   private final ChannelFsm channelFsm;
   private final EtherNetIpClientConfig config;

   public EtherNetIpClient(EtherNetIpClientConfig config) {
      this.config = config;
      this.executor = config.getExecutor();
      ChannelFsmConfig fsmConfig = ChannelFsmConfig.newBuilder()
         .setLazy(config.isLazy())
         .setPersistent(config.isPersistent())
         .setMaxIdleSeconds(IntUtil.saturatedCast(config.getMaxIdle().getSeconds()))
         .setMaxReconnectDelaySeconds(config.getMaxReconnectDelaySeconds())
         .setChannelActions(new EtherNetIpClient.EnipChannelActions())
         .setExecutor(config.getExecutor())
         .setScheduler(config.getScheduledExecutor())
         .setLoggerName("com.digitalpetri.enip.ChannelFsm")
         .setLoggingContext(config.getLoggingContext())
         .build();
      this.channelFsm = ChannelFsmFactory.newChannelFsm(fsmConfig);
   }

   public CompletableFuture<EtherNetIpClient> connect() {
      return FutureUtils.complete(new CompletableFuture<>()).with(this.channelFsm.connect().thenApply(c -> this));
   }

   public CompletableFuture<EtherNetIpClient> disconnect() {
      return FutureUtils.complete(new CompletableFuture<>()).with(this.channelFsm.disconnect().thenApply(c -> this));
   }

   public String getState() {
      return this.channelFsm.getState().toString();
   }

   public CompletableFuture<ListIdentity> listIdentity() {
      return this.sendCommand(new ListIdentity());
   }

   public CompletableFuture<SendRRData> sendRRData(SendRRData command) {
      return this.sendCommand(command);
   }

   public CompletableFuture<Void> sendUnitData(SendUnitData command) {
      CompletableFuture<Void> future = new CompletableFuture<>();
      this.channelFsm.getChannel(this.config.getWaitForReconnect()).whenComplete((ch, ex) -> {
         if (ch != null) {
            EnipPacket packet = new EnipPacket(command.getCommandCode(), this.sessionHandle, EnipStatus.EIP_SUCCESS, 0L, command);
            ch.writeAndFlush(packet).addListener(f -> {
               if (f.isSuccess()) {
                  future.complete(null);
               } else {
                  future.completeExceptionally(f.cause());
               }
            });
         } else {
            future.completeExceptionally(ex);
         }
      });
      return future;
   }

   public EtherNetIpClientConfig getConfig() {
      return this.config;
   }

   public ExecutorService getExecutor() {
      return this.executor;
   }

   public <T extends Command> CompletableFuture<T> sendCommand(Command command) {
      CompletableFuture<T> future = new CompletableFuture<>();
      this.channelFsm.getChannel(this.config.getWaitForReconnect()).whenComplete((ch, ex) -> {
         if (ch != null) {
            this.writeCommand(ch, command, future);
         } else {
            future.completeExceptionally(ex);
         }
      });
      return future;
   }

   public <T extends Command> void writeCommand(Channel channel, Command command, CompletableFuture<T> future) {
      EnipPacket packet = new EnipPacket(command.getCommandCode(), this.sessionHandle, EnipStatus.EIP_SUCCESS, this.senderContext.getAndIncrement(), command);
      Timeout timeout = this.config
         .getWheelTimer()
         .newTimeout(
            tt -> {
               if (!tt.isCancelled()) {
                  EtherNetIpClient.PendingRequest<?> p = this.pendingRequests.remove(packet.getSenderContext());
                  if (p != null) {
                     String message = String.format(
                        "senderContext=%s timed out waiting %sms for response", packet.getSenderContext(), this.config.getTimeout().toMillis()
                     );
                     p.promise.completeExceptionally(new TimeoutException(message));
                  }
               }
            },
            this.config.getTimeout().toMillis(),
            TimeUnit.MILLISECONDS
         );
      this.pendingRequests.put(packet.getSenderContext(), new EtherNetIpClient.PendingRequest<>(future, timeout));
      channel.writeAndFlush(packet).addListener(f -> {
         if (!f.isSuccess()) {
            EtherNetIpClient.PendingRequest pending = this.pendingRequests.remove(packet.getSenderContext());
            if (pending != null) {
               pending.timeout.cancel();
               pending.promise.completeExceptionally(f.cause());
            }
         }
      });
   }

   private void onChannelRead(EnipPacket packet) {
      CommandCode commandCode = packet.getCommandCode();
      EnipStatus status = packet.getStatus();
      if (commandCode == CommandCode.SendUnitData) {
         if (status == EnipStatus.EIP_SUCCESS) {
            this.onUnitDataReceived((SendUnitData)packet.getCommand());
         } else {
            this.config.getLoggingContext().forEach(MDC::put);

            try {
               this.logger.warn("Received SendUnitData command with status: {}", status);
            } finally {
               this.config.getLoggingContext().keySet().forEach(MDC::remove);
            }
         }
      } else {
         if (commandCode == CommandCode.RegisterSession) {
            if (status == EnipStatus.EIP_SUCCESS) {
               this.sessionHandle = packet.getSessionHandle();
            } else {
               this.sessionHandle = 0L;
            }
         }

         EtherNetIpClient.PendingRequest<?> pending = this.pendingRequests.remove(packet.getSenderContext());
         if (pending != null) {
            pending.timeout.cancel();
            if (status == EnipStatus.EIP_SUCCESS) {
               pending.promise.complete(packet.getCommand());
            } else {
               pending.promise.completeExceptionally(new Exception("EtherNet/IP status: " + status));
            }
         } else {
            this.config.getLoggingContext().forEach(MDC::put);

            try {
               this.logger.debug("Received response for unknown context: {}", packet.getSenderContext());
            } finally {
               this.config.getLoggingContext().keySet().forEach(MDC::remove);
            }

            if (packet.getCommand() instanceof SendRRData) {
               CpfPacket cpfPacket = ((SendRRData)packet.getCommand()).getPacket();
               Arrays.stream(cpfPacket.getItems()).forEach(item -> {
                  if (item instanceof ConnectedDataItemResponse) {
                     ReferenceCountUtil.safeRelease(((ConnectedDataItemResponse)item).getData());
                  } else if (item instanceof UnconnectedDataItemResponse) {
                     ReferenceCountUtil.safeRelease(((UnconnectedDataItemResponse)item).getData());
                  }
               });
            }
         }
      }
   }

   private void onChannelInactive(ChannelHandlerContext ctx) {
      this.config.getLoggingContext().forEach(MDC::put);

      try {
         this.logger.debug("onChannelInactive() {} <-> {}", ctx.channel().localAddress(), ctx.channel().remoteAddress());
      } finally {
         this.config.getLoggingContext().keySet().forEach(MDC::remove);
      }
   }

   private void onExceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      this.config.getLoggingContext().forEach(MDC::put);

      try {
         this.logger.debug("onExceptionCaught() {} <-> {}", new Object[]{ctx.channel().localAddress(), ctx.channel().remoteAddress(), cause});
      } finally {
         this.config.getLoggingContext().keySet().forEach(MDC::remove);
      }

      ctx.channel().close();
   }

   protected void onUnitDataReceived(SendUnitData command) {
   }

   private static CompletableFuture<Channel> bootstrap(final EtherNetIpClient client) {
      CompletableFuture<Channel> future = new CompletableFuture<>();
      EtherNetIpClientConfig config = client.getConfig();
      Bootstrap bootstrap = new Bootstrap();
      ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)bootstrap.group(config.getEventLoop())).channel(NioSocketChannel.class))
                  .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT))
               .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int)config.getTimeout().toMillis()))
            .option(ChannelOption.TCP_NODELAY, true))
         .handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) {
               ch.pipeline().addLast(new ChannelHandler[]{new EnipCodec()});
               ch.pipeline().addLast(new ChannelHandler[]{new EtherNetIpClient.EtherNetIpClientHandler(client)});
            }
         });
      config.getBootstrapConsumer().accept(bootstrap);
      bootstrap.connect(config.getHostname(), config.getPort()).addListener(f -> {
         if (f.isSuccess()) {
            future.complete(f.channel());
         } else {
            future.completeExceptionally(f.cause());
         }
      });
      return future;
   }

   private final class EnipChannelActions implements ChannelActions {
      private EnipChannelActions() {
      }

      public CompletableFuture<Channel> connect(FsmContext<State, Event> ctx) {
         return EtherNetIpClient.bootstrap(EtherNetIpClient.this).thenCompose(channel -> {
            CompletableFuture<RegisterSession> future = new CompletableFuture<>();
            EtherNetIpClient.this.writeCommand(channel, new RegisterSession(), future);
            return future.thenApply(rs -> channel);
         });
      }

      public CompletableFuture<Void> disconnect(FsmContext<State, Event> ctx, Channel channel) {
         final CompletableFuture<Void> disconnectFuture = new CompletableFuture<>();
         channel.pipeline().addFirst(new ChannelHandler[]{new ChannelInboundHandlerAdapter() {
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
               disconnectFuture.complete(null);
            }
         }});
         CompletableFuture<UnRegisterSession> future = new CompletableFuture<>();
         EtherNetIpClient.this.writeCommand(channel, new UnRegisterSession(), future);
         future.whenComplete((cmd, ex2) -> {
            channel.close();
            disconnectFuture.complete(null);
         });
         return disconnectFuture;
      }

      public CompletableFuture<Void> keepAlive(FsmContext<State, Event> ctx, Channel channel) {
         return EtherNetIpClient.this.listIdentity().whenComplete((li, ex) -> {
            if (ex != null) {
               EtherNetIpClient.this.config.getLoggingContext().forEach(MDC::put);

               try {
                  EtherNetIpClient.this.logger.debug("Keep alive failed: {}", ex.getMessage(), ex);
               } finally {
                  EtherNetIpClient.this.config.getLoggingContext().keySet().forEach(MDC::remove);
               }
            }
         }).thenApply(li -> null);
      }
   }

   private static final class EtherNetIpClientHandler extends SimpleChannelInboundHandler<EnipPacket> {
      private final ExecutorService executor;
      private final EtherNetIpClient client;

      private EtherNetIpClientHandler(EtherNetIpClient client) {
         this.client = client;
         this.executor = client.getExecutor();
      }

      protected void channelRead0(ChannelHandlerContext channelHandlerContext, EnipPacket packet) {
         this.executor.execute(() -> this.client.onChannelRead(packet));
      }

      public void channelInactive(ChannelHandlerContext ctx) throws Exception {
         this.client.onChannelInactive(ctx);
         super.channelInactive(ctx);
      }

      public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
         this.client.onExceptionCaught(ctx, cause);
         super.exceptionCaught(ctx, cause);
      }
   }

   private static final class PendingRequest<T> {
      private final CompletableFuture<Command> promise = new CompletableFuture<>();
      private final Timeout timeout;

      private PendingRequest(CompletableFuture<T> future, Timeout timeout) {
         this.timeout = timeout;
         this.promise.whenComplete((r, ex) -> {
            if (r != null) {
               try {
                  future.complete((T)r);
               } catch (ClassCastException var4) {
                  future.completeExceptionally(var4);
               }
            } else {
               future.completeExceptionally(ex);
            }
         });
      }
   }
}
