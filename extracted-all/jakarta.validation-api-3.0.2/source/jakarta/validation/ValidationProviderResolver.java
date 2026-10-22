package jakarta.validation;

import jakarta.validation.spi.ValidationProvider;
import java.util.List;

public interface ValidationProviderResolver {
   List<ValidationProvider<?>> getValidationProviders();
}
