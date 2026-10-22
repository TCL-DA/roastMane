package com.digitalpetri.enip.cip.services;

import com.digitalpetri.enip.cip.CipResponseException;
import com.digitalpetri.enip.cip.epath.EPath;
import com.digitalpetri.enip.cip.epath.LogicalSegment;
import com.digitalpetri.enip.cip.structs.ForwardOpenRequest;
import com.digitalpetri.enip.cip.structs.ForwardOpenResponse;
import com.digitalpetri.enip.cip.structs.MessageRouterRequest;
import com.digitalpetri.enip.cip.structs.MessageRouterResponse;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

public class ForwardOpenService implements CipService<ForwardOpenResponse> {
   public static final int SERVICE_CODE = 84;
   private static final EPath.PaddedEPath CONNECTION_MANAGER_PATH = new EPath.PaddedEPath(new LogicalSegment.ClassId(6), new LogicalSegment.InstanceId(1));
   private final ForwardOpenRequest request;

   public ForwardOpenService(ForwardOpenRequest request) {
      this.request = request;
   }

   @Override
   public void encodeRequest(ByteBuf buffer) {
      MessageRouterRequest mrr = new MessageRouterRequest(84, CONNECTION_MANAGER_PATH, this::encode);
      MessageRouterRequest.encode(mrr, buffer);
   }

   public ForwardOpenResponse decodeResponse(ByteBuf buffer) throws CipResponseException, CipService.PartialResponseException {
      MessageRouterResponse mResponse = MessageRouterResponse.decode(buffer);
      int generalStatus = mResponse.getGeneralStatus();

      ForwardOpenResponse var4;
      try {
         if (generalStatus != 0) {
            throw new CipResponseException(generalStatus, mResponse.getAdditionalStatus());
         }

         var4 = ForwardOpenResponse.decode(mResponse.getData());
      } finally {
         ReferenceCountUtil.release(mResponse.getData());
      }

      return var4;
   }

   private void encode(ByteBuf buffer) {
      ForwardOpenRequest.encode(this.request, buffer);
   }
}
