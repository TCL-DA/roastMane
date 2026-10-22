package org.glassfish.jersey.server.spi.internal;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import java.lang.reflect.InvocationHandler;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.ConfiguredValidator;
import org.glassfish.jersey.server.model.Invocable;

public interface ResourceMethodDispatcher {
   Response dispatch(Object var1, ContainerRequest var2) throws ProcessingException;

   public interface Provider {
      ResourceMethodDispatcher create(Invocable var1, InvocationHandler var2, ConfiguredValidator var3);
   }
}
