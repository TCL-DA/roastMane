package com.digitalpetri.enip.cip.services;

import com.digitalpetri.enip.cip.CipResponseException;
import com.digitalpetri.enip.cip.epath.EPath;
import com.digitalpetri.enip.cip.structs.AttributeResponse;
import com.digitalpetri.enip.cip.structs.MessageRouterRequest;
import com.digitalpetri.enip.cip.structs.MessageRouterResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

public class GetAttributeListService implements CipService<AttributeResponse[]> {
   public static final int SERVICE_CODE = 3;
   private final EPath.PaddedEPath requestPath;
   private final int[] attributeIds;
   private final int[] attributeSizes;

   public GetAttributeListService(EPath.PaddedEPath requestPath, int[] attributeIds, int[] attributeSizes) {
      this.attributeIds = attributeIds;
      this.attributeSizes = attributeSizes;
      this.requestPath = requestPath;
   }

   @Override
   public void encodeRequest(ByteBuf buffer) {
      MessageRouterRequest request = new MessageRouterRequest(3, this.requestPath, this::encode);
      MessageRouterRequest.encode(request, buffer);
   }

   public AttributeResponse[] decodeResponse(ByteBuf buffer) throws CipResponseException, CipService.PartialResponseException {
      MessageRouterResponse response = MessageRouterResponse.decode(buffer);

      AttributeResponse[] var3;
      try {
         if (response.getGeneralStatus() != 0) {
            throw new CipResponseException(response.getGeneralStatus(), response.getAdditionalStatus());
         }

         var3 = this.decode(response.getData());
      } finally {
         ReferenceCountUtil.release(response.getData());
      }

      return var3;
   }

   private void encode(ByteBuf buffer) {
      buffer.writeShort(this.attributeIds.length);

      for (int id : this.attributeIds) {
         buffer.writeShort(id);
      }
   }

   private AttributeResponse[] decode(ByteBuf buffer) {
      int count = buffer.readUnsignedShort();
      AttributeResponse[] attributeResponses = new AttributeResponse[count];

      for (int i = 0; i < count; i++) {
         int id = buffer.readUnsignedShort();
         int status = buffer.readUnsignedShort();
         ByteBuf data = status == 0 ? buffer.readSlice(this.attributeSizes[i]).copy() : Unpooled.EMPTY_BUFFER;
         attributeResponses[i] = new AttributeResponse(id, status, data);
      }

      return attributeResponses;
   }
}
