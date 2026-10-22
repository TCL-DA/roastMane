package org.eclipse.jetty.websocket.core.client.internal;

import org.eclipse.jetty.client.HttpUpgrader;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.core.WebSocketConstants;
import org.eclipse.jetty.websocket.core.client.CoreClientUpgradeRequest;

public class HttpUpgraderOverHTTP2 implements HttpUpgrader {
   private static final PreEncodedHttpField WS_VERSION_FIELD = new PreEncodedHttpField(HttpHeader.SEC_WEBSOCKET_VERSION, WebSocketConstants.SPEC_VERSION_STRING);
   private final CoreClientUpgradeRequest clientUpgradeRequest;

   public HttpUpgraderOverHTTP2(CoreClientUpgradeRequest clientUpgradeRequest) {
      this.clientUpgradeRequest = clientUpgradeRequest;
   }

   public void prepare(Request request) {
      request.attribute(HttpUpgrader.PROTOCOL_ATTRIBUTE, "websocket").method(HttpMethod.CONNECT).headers(headers -> headers.put(WS_VERSION_FIELD));
      this.clientUpgradeRequest.requestComplete();
   }

   public void upgrade(Response response, EndPoint endPoint, Callback callback) {
      try {
         this.clientUpgradeRequest.upgrade(response, endPoint);
         callback.succeeded();
      } catch (Throwable var5) {
         callback.failed(var5);
      }
   }
}
