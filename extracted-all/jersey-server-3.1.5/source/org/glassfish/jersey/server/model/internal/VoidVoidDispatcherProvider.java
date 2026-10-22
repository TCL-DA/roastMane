package org.glassfish.jersey.server.model.internal;

import jakarta.inject.Singleton;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.Response;
import java.lang.reflect.InvocationHandler;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.ConfiguredValidator;
import org.glassfish.jersey.server.model.Invocable;
import org.glassfish.jersey.server.spi.internal.ResourceMethodDispatcher;

@Singleton
final class VoidVoidDispatcherProvider implements ResourceMethodDispatcher.Provider {
   private final ResourceContext resourceContext;

   VoidVoidDispatcherProvider(ResourceContext resourceContext) {
      this.resourceContext = resourceContext;
   }

   @Override
   public ResourceMethodDispatcher create(Invocable resourceMethod, InvocationHandler handler, ConfiguredValidator validator) {
      return resourceMethod.getHandlingMethod().getReturnType() == void.class && resourceMethod.getParameters().isEmpty()
         ? (ResourceMethodDispatcher)this.resourceContext.initResource(new VoidVoidDispatcherProvider.VoidToVoidDispatcher(resourceMethod, handler, validator))
         : null;
   }

   private static class VoidToVoidDispatcher extends AbstractJavaResourceMethodDispatcher {
      private VoidToVoidDispatcher(Invocable resourceMethod, InvocationHandler handler, ConfiguredValidator validator) {
         super(resourceMethod, handler, validator);
      }

      @Override
      public Response doDispatch(Object resource, ContainerRequest containerRequest) throws ProcessingException {
         this.invoke(containerRequest, resource, new Object[0]);
         return Response.noContent().build();
      }
   }
}
