package org.glassfish.jersey.client.internal;

import jakarta.ws.rs.ProcessingException;
import org.glassfish.jersey.client.ClientResponse;

public class ClientResponseProcessingException extends ProcessingException {
   private static final long serialVersionUID = 3389677946623416847L;
   private final ClientResponse clientResponse;

   public ClientResponseProcessingException(ClientResponse clientResponse, Throwable cause) {
      super(cause);
      this.clientResponse = clientResponse;
   }

   public ClientResponse getClientResponse() {
      return this.clientResponse;
   }
}
