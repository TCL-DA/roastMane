package org.eclipse.jetty.websocket.api.exceptions;

public class InvalidWebSocketException extends WebSocketException {
   public InvalidWebSocketException(String message) {
      super(message);
   }

   public InvalidWebSocketException(String message, Throwable cause) {
      super(message, cause);
   }
}
