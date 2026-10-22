package com.yoctopuce.YoctoAPI;

import java.nio.ByteBuffer;

class WSHandlerJEE implements WSHandlerInterface {
   WSHandlerJEE(WSHandlerInterface.WSHandlerResponseInterface nhandler, Object session) {
   }

   @Override
   public void connect(YHTTPHub hub, boolean first_notification_connection, int mstimeout, int notifAbsPos) throws YAPI_Exception {
      throw new YAPI_Exception(-3, "WSHandlerJEE is only supported in library yoctolib-jEE");
   }

   @Override
   public void close() {
   }

   @Override
   public boolean isOpen() {
      return false;
   }

   @Override
   public void sendBinary(ByteBuffer partialByte, boolean isLast) throws YAPI_Exception {
   }

   @Override
   public String getThreadLabel() {
      return "WSHandlerJEE";
   }

   @Override
   public boolean isCallback() {
      return false;
   }
}
