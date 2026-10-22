package org.eclipse.jetty.client;

import java.util.List;
import org.eclipse.jetty.client.transport.HttpConversation;
import org.eclipse.jetty.client.transport.HttpRequest;
import org.eclipse.jetty.client.transport.ResponseListeners;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.Callback;

public class UpgradeProtocolHandler implements ProtocolHandler {
   private final List<String> protocols = List.of("websocket", "h2c");

   @Override
   public String getName() {
      return "upgrade";
   }

   @Override
   public boolean accept(Request request, Response response) {
      boolean upgraded = 101 == response.getStatus();
      boolean accepted = false;
      if (upgraded) {
         accepted = this.acceptHeaders(request, response);
      }

      return upgraded && accepted;
   }

   protected boolean acceptHeaders(Request request, Response response) {
      HttpField responseUpgrade = response.getHeaders().getField(HttpHeader.UPGRADE);
      if (responseUpgrade != null && this.protocols.stream().anyMatch(responseUpgrade::contains)) {
         return true;
      } else {
         HttpField requestUpgrade = request.getHeaders().getField(HttpHeader.UPGRADE);
         return requestUpgrade != null && this.protocols.stream().anyMatch(requestUpgrade::contains);
      }
   }

   @Override
   public Response.Listener getResponseListener() {
      return new Response.Listener() {
         @Override
         public void onComplete(Result result) {
            Response response = result.getResponse();
            HttpRequest request = (HttpRequest)response.getRequest();
            if (result.isSucceeded()) {
               try {
                  HttpConversation conversation = request.getConversation();
                  HttpUpgrader upgrader = (HttpUpgrader)conversation.getAttribute(HttpUpgrader.class.getName());
                  if (upgrader == null) {
                     throw new HttpResponseException("101 response without " + HttpUpgrader.class.getSimpleName(), response);
                  }

                  EndPoint endPoint = (EndPoint)conversation.getAttribute(EndPoint.class.getName());
                  if (endPoint == null) {
                     throw new HttpResponseException("Upgrade without " + EndPoint.class.getSimpleName(), response);
                  }

                  upgrader.upgrade(
                     response,
                     endPoint,
                     Callback.from(Callback.NOOP::succeeded, xx -> UpgradeProtocolHandler.this.forwardFailureComplete(request, null, response, xx))
                  );
               } catch (Throwable var7) {
                  UpgradeProtocolHandler.this.forwardFailureComplete(request, null, response, var7);
               }
            } else {
               UpgradeProtocolHandler.this.forwardFailureComplete(request, result.getRequestFailure(), response, result.getResponseFailure());
            }
         }
      };
   }

   private void forwardFailureComplete(HttpRequest request, Throwable requestFailure, Response response, Throwable responseFailure) {
      HttpConversation conversation = request.getConversation();
      conversation.updateResponseListeners(null);
      ResponseListeners responseListeners = conversation.getResponseListeners();
      responseListeners.emitFailureComplete(new Result(request, requestFailure, response, responseFailure));
   }
}
