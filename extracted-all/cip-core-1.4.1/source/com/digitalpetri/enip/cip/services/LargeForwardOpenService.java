package com.digitalpetri.enip.cip.services;

import com.digitalpetri.enip.cip.CipResponseException;
import com.digitalpetri.enip.cip.epath.EPath;
import com.digitalpetri.enip.cip.epath.LogicalSegment;
import com.digitalpetri.enip.cip.structs.LargeForwardOpenRequest;
import com.digitalpetri.enip.cip.structs.LargeForwardOpenResponse;
import com.digitalpetri.enip.cip.structs.MessageRouterRequest;
import com.digitalpetri.enip.cip.structs.MessageRouterResponse;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

public class LargeForwardOpenService implements CipService<LargeForwardOpenResponse> {
   public static final int SERVICE_CODE = 91;
   private static final EPath.PaddedEPath CONNECTION_MANAGER_PATH = new EPath.PaddedEPath(new LogicalSegment.ClassId(6), new LogicalSegment.InstanceId(1));
   private final LargeForwardOpenRequest request;

   public LargeForwardOpenService(LargeForwardOpenRequest request) {
      this.request = request;
   }

   @Override
   public void encodeRequest(ByteBuf buffer) {
      MessageRouterRequest mrr = new MessageRouterRequest(91, CONNECTION_MANAGER_PATH, this::encode);
      MessageRouterRequest.encode(mrr, buffer);
   }

   public LargeForwardOpenResponse decodeResponse(ByteBuf buffer) throws CipResponseException, CipService.PartialResponseException {
      MessageRouterResponse mResponse = MessageRouterResponse.decode(buffer);
      int generalStatus = mResponse.getGeneralStatus();

      LargeForwardOpenResponse var4;
      try {
         if (generalStatus != 0) {
            throw new CipResponseException(generalStatus, mResponse.getAdditionalStatus());
         }

         var4 = LargeForwardOpenResponse.decode(mResponse.getData());
      } finally {
         ReferenceCountUtil.release(mResponse.getData());
      }

      return var4;
   }

   private void encode(ByteBuf buffer) {
      LargeForwardOpenRequest.encode(this.request, buffer);
   }
}
