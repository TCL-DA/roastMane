package org.eclipse.jetty.websocket.core.messages;

import java.lang.invoke.MethodHandle;
import org.eclipse.jetty.websocket.core.CoreSession;

public class ReaderMessageSink extends DispatchedMessageSink {
   public ReaderMessageSink(CoreSession session, MethodHandle methodHandle, boolean autoDemand) {
      super(session, methodHandle, autoDemand);
   }

   public MessageReader newMessageSink() {
      return new MessageReader(this.getCoreSession());
   }
}
