package org.glassfish.jersey.message.filtering.spi;

import jakarta.xml.bind.JAXBElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.security.AccessController;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.glassfish.jersey.internal.util.ReflectionHelper;

public final class FilteringHelper {
   public static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];
   private static final ConcurrentMap<Type, Class<?>> ENTITY_CLASSES = new ConcurrentHashMap<>();

   public static boolean filterableEntityClass(Class<?> clazz) {
      return !ReflectionHelper.isPrimitive(clazz) && !clazz.getPackage().getName().startsWith("java.");
   }

   public static Class<?> getEntityClass(Type genericType) {
      if (!ENTITY_CLASSES.containsKey(genericType)) {
         ENTITY_CLASSES.putIfAbsent(genericType, _getEntityClass(genericType));
      }

      return ENTITY_CLASSES.get(genericType);
   }

   private static Class<?> _getEntityClass(Type genericType) {
      if (null == genericType) {
         return Object.class;
      } else if (genericType instanceof Class && genericType != JAXBElement.class) {
         Class<?> clazz = (Class<?>)genericType;
         return clazz.isArray() ? _getEntityClass(clazz.getComponentType()) : clazz;
      } else if (genericType instanceof ParameterizedType) {
         ParameterizedType parameterizedType = (ParameterizedType)genericType;
         Type[] arguments = parameterizedType.getActualTypeArguments();
         Type type = parameterizedType.getRawType() == Map.class ? arguments[1] : arguments[0];
         if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType)type).getRawType();
            if (rawType == JAXBElement.class) {
               return _getEntityClass(type);
            }
         } else if (type instanceof WildcardType) {
            Type[] upperTypes = ((WildcardType)type).getUpperBounds();
            if (upperTypes.length > 0) {
               Type upperType = upperTypes[0];
               if (upperType instanceof Class) {
                  return (Class<?>)upperType;
               }
            }
         } else if (JAXBElement.class == type || type instanceof TypeVariable) {
            return Object.class;
         }

         return (Class<?>)type;
      } else if (genericType instanceof GenericArrayType) {
         GenericArrayType genericArrayType = (GenericArrayType)genericType;
         return _getEntityClass(genericArrayType.getGenericComponentType());
      } else {
         return Object.class;
      }
   }

   public static Map<String, Method> getPropertyMethods(Class<?> clazz, boolean isGetter) {
      Map<String, Method> methods = new HashMap<>();

      for (Method method : (Collection)AccessController.doPrivileged(ReflectionHelper.getDeclaredMethodsPA(clazz))) {
         if (isGetter && ReflectionHelper.isGetter(method) || !isGetter && ReflectionHelper.isSetter(method)) {
            methods.put(ReflectionHelper.getPropertyName(method), method);
         }
      }

      Class<?> parent = clazz.getSuperclass();
      if (parent != null && !parent.getPackage().getName().startsWith("java.lang")) {
         methods.putAll(getPropertyMethods(parent, isGetter));
      }

      return methods;
   }

   public static Set<String> getDefaultFilteringScope() {
      return Collections.singleton(ScopeProvider.DEFAULT_SCOPE);
   }

   private FilteringHelper() {
   }
}
