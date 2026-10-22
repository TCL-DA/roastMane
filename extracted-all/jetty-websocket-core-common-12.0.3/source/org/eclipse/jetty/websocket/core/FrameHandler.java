package org.eclipse.jetty.websocket.core;

import org.eclipse.jetty.util.Callback;

public interface FrameHandler extends IncomingFrames {
   void onOpen(CoreSession var1, Callback var2);

   @Override
   void onFrame(Frame var1, Callback var2);

   void onError(Throwable var1, Callback var2);

   void onClosed(CloseStatus var1, Callback var2);
}
