package jakarta.validation;

public interface ValidatorFactory extends AutoCloseable {
   Validator getValidator();

   ValidatorContext usingContext();

   MessageInterpolator getMessageInterpolator();

   TraversableResolver getTraversableResolver();

   ConstraintValidatorFactory getConstraintValidatorFactory();

   ParameterNameProvider getParameterNameProvider();

   ClockProvider getClockProvider();

   <T> T unwrap(Class<T> var1);

   @Override
   void close();
}
