package org.eclipse.jetty.websocket.core;

import org.eclipse.jetty.util.Callback;

public interface IncomingFrames {
   void onFrame(Frame var1, Callback var2);
}
