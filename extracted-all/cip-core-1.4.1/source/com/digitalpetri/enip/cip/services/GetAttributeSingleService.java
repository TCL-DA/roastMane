package com.digitalpetri.enip.cip.services;

import com.digitalpetri.enip.cip.CipResponseException;
import com.digitalpetri.enip.cip.epath.EPath;
import com.digitalpetri.enip.cip.structs.MessageRouterRequest;
import com.digitalpetri.enip.cip.structs.MessageRouterResponse;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

public class GetAttributeSingleService implements CipService<ByteBuf> {
   public static final int SERVICE_CODE = 14;
   private final EPath.PaddedEPath requestPath;

   public GetAttributeSingleService(EPath.PaddedEPath requestPath) {
      this.requestPath = requestPath;
   }

   @Override
   public void encodeRequest(ByteBuf buffer) {
      MessageRouterRequest request = new MessageRouterRequest(14, this.requestPath, this::encode);
      MessageRouterRequest.encode(request, buffer);
   }

   public ByteBuf decodeResponse(ByteBuf buffer) throws CipResponseException, CipService.PartialResponseException {
      MessageRouterResponse response = MessageRouterResponse.decode(buffer);

      ByteBuf var3;
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
   }

   private ByteBuf decode(ByteBuf buffer) {
      return buffer.readSlice(buffer.readableBytes()).retain();
   }
}
