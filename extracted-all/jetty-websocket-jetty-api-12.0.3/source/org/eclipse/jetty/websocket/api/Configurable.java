package org.eclipse.jetty.websocket.api;

import java.time.Duration;

public interface Configurable {
   Duration getIdleTimeout();

   void setIdleTimeout(Duration var1);

   int getInputBufferSize();

   void setInputBufferSize(int var1);

   int getOutputBufferSize();

   void setOutputBufferSize(int var1);

   long getMaxBinaryMessageSize();

   void setMaxBinaryMessageSize(long var1);

   long getMaxTextMessageSize();

   void setMaxTextMessageSize(long var1);

   long getMaxFrameSize();

   void setMaxFrameSize(long var1);

   boolean isAutoFragment();

   void setAutoFragment(boolean var1);

   int getMaxOutgoingFrames();

   void setMaxOutgoingFrames(int var1);
}
