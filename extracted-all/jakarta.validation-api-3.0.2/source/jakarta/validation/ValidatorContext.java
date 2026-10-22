package jakarta.validation;

import jakarta.validation.valueextraction.ValueExtractor;

public interface ValidatorContext {
   ValidatorContext messageInterpolator(MessageInterpolator var1);

   ValidatorContext traversableResolver(TraversableResolver var1);

   ValidatorContext constraintValidatorFactory(ConstraintValidatorFactory var1);

   ValidatorContext parameterNameProvider(ParameterNameProvider var1);

   ValidatorContext clockProvider(ClockProvider var1);

   ValidatorContext addValueExtractor(ValueExtractor<?> var1);

   Validator getValidator();
}
