package org.eclipse.jetty.websocket.core.exception;

public class WebSocketException extends RuntimeException {
   public WebSocketException() {
   }

   public WebSocketException(String message) {
      super(message);
   }

   public WebSocketException(String message, Throwable cause) {
      super(message, cause);
   }

   public WebSocketException(Throwable cause) {
      super(cause);
   }
}
