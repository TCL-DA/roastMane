package org.eclipse.yasson.internal;

import jakarta.json.bind.JsonbException;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

public class ReflectionUtils {
   private static final Logger LOGGER = Logger.getLogger(ReflectionUtils.class.getName());

   private ReflectionUtils() {
      throw new IllegalStateException("Utility classes should not be instantiated.");
   }

   public static Optional<Class<?>> getOptionalRawType(Type type) {
      if (type instanceof Class) {
         return Optional.of((Class<?>)type);
      } else if (type instanceof ParameterizedType) {
         return Optional.of((Class<?>)((ParameterizedType)type).getRawType());
      } else if (type instanceof GenericArrayType) {
         return Optional.of(((GenericArrayType)type).getClass());
      } else {
         if (type instanceof TypeVariable) {
            TypeVariable<?> typeVariable = TypeVariable.class.cast(type);
            if (Objects.nonNull(typeVariable.getBounds())) {
               Optional<Class<?>> specializedClass = Optional.empty();

               for (Type bound : typeVariable.getBounds()) {
                  Optional<Class<?>> boundRawType = getOptionalRawType(bound);
                  if (boundRawType.isPresent()
                     && !Object.class.equals(boundRawType.get())
                     && (!specializedClass.isPresent() || specializedClass.get().isAssignableFrom(boundRawType.get()))) {
                     specializedClass = Optional.of(boundRawType.get());
                  }
               }

               return specializedClass;
            }
         }

         return Optional.empty();
      }
   }

   public static Class<?> getRawType(Type type) {
      return getOptionalRawType(type).orElseThrow(() -> new JsonbException(Messages.getMessage(MessageKeys.TYPE_RESOLUTION_ERROR, type)));
   }

   public static Class<?> resolveRawType(List<Type> chain, Type type) {
      if (type instanceof Class) {
         return (Class<?>)type;
      } else {
         return type instanceof ParameterizedType ? (Class)((ParameterizedType)type).getRawType() : getRawType(resolveType(chain, type));
      }
   }

   public static Type resolveType(List<Type> chain, Type type) {
      return resolveType(chain, type, true);
   }

   private static Type resolveType(List<Type> chain, Type type, boolean warn) {
      if (type instanceof GenericArrayType) {
         Type toResolve = ((GenericArrayType)type).getGenericComponentType();
         Type resolved = resolveType(chain, toResolve);
         return new ReflectionUtils.GenericArrayTypeImpl(resolved);
      } else if (type instanceof WildcardType) {
         return resolveMostSpecificBound(chain, (WildcardType)type, warn);
      } else if (type instanceof TypeVariable) {
         return resolveItemVariableType(chain, (TypeVariable<?>)type, warn);
      } else {
         return type instanceof ParameterizedType ? resolveTypeArguments((ParameterizedType)type, chain.get(chain.size() - 1)) : type;
      }
   }

   public static Optional<Type> resolveOptionalType(List<Type> chain, Type type) {
      try {
         return Optional.of(resolveType(chain, type, false));
      } catch (RuntimeException var3) {
         return Optional.empty();
      }
   }

   public static Type resolveItemVariableType(List<Type> chain, TypeVariable<?> typeVariable, boolean warn) {
      Type returnType = typeVariable;

      for (int i = chain.size() - 1; i >= 0; i--) {
         Type type = chain.get(i);
         Type tmp = new VariableTypeInheritanceSearch().searchParametrizedType(type, (TypeVariable<?>)returnType);
         if (tmp != null) {
            returnType = tmp;
         }

         if (!(returnType instanceof TypeVariable)) {
            break;
         }
      }

      return (Type)(returnType instanceof TypeVariable ? Object.class : returnType);
   }

   public static Type resolveTypeArguments(ParameterizedType typeToResolve, Type typeToSearch) {
      Type[] unresolvedArgs = typeToResolve.getActualTypeArguments();
      Type[] resolvedArgs = new Type[unresolvedArgs.length];

      for (int i = 0; i < unresolvedArgs.length; i++) {
         Type unresolvedArg = unresolvedArgs[i];
         if (!(unresolvedArg instanceof TypeVariable) && !(unresolvedArg instanceof GenericArrayType)) {
            resolvedArgs[i] = unresolvedArg;
         } else {
            Type variableType = unresolvedArg;
            if (unresolvedArg instanceof GenericArrayType) {
               variableType = ((GenericArrayType)unresolvedArg).getGenericComponentType();
            }

            resolvedArgs[i] = new VariableTypeInheritanceSearch().searchParametrizedType(typeToSearch, (TypeVariable<?>)variableType);
            if (resolvedArgs[i] == null) {
               if (typeToSearch instanceof Class) {
                  return Object.class;
               }

               throw new IllegalStateException(Messages.getMessage(MessageKeys.GENERIC_BOUND_NOT_FOUND, variableType, typeToSearch));
            }
         }

         if (resolvedArgs[i] instanceof ParameterizedType) {
            resolvedArgs[i] = resolveTypeArguments((ParameterizedType)resolvedArgs[i], typeToSearch);
         } else if (unresolvedArg instanceof GenericArrayType) {
            resolvedArgs[i] = new ReflectionUtils.GenericArrayTypeImpl(resolvedArgs[i]);
         }
      }

      return (Type)(Arrays.equals((Object[])resolvedArgs, (Object[])unresolvedArgs)
         ? typeToResolve
         : new ResolvedParameterizedType(typeToResolve, resolvedArgs));
   }

