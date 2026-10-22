package org.eclipse.jetty.server.handler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.Content.Sink;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownHandler extends Handler.Wrapper {
   private static final Logger LOG = LoggerFactory.getLogger(ShutdownHandler.class);
   private final String _shutdownPath;
   private final String _shutdownToken;
   private boolean _exitJvm = false;

   public ShutdownHandler(String shutdownToken) {
      this(null, null, shutdownToken, false);
   }

   public ShutdownHandler(String shutdownToken, boolean exitJVM) {
      this(null, null, shutdownToken, exitJVM);
   }

   public ShutdownHandler(Handler handler, String shutdownPath, String shutdownToken, boolean exitJVM) {
      super(handler);
      this._shutdownPath = StringUtil.isBlank(shutdownPath) ? "/shutdown" : shutdownPath;
      this._shutdownToken = shutdownToken;
      this._exitJvm = exitJVM;
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      String fullPath = request.getHttpURI().getCanonicalPath();
      ContextHandler contextHandler = ContextHandler.getContextHandler(request);
      if (contextHandler != null) {
         String pathInContext = contextHandler.getContext().getPathInContext(fullPath);
         if (!pathInContext.startsWith(this._shutdownPath)) {
            return super.handle(request, response, callback);
         }
      } else if (!fullPath.startsWith(this._shutdownPath)) {
         return super.handle(request, response, callback);
      }

      if (!request.getMethod().equals("POST")) {
         Response.writeError(request, response, callback, 400);
         return true;
      } else if (!this.hasCorrectSecurityToken(request)) {
         LOG.warn("Unauthorized tokenless shutdown attempt from {}", request.getConnectionMetaData().getRemoteSocketAddress());
         Response.writeError(request, response, callback, 401);
         return true;
      } else if (!this.requestFromLocalhost(request)) {
         LOG.warn("Unauthorized non-loopback shutdown attempt from {}", request.getConnectionMetaData().getRemoteSocketAddress());
         Response.writeError(request, response, callback, 401);
         return true;
      } else {
         LOG.info("Shutting down by request from {}", request.getConnectionMetaData().getRemoteSocketAddress());
         Callback triggerShutdownCallback = Callback.from(() -> CompletableFuture.runAsync(this::shutdownServer));
         response.getHeaders().put(HttpHeader.CONTENT_TYPE, "text/plain, charset=utf-8");
         String message = "Shutdown triggered";
         Sink.write(response, true, message, triggerShutdownCallback);
         return true;
      }
   }

   private boolean requestFromLocalhost(Request request) {
      SocketAddress socketAddress = request.getConnectionMetaData().getRemoteSocketAddress();
      if (socketAddress == null) {
         return false;
      } else {
         return socketAddress instanceof InetSocketAddress addr ? addr.getAddress().isLoopbackAddress() : false;
      }
   }

   private boolean hasCorrectSecurityToken(Request request) {
      Fields fields = Request.extractQueryParameters(request);
      String tok = fields.getValue("token");
      if (LOG.isDebugEnabled()) {
         LOG.debug("Token: {}", tok);
      }

      return this._shutdownToken.equals(tok);
   }

   private void shutdownServer() {
      try {
         this.getServer().stop();
      } catch (Exception var2) {
         LOG.warn("Unable to stop server", var2);
      }

      if (this._exitJvm) {
         System.exit(0);
      }
   }

   public void setExitJvm(boolean exitJvm) {
      this._exitJvm = exitJvm;
   }

   public boolean isExitJvm() {
      return this._exitJvm;
   }
}
