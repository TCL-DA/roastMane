package org.glassfish.jersey.client;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import org.glassfish.jersey.InjectionManagerProvider;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.InjectionManagerSupplier;

public class InjectionManagerClientProvider extends InjectionManagerProvider {
   public static InjectionManager getInjectionManager(ClientRequestContext clientRequestContext) {
      if (!(clientRequestContext instanceof InjectionManagerSupplier)) {
         throw new IllegalArgumentException(LocalizationMessages.ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_REQUEST(clientRequestContext.getClass().getName()));
      } else {
         return ((InjectionManagerSupplier)clientRequestContext).getInjectionManager();
      }
   }

   public static InjectionManager getInjectionManager(ClientResponseContext clientResponseContext) {
      if (!(clientResponseContext instanceof InjectionManagerSupplier)) {
         throw new IllegalArgumentException(LocalizationMessages.ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_RESPONSE(clientResponseContext.getClass().getName()));
      } else {
         return ((InjectionManagerSupplier)clientResponseContext).getInjectionManager();
      }
   }
}
