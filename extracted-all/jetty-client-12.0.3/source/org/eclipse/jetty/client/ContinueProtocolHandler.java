package org.eclipse.jetty.client;

import org.eclipse.jetty.client.internal.HttpContentResponse;
import org.eclipse.jetty.client.transport.HttpConversation;
import org.eclipse.jetty.client.transport.HttpExchange;
import org.eclipse.jetty.client.transport.HttpRequest;
import org.eclipse.jetty.client.transport.ResponseListeners;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;

public class ContinueProtocolHandler implements ProtocolHandler {
   public static final String NAME = "continue";
   private static final String ATTRIBUTE = ContinueProtocolHandler.class.getName() + ".100continue";

   @Override
   public String getName() {
      return "continue";
   }

   @Override
   public boolean accept(Request request, Response response) {
      boolean is100 = response.getStatus() == 100;
      boolean expect100 = request.getHeaders().contains(HttpHeader.EXPECT, HttpHeaderValue.CONTINUE.asString());
      boolean handled100 = request.getAttributes().containsKey(ATTRIBUTE);
      return (is100 || expect100) && !handled100;
   }

   @Override
   public Response.Listener getResponseListener() {
      return new ContinueProtocolHandler.ContinueListener();
   }

   protected void onContinue(Request request) {
   }

   protected class ContinueListener extends BufferingResponseListener {
      @Override
      public void onSuccess(Response response) {
         Request request = response.getRequest();
         HttpConversation conversation = ((HttpRequest)request).getConversation();
         request.attribute(ContinueProtocolHandler.ATTRIBUTE, Boolean.TRUE);
         conversation.updateResponseListeners(null);
         HttpExchange exchange = conversation.getExchanges().peekLast();

         assert exchange != null;

         if (response.getStatus() == 100) {
            exchange.resetResponse();
            exchange.proceed(null);
            ContinueProtocolHandler.this.onContinue(request);
         } else {
            ResponseListeners listeners = exchange.getResponseListeners();
            HttpContentResponse contentResponse = new HttpContentResponse(response, this.getContent(), this.getMediaType(), this.getEncoding());
            listeners.emitSuccess(contentResponse);
            exchange.proceed(new HttpRequestException("Expectation failed", request));
         }
      }

      @Override
      public void onFailure(Response response, Throwable failure) {
         HttpConversation conversation = ((HttpRequest)response.getRequest()).getConversation();
         conversation.setAttribute(ContinueProtocolHandler.ATTRIBUTE, Boolean.TRUE);
         conversation.updateResponseListeners(null);
         HttpExchange exchange = conversation.getExchanges().peekLast();

         assert exchange.getResponse() == response;

         ResponseListeners listeners = exchange.getResponseListeners();
         HttpContentResponse contentResponse = new HttpContentResponse(response, this.getContent(), this.getMediaType(), this.getEncoding());
         listeners.emitFailureComplete(new Result(exchange.getRequest(), exchange.getRequestFailure(), contentResponse, failure));
      }

      @Override
      public void onComplete(Result result) {
      }
   }
}
