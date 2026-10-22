package org.glassfish.jersey.client;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.MessageBodyReader;
import java.util.Map;
import java.util.function.Supplier;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.inject.ReferencingFactory;
import org.glassfish.jersey.internal.inject.SupplierClassBinding;
import org.glassfish.jersey.internal.inject.SupplierInstanceBinding;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.message.internal.MessagingBinders.HeaderDelegateProviders;
import org.glassfish.jersey.message.internal.MessagingBinders.MessageBodyProviders;
import org.glassfish.jersey.process.internal.RequestScoped;

class ClientBinder extends AbstractBinder {
   private final Map<String, Object> clientRuntimeProperties;

   ClientBinder(Map<String, Object> clientRuntimeProperties) {
      this.clientRuntimeProperties = clientRuntimeProperties;
   }

   protected void configure() {
      this.install(new AbstractBinder[]{new MessageBodyProviders(this.clientRuntimeProperties, RuntimeType.CLIENT), new HeaderDelegateProviders()});
      ((SupplierInstanceBinding)this.bindFactory(ReferencingFactory.referenceFactory()).to(new GenericType<Ref<ClientConfig>>() {
      })).in(RequestScoped.class);
      ((SupplierClassBinding)this.bindFactory(ClientBinder.RequestContextInjectionFactory.class).to(ClientRequest.class)).in(RequestScoped.class);
      ((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)this.bindFactory(ClientBinder.RequestContextInjectionFactory.class)
                  .to(HttpHeaders.class))
               .proxy(true))
            .proxyForSameScope(false))
         .in(RequestScoped.class);
      ((SupplierInstanceBinding)this.bindFactory(ReferencingFactory.referenceFactory()).to(new GenericType<Ref<ClientRequest>>() {
      })).in(RequestScoped.class);
      ((SupplierClassBinding)this.bindFactory(ClientBinder.PropertiesDelegateFactory.class, Singleton.class).to(PropertiesDelegate.class))
         .in(RequestScoped.class);
      ((ClassBinding)this.bind(ChunkedInputReader.class).to(MessageBodyReader.class)).in(Singleton.class);
   }

   private static class PropertiesDelegateFactory implements Supplier<PropertiesDelegate> {
      private final Provider<ClientRequest> requestProvider;

      @Inject
      private PropertiesDelegateFactory(Provider<ClientRequest> requestProvider) {
         this.requestProvider = requestProvider;
      }

      public PropertiesDelegate get() {
         return ((ClientRequest)this.requestProvider.get()).getPropertiesDelegate();
      }
   }

   private static class RequestContextInjectionFactory extends ReferencingFactory<ClientRequest> {
      @Inject
      public RequestContextInjectionFactory(Provider<Ref<ClientRequest>> referenceFactory) {
         super(referenceFactory);
      }
   }
}
