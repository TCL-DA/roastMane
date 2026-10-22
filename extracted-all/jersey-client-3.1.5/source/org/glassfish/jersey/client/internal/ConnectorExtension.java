package org.glassfish.jersey.client.internal;

import org.glassfish.jersey.client.ClientRequest;

public interface ConnectorExtension<T, E extends Exception> {
   void invoke(ClientRequest var1, T var2);

   void postConnectionProcessing(T var1);

   boolean handleException(ClientRequest var1, T var2, E var3) throws E;
}
