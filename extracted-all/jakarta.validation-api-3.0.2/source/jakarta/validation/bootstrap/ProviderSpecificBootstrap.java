package jakarta.validation.bootstrap;

import jakarta.validation.Configuration;
import jakarta.validation.ValidationProviderResolver;

public interface ProviderSpecificBootstrap<T extends Configuration<T>> {
   ProviderSpecificBootstrap<T> providerResolver(ValidationProviderResolver var1);

   T configure();
}
