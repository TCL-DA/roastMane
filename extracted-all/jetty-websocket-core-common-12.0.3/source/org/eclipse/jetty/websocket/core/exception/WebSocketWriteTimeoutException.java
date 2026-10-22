package org.eclipse.jetty.websocket.core.exception;

public class WebSocketWriteTimeoutException extends WebSocketTimeoutException {
   public WebSocketWriteTimeoutException(String message) {
      super(message);
   }
}
