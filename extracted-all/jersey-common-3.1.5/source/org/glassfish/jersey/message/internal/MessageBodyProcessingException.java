package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.ProcessingException;

public class MessageBodyProcessingException extends ProcessingException {
   private static final long serialVersionUID = 2093175681702118380L;

   public MessageBodyProcessingException(Throwable cause) {
      super(cause);
   }

   public MessageBodyProcessingException(String message, Throwable cause) {
      super(message, cause);
   }

   public MessageBodyProcessingException(String message) {
      super(message);
   }
}
