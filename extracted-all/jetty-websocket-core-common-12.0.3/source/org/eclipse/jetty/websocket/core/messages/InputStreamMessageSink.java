package org.eclipse.jetty.websocket.core.messages;

import java.lang.invoke.MethodHandle;
import org.eclipse.jetty.websocket.core.CoreSession;

public class InputStreamMessageSink extends DispatchedMessageSink {
   public InputStreamMessageSink(CoreSession session, MethodHandle methodHandle, boolean autoDemand) {
      super(session, methodHandle, autoDemand);
   }

   @Override
   public MessageSink newMessageSink() {
      return new MessageInputStream(this.getCoreSession());
   }
}
