package org.glassfish.jersey.client.internal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.innate.Expect100ContinueUsage;

class HttpUrlExpect100ContinueConnectorExtension implements ConnectorExtension<HttpURLConnection, IOException> {
   private static final String EXCEPTION_MESSAGE = "Server rejected operation";

   public void invoke(ClientRequest request, HttpURLConnection uc) {
      if (Expect100ContinueUsage.isAllowed(request, uc.getRequestMethod())) {
         uc.setRequestProperty("Expect", "100-Continue");
      }
   }

   public void postConnectionProcessing(HttpURLConnection extensionParam) {
   }

   public boolean handleException(ClientRequest request, HttpURLConnection extensionParam, IOException ex) {
      Boolean expectContinueActivated = request.resolveProperty("jersey.config.client.request.expect.100.continue.processing", Boolean.FALSE);
      return expectContinueActivated && ex instanceof ProtocolException && ex.getMessage().equals("Server rejected operation");
   }
}
