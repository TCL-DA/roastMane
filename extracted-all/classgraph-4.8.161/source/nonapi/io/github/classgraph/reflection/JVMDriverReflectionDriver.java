package nonapi.io.github.classgraph.reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

class JVMDriverReflectionDriver extends ReflectionDriver {
   private Object driver;
   private final Method getDeclaredMethods;
   private final Method getDeclaredConstructors;
   private final Method getDeclaredFields;
   private final Method getField;
   private final Method setField;
   private final Method invokeMethod;
   private final Method setAccessibleMethod;
   private JVMDriverReflectionDriver.ClassFinder classFinder;

   JVMDriverReflectionDriver() throws Exception {
      StandardReflectionDriver drv = new StandardReflectionDriver();
      Class<?> driverClass = drv.findClass("io.github.toolfactory.jvm.DefaultDriver");

      for (Constructor<?> constructor : drv.getDeclaredConstructors(driverClass)) {
         if (constructor.getParameterTypes().length == 0) {
            this.driver = constructor.newInstance();
            break;
         }
      }

      if (this.driver == null) {
         throw new IllegalArgumentException("Could not instantiate jvm.DefaultDriver");
      } else {
         this.getDeclaredMethods = drv.findInstanceMethod(this.driver, "getDeclaredMethods", new Class[]{Class.class});
         this.getDeclaredConstructors = drv.findInstanceMethod(this.driver, "getDeclaredConstructors", new Class[]{Class.class});
         this.getDeclaredFields = drv.findInstanceMethod(this.driver, "getDeclaredFields", new Class[]{Class.class});
         this.getField = drv.findInstanceMethod(this.driver, "getFieldValue", new Class[]{Object.class, Field.class});
         this.setField = drv.findInstanceMethod(this.driver, "setFieldValue", new Class[]{Object.class, Field.class, Object.class});
         this.invokeMethod = drv.findInstanceMethod(this.driver, "invoke", new Class[]{Object.class, Method.class, Object[].class});
         this.setAccessibleMethod = drv.findInstanceMethod(this.driver, "setAccessible", new Class[]{AccessibleObject.class, boolean.class});

         try {
            final Method forName0_method = this.findStaticMethod(Class.class, "forName0", new Class[]{String.class, boolean.class, ClassLoader.class});
            this.classFinder = new JVMDriverReflectionDriver.ClassFinder() {
               @Override
               public Class<?> findClass(String className) throws Exception {
                  return (Class<?>)forName0_method.invoke(null, className, true, Thread.currentThread().getContextClassLoader());
               }
            };
         } catch (Throwable var9) {
         }

         if (this.classFinder == null) {
            try {
               final Method forName0_method = this.findStaticMethod(
                  Class.class, "forName0", new Class[]{String.class, boolean.class, ClassLoader.class, Class.class}
               );
               this.classFinder = new JVMDriverReflectionDriver.ClassFinder() {
                  @Override
                  public Class<?> findClass(String className) throws Exception {
                     return (Class<?>)forName0_method.invoke(
                        null, className, true, Thread.currentThread().getContextClassLoader(), JVMDriverReflectionDriver.class
                     );
                  }
               };
            } catch (Throwable var8) {
            }
         }

         if (this.classFinder == null) {
            try {
               final Method forNameImpl_method = this.findStaticMethod(Class.class, "forNameImpl", new Class[]{String.class, boolean.class, ClassLoader.class});
               this.classFinder = new JVMDriverReflectionDriver.ClassFinder() {
                  @Override
                  public Class<?> findClass(String className) throws Exception {
                     return (Class<?>)forNameImpl_method.invoke(null, className, true, Thread.currentThread().getContextClassLoader());
                  }
               };
            } catch (Throwable var7) {
            }
         }

         if (this.classFinder == null) {
            final Method forName_method = this.findStaticMethod(Class.class, "forName", new Class[]{String.class});
            this.classFinder = new JVMDriverReflectionDriver.ClassFinder() {
               @Override
               public Class<?> findClass(String className) throws Exception {
                  return (Class<?>)forName_method.invoke(null, className);
               }
            };
         }
      }
   }

   @Override
   public boolean makeAccessible(Object instance, AccessibleObject accessibleObject) {
      try {
         this.setAccessibleMethod.invoke(this.driver, accessibleObject, true);
         return true;
      } catch (Throwable var4) {
         return false;
      }
   }

   @Override
   Class<?> findClass(String className) throws Exception {
      return this.classFinder.findClass(className);
   }

   @Override
   Method[] getDeclaredMethods(Class<?> cls) throws Exception {
      return (Method[])this.getDeclaredMethods.invoke(this.driver, cls);
   }

   @Override
   <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls) throws Exception {
      return (Constructor<T>[])this.getDeclaredConstructors.invoke(this.driver, cls);
   }

   @Override
   Field[] getDeclaredFields(Class<?> cls) throws Exception {
      return (Field[])this.getDeclaredFields.invoke(this.driver, cls);
   }

   @Override
   Object getField(Object object, Field field) throws Exception {
      return this.getField.invoke(this.driver, object, field);
   }

   @Override
   void setField(Object object, Field field, Object value) throws Exception {
      this.setField.invoke(this.driver, object, field, value);
   }

   @Override
   Object getStaticField(Field field) throws Exception {
      return this.getField.invoke(this.driver, null, field);
   }

   @Override
   void setStaticField(Field field, Object value) throws Exception {
      this.setField.invoke(this.driver, null, field, value);
   }

   @Override
   Object invokeMethod(Object object, Method method, Object... args) throws Exception {
      return this.invokeMethod.invoke(this.driver, object, method, args);
   }

   @Override
   Object invokeStaticMethod(Method method, Object... args) throws Exception {
      return this.invokeMethod.invoke(this.driver, null, method, args);
   }

   private interface ClassFinder {
      Class<?> findClass(String var1) throws Exception;
   }
}
