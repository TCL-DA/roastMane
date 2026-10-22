package com.digitalpetri.enip.cip.services;

import com.digitalpetri.enip.cip.CipResponseException;
import io.netty.buffer.ByteBuf;

public interface CipService<T> {
   void encodeRequest(ByteBuf var1);

   T decodeResponse(ByteBuf var1) throws CipResponseException, CipService.PartialResponseException;

   public static final class PartialResponseException extends Exception {
      public static final CipService.PartialResponseException INSTANCE = new CipService.PartialResponseException();
   }
}
