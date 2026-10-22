package org.eclipse.jetty.util;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class IntrospectionUtil {
   public static boolean isJavaBeanCompliantSetter(Method method) {
      if (method == null) {
         return false;
      } else if (method.getReturnType() != void.class) {
         return false;
      } else {
         return !method.getName().startsWith("set") ? false : method.getParameterCount() == 1;
      }
   }

   public static Method findMethod(Class<?> clazz, String methodName, Class<?>[] args, boolean checkInheritance, boolean strictArgs) throws NoSuchMethodException {
      if (clazz == null) {
         throw new NoSuchMethodException("No class");
      } else if (methodName != null && !methodName.trim().equals("")) {
         Method method = null;
         Method[] methods = clazz.getDeclaredMethods();

         for (int i = 0; i < methods.length && method == null; i++) {
            if (methods[i].getName().equals(methodName) && checkParams(methods[i].getParameterTypes(), args == null ? new Class[0] : args, strictArgs)) {
               method = methods[i];
            }
         }

         if (method != null) {
            return method;
         } else if (checkInheritance) {
            return findInheritedMethod(clazz.getPackage(), clazz.getSuperclass(), methodName, args, strictArgs);
         } else {
            throw new NoSuchMethodException("No such method " + methodName + " on class " + clazz.getName());
         }
      } else {
         throw new NoSuchMethodException("No method name");
      }
   }

   public static Field findField(Class<?> clazz, String targetName, Class<?> targetType, boolean checkInheritance, boolean strictType) throws NoSuchFieldException {
      if (clazz == null) {
         throw new NoSuchFieldException("No class");
      } else if (targetName == null) {
         throw new NoSuchFieldException("No field name");
      } else {
         try {
            Field field = clazz.getDeclaredField(targetName);
            if (strictType) {
               if (field.getType().equals(targetType)) {
                  return field;
               }
            } else if (field.getType().isAssignableFrom(targetType)) {
               return field;
            }

            if (checkInheritance) {
               return findInheritedField(clazz.getPackage(), clazz.getSuperclass(), targetName, targetType, strictType);
            } else {
               throw new NoSuchFieldException("No field with name " + targetName + " in class " + clazz.getName() + " of type " + targetType);
            }
         } catch (NoSuchFieldException var6) {
            return findInheritedField(clazz.getPackage(), clazz.getSuperclass(), targetName, targetType, strictType);
         }
      }
   }

   public static boolean isInheritable(Package pack, Member member) {
      if (pack == null) {
         return false;
      } else if (member == null) {
         return false;
      } else {
         int modifiers = member.getModifiers();
         if (Modifier.isPublic(modifiers)) {
            return true;
         } else {
            return Modifier.isProtected(modifiers) ? true : !Modifier.isPrivate(modifiers) && pack.equals(member.getDeclaringClass().getPackage());
         }
      }
   }

   public static boolean checkParams(Class<?>[] formalParams, Class<?>[] actualParams, boolean strict) {
      if (formalParams == null) {
         return actualParams == null;
      } else if (actualParams == null) {
         return false;
      } else if (formalParams.length != actualParams.length) {
         return false;
      } else if (formalParams.length == 0) {
         return true;
      } else {
         int j = 0;
         if (strict) {
            while (j < formalParams.length && formalParams[j].equals(actualParams[j])) {
               j++;
            }
         } else {
            while (j < formalParams.length && formalParams[j].isAssignableFrom(actualParams[j])) {
               j++;
            }
         }

         return j == formalParams.length;
      }
   }

   public static boolean isSameSignature(Method methodA, Method methodB) {
      if (methodA == null) {
         return false;
      } else if (methodB == null) {
         return false;
      } else {
         List<Class<?>> parameterTypesA = Arrays.asList(methodA.getParameterTypes());
         List<Class<?>> parameterTypesB = Arrays.asList(methodB.getParameterTypes());
         return methodA.getName().equals(methodB.getName()) && parameterTypesA.containsAll(parameterTypesB);
      }
   }

   public static boolean isTypeCompatible(Class<?> formalType, Class<?> actualType, boolean strict) {
      if (formalType == null) {
         return actualType == null;
      } else if (actualType == null) {
         return false;
      } else {
         return strict ? formalType.equals(actualType) : formalType.isAssignableFrom(actualType);
      }
   }

   public static boolean containsSameMethodSignature(Method method, Class<?> c, boolean checkPackage) {
      if (checkPackage && !c.getPackage().equals(method.getDeclaringClass().getPackage())) {
         return false;
      } else {
         boolean samesig = false;
         Method[] methods = c.getDeclaredMethods();

         for (int i = 0; i < methods.length && !samesig; i++) {
            if (isSameSignature(method, methods[i])) {
               samesig = true;
            }
         }

         return samesig;
      }
   }

   public static boolean containsSameFieldName(Field field, Class<?> c, boolean checkPackage) {
      if (checkPackage && !c.getPackage().equals(field.getDeclaringClass().getPackage())) {
         return false;
      } else {
         boolean sameName = false;
         Field[] fields = c.getDeclaredFields();

         for (int i = 0; i < fields.length && !sameName; i++) {
            if (fields[i].getName().equals(field.getName())) {
               sameName = true;
            }
         }

         return sameName;
      }
   }

   protected static Method findInheritedMethod(Package pack, Class<?> clazz, String methodName, Class<?>[] args, boolean strictArgs) throws NoSuchMethodException {
      if (clazz == null) {
         throw new NoSuchMethodException("No class");
      } else if (methodName == null) {
         throw new NoSuchMethodException("No method name");
      } else {
         Method method = null;
         Method[] methods = clazz.getDeclaredMethods();

         for (int i = 0; i < methods.length && method == null; i++) {
            if (methods[i].getName().equals(methodName) && isInheritable(pack, methods[i]) && checkParams(methods[i].getParameterTypes(), args, strictArgs)) {
               method = methods[i];
            }
         }

         return method != null ? method : findInheritedMethod(clazz.getPackage(), clazz.getSuperclass(), methodName, args, strictArgs);
      }
   }

   protected static Field findInheritedField(Package pack, Class<?> clazz, String fieldName, Class<?> fieldType, boolean strictType) throws NoSuchFieldException {
      if (clazz == null) {
         throw new NoSuchFieldException("No class");
      } else if (fieldName == null) {
         throw new NoSuchFieldException("No field name");
      } else {
         try {
            Field field = clazz.getDeclaredField(fieldName);
            return isInheritable(pack, field) && isTypeCompatible(fieldType, field.getType(), strictType)
               ? field
               : findInheritedField(clazz.getPackage(), clazz.getSuperclass(), fieldName, fieldType, strictType);
         } catch (NoSuchFieldException var6) {
            return findInheritedField(clazz.getPackage(), clazz.getSuperclass(), fieldName, fieldType, strictType);
         }
      }
   }
}
