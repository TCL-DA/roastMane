package org.glassfish.jersey.internal.util;

import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.GenericType;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.OsgiRegistry;
import org.glassfish.jersey.internal.util.collection.ClassTypePair;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.internal.util.collection.Values;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public final class ReflectionHelper {
   private static final Logger LOGGER = Logger.getLogger(ReflectionHelper.class.getName());
   private static final PrivilegedAction<?> NoOpPrivilegedACTION = new PrivilegedAction<Object>() {
      @Override
      public Object run() {
         return null;
      }
   };
   private static final TypeVisitor<Class> eraser = new TypeVisitor<Class>() {
      protected Class onClass(Class clazz) {
         return clazz;
      }

      protected Class onParameterizedType(ParameterizedType type) {
         return this.visit(type.getRawType());
      }

      protected Class onGenericArray(GenericArrayType type) {
         return Array.newInstance(this.visit(type.getGenericComponentType()), 0).getClass();
      }

      protected Class onVariable(TypeVariable type) {
         return this.visit(type.getBounds()[0]);
      }

      protected Class onWildcard(WildcardType type) {
         return this.visit(type.getUpperBounds()[0]);
      }

      @Override
      protected RuntimeException createError(Type type) {
         return new IllegalArgumentException(LocalizationMessages.TYPE_TO_CLASS_CONVERSION_NOT_SUPPORTED(type));
      }
   };
   private static final Class<?> bundleReferenceClass = AccessController.doPrivileged(classForNamePA("org.osgi.framework.BundleReference", null));
   private static final LazyValue<Object> osgiInstance = Values.lazy((Value<Object>)(() -> {
      try {
         if (bundleReferenceClass != null) {
            return OsgiRegistry.getInstance();
         }
      } catch (Throwable var1) {
      }

      return null;
   }));

   private ReflectionHelper() {
      throw new AssertionError("No instances allowed.");
   }

   public static Class<?> getDeclaringClass(AccessibleObject ao) {
      if (!(ao instanceof Member) || !(ao instanceof Field) && !(ao instanceof Method) && !(ao instanceof Constructor)) {
         throw new IllegalArgumentException("Unsupported accessible object type: " + ao.getClass().getName());
      } else {
         return ((Member)ao).getDeclaringClass();
      }
   }

   public static String objectToString(Object o) {
      return o == null ? "null" : o.getClass().getName() + '@' + Integer.toHexString(o.hashCode());
   }

   public static String methodInstanceToString(Object o, Method m) {
      StringBuilder sb = new StringBuilder();
      sb.append(o.getClass().getName()).append('@').append(Integer.toHexString(o.hashCode())).append('.').append(m.getName()).append('(');
      Class[] params = m.getParameterTypes();

      for (int i = 0; i < params.length; i++) {
         sb.append(getTypeName(params[i]));
         if (i < params.length - 1) {
            sb.append(",");
         }
      }

      sb.append(')');
      return sb.toString();
   }

   private static String getTypeName(Class<?> type) {
      if (!type.isArray()) {
         return type.getName();
      } else {
         Class<?> cl = type;

         int dimensions;
         for (dimensions = 0; cl.isArray(); cl = cl.getComponentType()) {
            dimensions++;
         }

         StringBuilder sb = new StringBuilder();
         sb.append(cl.getName());

         for (int i = 0; i < dimensions; i++) {
            sb.append("[]");
         }

         return sb.toString();
      }
   }

   public static <T> PrivilegedAction<Class<T>> classForNamePA(String name) {
      return classForNamePA(name, getContextClassLoader());
   }

   public static <T> PrivilegedAction<Class<T>> classForNamePA(final String name, final ClassLoader cl) {
      return new PrivilegedAction<Class<T>>() {
         public Class<T> run() {
            if (cl != null) {
               try {
                  return (Class<T>)Class.forName(name, false, cl);
               } catch (ClassNotFoundException var3) {
                  if (ReflectionHelper.LOGGER.isLoggable(Level.FINER)) {
                     ReflectionHelper.LOGGER
                        .log(Level.FINER, "Unable to load class " + name + " using the supplied class loader " + cl.getClass().getName() + ".", (Throwable)var3);
                  }
               }
            }

            try {
               return (Class<T>)Class.forName(name);
            } catch (ClassNotFoundException var2) {
               if (ReflectionHelper.LOGGER.isLoggable(Level.FINER)) {
                  ReflectionHelper.LOGGER.log(Level.FINER, "Unable to load class " + name + " using the current class loader.", (Throwable)var2);
               }

               return null;
            }
         }
      };
   }

   public static PrivilegedAction<ClassLoader> getClassLoaderPA(final Class<?> clazz) {
      return new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return clazz.getClassLoader();
         }
      };
   }

   public static PrivilegedAction<Field[]> getDeclaredFieldsPA(final Class<?> clazz) {
      return new PrivilegedAction<Field[]>() {
         public Field[] run() {
            return clazz.getDeclaredFields();
         }
      };
   }

   public static PrivilegedAction<Field[]> getAllFieldsPA(final Class<?> clazz) {
      return new PrivilegedAction<Field[]>() {
         public Field[] run() {
            List<Field> fields = new ArrayList<>();
            this.recurse(clazz, fields);
            return fields.toArray(new Field[fields.size()]);
         }

         private void recurse(Class<?> clazzx, List<Field> fields) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            if (clazz.getSuperclass() != null) {
               this.recurse(clazz.getSuperclass(), fields);
            }
         }
      };
   }

   public static PrivilegedAction<Collection<? extends Method>> getDeclaredMethodsPA(final Class<?> clazz) {
      return new PrivilegedAction<Collection<? extends Method>>() {
         public Collection<? extends Method> run() {
            return Arrays.asList(clazz.getDeclaredMethods());
         }
      };
   }

   public static <T> PrivilegedExceptionAction<Class<T>> classForNameWithExceptionPEA(String name) throws ClassNotFoundException {
      return classForNameWithExceptionPEA(name, getContextClassLoader());
   }

   public static <T> PrivilegedExceptionAction<Class<T>> classForNameWithExceptionPEA(final String name, final ClassLoader cl) throws ClassNotFoundException {
      return new PrivilegedExceptionAction<Class<T>>() {
         public Class<T> run() throws ClassNotFoundException {
            if (cl != null) {
               try {
                  return (Class<T>)Class.forName(name, false, cl);
               } catch (ClassNotFoundException var2) {
               }
            }

            return (Class<T>)Class.forName(name);
         }
      };
   }

   public static PrivilegedAction<ClassLoader> getContextClassLoaderPA() {
      return new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
         }
      };
   }

   private static ClassLoader getContextClassLoader() {
      return AccessController.doPrivileged(getContextClassLoaderPA());
   }

   public static PrivilegedAction setContextClassLoaderPA(final ClassLoader classLoader) {
      return new PrivilegedAction() {
         @Override
         public Object run() {
            Thread.currentThread().setContextClassLoader(classLoader);
            return null;
         }
      };
   }

   public static PrivilegedAction setAccessibleMethodPA(final Method m) {
      return isPublic(m) ? NoOpPrivilegedACTION : new PrivilegedAction<Object>() {
         @Override
         public Object run() {
            if (!m.isAccessible()) {
               m.setAccessible(true);
            }

            return m;
         }
      };
   }

   public static boolean isPublic(Class<?> clazz) {
      return Modifier.isPublic(clazz.getModifiers());
   }

   public static boolean isPublic(Executable executable) {
      return Modifier.isPublic(executable.getModifiers());
   }

   public static List<Class<?>> getGenericTypeArgumentClasses(Type type) throws IllegalArgumentException {
      Type[] types = getTypeArguments(type);
      return types == null ? Collections.emptyList() : Arrays.stream(types).map(ReflectionHelper::erasure).collect(Collectors.toList());
   }

   public static List<ClassTypePair> getTypeArgumentAndClass(Type type) throws IllegalArgumentException {
      Type[] types = getTypeArguments(type);
      return types == null ? Collections.emptyList() : Arrays.stream(types).map(type1 -> ClassTypePair.of(erasure(type1), type1)).collect(Collectors.toList());
   }

   public static boolean isPrimitive(Type type) {
      if (type instanceof Class) {
         Class c = (Class)type;
         return c.isPrimitive();
      } else {
         return false;
      }
   }

   public static Type[] getTypeArguments(Type type) {
      return !(type instanceof ParameterizedType) ? null : ((ParameterizedType)type).getActualTypeArguments();
   }

   public static Type getTypeArgument(Type type, int index) {
      if (type instanceof ParameterizedType) {
         ParameterizedType p = (ParameterizedType)type;
         return fix(p.getActualTypeArguments()[index]);
      } else {
         return null;
      }
   }

   private static Type fix(Type t) {
      if (!(t instanceof GenericArrayType)) {
         return t;
      } else {
         GenericArrayType gat = (GenericArrayType)t;
         if (gat.getGenericComponentType() instanceof Class) {
            Class c = (Class)gat.getGenericComponentType();
            return Array.newInstance(c, 0).getClass();
         } else {
            return t;
         }
      }
   }

   public static <T> Class<T> erasure(Type type) {
      return eraser.visit(type);
   }

   public static boolean isSubClassOf(Type subType, Type superType) {
      return erasure(superType).isAssignableFrom(erasure(subType));
   }

   public static boolean isArray(Type type) {
      if (type instanceof Class) {
         Class c = (Class)type;
         return c.isArray();
      } else {
         return type instanceof GenericArrayType;
      }
   }

   public static boolean isArrayOfType(Type type, Class<?> componentType) {
      if (!(type instanceof Class)) {
         if (type instanceof GenericArrayType) {
            Type arrayComponentType = ((GenericArrayType)type).getGenericComponentType();
            return arrayComponentType == componentType;
         } else {
            return false;
         }
      } else {
         Class c = (Class)type;
         return c.isArray() && c != byte[].class;
      }
   }

   public static Type getArrayComponentType(Type type) {
      if (type instanceof Class) {
         Class c = (Class)type;
         return c.getComponentType();
      } else if (type instanceof GenericArrayType) {
         return ((GenericArrayType)type).getGenericComponentType();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static Class<?> getArrayForComponentType(Class<?> c) {
      try {
         Object o = Array.newInstance(c, 0);
         return o.getClass();
      } catch (Exception var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   public static PrivilegedAction<Method> getValueOfStringMethodPA(Class<?> clazz) {
      return getStringToObjectMethodPA(clazz, "valueOf");
   }

   public static PrivilegedAction<Method> getFromStringStringMethodPA(Class<?> clazz) {
      return getStringToObjectMethodPA(clazz, "fromString");
   }

   private static PrivilegedAction<Method> getStringToObjectMethodPA(final Class<?> clazz, final String methodName) {
      return new PrivilegedAction<Method>() {
         public Method run() {
            try {
               Method method = clazz.getDeclaredMethod(methodName, String.class);
               return Modifier.isStatic(method.getModifiers()) && method.getReturnType() == clazz ? method : null;
            } catch (NoSuchMethodException var2) {
               return null;
            }
         }
      };
   }

   public static PrivilegedAction<Constructor> getStringConstructorPA(final Class<?> clazz) {
      return new PrivilegedAction<Constructor>() {
         public Constructor run() {
            try {
               return clazz.getConstructor(String.class);
            } catch (SecurityException var2) {
               throw var2;
            } catch (Exception var3) {
               return null;
            }
         }
      };
   }

   public static PrivilegedAction<Constructor<?>[]> getDeclaredConstructorsPA(final Class<?> clazz) {
      return new PrivilegedAction<Constructor<?>[]>() {
         public Constructor<?>[] run() {
            return clazz.getDeclaredConstructors();
         }
      };
   }

   public static PrivilegedAction<Constructor<?>> getDeclaredConstructorPA(final Class<?> clazz, final Class<?>... params) {
      return new PrivilegedAction<Constructor<?>>() {
         public Constructor<?> run() {
            try {
               return clazz.getDeclaredConstructor(params);
            } catch (NoSuchMethodException var2) {
               return null;
            }
         }
      };
   }

   public static Collection<Class<? extends Annotation>> getAnnotationTypes(AnnotatedElement annotatedElement, Class<? extends Annotation> metaAnnotation) {
      Set<Class<? extends Annotation>> result = Collections.newSetFromMap(new IdentityHashMap<>());

      for (Annotation a : annotatedElement.getAnnotations()) {
         Class<? extends Annotation> aType = a.annotationType();
         if (metaAnnotation == null || aType.getAnnotation(metaAnnotation) != null) {
            result.add(aType);
         }
      }

      return result;
   }

   public static boolean isGetter(Method method) {
      if (method.getParameterTypes().length == 0 && isPublic(method)) {
         String methodName = method.getName();
         if (methodName.startsWith("get") && methodName.length() > 3) {
            return !void.class.equals(method.getReturnType());
         }

         if (methodName.startsWith("is") && methodName.length() > 2) {
            return boolean.class.equals(method.getReturnType()) || Boolean.class.equals(method.getReturnType());
         }
      }

      return false;
   }

   public static GenericType genericTypeFor(Object instance) {
      GenericType genericType;
      if (instance instanceof GenericEntity) {
         genericType = new GenericType(((GenericEntity)instance).getType());
      } else {
         genericType = instance == null ? null : new GenericType(instance.getClass());
      }

      return genericType;
   }

   public static boolean isSetter(Method method) {
      return isPublic(method) && void.class.equals(method.getReturnType()) && method.getParameterTypes().length == 1 && method.getName().startsWith("set");
   }

   public static String getPropertyName(Method method) {
      if (!isGetter(method) && !isSetter(method)) {
         throw new IllegalArgumentException(LocalizationMessages.METHOD_NOT_GETTER_NOR_SETTER());
      } else {
         String methodName = method.getName();
         int offset = methodName.startsWith("is") ? 2 : 3;
         char[] chars = methodName.toCharArray();
         chars[offset] = Character.toLowerCase(chars[offset]);
         return new String(chars, offset, chars.length - offset);
      }
   }

   public static Class<?> theMostSpecificTypeOf(Set<Type> contractTypes) {
      Class<?> result = null;

      for (Type t : contractTypes) {
         Class<?> next = (Class<?>)t;
         if (result == null) {
            result = next;
         } else if (result.isAssignableFrom(next)) {
            result = next;
         }
      }

      return result;
   }

   public static Class[] getParameterizedClassArguments(ReflectionHelper.DeclaringClassInterfacePair p) {
      if (p.genericInterface instanceof ParameterizedType) {
         ParameterizedType pt = (ParameterizedType)p.genericInterface;
         Type[] as = pt.getActualTypeArguments();
         Class[] cas = new Class[as.length];

         for (int i = 0; i < as.length; i++) {
            Type a = as[i];
            if (a instanceof Class) {
               cas[i] = (Class)a;
            } else if (a instanceof ParameterizedType) {
               pt = (ParameterizedType)a;
               cas[i] = (Class)pt.getRawType();
            } else if (a instanceof TypeVariable) {
               TypeVariable tv = (TypeVariable)a;
               ClassTypePair ctp = resolveTypeVariable(p.concreteClass, p.declaringClass, tv);
               cas[i] = ctp != null ? ctp.rawClass() : (Class)tv.getBounds()[0];
            } else if (a instanceof GenericArrayType) {
               GenericArrayType gat = (GenericArrayType)a;
               Type t = gat.getGenericComponentType();
               if (t instanceof Class) {
                  cas[i] = getArrayForComponentType((Class<?>)t);
               }
            }
         }

         return cas;
      } else {
         return null;
      }
   }

   public static Type[] getParameterizedTypeArguments(ReflectionHelper.DeclaringClassInterfacePair p) {
      if (p.genericInterface instanceof ParameterizedType) {
         ParameterizedType pt = (ParameterizedType)p.genericInterface;
         Type[] as = pt.getActualTypeArguments();
         Type[] ras = new Type[as.length];

         for (int i = 0; i < as.length; i++) {
            Type a = as[i];
            if (a instanceof Class) {
               ras[i] = a;
            } else if (a instanceof ParameterizedType) {
               ras[i] = a;
            } else if (a instanceof TypeVariable) {
               ClassTypePair ctp = resolveTypeVariable(p.concreteClass, p.declaringClass, (TypeVariable)a);
               if (ctp == null) {
                  throw new IllegalArgumentException(LocalizationMessages.ERROR_RESOLVING_GENERIC_TYPE_VALUE(p.genericInterface, p.concreteClass));
               }

               ras[i] = ctp.type();
            }
         }

         return ras;
      } else {
         return null;
      }
   }

   public static ReflectionHelper.DeclaringClassInterfacePair getClass(Class<?> concrete, Class<?> iface) {
      return getClass(concrete, iface, concrete);
   }

   private static ReflectionHelper.DeclaringClassInterfacePair getClass(Class<?> concrete, Class<?> iface, Class<?> c) {
      Type[] gis = c.getGenericInterfaces();
      ReflectionHelper.DeclaringClassInterfacePair p = getType(concrete, iface, c, gis);
      if (p != null) {
         return p;
      } else {
         c = c.getSuperclass();
         return c != null && c != Object.class ? getClass(concrete, iface, c) : null;
      }
   }

   private static ReflectionHelper.DeclaringClassInterfacePair getType(Class<?> concrete, Class<?> iface, Class<?> c, Type[] ts) {
      for (Type t : ts) {
         ReflectionHelper.DeclaringClassInterfacePair p = getType(concrete, iface, c, t);
         if (p != null) {
            return p;
         }
      }

      return null;
   }

   private static ReflectionHelper.DeclaringClassInterfacePair getType(Class<?> concrete, Class<?> iface, Class<?> c, Type t) {
      if (t instanceof Class) {
         return t == iface ? new ReflectionHelper.DeclaringClassInterfacePair(concrete, c, t) : getClass(concrete, iface, (Class<?>)t);
      } else if (t instanceof ParameterizedType) {
         ParameterizedType pt = (ParameterizedType)t;
         return pt.getRawType() == iface
            ? new ReflectionHelper.DeclaringClassInterfacePair(concrete, c, t)
            : getClass(concrete, iface, (Class<?>)pt.getRawType());
      } else {
         return null;
      }
   }

   public static ClassTypePair resolveGenericType(Class concreteClass, Class declaringClass, Class rawResolvedType, Type genericResolvedType) {
      if (genericResolvedType instanceof TypeVariable) {
         ClassTypePair ct = resolveTypeVariable(concreteClass, declaringClass, (TypeVariable)genericResolvedType);
         if (ct != null) {
            return ct;
         }
      } else if (genericResolvedType instanceof ParameterizedType) {
         final ParameterizedType pt = (ParameterizedType)genericResolvedType;
         final Type[] ptts = pt.getActualTypeArguments();
         boolean modified = false;

         for (int i = 0; i < ptts.length; i++) {
            ClassTypePair ct = resolveGenericType(concreteClass, declaringClass, (Class)pt.getRawType(), ptts[i]);
            if (ct.type() != ptts[i]) {
               ptts[i] = ct.type();
               modified = true;
            }
         }

         if (modified) {
            ParameterizedType rpt = new ParameterizedType() {
               @Override
               public Type[] getActualTypeArguments() {
                  return (Type[])ptts.clone();
               }

               @Override
               public Type getRawType() {
                  return pt.getRawType();
               }

               @Override
               public Type getOwnerType() {
                  return pt.getOwnerType();
               }
            };
            return ClassTypePair.of((Class<?>)pt.getRawType(), rpt);
         }
      } else if (genericResolvedType instanceof GenericArrayType) {
         GenericArrayType gat = (GenericArrayType)genericResolvedType;
         ClassTypePair ct = resolveGenericType(concreteClass, declaringClass, null, gat.getGenericComponentType());
         if (gat.getGenericComponentType() != ct.type()) {
            try {
               Class ac = getArrayForComponentType(ct.rawClass());
               return ClassTypePair.of(ac);
            } catch (Exception var9) {
               LOGGER.log(Level.FINEST, "", (Throwable)var9);
            }
         }
      }

      return ClassTypePair.of(rawResolvedType, genericResolvedType);
   }

   public static ClassTypePair resolveTypeVariable(Class<?> c, Class<?> dc, TypeVariable tv) {
      return resolveTypeVariable(c, dc, tv, new HashMap<>());
   }

   private static ClassTypePair resolveTypeVariable(Class<?> c, Class<?> dc, TypeVariable tv, Map<TypeVariable, Type> map) {
      Type[] gis = c.getGenericInterfaces();

      for (Type gi : gis) {
         if (gi instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)gi;
            ClassTypePair ctp = resolveTypeVariable(pt, (Class<?>)pt.getRawType(), dc, tv, map);
            if (ctp != null) {
               return ctp;
            }
         }
      }

      Type gsc = c.getGenericSuperclass();
      if (gsc instanceof ParameterizedType) {
         ParameterizedType pt = (ParameterizedType)gsc;
         return resolveTypeVariable(pt, c.getSuperclass(), dc, tv, map);
      } else {
         return gsc instanceof Class ? resolveTypeVariable(c.getSuperclass(), dc, tv, map) : null;
      }
   }

   private static ClassTypePair resolveTypeVariable(ParameterizedType pt, Class<?> c, Class<?> dc, TypeVariable tv, Map<TypeVariable, Type> map) {
      Type[] typeArguments = pt.getActualTypeArguments();
      TypeVariable[] typeParameters = c.getTypeParameters();
      Map<TypeVariable, Type> subMap = new HashMap<>();

      for (int i = 0; i < typeArguments.length; i++) {
         Type typeArgument = typeArguments[i];
         if (typeArgument instanceof TypeVariable) {
            Type t = map.get(typeArgument);
            subMap.put(typeParameters[i], t);
         } else {
            subMap.put(typeParameters[i], typeArgument);
         }
      }

      if (c == dc) {
         Type t = subMap.get(tv);
         if (t instanceof Class) {
            return ClassTypePair.of((Class<?>)t);
         } else if (t instanceof GenericArrayType) {
            GenericArrayType gat = (GenericArrayType)t;
            t = gat.getGenericComponentType();
            if (t instanceof Class) {
               c = (Class<?>)t;

               try {
                  return ClassTypePair.of(getArrayForComponentType(c));
               } catch (Exception var12) {
                  return null;
               }
            } else if (t instanceof ParameterizedType) {
               Type rt = ((ParameterizedType)t).getRawType();
               if (rt instanceof Class) {
                  c = (Class<?>)rt;

                  try {
                     return ClassTypePair.of(getArrayForComponentType(c), gat);
                  } catch (Exception var13) {
                     return null;
                  }
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else if (t instanceof ParameterizedType) {
            pt = (ParameterizedType)t;
            return pt.getRawType() instanceof Class ? ClassTypePair.of((Class<?>)pt.getRawType(), pt) : null;
         } else {
            return null;
         }
      } else {
         return resolveTypeVariable(c, dc, tv, subMap);
      }
   }

   public static PrivilegedAction<Method> findMethodOnClassPA(final Class<?> c, final Method m) {
      return new PrivilegedAction<Method>() {
         public Method run() {
            try {
               return c.getMethod(m.getName(), m.getParameterTypes());
            } catch (NoSuchMethodException var6) {
               for (Method _m : c.getMethods()) {
                  if (_m.getName().equals(m.getName())
                     && _m.getParameterTypes().length == m.getParameterTypes().length
                     && ReflectionHelper.compareParameterTypes(m.getGenericParameterTypes(), _m.getGenericParameterTypes())) {
                     return _m;
                  }
               }

               return null;
            }
         }
      };
   }

   public static PrivilegedAction<Method[]> getMethodsPA(final Class<?> c) {
      return new PrivilegedAction<Method[]>() {
         public Method[] run() {
            return c.getMethods();
         }
      };
   }

   private static Method[] _getMethods(Class<?> clazz) {
      return AccessController.doPrivileged(getMethodsPA(clazz));
   }

   public static Method findOverridingMethodOnClass(Class<?> clazz, Method method) {
      for (Method _method : _getMethods(clazz)) {
         if (!_method.isBridge()
            && !Modifier.isAbstract(_method.getModifiers())
            && _method.getName().equals(method.getName())
            && _method.getParameterTypes().length == method.getParameterTypes().length
            && compareParameterTypes(_method.getGenericParameterTypes(), method.getGenericParameterTypes())) {
            return _method;
         }
      }

      if (method.isBridge() || Modifier.isAbstract(method.getModifiers())) {
         LOGGER.log(Level.INFO, LocalizationMessages.OVERRIDING_METHOD_CANNOT_BE_FOUND(method, clazz));
      }

      return method;
   }

   private static boolean compareParameterTypes(Type[] ts, Type[] _ts) {
      for (int i = 0; i < ts.length; i++) {
         if (!ts[i].equals(_ts[i]) && !compareParameterTypes(ts[i], _ts[i])) {
            return false;
         }
      }

      return true;
   }

   private static boolean compareParameterTypes(Type ts, Type _ts) {
      if (ts instanceof Class) {
         Class<?> clazz = (Class<?>)ts;
         if (_ts instanceof Class) {
            return ((Class)_ts).isAssignableFrom(clazz);
         }

         if (_ts instanceof TypeVariable) {
            return checkTypeBounds(clazz, ((TypeVariable)_ts).getBounds());
         }
      }

      return _ts instanceof TypeVariable;
   }

   private static boolean checkTypeBounds(Class type, Type[] bounds) {
      for (Type bound : bounds) {
         if (bound instanceof Class && !((Class)bound).isAssignableFrom(type)) {
            return false;
         }
      }

      return true;
   }

   public static OsgiRegistry getOsgiRegistryInstance() {
      return (OsgiRegistry)osgiInstance.get();
   }

   public static InputStream getResourceAsStream(ClassLoader loader, Class<?> originClass, String name) {
      try {
         if (bundleReferenceClass != null && originClass != null && bundleReferenceClass.isInstance(ReflectionHelper.class.getClassLoader())) {
            Bundle bundle = FrameworkUtil.getBundle(originClass);
            URL resourceUrl = bundle != null ? bundle.getEntry(name) : null;
            if (resourceUrl != null) {
               return resourceUrl.openStream();
            }
         }
      } catch (IOException var5) {
      }

      return loader.getResourceAsStream(name);
   }

   public static Class<?> getRawClass(Type type) {
      if (type == null) {
         return null;
      } else if (type instanceof GenericArrayType) {
         Type componentType = ((GenericArrayType)type).getGenericComponentType();
         if (!(componentType instanceof ParameterizedType) && !(componentType instanceof Class)) {
            return null;
         } else {
            Class<?> rawComponentClass = getRawClass(componentType);
            String forNameName = "[L" + rawComponentClass.getName() + ";";

            try {
               return Class.forName(forNameName);
            } catch (Throwable var5) {
               return null;
            }
         }
      } else if (type instanceof Class) {
         return (Class<?>)type;
      } else {
         if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType)type).getRawType();
            if (rawType instanceof Class) {
               return (Class<?>)rawType;
            }
         }

         return null;
      }
   }

   public static boolean isJaxbAvailable() {
      Class<?> aClass = AccessController.doPrivileged(classForNamePA("jakarta.xml.bind.JAXBException"));
      return aClass != null;
   }

   public static boolean isXmlTransformAvailable() {
      Class<?> aClass = AccessController.doPrivileged(classForNamePA("javax.xml.transform.Source"));
      return aClass != null;
   }

   public static class DeclaringClassInterfacePair {
      public final Class<?> concreteClass;
      public final Class<?> declaringClass;
      public final Type genericInterface;

      private DeclaringClassInterfacePair(Class<?> concreteClass, Class<?> declaringClass, Type genericInterface) {
         this.concreteClass = concreteClass;
         this.declaringClass = declaringClass;
         this.genericInterface = genericInterface;
      }
   }
}
