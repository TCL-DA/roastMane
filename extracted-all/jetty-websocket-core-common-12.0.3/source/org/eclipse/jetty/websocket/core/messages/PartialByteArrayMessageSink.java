package org.eclipse.jetty.websocket.core.messages;

import java.lang.invoke.MethodHandle;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.Frame;

public class PartialByteArrayMessageSink extends AbstractMessageSink {
   public PartialByteArrayMessageSink(CoreSession session, MethodHandle methodHandle, boolean autoDemand) {
      super(session, methodHandle, autoDemand);
   }

   @Override
   public void accept(Frame frame, Callback callback) {
      try {
         if (!frame.hasPayload() && !frame.isFin()) {
            callback.succeeded();
            this.getCoreSession().demand();
         } else {
            byte[] buffer = BufferUtil.toArray(frame.getPayload());
            this.getMethodHandle().invoke((byte[])buffer, (boolean)frame.isFin());
            callback.succeeded();
            this.autoDemand();
         }
      } catch (Throwable var4) {
         callback.failed(var4);
      }
   }
}
