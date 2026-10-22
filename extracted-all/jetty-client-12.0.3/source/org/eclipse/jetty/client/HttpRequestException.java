package org.eclipse.jetty.client;

public class HttpRequestException extends RuntimeException {
   private final Request request;

   public HttpRequestException(String message, Request request) {
      super(message);
      this.request = request;
   }

   public Request getRequest() {
      return this.request;
   }
}
