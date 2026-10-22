package org.glassfish.jersey.server.spi;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface ContainerProvider {
   <T> T createContainer(Class<T> var1, Application var2) throws ProcessingException;
}
