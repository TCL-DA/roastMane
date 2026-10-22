package org.glassfish.jersey.server.model.internal;

import jakarta.inject.Singleton;
import java.lang.reflect.InvocationHandler;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.Invocable;
import org.glassfish.jersey.server.spi.internal.ResourceMethodInvocationHandlerProvider;

@Singleton
public final class ResourceMethodInvocationHandlerFactory implements ResourceMethodInvocationHandlerProvider {
   private static final InvocationHandler DEFAULT_HANDLER = (target, method, args) -> method.invoke(target, args);
   private static final Logger LOGGER = Logger.getLogger(ResourceMethodInvocationHandlerFactory.class.getName());
   private final LazyValue<Set<ResourceMethodInvocationHandlerProvider>> providers;

   ResourceMethodInvocationHandlerFactory(InjectionManager injectionManager) {
      this.providers = Values.lazy(() -> Providers.getProviders(injectionManager, ResourceMethodInvocationHandlerProvider.class));
   }

   @Override
   public InvocationHandler create(Invocable resourceMethod) {
      for (ResourceMethodInvocationHandlerProvider provider : (Set)this.providers.get()) {
         try {
            InvocationHandler handler = provider.create(resourceMethod);
            if (handler != null) {
               return handler;
            }
         } catch (Exception var5) {
            LOGGER.log(Level.SEVERE, LocalizationMessages.ERROR_PROCESSING_METHOD(resourceMethod, provider.getClass().getName()), (Throwable)var5);
         }
      }

      return DEFAULT_HANDLER;
   }
}
