package com.digitalpetri.enip.cip.services;

import com.digitalpetri.enip.cip.CipResponseException;
import com.digitalpetri.enip.cip.epath.EPath;
import com.digitalpetri.enip.cip.epath.LogicalSegment;
import com.digitalpetri.enip.cip.structs.ForwardCloseRequest;
import com.digitalpetri.enip.cip.structs.ForwardCloseResponse;
import com.digitalpetri.enip.cip.structs.MessageRouterRequest;
import com.digitalpetri.enip.cip.structs.MessageRouterResponse;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

public class ForwardCloseService implements CipService<ForwardCloseResponse> {
   public static final int SERVICE_CODE = 78;
   private static final EPath.PaddedEPath CONNECTION_MANAGER_PATH = new EPath.PaddedEPath(new LogicalSegment.ClassId(6), new LogicalSegment.InstanceId(1));
   private final ForwardCloseRequest request;

   public ForwardCloseService(ForwardCloseRequest request) {
      this.request = request;
   }

   @Override
   public void encodeRequest(ByteBuf buffer) {
      MessageRouterRequest mrr = new MessageRouterRequest(78, CONNECTION_MANAGER_PATH, b -> ForwardCloseRequest.encode(this.request, b));
      MessageRouterRequest.encode(mrr, buffer);
   }

   public ForwardCloseResponse decodeResponse(ByteBuf buffer) throws CipResponseException, CipService.PartialResponseException {
      MessageRouterResponse mResponse = MessageRouterResponse.decode(buffer);
      int generalStatus = mResponse.getGeneralStatus();

      ForwardCloseResponse var4;
      try {
         if (generalStatus != 0) {
            throw new CipResponseException(generalStatus, mResponse.getAdditionalStatus());
         }

         var4 = ForwardCloseResponse.decode(mResponse.getData());
      } finally {
         ReferenceCountUtil.release(mResponse.getData());
      }

      return var4;
   }
}
