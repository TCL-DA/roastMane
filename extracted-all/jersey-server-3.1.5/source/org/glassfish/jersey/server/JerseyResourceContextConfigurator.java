package org.glassfish.jersey.server;

import jakarta.ws.rs.container.ResourceContext;
import java.util.function.Consumer;
import java.util.function.Function;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.server.internal.JerseyResourceContext;

class JerseyResourceContextConfigurator implements BootstrapConfigurator {
   public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
      Consumer<Binding> registerBinding = injectionManager::register;
      Function<Class<?>, ?> getOrCreateInstance = clazz -> Injections.getOrCreate(injectionManager, clazz);
      Consumer<Object> injectInstance = injectionManager::inject;
      JerseyResourceContext resourceContext = new JerseyResourceContext(getOrCreateInstance, injectInstance, registerBinding);
      InstanceBinding<JerseyResourceContext> resourceContextBinding = (InstanceBinding<JerseyResourceContext>)((InstanceBinding)Bindings.service(
               resourceContext
            )
            .to(ResourceContext.class))
         .to(ExtendedResourceContext.class);
      injectionManager.register(resourceContextBinding);
      serverBag.setResourceContext(resourceContext);
   }

   public void postInit(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
   }
}
