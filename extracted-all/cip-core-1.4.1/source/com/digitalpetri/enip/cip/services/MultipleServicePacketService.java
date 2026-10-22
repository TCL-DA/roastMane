package com.digitalpetri.enip.cip.services;

import com.digitalpetri.enip.cip.CipResponseException;
import com.digitalpetri.enip.cip.epath.EPath;
import com.digitalpetri.enip.cip.epath.LogicalSegment;
import com.digitalpetri.enip.cip.structs.MessageRouterRequest;
import com.digitalpetri.enip.cip.structs.MessageRouterResponse;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class MultipleServicePacketService implements CipService<Void> {
   public static final int SERVICE_CODE = 10;
   private static final EPath.PaddedEPath MESSAGE_ROUTER_PATH = new EPath.PaddedEPath(new LogicalSegment.ClassId(2), new LogicalSegment.InstanceId(1));
   private final List<CipService<?>> services;
   private final List<BiConsumer<?, Throwable>> consumers;
   private final List<CipService<?>> currentServices;
   private final List<BiConsumer<?, Throwable>> currentConsumers;

   public MultipleServicePacketService(List<CipService<?>> services, List<BiConsumer<?, Throwable>> consumers) {
      assert services.size() == consumers.size();

      this.services = Collections.synchronizedList(new ArrayList<>(services));
      this.consumers = Collections.synchronizedList(new ArrayList<>(consumers));
      this.currentServices = Collections.synchronizedList(new ArrayList<>(services));
      this.currentConsumers = Collections.synchronizedList(new ArrayList<>(consumers));
   }

   @Override
   public void encodeRequest(ByteBuf buffer) {
      MessageRouterRequest request = new MessageRouterRequest(10, MESSAGE_ROUTER_PATH, this::encode);
      MessageRouterRequest.encode(request, buffer);
   }

   public Void decodeResponse(ByteBuf buffer) throws CipResponseException, CipService.PartialResponseException {
      MessageRouterResponse response = MessageRouterResponse.decode(buffer);

      Object var23;
      try {
         if (response.getGeneralStatus() != 0 && response.getGeneralStatus() != 30) {
            throw new CipResponseException(response.getGeneralStatus(), response.getAdditionalStatus());
         }

         List<Object[]> partials = new ArrayList<>();
         ByteBuf[] serviceData = this.decode(response.getData());

         for (int i = 0; i < serviceData.length; i++) {
            CipService<?> service = this.currentServices.get(i);
            BiConsumer<Object, Throwable> consumer = (BiConsumer<Object, Throwable>)this.currentConsumers.get(i);

            try {
               consumer.accept(service.decodeResponse(serviceData[i]), null);
            } catch (CipService.PartialResponseException var19) {
               partials.add(new Object[]{service, consumer});
            } catch (Throwable var20) {
               consumer.accept(null, var20);
            } finally {
               ReferenceCountUtil.release(serviceData[i]);
            }
         }

         if (!partials.isEmpty()) {
            this.currentServices.clear();
            this.currentConsumers.clear();

            for (Object[] oa : partials) {
               CipService<?> service = (CipService<?>)oa[0];
               BiConsumer<Object, Throwable> consumer = (BiConsumer<Object, Throwable>)oa[1];
               this.currentServices.add(service);
               this.currentConsumers.add(consumer);
            }

            throw CipService.PartialResponseException.INSTANCE;
         }

         this.currentServices.clear();
         this.currentServices.addAll(this.services);
         this.currentConsumers.clear();
         this.currentConsumers.addAll(this.consumers);
         var23 = null;
      } finally {
         ReferenceCountUtil.release(response.getData());
      }

      return (Void)var23;
   }

   private void encode(ByteBuf buffer) {
      int serviceCount = this.currentServices.size();
      buffer.writeShort(serviceCount);
      int[] offsets = new int[serviceCount];
      int offsetsStartIndex = buffer.writerIndex();
      buffer.writeZero(serviceCount * 2);

      for (int i = 0; i < serviceCount; i++) {
         offsets[i] = buffer.writerIndex() - offsetsStartIndex + 2;
         this.currentServices.get(i).encodeRequest(buffer);
      }

      buffer.markWriterIndex();
      buffer.writerIndex(offsetsStartIndex);

      for (int offset : offsets) {
         buffer.writeShort(offset);
      }

      buffer.resetWriterIndex();
   }

   private ByteBuf[] decode(ByteBuf buffer) {
      int dataStartIndex = buffer.readerIndex();
      int serviceCount = buffer.readUnsignedShort();
      int[] offsets = new int[serviceCount];

      for (int i = 0; i < serviceCount; i++) {
         offsets[i] = buffer.readUnsignedShort();
      }

      ByteBuf[] serviceData = new ByteBuf[serviceCount];

      for (int i = 0; i < serviceCount; i++) {
         int offset = offsets[i];
         int length = i + 1 < serviceCount ? offsets[i + 1] - offset : buffer.readableBytes();
         serviceData[i] = buffer.slice(dataStartIndex + offsets[i], length).retain();
         buffer.skipBytes(length);
      }

      return serviceData;
   }
}
