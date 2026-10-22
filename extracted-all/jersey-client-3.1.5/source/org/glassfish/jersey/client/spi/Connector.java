package org.glassfish.jersey.client.spi;

import java.util.concurrent.Future;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.process.Inflector;

public interface Connector extends Inflector<ClientRequest, ClientResponse> {
   ClientResponse apply(ClientRequest var1);

   Future<?> apply(ClientRequest var1, AsyncConnectorCallback var2);

   String getName();

   void close();
}
