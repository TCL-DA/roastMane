package org.glassfish.jersey.server.internal.process;

import jakarta.ws.rs.ProcessingException;

public class MappableException extends ProcessingException {
   private static final long serialVersionUID = -7326005523956892754L;

   public MappableException(Throwable cause) {
      super(unwrap(cause));
   }

   public MappableException(String message, Throwable cause) {
      super(message, unwrap(cause));
   }

   private static Throwable unwrap(Throwable cause) {
      if (cause instanceof MappableException) {
         do {
            MappableException mce = (MappableException)cause;
            cause = mce.getCause();
         } while (cause instanceof MappableException);
      }

      return cause;
   }
}
