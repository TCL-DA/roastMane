package retrofit2;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import javax.annotation.Nullable;

abstract class ServiceMethod<T> {
   static <T> ServiceMethod<T> parseAnnotations(Retrofit retrofit, Class<?> service, Method method) {
      RequestFactory requestFactory = RequestFactory.parseAnnotations(retrofit, service, method);
      Type returnType = method.getGenericReturnType();
      if (Utils.hasUnresolvableType(returnType)) {
         throw Utils.methodError(method, "Method return type must not include a type variable or wildcard: %s", returnType);
      } else if (returnType == void.class) {
         throw Utils.methodError(method, "Service methods cannot return void.");
      } else {
         return HttpServiceMethod.parseAnnotations(retrofit, method, requestFactory);
      }
   }

   @Nullable
   abstract T invoke(Object var1, Object[] var2);
}
