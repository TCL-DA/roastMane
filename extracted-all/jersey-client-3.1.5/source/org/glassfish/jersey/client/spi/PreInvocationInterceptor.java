package org.glassfish.jersey.client.spi;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.client.ClientRequestContext;
import org.glassfish.jersey.Beta;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.CLIENT)
@Beta
public interface PreInvocationInterceptor {
   void beforeRequest(ClientRequestContext var1);
}
