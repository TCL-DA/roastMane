package com.yoctopuce.YoctoAPI;

import java.nio.ByteBuffer;

public interface WSHandlerInterface {
   void connect(YHTTPHub var1, boolean var2, int var3, int var4) throws YAPI_Exception;

   void close();

   boolean isOpen();

   void sendBinary(ByteBuffer var1, boolean var2) throws YAPI_Exception;

   String getThreadLabel();

   boolean isCallback();

   public interface WSHandlerResponseInterface {
      void WSLOG(String var1);

      void parseBinaryMessage(ByteBuffer var1);

      void errorOnSession(int var1, String var2);
   }
}
