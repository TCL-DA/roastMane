package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

public class IdleTimeoutHandler extends Handler.Wrapper {
   private long _idleTimeoutMs = 1000L;
   private boolean _applyToAsync = false;

   public IdleTimeoutHandler() {
      this(null);
   }

   public IdleTimeoutHandler(Handler handler) {
      super(handler);
   }

   public boolean isApplyToAsync() {
      return this._applyToAsync;
   }

   public void setApplyToAsync(boolean applyToAsync) {
      this._applyToAsync = applyToAsync;
   }

   public long getIdleTimeoutMs() {
      return this._idleTimeoutMs;
   }

   public void setIdleTimeoutMs(long idleTimeoutMs) {
      this._idleTimeoutMs = idleTimeoutMs;
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      long idleTimeout = 0L;
      return super.handle(request, response, Callback.from(callback, () -> {
      }));
   }
}
