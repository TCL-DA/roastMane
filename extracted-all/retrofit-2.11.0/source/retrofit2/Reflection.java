package retrofit2;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import javax.annotation.Nullable;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

class Reflection {
   boolean isDefaultMethod(Method method) {
      return false;
   }

   @Nullable
   Object invokeDefaultMethod(Method method, Class<?> declaringClass, Object proxy, @Nullable Object[] args) throws Throwable {
      throw new AssertionError();
   }

   String describeMethodParameter(Method method, int index) {
      return "parameter #" + (index + 1);
   }

   @TargetApi(24)
   @IgnoreJRERequirement
   static final class Android24 extends Reflection {
      @Override
      boolean isDefaultMethod(Method method) {
         return method.isDefault();
      }

      @Override
      Object invokeDefaultMethod(Method method, Class<?> declaringClass, Object proxy, @Nullable Object[] args) throws Throwable {
         if (VERSION.SDK_INT < 26) {
            throw new UnsupportedOperationException("Calling default methods on API 24 and 25 is not supported");
         } else {
            return DefaultMethodSupport.invoke(method, declaringClass, proxy, args);
         }
      }
   }

   @IgnoreJRERequirement
   static class Java8 extends Reflection {
      @Override
      boolean isDefaultMethod(Method method) {
         return method.isDefault();
      }

      @Override
      Object invokeDefaultMethod(Method method, Class<?> declaringClass, Object proxy, @Nullable Object[] args) throws Throwable {
         return DefaultMethodSupport.invoke(method, declaringClass, proxy, args);
      }

      @Override
      String describeMethodParameter(Method method, int index) {
         Parameter parameter = method.getParameters()[index];
         return parameter.isNamePresent() ? "parameter '" + parameter.getName() + '\'' : super.describeMethodParameter(method, index);
      }
   }
}
