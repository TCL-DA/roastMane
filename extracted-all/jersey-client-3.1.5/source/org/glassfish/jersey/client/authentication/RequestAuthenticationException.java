package org.glassfish.jersey.client.authentication;

import jakarta.ws.rs.ProcessingException;

public class RequestAuthenticationException extends ProcessingException {
   public RequestAuthenticationException(Throwable cause) {
      super(cause);
   }

   public RequestAuthenticationException(String message) {
      super(message);
   }

   public RequestAuthenticationException(String message, Throwable cause) {
      super(message, cause);
   }
}
