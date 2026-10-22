package org.eclipse.jetty.websocket.core.messages;

import java.lang.invoke.MethodHandle;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.Frame;

public class PartialByteBufferMessageSink extends AbstractMessageSink {
   public PartialByteBufferMessageSink(CoreSession session, MethodHandle methodHandle, boolean autoDemand) {
      super(session, methodHandle, autoDemand);
   }

   @Override
   public void accept(Frame frame, Callback callback) {
      try {
         if (!frame.hasPayload() && !frame.isFin()) {
            callback.succeeded();
            this.getCoreSession().demand();
         } else {
            this.invoke(this.getMethodHandle(), frame.getPayload(), frame.isFin(), callback);
            this.autoDemand();
         }
      } catch (Throwable var4) {
         callback.failed(var4);
      }
   }

   protected void invoke(MethodHandle methodHandle, ByteBuffer byteBuffer, boolean fin, Callback callback) throws Throwable {
      methodHandle.invoke((ByteBuffer)byteBuffer, (boolean)fin);
      callback.succeeded();
   }
}
