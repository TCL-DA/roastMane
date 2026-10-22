package org.eclipse.jetty.client;

import org.eclipse.jetty.client.internal.HttpContentResponse;
import org.eclipse.jetty.client.transport.HttpConversation;
import org.eclipse.jetty.client.transport.HttpExchange;
import org.eclipse.jetty.client.transport.HttpRequest;
import org.eclipse.jetty.client.transport.ResponseListeners;
import org.eclipse.jetty.http.HttpFields;

public class ProcessingProtocolHandler implements ProtocolHandler {
   public static final String NAME = "processing";

   @Override
   public String getName() {
      return "processing";
   }

   @Override
   public boolean accept(Request request, Response response) {
      return response.getStatus() == 102;
   }

   @Override
   public Response.Listener getResponseListener() {
      return new ProcessingProtocolHandler.ProcessingListener();
   }

   protected void onProcessing(Request request, HttpFields responseHeaders) {
   }

   private class ProcessingListener extends BufferingResponseListener {
      @Override
      public void onSuccess(Response response) {
         Request request = response.getRequest();
         HttpConversation conversation = ((HttpRequest)request).getConversation();
         conversation.updateResponseListeners(null);
         HttpExchange exchange = conversation.getExchanges().peekLast();

         assert exchange != null;

         HttpFields responseHeaders = HttpFields.build(response.getHeaders());
         exchange.resetResponse();
         ProcessingProtocolHandler.this.onProcessing(request, responseHeaders);
      }

      @Override
      public void onFailure(Response response, Throwable failure) {
         HttpConversation conversation = ((HttpRequest)response.getRequest()).getConversation();
         conversation.updateResponseListeners(null);
         HttpExchange exchange = conversation.getExchanges().peekLast();
         if (exchange != null) {
            ResponseListeners listeners = exchange.getResponseListeners();
            HttpContentResponse contentResponse = new HttpContentResponse(response, this.getContent(), this.getMediaType(), this.getEncoding());
            listeners.emitFailureComplete(new Result(exchange.getRequest(), exchange.getRequestFailure(), contentResponse, failure));
         }
      }

      @Override
      public void onComplete(Result result) {
      }
   }
}
