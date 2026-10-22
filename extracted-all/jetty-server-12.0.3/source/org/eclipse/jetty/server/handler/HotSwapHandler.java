package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;

public class HotSwapHandler extends Handler.AbstractContainer implements Handler.Singleton {
   private volatile Handler _handler;

   public HotSwapHandler() {
      super(true);
   }

   @Override
   public Handler getHandler() {
      return this._handler;
   }

   @Override
   public void setHandler(Handler handler) {
      Server server1 = this.getServer();
      if (server1 != null
         && server1.isStarted()
         && handler != null
         && server1.getInvocationType() != Invocable.combine(server1.getInvocationType(), handler.getInvocationType())) {
         throw new IllegalArgumentException("Cannot change invocation type of started server");
      } else if (handler == this) {
         throw new IllegalStateException("setHandler loop");
      } else {
         if (handler instanceof Handler.Container container && container.getDescendants().contains(this)) {
            throw new IllegalStateException("setHandler loop");
         }

         try {
            Server server = this.getServer();
            if (handler != this._handler) {
               Handler oldHandler = this._handler;
               if (handler != null) {
                  handler.setServer(server);
                  this.addBean(handler, true);
                  if (oldHandler != null && oldHandler.isStarted()) {
                     handler.start();
                  }
               }

               this._handler = handler;
               if (oldHandler != null) {
                  this.removeBean(oldHandler);
               }
            }
         } catch (Exception var5) {
            throw new RuntimeException(var5);
         }
      }
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      Handler next = this._handler;
      return next != null && next.handle(request, response, callback);
   }

   @Override
   public InvocationType getInvocationType() {
      Handler next = this.getHandler();
      return next == null ? InvocationType.NON_BLOCKING : next.getInvocationType();
   }

   @Override
   public void destroy() {
      if (!this.isStopped()) {
         throw new IllegalStateException("!STOPPED");
      } else {
         Handler child = this.getHandler();
         if (child != null) {
            this.setHandler((Handler)null);
            child.destroy();
         }

         super.destroy();
      }
   }
}
