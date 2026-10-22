package org.eclipse.jetty.websocket.core.exception;

import java.net.URI;

public class UpgradeException extends WebSocketException {
   private final URI requestURI;
   private final int responseStatusCode;

   public UpgradeException(URI requestURI, int responseStatusCode, String message) {
      super(message);
      this.requestURI = requestURI;
      this.responseStatusCode = responseStatusCode;
   }

   public UpgradeException(URI requestURI, int responseStatusCode, String message, Throwable cause) {
      super(message, cause);
      this.requestURI = requestURI;
      this.responseStatusCode = responseStatusCode;
   }

   public UpgradeException(URI requestURI, Throwable cause) {
      super(cause);
      this.requestURI = requestURI;
      this.responseStatusCode = -1;
   }

   public URI getRequestURI() {
      return this.requestURI;
   }

   public int getResponseStatusCode() {
      return this.responseStatusCode;
   }
}