   public static <T> T createNoArgConstructorInstance(Constructor<T> constructor) {
      Objects.requireNonNull(constructor);

      try {
         return constructor.newInstance();
      } catch (IllegalAccessException | InvocationTargetException | InstantiationException var2) {
         throw new JsonbException("Can't create instance", var2);
      }
   }

   public static <T> Constructor<T> getDefaultConstructor(Class<T> clazz, boolean required) {
      return AccessController.doPrivileged((PrivilegedAction<Constructor<T>>)(() -> {
         try {
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
            if (declaredConstructor.getModifiers() == 4) {
               declaredConstructor.setAccessible(true);
            }

            return declaredConstructor;
         } catch (RuntimeException | NoSuchMethodException var3) {
            if (required) {
               throw new JsonbException(Messages.getMessage(MessageKeys.NO_DEFAULT_CONSTRUCTOR, clazz), var3);
            } else {
               return null;
            }
         }
      }));
   }

   public static ParameterizedType findParameterizedType(Class<?> classToSearch, Class<?> parameterizedInterface) {
      for (Class current = classToSearch; current != Object.class; current = current.getSuperclass()) {
         for (Type currentInterface : current.getGenericInterfaces()) {
            if (currentInterface instanceof ParameterizedType
               && parameterizedInterface.isAssignableFrom(getRawType(((ParameterizedType)currentInterface).getRawType()))) {
               return (ParameterizedType)currentInterface;
            }
         }
      }

      throw new JsonbException(Messages.getMessage(MessageKeys.NON_PARAMETRIZED_TYPE, parameterizedInterface));
   }

   public static boolean isResolvedType(Type type) {
      if (type instanceof ParameterizedType) {
         for (Type typeArg : ((ParameterizedType)type).getActualTypeArguments()) {
            if (!isResolvedType(typeArg)) {
               return false;
            }
         }

         return true;
      } else {
         return type instanceof Class;
      }
   }

   private static ParameterizedType findParameterizedSuperclass(Type type) {
      if (type == null || type instanceof ParameterizedType) {
         return (ParameterizedType)type;
      } else if (!(type instanceof Class)) {
         throw new JsonbException("Can't resolve ParameterizedType superclass for: " + type);
      } else {
         return findParameterizedSuperclass(((Class)type).getGenericSuperclass());
      }
   }

   private static Type resolveMostSpecificBound(List<Type> chain, WildcardType wildcardType, boolean warn) {
      Class<?> result = Object.class;

      for (Type upperBound : wildcardType.getUpperBounds()) {
         result = getMostSpecificBound(chain, result, upperBound, warn);
      }

      for (Type lowerBound : wildcardType.getLowerBounds()) {
         result = getMostSpecificBound(chain, result, lowerBound, warn);
      }

      return result;
   }

   private static Class<?> getMostSpecificBound(List<Type> chain, Class<?> result, Type bound, boolean warn) {
      if (bound == Object.class) {
         return result;
      } else {
         Type resolvedBoundType = bound instanceof TypeVariable ? resolveType(chain, bound, warn) : bound;
         Class<?> boundRawType = getRawType(resolvedBoundType);
         if (result.isAssignableFrom(boundRawType)) {
            result = boundRawType;
         }

         return result;
      }
   }

   public static final class GenericArrayTypeImpl implements GenericArrayType {
      private final Type genericComponentType;

      private GenericArrayTypeImpl(Type ct) {
         this.genericComponentType = ct;
      }

      @Override
      public Type getGenericComponentType() {
         return this.genericComponentType;
      }

      @Override
      public String toString() {
         return this.getGenericComponentType().getTypeName() + "[]";
      }

      @Override
      public boolean equals(Object o) {
         if (o instanceof GenericArrayType) {
            GenericArrayType that = (GenericArrayType)o;
            return Objects.equals(this.genericComponentType, that.getGenericComponentType());
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.genericComponentType);
      }
   }
}
