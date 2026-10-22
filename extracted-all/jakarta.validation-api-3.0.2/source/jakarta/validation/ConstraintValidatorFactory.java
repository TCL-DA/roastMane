package jakarta.validation;

public interface ConstraintValidatorFactory {
   <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> var1);

   void releaseInstance(ConstraintValidator<?, ?> var1);
}
