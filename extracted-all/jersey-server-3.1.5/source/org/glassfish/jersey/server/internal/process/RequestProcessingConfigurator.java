package org.glassfish.jersey.server.internal.process;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import java.util.function.Supplier;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.SupplierClassBinding;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.AsyncContext;
import org.glassfish.jersey.server.CloseableService;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.internal.routing.UriRoutingContext;

public class RequestProcessingConfigurator implements BootstrapConfigurator {
   public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      injectionManager.register(new RequestProcessingConfigurator.ServerProcessingBinder());
   }

   private static class AsyncContextFactory implements Supplier<AsyncContext> {
      private final RequestProcessingContextReference reference;

      @Inject
      private AsyncContextFactory(RequestProcessingContextReference reference) {
         this.reference = reference;
      }

      public AsyncContext get() {
         return this.reference.get().asyncContext();
      }
   }

   private static class CloseableServiceFactory implements Supplier<CloseableService> {
      private final RequestProcessingContextReference reference;

      @Inject
      private CloseableServiceFactory(RequestProcessingContextReference reference) {
         this.reference = reference;
      }

      public CloseableService get() {
         return this.reference.get().closeableService();
      }
   }

   private static class ContainerRequestFactory implements Supplier<ContainerRequest> {
      private final RequestProcessingContextReference reference;

      @Inject
      private ContainerRequestFactory(RequestProcessingContextReference reference) {
         this.reference = reference;
      }

      public ContainerRequest get() {
         return this.reference.get().request();
      }
   }

   private class ServerProcessingBinder extends AbstractBinder {
      private ServerProcessingBinder() {
      }

      protected void configure() {
         this.bindAsContract(RequestProcessingContextReference.class).in(RequestScoped.class);
         ((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)this.bindFactory(RequestProcessingConfigurator.ContainerRequestFactory.class)
                     .to(ContainerRequest.class))
                  .to(ContainerRequestContext.class))
               .proxy(false))
            .in(RequestScoped.class);
         ((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)this.bindFactory(
                              RequestProcessingConfigurator.ContainerRequestFactory.class
                           )
                           .to(HttpHeaders.class))
                        .to(Request.class))
                     .to(PropertiesDelegate.class))
                  .proxy(true))
               .proxyForSameScope(false))
            .in(RequestScoped.class);
         ((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)this.bindFactory(
                              RequestProcessingConfigurator.UriRoutingContextFactory.class
                           )
                           .to(UriInfo.class))
                        .to(ExtendedUriInfo.class))
                     .to(ResourceInfo.class))
                  .proxy(true))
               .proxyForSameScope(false))
            .in(RequestScoped.class);
         ((ClassBinding)((ClassBinding)((ClassBinding)this.bind(SecurityContextInjectee.class).to(SecurityContext.class)).proxy(true)).proxyForSameScope(false))
            .in(RequestScoped.class);
         ((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)this.bindFactory(RequestProcessingConfigurator.CloseableServiceFactory.class)
                     .to(CloseableService.class))
                  .proxy(true))
               .proxyForSameScope(false))
            .in(RequestScoped.class);
         ((SupplierClassBinding)((SupplierClassBinding)this.bindFactory(RequestProcessingConfigurator.AsyncContextFactory.class).to(AsyncContext.class))
               .to(AsyncResponse.class))
            .in(RequestScoped.class);
      }
   }

   private static class UriRoutingContextFactory implements Supplier<UriRoutingContext> {
      private final RequestProcessingContextReference reference;

      @Inject
      private UriRoutingContextFactory(RequestProcessingContextReference reference) {
         this.reference = reference;
      }

      public UriRoutingContext get() {
         return this.reference.get().uriRoutingContext();
      }
   }
}
