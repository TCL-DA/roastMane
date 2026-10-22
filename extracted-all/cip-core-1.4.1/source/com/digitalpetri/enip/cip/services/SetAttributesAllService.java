package com.digitalpetri.enip.cip.services;

import com.digitalpetri.enip.cip.CipResponseException;
import com.digitalpetri.enip.cip.epath.EPath;
import com.digitalpetri.enip.cip.structs.MessageRouterRequest;
import com.digitalpetri.enip.cip.structs.MessageRouterResponse;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import java.util.function.Consumer;

public class SetAttributesAllService implements CipService<Void> {
   public static final int SERVICE_CODE = 2;
   private final EPath.PaddedEPath requestPath;
   private final Consumer<ByteBuf> attributeEncoder;

   public SetAttributesAllService(EPath.PaddedEPath requestPath, Consumer<ByteBuf> attributeEncoder) {
      this.requestPath = requestPath;
      this.attributeEncoder = attributeEncoder;
   }

   @Override
   public void encodeRequest(ByteBuf buffer) {
      MessageRouterRequest request = new MessageRouterRequest(2, this.requestPath, this.attributeEncoder);
      MessageRouterRequest.encode(request, buffer);
   }

   public Void decodeResponse(ByteBuf buffer) throws CipResponseException, CipService.PartialResponseException {
      MessageRouterResponse response = MessageRouterResponse.decode(buffer);

      Object var3;
      try {
         if (response.getGeneralStatus() != 0) {
            throw new CipResponseException(response.getGeneralStatus(), response.getAdditionalStatus());
         }

         var3 = null;
      } finally {
         ReferenceCountUtil.release(response.getData());
      }

      return (Void)var3;
   }
}
