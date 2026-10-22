package org.eclipse.jetty.websocket.core.client.internal;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.jetty.client.HttpResponseException;
import org.eclipse.jetty.client.HttpUpgrader;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.core.WebSocketConstants;
import org.eclipse.jetty.websocket.core.client.CoreClientUpgradeRequest;
import org.eclipse.jetty.websocket.core.util.WebSocketUtils;

public class HttpUpgraderOverHTTP implements HttpUpgrader {
   private static final PreEncodedHttpField WS_VERSION_FIELD = new PreEncodedHttpField(HttpHeader.SEC_WEBSOCKET_VERSION, WebSocketConstants.SPEC_VERSION_STRING);
   private static final PreEncodedHttpField WS_UPGRADE_FIELD = new PreEncodedHttpField(HttpHeader.UPGRADE, "websocket");
   private static final PreEncodedHttpField WS_CONNECTION_FIELD = new PreEncodedHttpField(HttpHeader.CONNECTION, "Upgrade");
   private static final PreEncodedHttpField PRAGMA_NO_CACHE_FIELD = new PreEncodedHttpField(HttpHeader.PRAGMA, "no-cache");
   private static final PreEncodedHttpField CACHE_CONTROL_NO_CACHE_FIELD = new PreEncodedHttpField(HttpHeader.CACHE_CONTROL, "no-cache");
   private final CoreClientUpgradeRequest clientUpgradeRequest;

   public HttpUpgraderOverHTTP(CoreClientUpgradeRequest clientUpgradeRequest) {
      this.clientUpgradeRequest = clientUpgradeRequest;
   }

   public void prepare(Request request) {
      request.method(HttpMethod.GET)
         .version(HttpVersion.HTTP_1_1)
         .headers(
            headers -> headers.put(WS_VERSION_FIELD)
                  .put(WS_UPGRADE_FIELD)
                  .put(WS_CONNECTION_FIELD)
                  .put(HttpHeader.SEC_WEBSOCKET_KEY, this.generateRandomKey())
                  .put(PRAGMA_NO_CACHE_FIELD)
                  .put(CACHE_CONTROL_NO_CACHE_FIELD)
         );
      this.clientUpgradeRequest.requestComplete();
   }

   private String generateRandomKey() {
      byte[] bytes = new byte[16];
      ThreadLocalRandom.current().nextBytes(bytes);
      return new String(Base64.getEncoder().encode(bytes), StandardCharsets.US_ASCII);
   }

   public void upgrade(Response response, EndPoint endPoint, Callback callback) {
      Request request = response.getRequest();
      HttpFields requestHeaders = request.getHeaders();
      if (requestHeaders.contains(HttpHeader.UPGRADE, "websocket")) {
         HttpFields responseHeaders = response.getHeaders();
         if (responseHeaders.contains(HttpHeader.CONNECTION, "upgrade")) {
            String reqKey = requestHeaders.get(HttpHeader.SEC_WEBSOCKET_KEY);
            String expectedHash = WebSocketUtils.hashKey(reqKey);
            String respHash = responseHeaders.get(HttpHeader.SEC_WEBSOCKET_ACCEPT);
            if (expectedHash.equalsIgnoreCase(respHash)) {
               this.clientUpgradeRequest.upgrade(response, endPoint);
               callback.succeeded();
            } else {
               callback.failed(new HttpResponseException("Invalid Sec-WebSocket-Accept hash (was: " + respHash + " expected: " + expectedHash + ")", response));
            }
         } else {
            callback.failed(new HttpResponseException("WebSocket upgrade missing 'Connection: Upgrade' header", response));
         }
      } else {
         callback.failed(new HttpResponseException("Not a WebSocket upgrade", response));
      }
   }
}
