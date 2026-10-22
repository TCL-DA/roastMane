package org.eclipse.jetty.websocket.core.util;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.eclipse.jetty.websocket.core.exception.DuplicateAnnotationException;

public class ReflectUtils {
   private static final Pattern JAVAX_CLASSNAME_PATTERN = Pattern.compile("^javax*\\..*");
   private static final Pattern JAKARTA_CLASSNAME_PATTERN = Pattern.compile("^jakarta*\\..*");

   private static StringBuilder appendTypeName(StringBuilder sb, Type type, boolean ellipses) {
      if (type instanceof Class<?> ctype) {
         if (ctype.isArray()) {
            try {
               int dimensions;
               for (dimensions = 0; ctype.isArray(); ctype = ctype.getComponentType()) {
                  dimensions++;
               }

               sb.append(ctype.getName());

               for (int i = 0; i < dimensions; i++) {
                  if (ellipses) {
                     sb.append("...");
                  } else {
                     sb.append("[]");
                  }
               }

               return sb;
            } catch (Throwable var6) {
            }
         }

         sb.append(ctype.getName());
      } else {
         sb.append(type.toString());
      }

      return sb;
   }

   public static Method findMethod(Class<?> pojo, String methodName, Class<?>... params) {
      try {
         return pojo.getMethod(methodName, params);
      } catch (NoSuchMethodException var4) {
         return null;
      }
   }

   public static Method findAnnotatedMethod(Class<?> pojo, Class<? extends Annotation> anno) {
      Method[] methods = findAnnotatedMethods(pojo, anno);
      if (methods == null) {
         return null;
      } else if (methods.length > 1) {
         throw DuplicateAnnotationException.build(pojo, anno, methods);
      } else {
         return methods[0];
      }
   }

   public static Method[] findAnnotatedMethods(Class<?> pojo, Class<? extends Annotation> anno) {
      Class<?> clazz = pojo;

      List<Method> methods;
      for (methods = new ArrayList<>(); clazz != null && Object.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
         Stream.of(clazz.getDeclaredMethods()).filter(method -> !method.isSynthetic() && method.getAnnotation(anno) != null).forEach(methods::add);
      }

      return methods.isEmpty() ? null : methods.toArray(new Method[0]);
   }

   public static Class<?> findGenericClassFor(Class<?> baseClass, Class<?> ifaceClass) {
      ReflectUtils.GenericRef ref = new ReflectUtils.GenericRef(baseClass, ifaceClass);
      return resolveGenericRef(ref, baseClass) ? ref.genericClass : null;
   }

   private static int findTypeParameterIndex(Class<?> clazz, TypeVariable<?> needVar) {
      TypeVariable<?>[] params = clazz.getTypeParameters();

      for (int i = 0; i < params.length; i++) {
         if (params[i].getName().equals(needVar.getName())) {
            return i;
         }
      }

      return -1;
   }

