package org.glassfish.jersey.server;

import jakarta.ws.rs.ProcessingException;

public class ContainerException extends ProcessingException {
   private static final long serialVersionUID = -1721209891860592440L;

   public ContainerException(Throwable cause) {
      super(cause);
   }

   public ContainerException(String message, Throwable cause) {
      super(message, cause);
   }

   public ContainerException(String message) {
      super(message);
   }
}
