package org.glassfish.jersey.client.authentication;

import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.core.Response;

public class ResponseAuthenticationException extends ResponseProcessingException {
   public ResponseAuthenticationException(Response response, Throwable cause) {
      super(response, cause);
   }

   public ResponseAuthenticationException(Response response, String message) {
      super(response, message);
   }

   public ResponseAuthenticationException(Response response, String message, Throwable cause) {
      super(response, message, cause);
   }
}
