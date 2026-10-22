package jakarta.validation;

import jakarta.validation.valueextraction.ValueExtractor;
import java.io.InputStream;

public interface Configuration<T extends Configuration<T>> {
   T ignoreXmlConfiguration();

   T messageInterpolator(MessageInterpolator var1);

   T traversableResolver(TraversableResolver var1);

   T constraintValidatorFactory(ConstraintValidatorFactory var1);

   T parameterNameProvider(ParameterNameProvider var1);

   T clockProvider(ClockProvider var1);

   T addValueExtractor(ValueExtractor<?> var1);

   T addMapping(InputStream var1);

   T addProperty(String var1, String var2);

   MessageInterpolator getDefaultMessageInterpolator();

   TraversableResolver getDefaultTraversableResolver();

   ConstraintValidatorFactory getDefaultConstraintValidatorFactory();

   ParameterNameProvider getDefaultParameterNameProvider();

   ClockProvider getDefaultClockProvider();

   BootstrapConfiguration getBootstrapConfiguration();

   ValidatorFactory buildValidatorFactory();
}
