package org.glassfish.jersey.client;

import jakarta.ws.rs.ProcessingException;

class AbortException extends ProcessingException {
   private final transient ClientResponse abortResponse;

   AbortException(ClientResponse abortResponse) {
      super("Request processing has been aborted");
      this.abortResponse = abortResponse;
   }

   public ClientResponse getAbortResponse() {
      return this.abortResponse;
   }
}
