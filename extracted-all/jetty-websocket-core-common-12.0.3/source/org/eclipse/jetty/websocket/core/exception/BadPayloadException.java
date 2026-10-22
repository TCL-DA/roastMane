package org.eclipse.jetty.websocket.core.exception;

import org.eclipse.jetty.util.Utf8StringBuilder.Utf8CharacterCodingException;

public class BadPayloadException extends CloseException {
   public BadPayloadException(String message) {
      super(1007, message);
   }

   public BadPayloadException(String message, Throwable t) {
      super(1007, message, t);
   }

   public BadPayloadException(Throwable t) {
      super(1007, t);
   }

   public static class InvalidUtf8 extends BadPayloadException {
      public InvalidUtf8() {
         super("Invalid UTF-8", new Utf8CharacterCodingException());
      }
   }
}
