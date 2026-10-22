package org.eclipse.jetty.server.handler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.ExceptionUtil;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ReHandlingErrorHandler extends ErrorHandler {
   private static final Logger LOG = LoggerFactory.getLogger(ReHandlingErrorHandler.class);
   private final Handler _handler;

   protected ReHandlingErrorHandler(Handler handler) {
      this._handler = handler;
   }

   @Override
   public InvocationType getInvocationType() {
      return this._handler.getInvocationType();
   }

   @Override
   protected void generateResponse(Request request, Response response, int code, String message, Throwable cause, Callback callback) throws IOException {
      if (request.getAttribute(ReHandlingErrorHandler.class.getName()) == null) {
         String pathInContext = this.getReHandlePathInContext(request, code, cause);
         if (pathInContext != null) {
            request.setAttribute(ReHandlingErrorHandler.class.getName(), pathInContext);
            HttpURI uri = Request.newHttpURIFrom(request, pathInContext);
            Request reRequest = Request.serveAs(request, uri);

            try {
               response.setStatus(200);
               if (this._handler.handle(reRequest, response, callback)) {
                  return;
               }
            } catch (Exception var11) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Unable to process error {}", reRequest, var11);
               }

               ExceptionUtil.addSuppressedIfNotAssociated(cause, var11);
               response.setStatus(code);
            }
         }
      }

      super.generateResponse(request, response, code, message, cause, callback);
   }

   protected abstract String getReHandlePathInContext(Request var1, int var2, Throwable var3);

   public static class ByHttpStatus extends ReHandlingErrorHandler {
      private final Map<Integer, String> _statusMap = new ConcurrentHashMap<>();

      public ByHttpStatus(Handler handler) {
         super(handler);
      }

      @Override
      protected String getReHandlePathInContext(Request request, int code, Throwable cause) {
         return this.get(code);
      }

      public String put(int code, String pathInContext) {
         return this._statusMap.put(code, pathInContext);
      }

      public String get(int code) {
         return this._statusMap.get(code);
      }

      public String remove(int code) {
         return this._statusMap.remove(code);
      }
   }
}
