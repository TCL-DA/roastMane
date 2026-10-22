package org.glassfish.jersey.internal.inject;

import jakarta.ws.rs.ProcessingException;

public class ExtractorException extends ProcessingException {
   private static final long serialVersionUID = -4918023257104413981L;

   public ExtractorException(String message) {
      super(message);
   }

   public ExtractorException(String message, Throwable cause) {
      super(message, cause);
   }

   public ExtractorException(Throwable cause) {
      super(cause);
   }
}
