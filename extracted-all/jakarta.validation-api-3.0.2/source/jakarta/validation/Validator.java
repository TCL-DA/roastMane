package jakarta.validation;

import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.metadata.BeanDescriptor;
import java.util.Set;

public interface Validator {
   <T> Set<ConstraintViolation<T>> validate(T var1, Class<?>... var2);

   <T> Set<ConstraintViolation<T>> validateProperty(T var1, String var2, Class<?>... var3);

   <T> Set<ConstraintViolation<T>> validateValue(Class<T> var1, String var2, Object var3, Class<?>... var4);

   BeanDescriptor getConstraintsForClass(Class<?> var1);

   <T> T unwrap(Class<T> var1);

   ExecutableValidator forExecutables();
}
