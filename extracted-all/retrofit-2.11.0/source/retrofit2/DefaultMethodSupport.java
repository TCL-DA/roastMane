package retrofit2;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

final class DefaultMethodSupport {
   @Nullable
   private static Constructor<Lookup> lookupConstructor;

   @Nullable
   @IgnoreJRERequirement
   static Object invoke(Method method, Class<?> declaringClass, Object proxy, @Nullable Object[] args) throws Throwable {
      Constructor<Lookup> constructor = lookupConstructor;
      if (constructor == null) {
         constructor = Lookup.class.getDeclaredConstructor(Class.class, int.class);
         constructor.setAccessible(true);
         lookupConstructor = constructor;
      }

      return constructor.newInstance(declaringClass, -1).unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
   }

   private DefaultMethodSupport() {
   }
}
