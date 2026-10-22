package org.glassfish.jersey.internal.inject;

import jakarta.ws.rs.ProcessingException;

public class UpdaterException extends ProcessingException {
   private static final long serialVersionUID = -4918023257104413981L;

   public UpdaterException(String message) {
      super(message);
   }

   public UpdaterException(String message, Throwable cause) {
      super(message, cause);
   }

   public UpdaterException(Throwable cause) {
      super(cause);
   }
}
