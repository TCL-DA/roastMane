package org.glassfish.jersey.server.internal.process;

import jakarta.inject.Provider;
import java.util.function.Function;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.spi.RequestScopedInitializer;

public final class ReferencesInitializer implements Function<RequestProcessingContext, RequestProcessingContext> {
   private final InjectionManager injectionManager;
   private final Provider<RequestProcessingContextReference> processingContextRefProvider;

   public ReferencesInitializer(InjectionManager injectionManager, Provider<RequestProcessingContextReference> processingContextRefProvider) {
      this.injectionManager = injectionManager;
      this.processingContextRefProvider = processingContextRefProvider;
   }

   public RequestProcessingContext apply(RequestProcessingContext context) {
      ((RequestProcessingContextReference)this.processingContextRefProvider.get()).set(context);
      RequestScopedInitializer requestScopedInitializer = context.request().getRequestScopedInitializer();
      if (requestScopedInitializer != null) {
         requestScopedInitializer.initialize(this.injectionManager);
      }

      return context;
   }
}
