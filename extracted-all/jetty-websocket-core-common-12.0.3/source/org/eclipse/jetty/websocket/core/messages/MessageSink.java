package org.eclipse.jetty.websocket.core.messages;

import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.core.Frame;

public interface MessageSink {
   void accept(Frame var1, Callback var2);

   default void fail(Throwable failure) {
   }
}
