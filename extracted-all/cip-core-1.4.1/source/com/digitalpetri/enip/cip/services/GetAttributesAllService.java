package com.digitalpetri.enip.cip.services;

import com.digitalpetri.enip.cip.CipResponseException;
import com.digitalpetri.enip.cip.epath.EPath;
import com.digitalpetri.enip.cip.structs.MessageRouterRequest;
import com.digitalpetri.enip.cip.structs.MessageRouterResponse;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

public class GetAttributesAllService implements CipService<ByteBuf> {
   public static final int SERVICE_CODE = 1;
   private final EPath.PaddedEPath requestPath;

   public GetAttributesAllService(EPath.PaddedEPath requestPath) {
      this.requestPath = requestPath;
   }

   @Override
   public void encodeRequest(ByteBuf buffer) {
      MessageRouterRequest request = new MessageRouterRequest(1, this.requestPath, byteBuf -> {
      });
      MessageRouterRequest.encode(request, buffer);
   }

   public ByteBuf decodeResponse(ByteBuf buffer) throws CipResponseException {
      MessageRouterResponse response = MessageRouterResponse.decode(buffer);
      if (response.getGeneralStatus() == 0) {
         return response.getData();
      } else {
         ReferenceCountUtil.release(response.getData());
         throw new CipResponseException(response.getGeneralStatus(), response.getAdditionalStatus());
      }
   }
}
