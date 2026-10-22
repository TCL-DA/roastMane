package com.digitalpetri.enip.cip.services;

import com.digitalpetri.enip.cip.CipResponseException;
import com.digitalpetri.enip.cip.epath.EPath;
import com.digitalpetri.enip.cip.structs.AttributeRequest;
import com.digitalpetri.enip.cip.structs.AttributeResponse;
import com.digitalpetri.enip.cip.structs.MessageRouterRequest;
import com.digitalpetri.enip.cip.structs.MessageRouterResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import java.util.function.Function;

public class SetAttributeListService implements CipService<AttributeResponse[]> {
   public static final int SERVICE_CODE = 4;
   private final EPath.PaddedEPath requestPath;
   private final AttributeRequest[] attributeRequests;
   private final Function<Integer, ByteBuf> attributeDataDecoder;

   public SetAttributeListService(EPath.PaddedEPath requestPath, AttributeRequest[] attributeRequests, Function<Integer, ByteBuf> attributeDataDecoder) {
      this.requestPath = requestPath;
      this.attributeRequests = attributeRequests;
      this.attributeDataDecoder = attributeDataDecoder;
   }

   @Override
   public void encodeRequest(ByteBuf buffer) {
      MessageRouterRequest request = new MessageRouterRequest(4, this.requestPath, this::encode);
      MessageRouterRequest.encode(request, buffer);
   }

   public AttributeResponse[] decodeResponse(ByteBuf buffer) throws CipResponseException, CipService.PartialResponseException {
      MessageRouterResponse response = MessageRouterResponse.decode(buffer);

      AttributeResponse[] var3;
      try {
         if (response.getGeneralStatus() != 0) {
            throw new CipResponseException(response.getGeneralStatus(), response.getAdditionalStatus());
         }

         var3 = this.decode(buffer);
      } finally {
         ReferenceCountUtil.release(buffer);
      }

      return var3;
   }

   private void encode(ByteBuf buffer) {
      buffer.writeShort(this.attributeRequests.length);

      for (AttributeRequest request : this.attributeRequests) {
         buffer.writeShort(request.getId());
         buffer.writeBytes(request.getData());
      }
   }

   private AttributeResponse[] decode(ByteBuf buffer) {
      int count = buffer.readUnsignedShort();
      AttributeResponse[] responses = new AttributeResponse[count];

      for (int i = 0; i < count; i++) {
         int id = buffer.readShort();
         int status = buffer.readShort();
         ByteBuf data = status == 0 ? this.attributeDataDecoder.apply(id) : Unpooled.EMPTY_BUFFER;
         responses[i] = new AttributeResponse(id, status, data);
      }

      return responses;
   }
}