   public static boolean isDefaultConstructable(Class<?> clazz) {
      int mods = clazz.getModifiers();
      if (!Modifier.isAbstract(mods) && Modifier.isPublic(mods)) {
         Class<?>[] noargs = new Class[0];

         try {
            Constructor<?> constructor = clazz.getConstructor(noargs);
            return Modifier.isPublic(constructor.getModifiers());
         } catch (SecurityException | NoSuchMethodException var4) {
            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean resolveGenericRef(ReflectUtils.GenericRef ref, Class<?> clazz, Type type) {
      if (type instanceof Class) {
         if (type == ref.ifaceClass) {
            ref.setGenericFromType(type, 0);
            return true;
         } else {
            return resolveGenericRef(ref, type);
         }
      } else if (type instanceof ParameterizedType ptype) {
         Type rawType = ptype.getRawType();
         if (rawType == ref.ifaceClass) {
            ref.setGenericFromType(ptype.getActualTypeArguments()[0], 0);
            return true;
         } else {
            return resolveGenericRef(ref, rawType);
         }
      } else {
         return false;
      }
   }

   private static boolean resolveGenericRef(ReflectUtils.GenericRef ref, Type type) {
      if (type == null || type == Object.class) {
         return false;
      } else if (type instanceof Class<?> clazz) {
         if (!JAKARTA_CLASSNAME_PATTERN.matcher(clazz.getName()).matches() && !JAVAX_CLASSNAME_PATTERN.matcher(clazz.getName()).matches()) {
            Type[] ifaces = clazz.getGenericInterfaces();

            for (Type iface : ifaces) {
               if (resolveGenericRef(ref, clazz, iface)) {
                  if (ref.needsUnwrap()) {
                     TypeVariable<?> needVar = (TypeVariable<?>)ref.genericType;
                     int typeParamIdx = findTypeParameterIndex(clazz, needVar);
                     if (typeParamIdx >= 0) {
                        TypeVariable<?>[] params = clazz.getTypeParameters();
                        if (params.length >= typeParamIdx) {
                           ref.setGenericFromType(params[typeParamIdx], typeParamIdx);
                        }
                     } else if (iface instanceof ParameterizedType) {
                        Type arg = ((ParameterizedType)iface).getActualTypeArguments()[ref.genericIndex];
                        ref.setGenericFromType(arg, ref.genericIndex);
                     }
                  }

                  return true;
               }
            }

            type = clazz.getGenericSuperclass();
            return resolveGenericRef(ref, type);
         } else {
            return false;
         }
      } else {
         if (type instanceof ParameterizedType ptype) {
            Class<?> rawClass = (Class<?>)ptype.getRawType();
            if (resolveGenericRef(ref, rawClass) && ref.needsUnwrap()) {
               TypeVariable<?> needVar = (TypeVariable<?>)ref.genericType;
               int typeParamIdx = findTypeParameterIndex(rawClass, needVar);
               Type arg = ptype.getActualTypeArguments()[typeParamIdx];
               ref.setGenericFromType(arg, typeParamIdx);
               return true;
            }
         }

         return false;
      }
   }

   public static String toString(Class<?> pojo, Method method) {
      StringBuilder str = new StringBuilder();
      append(str, pojo, method);
      return str.toString();
   }

   public static void append(StringBuilder str, Class<?> pojo, Method method) {
      int mod = method.getModifiers() & Modifier.methodModifiers();
      if (mod != 0) {
         str.append(Modifier.toString(mod)).append(' ');
      }

      Type retType = method.getGenericReturnType();
      appendTypeName(str, retType, false).append(' ');
      if (pojo != null) {
         str.append(pojo.getName());
         str.append("#");
      }

      str.append(method.getName());
      str.append('(');
      Type[] params = method.getGenericParameterTypes();

      for (int j = 0; j < params.length; j++) {
         boolean ellipses = method.isVarArgs() && j == params.length - 1;
         appendTypeName(str, params[j], ellipses);
         if (j < params.length - 1) {
            str.append(", ");
         }
      }

      str.append(')');
   }

   public static void append(StringBuilder str, Method method) {
      append(str, null, method);
   }

   public static void append(StringBuilder str, MethodType methodType) {
      str.append(methodType.returnType().getName());
      str.append("(");
      boolean delim = false;

      for (Class<?> paramType : methodType.parameterList()) {
         if (delim) {
            str.append(", ");
         }

         str.append(paramType.getName());
         delim = true;
      }

      str.append(")");
   }

   private static class GenericRef {
      private final Class<?> baseClass;
      private final Class<?> ifaceClass;
      Class<?> genericClass;
      public Type genericType;
      private int genericIndex;

      public GenericRef(Class<?> baseClass, Class<?> ifaceClass) {
         this.baseClass = baseClass;
         this.ifaceClass = ifaceClass;
      }

      public boolean needsUnwrap() {
         return this.genericClass == null && this.genericType != null && this.genericType instanceof TypeVariable;
      }

      public void setGenericFromType(Type type, int index) {
         this.genericType = type;
         this.genericIndex = index;
         if (type instanceof Class) {
            this.genericClass = (Class<?>)type;
         }
      }

      @Override
      public String toString() {
         return "GenericRef [baseClass="
            + this.baseClass
            + ", ifaceClass="
            + this.ifaceClass
            + ", genericType="
            + this.genericType
            + ", genericClass="
            + this.genericClass
            + "]";
      }
   }
}
