package org.glassfish.jersey.client.spi;

import org.glassfish.jersey.client.ClientResponse;

public interface AsyncConnectorCallback {
   void response(ClientResponse var1);

   void failure(Throwable var1);
}
