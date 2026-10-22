package jakarta.validation.executable;

import jakarta.validation.ConstraintViolation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;

public interface ExecutableValidator {
   <T> Set<ConstraintViolation<T>> validateParameters(T var1, Method var2, Object[] var3, Class<?>... var4);

   <T> Set<ConstraintViolation<T>> validateReturnValue(T var1, Method var2, Object var3, Class<?>... var4);

   <T> Set<ConstraintViolation<T>> validateConstructorParameters(Constructor<? extends T> var1, Object[] var2, Class<?>... var3);

   <T> Set<ConstraintViolation<T>> validateConstructorReturnValue(Constructor<? extends T> var1, T var2, Class<?>... var3);
}
