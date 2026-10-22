package com.digitalpetri.enip.cip;

import com.digitalpetri.enip.EtherNetIpClient;
import com.digitalpetri.enip.EtherNetIpClientConfig;
import com.digitalpetri.enip.cip.epath.EPath.PaddedEPath;
import com.digitalpetri.enip.cip.services.CipService;
import com.digitalpetri.enip.cip.services.CipServiceInvoker;
import com.digitalpetri.enip.cip.services.UnconnectedSendService;
import com.digitalpetri.enip.cip.services.CipService.PartialResponseException;
import com.digitalpetri.enip.commands.SendRRData;
import com.digitalpetri.enip.commands.SendUnitData;
import com.digitalpetri.enip.cpf.ConnectedAddressItem;
import com.digitalpetri.enip.cpf.ConnectedDataItemRequest;
import com.digitalpetri.enip.cpf.ConnectedDataItemResponse;
import com.digitalpetri.enip.cpf.CpfItem;
import com.digitalpetri.enip.cpf.CpfPacket;
import com.digitalpetri.enip.cpf.NullAddressItem;
import com.digitalpetri.enip.cpf.UnconnectedDataItemRequest;
import com.digitalpetri.enip.cpf.UnconnectedDataItemResponse;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.Timeout;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class CipClient extends EtherNetIpClient implements CipServiceInvoker {
   private final Logger logger = LoggerFactory.getLogger(this.getClass());
   private final CipClient.ConnectedDataHandler connectedDataHandler = new CipClient.ConnectedDataHandler();
   private final List<CipClient.CpfItemHandler> additionalHandlers = new CopyOnWriteArrayList<>();
   private final Map<Integer, CompletableFuture<ByteBuf>> pending = new ConcurrentHashMap<>();
   private final Map<Integer, Timeout> timeouts = new ConcurrentHashMap<>();
   private final AtomicInteger sequenceNumber = new AtomicInteger(0);
   private final PaddedEPath connectionPath;

   public CipClient(EtherNetIpClientConfig config, PaddedEPath connectionPath) {
      super(config);
      this.connectionPath = connectionPath;
   }

   public <T> CompletableFuture<T> invokeConnected(int connectionId, CipService<T> service) {
      CompletableFuture<T> future = new CompletableFuture<>();
      return this.invokeConnected(connectionId, service, future);
   }

   private <T> CompletableFuture<T> invokeConnected(int connectionId, CipService<T> service, CompletableFuture<T> future) {
      this.sendConnectedData(service::encodeRequest, connectionId).whenComplete((buffer, ex) -> {
         if (buffer != null) {
            try {
               T response = (T)service.decodeResponse(buffer);
               future.complete(response);
            } catch (PartialResponseException var11) {
               this.invokeConnected(connectionId, service, future);
            } catch (CipResponseException var12) {
               future.completeExceptionally(var12);
            } finally {
               ReferenceCountUtil.release(buffer);
            }
         } else {
            future.completeExceptionally(ex);
         }
      });
      return future;
   }

   public <T> CompletableFuture<T> invokeUnconnected(CipService<T> service) {
      return this.invokeUnconnected(service, 0);
   }

   public <T> CompletableFuture<T> invokeUnconnected(CipService<T> service, int maxRetries) {
      UnconnectedSendService<T> uss = new UnconnectedSendService(service, this.connectionPath, this.getConfig().getTimeout());
      return this.invoke(uss, maxRetries);
   }

   public <T> CompletableFuture<T> invoke(CipService<T> service) {
      return this.invoke(service, 0);
   }

   public <T> CompletableFuture<T> invoke(CipService<T> service, int maxRetries) {
      CompletableFuture<T> future = new CompletableFuture<>();
      return this.invoke(service, future, 0, maxRetries);
   }

   private <T> CompletableFuture<T> invoke(CipService<T> service, CompletableFuture<T> future, int count, int maxRetries) {
      this.sendUnconnectedData(service::encodeRequest).whenComplete((buffer, ex) -> {
         if (buffer != null) {
            try {
               T response = (T)service.decodeResponse(buffer);
               future.complete(response);
            } catch (PartialResponseException var20) {
               this.invoke(service, future, count, maxRetries);
            } catch (CipResponseException var21) {
               if (var21.getGeneralStatus() == 1) {
                  boolean requestTimedOut = Arrays.stream(var21.getAdditionalStatus()).anyMatch(i -> i == 516);
                  if (requestTimedOut && count < maxRetries) {
                     this.getConfig().getLoggingContext().forEach(MDC::put);

                     try {
                        this.logger.debug("Unconnected request timed out; retrying, count={}, max={}", count, maxRetries);
                     } finally {
                        this.getConfig().getLoggingContext().keySet().forEach(MDC::remove);
                     }

                     this.invoke(service, future, count + 1, maxRetries);
                  } else {
                     future.completeExceptionally(var21);
                  }
               } else {
                  future.completeExceptionally(var21);
               }
            } finally {
               ReferenceCountUtil.release(buffer);
            }
         } else {
            future.completeExceptionally(ex);
         }
      });
      return future;
   }

   public CompletableFuture<ByteBuf> sendConnectedData(ByteBuf data, int connectionId) {
      return this.sendConnectedData((Consumer<ByteBuf>)(buffer -> buffer.writeBytes(data)), connectionId);
   }

   public CompletableFuture<ByteBuf> sendConnectedData(Consumer<ByteBuf> dataEncoder, int connectionId) {
      CompletableFuture<ByteBuf> future = new CompletableFuture<>();
      ConnectedAddressItem addressItem = new ConnectedAddressItem(connectionId);
      int sequenceNumber = this.nextSequenceNumber();
      ConnectedDataItemRequest dataItem = new ConnectedDataItemRequest(b -> {
         b.writeShort(sequenceNumber);
         dataEncoder.accept(b);
      });
      CpfPacket packet = new CpfPacket(new CpfItem[]{addressItem, dataItem});
      SendUnitData command = new SendUnitData(packet);
      Timeout timeout = this.getConfig()
         .getWheelTimer()
         .newTimeout(
            tt -> {
               if (!tt.isCancelled()) {
                  CompletableFuture<ByteBuf> f = this.pending.remove(sequenceNumber);
                  if (f != null) {
                     String message = String.format(
                        "sequenceNumber=%s timed out waiting %sms for response", sequenceNumber, this.getConfig().getTimeout().toMillis()
                     );
                     f.completeExceptionally(new TimeoutException(message));
                  }
               }
            },
            this.getConfig().getTimeout().toMillis(),
            TimeUnit.MILLISECONDS
         );
      this.pending.put(sequenceNumber, future);
      this.timeouts.put(sequenceNumber, timeout);
      this.sendUnitData(command).whenComplete((v, ex) -> {
         if (ex != null) {
            future.completeExceptionally(ex);
         }
      });
      return future;
   }

   public CompletableFuture<ByteBuf> sendUnconnectedData(ByteBuf data) {
      return this.sendUnconnectedData((Consumer<ByteBuf>)(buffer -> buffer.writeBytes(data)));
   }

   public CompletableFuture<ByteBuf> sendUnconnectedData(Consumer<ByteBuf> dataEncoder) {
      CompletableFuture<ByteBuf> future = new CompletableFuture<>();
      UnconnectedDataItemRequest dataItem = new UnconnectedDataItemRequest(dataEncoder);
      CpfPacket packet = new CpfPacket(new CpfItem[]{new NullAddressItem(), dataItem});
      this.sendRRData(new SendRRData(packet)).whenComplete((command, ex) -> {
         if (command != null) {
            CpfItem[] items = command.getPacket().getItems();
            if (items.length == 2 && items[0].getTypeId() == 0 && items[1].getTypeId() == 178) {
               ByteBuf data = ((UnconnectedDataItemResponse)items[1]).getData();
               future.complete(data);
            } else {
               future.completeExceptionally(new Exception("received unexpected items"));
            }
         } else {
            future.completeExceptionally(ex);
         }
      });
      return future;
   }

   protected void onUnitDataReceived(SendUnitData command) {
      CpfItem[] items = command.getPacket().getItems();
      if (this.connectedDataHandler.itemsMatch(items)) {
         this.connectedDataHandler.itemsReceived(items);
      } else {
         for (CipClient.CpfItemHandler handler : this.additionalHandlers) {
            if (handler.itemsMatch(items)) {
               handler.itemsReceived(items);
               break;
            }
         }
      }
   }

   private short nextSequenceNumber() {
      return (short)this.sequenceNumber.incrementAndGet();
   }

   private class ConnectedDataHandler implements CipClient.CpfItemHandler {
      private ConnectedDataHandler() {
      }

      @Override
      public void itemsReceived(CpfItem[] items) {
         int connectionId = ((ConnectedAddressItem)items[0]).getConnectionId();
         ByteBuf buffer = ((ConnectedDataItemResponse)items[1]).getData();
         int sequenceNumber = buffer.readShort();
         ByteBuf data = buffer.readSlice(buffer.readableBytes()).retain();
         Timeout timeout = CipClient.this.timeouts.remove(sequenceNumber);
         if (timeout != null) {
            timeout.cancel();
         }

         CompletableFuture<ByteBuf> future = CipClient.this.pending.remove(sequenceNumber);
         if (future != null) {
            future.complete(data);
         } else {
            ReferenceCountUtil.release(data);
         }

         ReferenceCountUtil.release(buffer);
      }

      @Override
      public boolean itemsMatch(CpfItem[] items) {
         return items.length == 2 && items[0].getTypeId() == 161 && items[1].getTypeId() == 177;
      }
   }

   public interface CpfItemHandler {
      boolean itemsMatch(CpfItem[] var1);

      void itemsReceived(CpfItem[] var1);
   }
}
