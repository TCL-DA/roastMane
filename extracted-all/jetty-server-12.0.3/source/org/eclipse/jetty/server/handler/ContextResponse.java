package org.eclipse.jetty.server.handler;

import java.nio.ByteBuffer;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;

public class ContextResponse extends Response.Wrapper {
   private final ContextHandler.ScopedContext _context;

   public ContextResponse(ContextHandler.ScopedContext context, Request request, Response response) {
      super(request, response);
      this._context = context;
   }

   @Override
   public void write(boolean last, ByteBuffer content, final Callback callback) {
      Callback contextCallback = new Callback() {
         public void succeeded() {
            ContextResponse.this._context.run(callback::succeeded, ContextResponse.this.getRequest());
         }

         public void failed(Throwable x) {
            ContextResponse.this._context.accept(callback::failed, x, ContextResponse.this.getRequest());
         }

         public InvocationType getInvocationType() {
            return Invocable.getInvocationType(callback);
         }
      };
      super.write(last, content, contextCallback);
   }
}
