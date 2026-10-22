package retrofit2;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import javax.annotation.Nullable;

final class DefaultMethodSupport {
   @Nullable
   static Object invoke(Method method, Class<?> declaringClass, Object proxy, @Nullable Object[] args) throws Throwable {
      return MethodHandles.lookup().unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
   }

   private DefaultMethodSupport() {
   }
}
