package jakarta.validation.bootstrap;

import jakarta.validation.Configuration;
import jakarta.validation.ValidationProviderResolver;

public interface GenericBootstrap {
   GenericBootstrap providerResolver(ValidationProviderResolver var1);

   Configuration<?> configure();
}
