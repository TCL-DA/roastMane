package jakarta.validation.spi;

import jakarta.validation.Configuration;
import jakarta.validation.ValidatorFactory;

public interface ValidationProvider<T extends Configuration<T>> {
   T createSpecializedConfiguration(BootstrapState var1);

   Configuration<?> createGenericConfiguration(BootstrapState var1);

   ValidatorFactory buildValidatorFactory(ConfigurationState var1);
}
