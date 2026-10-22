package org.eclipse.jetty.websocket.common.internal;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.exception.InvalidSignatureException;

public class ByteBufferMessageSink extends org.eclipse.jetty.websocket.core.messages.ByteBufferMessageSink {
   public ByteBufferMessageSink(CoreSession session, MethodHandle methodHandle, boolean autoDemand) {
      super(session, methodHandle, autoDemand, false);
      MethodType onMessageType = MethodType.methodType(void.class, ByteBuffer.class, Callback.class);
      if (methodHandle.type() != onMessageType) {
         throw InvalidSignatureException.build(onMessageType, methodHandle.type());
      }
   }

   protected void invoke(MethodHandle methodHandle, ByteBuffer byteBuffer, org.eclipse.jetty.util.Callback callback) throws Throwable {
      methodHandle.invoke((ByteBuffer)byteBuffer, (Callback)Callback.from(callback::succeeded, callback::failed));
   }
}
