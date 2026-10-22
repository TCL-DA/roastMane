package jakarta.validation;

import jakarta.validation.executable.ExecutableType;
import java.util.Map;
import java.util.Set;

public interface BootstrapConfiguration {
   String getDefaultProviderClassName();

   String getConstraintValidatorFactoryClassName();

   String getMessageInterpolatorClassName();

   String getTraversableResolverClassName();

   String getParameterNameProviderClassName();

   String getClockProviderClassName();

   Set<String> getValueExtractorClassNames();

   Set<String> getConstraintMappingResourcePaths();

   boolean isExecutableValidationEnabled();

   Set<ExecutableType> getDefaultValidatedExecutableTypes();

   Map<String, String> getProperties();
}
