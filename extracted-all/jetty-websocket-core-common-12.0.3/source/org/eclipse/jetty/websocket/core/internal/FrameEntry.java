package org.eclipse.jetty.websocket.core.internal;

import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.core.Frame;

public class FrameEntry {
   public final Frame frame;
   public final Callback callback;
   public final boolean batch;

   public FrameEntry(Frame frame, Callback callback, boolean batch) {
      this.frame = frame;
      this.callback = callback;
      this.batch = batch;
   }

   @Override
   public String toString() {
      return this.frame.toString();
   }
}
