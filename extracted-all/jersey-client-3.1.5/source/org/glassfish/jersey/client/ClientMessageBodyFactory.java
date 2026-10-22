package org.glassfish.jersey.client;

import jakarta.ws.rs.core.Configuration;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.message.internal.MessageBodyFactory;

class ClientMessageBodyFactory extends MessageBodyFactory {
   private final LazyValue<ClientRuntime> clientRuntime;

   private ClientMessageBodyFactory(Configuration configuration, Value<ClientRuntime> clientRuntimeValue) {
      super(configuration);
      this.clientRuntime = Values.lazy(clientRuntimeValue);
   }

   ClientRuntime getClientRuntime() {
      return (ClientRuntime)this.clientRuntime.get();
   }

   static class MessageBodyWorkersConfigurator implements BootstrapConfigurator {
      private ClientMessageBodyFactory messageBodyFactory;
      private ClientRuntime clientRuntime;

      public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
         this.messageBodyFactory = new ClientMessageBodyFactory(bootstrapBag.getConfiguration(), () -> this.clientRuntime);
         InstanceBinding<ClientMessageBodyFactory> binding = (InstanceBinding<ClientMessageBodyFactory>)Bindings.service(this.messageBodyFactory)
            .to(MessageBodyWorkers.class);
         injectionManager.register(binding);
      }

      public void postInit(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
         this.messageBodyFactory.initialize(injectionManager);
         bootstrapBag.setMessageBodyWorkers(this.messageBodyFactory);
      }

      void setClientRuntime(ClientRuntime clientRuntime) {
         this.clientRuntime = clientRuntime;
      }
   }
}
