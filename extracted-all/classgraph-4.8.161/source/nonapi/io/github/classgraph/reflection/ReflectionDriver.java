package nonapi.io.github.classgraph.reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.concurrency.SingletonMap;
import nonapi.io.github.classgraph.utils.LogNode;

abstract class ReflectionDriver {
   private final SingletonMap<Class<?>, ReflectionDriver.ClassMemberCache, Exception> classToClassMemberCache = new SingletonMap<Class<?>, ReflectionDriver.ClassMemberCache, Exception>(
      
   ) {
      public ReflectionDriver.ClassMemberCache newInstance(Class<?> cls, LogNode log) throws Exception, InterruptedException {
         return ReflectionDriver.this.new ClassMemberCache(cls);
      }
   };
   private static Method isAccessibleMethod;
   private static Method canAccessMethod;

   abstract Class<?> findClass(String var1) throws Exception;

   abstract Method[] getDeclaredMethods(Class<?> var1) throws Exception;

   abstract <T> Constructor<T>[] getDeclaredConstructors(Class<T> var1) throws Exception;

   abstract Field[] getDeclaredFields(Class<?> var1) throws Exception;

   abstract Object getField(Object var1, Field var2) throws Exception;

   abstract void setField(Object var1, Field var2, Object var3) throws Exception;

   abstract Object getStaticField(Field var1) throws Exception;

   abstract void setStaticField(Field var1, Object var2) throws Exception;

   abstract Object invokeMethod(Object var1, Method var2, Object... var3) throws Exception;

   abstract Object invokeStaticMethod(Method var1, Object... var2) throws Exception;

   abstract boolean makeAccessible(Object var1, AccessibleObject var2);

   boolean isAccessible(Object instance, AccessibleObject fieldOrMethod) {
      if (canAccessMethod != null) {
         try {
            return (Boolean)canAccessMethod.invoke(fieldOrMethod, instance);
         } catch (Throwable var5) {
         }
      }

      if (isAccessibleMethod != null) {
         try {
            return (Boolean)isAccessibleMethod.invoke(fieldOrMethod);
         } catch (Throwable var4) {
         }
      }

      return false;
   }

   protected Field findField(Class<?> cls, Object obj, String fieldName) throws Exception {
      Field field = this.classToClassMemberCache.get(cls, null).fieldNameToField.get(fieldName);
      if (field != null) {
         if (!this.isAccessible(obj, field)) {
            this.makeAccessible(obj, field);
         }

         return field;
      } else {
         throw new NoSuchFieldException("Could not find field " + cls.getName() + "." + fieldName);
      }
   }

   protected Field findStaticField(Class<?> cls, String fieldName) throws Exception {
      return this.findField(cls, null, fieldName);
   }

   protected Field findInstanceField(Object obj, String fieldName) throws Exception {
      if (obj == null) {
         throw new IllegalArgumentException("obj cannot be null");
      } else {
         return this.findField(obj.getClass(), obj, fieldName);
      }
   }

   protected Method findMethod(Class<?> cls, Object obj, String methodName, Class<?>... paramTypes) throws Exception {
      List<Method> methodsForName = this.classToClassMemberCache.get(cls, null).methodNameToMethods.get(methodName);
      if (methodsForName == null) {
         throw new NoSuchMethodException("Could not find method " + cls.getName() + "." + methodName);
      } else {
         boolean found = false;

         for (Method method : methodsForName) {
            if (Arrays.equals((Object[])method.getParameterTypes(), (Object[])paramTypes)) {
               found = true;
               if (this.isAccessible(obj, method)) {
                  return method;
               }
            }
         }

         if (found) {
            for (Method methodx : methodsForName) {
               if (Arrays.equals((Object[])methodx.getParameterTypes(), (Object[])paramTypes) && this.makeAccessible(obj, methodx)) {
                  return methodx;
               }
            }
         }

         throw new NoSuchMethodException("Could not make method accessible: " + cls.getName() + "." + methodName);
      }
   }

   protected Method findStaticMethod(Class<?> cls, String methodName, Class<?>... paramTypes) throws Exception {
      return this.findMethod(cls, null, methodName, paramTypes);
   }

   protected Method findInstanceMethod(Object obj, String methodName, Class<?>... paramTypes) throws Exception {
      if (obj == null) {
         throw new IllegalArgumentException("obj cannot be null");
      } else {
         return this.findMethod(obj.getClass(), obj, methodName, paramTypes);
      }
   }

   static {
      try {
         isAccessibleMethod = AccessibleObject.class.getDeclaredMethod("isAccessible");
      } catch (Throwable var2) {
      }

      try {
         canAccessMethod = AccessibleObject.class.getDeclaredMethod("canAccess", Object.class);
      } catch (Throwable var1) {
      }
   }

   public class ClassMemberCache {
      private final Map<String, List<Method>> methodNameToMethods = new HashMap<>();
      private final Map<String, Field> fieldNameToField = new HashMap<>();

      private ClassMemberCache(Class<?> cls) throws Exception {
         Set<Class<?>> visited = new HashSet<>();
         LinkedList<Class<?>> interfaceQueue = new LinkedList<>();

         for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            try {
               for (Method m : ReflectionDriver.this.getDeclaredMethods(c)) {
                  this.cacheMethod(m);
               }

               for (Field f : ReflectionDriver.this.getDeclaredFields(c)) {
                  this.cacheField(f);
               }

               if (c.isInterface() && visited.add(c)) {
                  interfaceQueue.add(c);
               }

               for (Class<?> iface : c.getInterfaces()) {
                  if (visited.add(iface)) {
                     interfaceQueue.add(iface);
                  }
               }
            } catch (Exception var11) {
            }
         }

         while (!interfaceQueue.isEmpty()) {
            Class<?> ifacex = interfaceQueue.remove();

            try {
               for (Method m : ReflectionDriver.this.getDeclaredMethods(ifacex)) {
                  this.cacheMethod(m);
               }
            } catch (Exception var10) {
            }

            for (Class<?> superIface : ifacex.getInterfaces()) {
               if (visited.add(superIface)) {
                  interfaceQueue.add(superIface);
               }
            }
         }
      }

      private void cacheMethod(Method method) {
         List<Method> methodsForName = this.methodNameToMethods.get(method.getName());
         if (methodsForName == null) {
            this.methodNameToMethods.put(method.getName(), methodsForName = new ArrayList<>());
         }

         methodsForName.add(method);
      }

      private void cacheField(Field field) {
         if (!this.fieldNameToField.containsKey(field.getName())) {
            this.fieldNameToField.put(field.getName(), field);
         }
      }
   }
}
