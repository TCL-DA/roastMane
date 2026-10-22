package jakarta.validation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public interface ParameterNameProvider {
   List<String> getParameterNames(Constructor<?> var1);

   List<String> getParameterNames(Method var1);
}
