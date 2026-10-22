package org.glassfish.jersey.server.internal.scanning;

public class ResourceFinderException extends RuntimeException {
   public ResourceFinderException() {
   }

   public ResourceFinderException(String message) {
      super(message);
   }

   public ResourceFinderException(String message, Throwable cause) {
      super(message, cause);
   }

   public ResourceFinderException(Throwable cause) {
      super(cause);
   }
}
