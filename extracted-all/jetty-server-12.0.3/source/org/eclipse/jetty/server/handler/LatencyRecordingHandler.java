package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.NanoTime;

public abstract class LatencyRecordingHandler extends EventsHandler {
   public LatencyRecordingHandler() {
   }

   public LatencyRecordingHandler(Handler handler) {
      super(handler);
   }

   @Override
   protected final void onComplete(Request request, Throwable failure) {
      this.onRequestComplete(request.getId(), NanoTime.since(request.getBeginNanoTime()));
   }

   protected abstract void onRequestComplete(String var1, long var2);
}
