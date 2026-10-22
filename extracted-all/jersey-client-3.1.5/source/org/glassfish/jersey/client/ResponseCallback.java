package org.glassfish.jersey.client;

import jakarta.ws.rs.ProcessingException;
import org.glassfish.jersey.process.internal.RequestScope;

interface ResponseCallback {
   void completed(ClientResponse var1, RequestScope var2);

   void failed(ProcessingException var1);
}
