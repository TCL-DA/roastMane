package jakarta.validation.spi;

import jakarta.validation.ValidationProviderResolver;

public interface BootstrapState {
   ValidationProviderResolver getValidationProviderResolver();

   ValidationProviderResolver getDefaultValidationProviderResolver();
}
