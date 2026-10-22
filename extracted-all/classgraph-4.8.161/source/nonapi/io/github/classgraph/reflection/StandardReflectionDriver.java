package nonapi.io.github.classgraph.reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

class StandardReflectionDriver extends ReflectionDriver {
   private static Method setAccessibleMethod;
   private static Method trySetAccessibleMethod;
   private static Class<?> accessControllerClass;
   private static Class<?> privilegedActionClass;
   private static Method accessControllerDoPrivileged;

   private <T> T doPrivileged(Callable<T> callable) throws Throwable {
      if (accessControllerDoPrivileged != null) {
         Object privilegedAction = Proxy.newProxyInstance(
            privilegedActionClass.getClassLoader(),
            new Class[]{privilegedActionClass},
            new StandardReflectionDriver.PrivilegedActionInvocationHandler<>(callable)
         );
         return (T)accessControllerDoPrivileged.invoke(null, privilegedAction);
      } else {
         return callable.call();
      }
   }

   private static boolean tryMakeAccessible(AccessibleObject obj) {
      if (trySetAccessibleMethod != null) {
         try {
            return (Boolean)trySetAccessibleMethod.invoke(obj);
         } catch (Throwable var3) {
         }
      }

      if (setAccessibleMethod != null) {
         try {
            setAccessibleMethod.invoke(obj, true);
            return true;
         } catch (Throwable var2) {
         }
      }

      return false;
   }

   @Override
   public boolean makeAccessible(Object instance, final AccessibleObject obj) {
      if (this.isAccessible(instance, obj)) {
         return true;
      } else {
         try {
            return this.doPrivileged(new Callable<Boolean>() {
               public Boolean call() throws Exception {
                  return StandardReflectionDriver.tryMakeAccessible(obj);
               }
            });
         } catch (Throwable var4) {
            return tryMakeAccessible(obj);
         }
      }
   }

   @Override
   Class<?> findClass(String className) throws Exception {
      return Class.forName(className);
   }

   @Override
   Method[] getDeclaredMethods(Class<?> cls) throws Exception {
      return cls.getDeclaredMethods();
   }

   @Override
   <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls) throws Exception {
      return (Constructor<T>[])cls.getDeclaredConstructors();
   }

   @Override
   Field[] getDeclaredFields(Class<?> cls) throws Exception {
      return cls.getDeclaredFields();
   }

   @Override
   Object getField(Object object, Field field) throws Exception {
      this.makeAccessible(object, field);
      return field.get(object);
   }

   @Override
   void setField(Object object, Field field, Object value) throws Exception {
      this.makeAccessible(object, field);
      field.set(object, value);
   }

   @Override
   Object getStaticField(Field field) throws Exception {
      this.makeAccessible(null, field);
      return field.get(null);
   }

   @Override
   void setStaticField(Field field, Object value) throws Exception {
      this.makeAccessible(null, field);
      field.set(null, value);
   }

   @Override
   Object invokeMethod(Object object, Method method, Object... args) throws Exception {
      this.makeAccessible(object, method);
      return method.invoke(object, args);
   }

   @Override
   Object invokeStaticMethod(Method method, Object... args) throws Exception {
      this.makeAccessible(null, method);
      return method.invoke(null, args);
   }

   static {
      try {
         setAccessibleMethod = AccessibleObject.class.getDeclaredMethod("setAccessible", boolean.class);
      } catch (Throwable var3) {
      }

      try {
         trySetAccessibleMethod = AccessibleObject.class.getDeclaredMethod("trySetAccessible");
      } catch (Throwable var2) {
      }

      try {
         accessControllerClass = Class.forName("java.security.AccessController");
         privilegedActionClass = Class.forName("java.security.PrivilegedAction");
         accessControllerDoPrivileged = accessControllerClass.getMethod("doPrivileged", privilegedActionClass);
      } catch (Throwable var1) {
      }
   }

   private class PrivilegedActionInvocationHandler<T> implements InvocationHandler {
      private final Callable<T> callable;

      public PrivilegedActionInvocationHandler(Callable<T> callable) {
         this.callable = callable;
      }

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         return this.callable.call();
      }
   }
}
