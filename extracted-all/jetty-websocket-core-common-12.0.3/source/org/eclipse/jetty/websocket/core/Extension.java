package org.eclipse.jetty.websocket.core;

import java.io.Closeable;

public interface Extension extends IncomingFrames, OutgoingFrames, Closeable {
   void init(ExtensionConfig var1, WebSocketComponents var2);

   @Override
   default void close() {
   }

   ExtensionConfig getConfig();

   String getName();

   boolean isRsv1User();

   boolean isRsv2User();

   boolean isRsv3User();

   void setNextIncomingFrames(IncomingFrames var1);

   void setNextOutgoingFrames(OutgoingFrames var1);

   void setCoreSession(CoreSession var1);
}
