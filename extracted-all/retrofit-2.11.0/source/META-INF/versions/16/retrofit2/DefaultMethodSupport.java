package retrofit2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import javax.annotation.Nullable;

final class DefaultMethodSupport {
   @Nullable
   static Object invoke(Method method, Class<?> declaringClass, Object proxy, @Nullable Object[] args) throws Throwable {
      return InvocationHandler.invokeDefault(proxy, method, args);
   }

   private DefaultMethodSupport() {
   }
}
