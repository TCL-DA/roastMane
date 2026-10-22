package org.eclipse.jetty.websocket.client.internal;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.common.JettyWebSocketFrameHandler;
import org.eclipse.jetty.websocket.common.JettyWebSocketFrameHandlerFactory;
import org.eclipse.jetty.websocket.core.ExtensionConfig;
import org.eclipse.jetty.websocket.core.FrameHandler;
import org.eclipse.jetty.websocket.core.client.CoreClientUpgradeRequest;
import org.eclipse.jetty.websocket.core.client.WebSocketCoreClient;

public class JettyClientUpgradeRequest extends CoreClientUpgradeRequest {
   private final JettyWebSocketFrameHandler frameHandler;

   public JettyClientUpgradeRequest(
      WebSocketCoreClient coreClient, ClientUpgradeRequest request, URI requestURI, JettyWebSocketFrameHandlerFactory frameHandlerFactory, Object websocketPojo
   ) {
      super(coreClient, requestURI);
      if (request != null) {
         this.headers(fields -> request.getHeaders().forEach(fields::put));
         this.headers(fields -> request.getCookies().forEach(cookie -> fields.add(HttpHeader.COOKIE, cookie.toString())));
         this.setSubProtocols(request.getSubProtocols());
         this.setExtensions(request.getExtensions().stream().map(c -> new ExtensionConfig(c.getName(), c.getParameters())).collect(Collectors.toList()));
         this.timeout(request.getTimeout(), TimeUnit.MILLISECONDS);
      }

      this.frameHandler = frameHandlerFactory.newJettyFrameHandler(websocketPojo);
   }

   public void upgrade(Response response, EndPoint endPoint) {
      this.frameHandler.setUpgradeRequest(new DelegatedJettyClientUpgradeRequest(this));
      this.frameHandler.setUpgradeResponse(new DelegatedJettyClientUpgradeResponse(response));
      super.upgrade(response, endPoint);
   }

   public FrameHandler getFrameHandler() {
      return this.frameHandler;
   }
}
