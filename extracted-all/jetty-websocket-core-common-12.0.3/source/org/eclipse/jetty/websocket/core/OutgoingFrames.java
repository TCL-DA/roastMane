package org.eclipse.jetty.websocket.core;

import org.eclipse.jetty.util.Callback;

public interface OutgoingFrames {
   void sendFrame(Frame var1, Callback var2, boolean var3);
}
