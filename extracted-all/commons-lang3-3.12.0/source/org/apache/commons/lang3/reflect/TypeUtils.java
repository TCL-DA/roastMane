package org.apache.commons.lang3.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.Builder;

public class TypeUtils {
   public static final WildcardType WILDCARD_ALL = wildcardType().withUpperBounds(Object.class).build();

   private static <T> StringBuilder appendAllTo(StringBuilder builder, String sep, T... types) {
      Validate.notEmpty(Validate.noNullElements(types));
      if (types.length > 0) {
         builder.append(toString(types[0]));

         for (int i = 1; i < types.length; i++) {
            builder.append(sep).append(toString(types[i]));
         }
      }

      return builder;
   }

   private static void appendRecursiveTypes(StringBuilder builder, int[] recursiveTypeIndexes, Type[] argumentTypes) {
      for (int i = 0; i < recursiveTypeIndexes.length; i++) {
         appendAllTo(builder.append('<'), ", ", argumentTypes[i].toString()).append('>');
      }

      Type[] argumentsFiltered = ArrayUtils.removeAll((Type[])argumentTypes, recursiveTypeIndexes);
      if (argumentsFiltered.length > 0) {
         appendAllTo(builder.append('<'), ", ", argumentsFiltered).append('>');
      }
   }

   private static String classToString(Class<?> cls) {
      if (cls.isArray()) {
         return toString(cls.getComponentType()) + "[]";
      } else {
         StringBuilder buf = new StringBuilder();
         if (cls.getEnclosingClass() != null) {
            buf.append(classToString(cls.getEnclosingClass())).append('.').append(cls.getSimpleName());
         } else {
            buf.append(cls.getName());
         }

         if (cls.getTypeParameters().length > 0) {
            buf.append('<');
            appendAllTo(buf, ", ", cls.getTypeParameters());
            buf.append('>');
         }

         return buf.toString();
      }
   }

   public static boolean containsTypeVariables(Type type) {
      if (type instanceof TypeVariable) {
         return true;
      } else if (type instanceof Class) {
         return ((Class)type).getTypeParameters().length > 0;
      } else if (type instanceof ParameterizedType) {
         for (Type arg : ((ParameterizedType)type).getActualTypeArguments()) {
            if (containsTypeVariables(arg)) {
               return true;
            }
         }

         return false;
      } else if (!(type instanceof WildcardType)) {
         return type instanceof GenericArrayType ? containsTypeVariables(((GenericArrayType)type).getGenericComponentType()) : false;
      } else {
         WildcardType wild = (WildcardType)type;
         return containsTypeVariables(getImplicitLowerBounds(wild)[0]) || containsTypeVariables(getImplicitUpperBounds(wild)[0]);
      }
   }

   private static boolean containsVariableTypeSameParametrizedTypeBound(TypeVariable<?> typeVariable, ParameterizedType parameterizedType) {
      return ArrayUtils.contains(typeVariable.getBounds(), parameterizedType);
   }

   public static Map<TypeVariable<?>, Type> determineTypeArguments(Class<?> cls, ParameterizedType superParameterizedType) {
      Validate.notNull(cls, "cls");
      Validate.notNull(superParameterizedType, "superParameterizedType");
      Class<?> superClass = getRawType(superParameterizedType);
      if (!isAssignable(cls, superClass)) {
         return null;
      } else if (cls.equals(superClass)) {
         return getTypeArguments(superParameterizedType, superClass, null);
      } else {
         Type midType = getClosestParentType(cls, superClass);
         if (midType instanceof Class) {
            return determineTypeArguments((Class<?>)midType, superParameterizedType);
         } else {
            ParameterizedType midParameterizedType = (ParameterizedType)midType;
            Class<?> midClass = getRawType(midParameterizedType);
            Map<TypeVariable<?>, Type> typeVarAssigns = determineTypeArguments(midClass, superParameterizedType);
            mapTypeVariablesToArguments(cls, midParameterizedType, typeVarAssigns);
            return typeVarAssigns;
         }
      }
   }

   private static boolean equals(GenericArrayType genericArrayType, Type type) {
      return type instanceof GenericArrayType && equals(genericArrayType.getGenericComponentType(), ((GenericArrayType)type).getGenericComponentType());
   }

   private static boolean equals(ParameterizedType parameterizedType, Type type) {
      if (type instanceof ParameterizedType) {
         ParameterizedType other = (ParameterizedType)type;
         if (equals(parameterizedType.getRawType(), other.getRawType()) && equals(parameterizedType.getOwnerType(), other.getOwnerType())) {
            return equals(parameterizedType.getActualTypeArguments(), other.getActualTypeArguments());
         }
      }

      return false;
   }

   public static boolean equals(Type type1, Type type2) {
      if (Objects.equals(type1, type2)) {
         return true;
      } else if (type1 instanceof ParameterizedType) {
         return equals((ParameterizedType)type1, type2);
      } else if (type1 instanceof GenericArrayType) {
         return equals((GenericArrayType)type1, type2);
      } else {
         return type1 instanceof WildcardType ? equals((WildcardType)type1, type2) : false;
      }
   }

   private static boolean equals(Type[] type1, Type[] type2) {
      if (type1.length == type2.length) {
         for (int i = 0; i < type1.length; i++) {
            if (!equals(type1[i], type2[i])) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static boolean equals(WildcardType wildcardType, Type type) {
      if (!(type instanceof WildcardType)) {
         return false;
      } else {
         WildcardType other = (WildcardType)type;
         return equals(getImplicitLowerBounds(wildcardType), getImplicitLowerBounds(other))
            && equals(getImplicitUpperBounds(wildcardType), getImplicitUpperBounds(other));
      }
   }

   private static Type[] extractTypeArgumentsFrom(Map<TypeVariable<?>, Type> mappings, TypeVariable<?>[] variables) {
      Type[] result = new Type[variables.length];
      int index = 0;

      for (TypeVariable<?> var : variables) {
         Validate.isTrue(mappings.containsKey(var), "missing argument mapping for %s", toString(var));
         result[index++] = mappings.get(var);
      }

      return result;
   }

   private static int[] findRecursiveTypes(ParameterizedType parameterizedType) {
      Type[] filteredArgumentTypes = Arrays.copyOf(parameterizedType.getActualTypeArguments(), parameterizedType.getActualTypeArguments().length);
      int[] indexesToRemove = new int[0];

      for (int i = 0; i < filteredArgumentTypes.length; i++) {
         if (filteredArgumentTypes[i] instanceof TypeVariable
            && containsVariableTypeSameParametrizedTypeBound((TypeVariable<?>)filteredArgumentTypes[i], parameterizedType)) {
            indexesToRemove = ArrayUtils.add(indexesToRemove, i);
         }
      }

      return indexesToRemove;
   }

   public static GenericArrayType genericArrayType(Type componentType) {
      return new TypeUtils.GenericArrayTypeImpl(Validate.notNull(componentType, "componentType"));
   }

   private static String genericArrayTypeToString(GenericArrayType genericArrayType) {
      return String.format("%s[]", toString(genericArrayType.getGenericComponentType()));
   }

   public static Type getArrayComponentType(Type type) {
      if (type instanceof Class) {
         Class<?> cls = (Class<?>)type;
         return cls.isArray() ? cls.getComponentType() : null;
      } else {
         return type instanceof GenericArrayType ? ((GenericArrayType)type).getGenericComponentType() : null;
      }
   }

   private static Type getClosestParentType(Class<?> cls, Class<?> superClass) {
      if (superClass.isInterface()) {
         Type[] interfaceTypes = cls.getGenericInterfaces();
         Type genericInterface = null;

         for (Type midType : interfaceTypes) {
            Class<?> midClass = null;
            if (midType instanceof ParameterizedType) {
               midClass = getRawType((ParameterizedType)midType);
            } else {
               if (!(midType instanceof Class)) {
                  throw new IllegalStateException("Unexpected generic interface type found: " + midType);
               }

               midClass = (Class<?>)midType;
            }

            if (isAssignable(midClass, superClass) && isAssignable(genericInterface, midClass)) {
               genericInterface = midType;
            }
         }

         if (genericInterface != null) {
            return genericInterface;
         }
      }

      return cls.getGenericSuperclass();
   }

   public static Type[] getImplicitBounds(TypeVariable<?> typeVariable) {
      Validate.notNull(typeVariable, "typeVariable");
      Type[] bounds = typeVariable.getBounds();
      return bounds.length == 0 ? new Type[]{Object.class} : normalizeUpperBounds(bounds);
   }

   public static Type[] getImplicitLowerBounds(WildcardType wildcardType) {
      Validate.notNull(wildcardType, "wildcardType");
      Type[] bounds = wildcardType.getLowerBounds();
      return bounds.length == 0 ? new Type[]{null} : bounds;
   }

   public static Type[] getImplicitUpperBounds(WildcardType wildcardType) {
      Validate.notNull(wildcardType, "wildcardType");
      Type[] bounds = wildcardType.getUpperBounds();
      return bounds.length == 0 ? new Type[]{Object.class} : normalizeUpperBounds(bounds);
   }

   private static Class<?> getRawType(ParameterizedType parameterizedType) {
      Type rawType = parameterizedType.getRawType();
      if (!(rawType instanceof Class)) {
         throw new IllegalStateException("Wait... What!? Type of rawType: " + rawType);
      } else {
         return (Class<?>)rawType;
      }
   }

   public static Class<?> getRawType(Type type, Type assigningType) {
      if (type instanceof Class) {
         return (Class<?>)type;
      } else if (type instanceof ParameterizedType) {
         return getRawType((ParameterizedType)type);
      } else if (type instanceof TypeVariable) {
         if (assigningType == null) {
            return null;
         } else {
            Object genericDeclaration = ((TypeVariable)type).getGenericDeclaration();
            if (!(genericDeclaration instanceof Class)) {
               return null;
            } else {
               Map<TypeVariable<?>, Type> typeVarAssigns = getTypeArguments(assigningType, (Class<?>)genericDeclaration);
               if (typeVarAssigns == null) {
                  return null;
               } else {
                  Type typeArgument = typeVarAssigns.get(type);
                  return typeArgument == null ? null : getRawType(typeArgument, assigningType);
               }
            }
         }
      } else if (type instanceof GenericArrayType) {
         Class<?> rawComponentType = getRawType(((GenericArrayType)type).getGenericComponentType(), assigningType);
         return Array.newInstance(rawComponentType, 0).getClass();
      } else if (type instanceof WildcardType) {
         return null;
      } else {
         throw new IllegalArgumentException("unknown type: " + type);
      }
   }

   private static Map<TypeVariable<?>, Type> getTypeArguments(Class<?> cls, Class<?> toClass, Map<TypeVariable<?>, Type> subtypeVarAssigns) {
      if (!isAssignable(cls, toClass)) {
         return null;
      } else {
         if (cls.isPrimitive()) {
            if (toClass.isPrimitive()) {
               return new HashMap<>();
            }

            cls = ClassUtils.primitiveToWrapper(cls);
         }

         HashMap<TypeVariable<?>, Type> typeVarAssigns = subtypeVarAssigns == null ? new HashMap<>() : new HashMap<>(subtypeVarAssigns);
         return (Map<TypeVariable<?>, Type>)(toClass.equals(cls)
            ? typeVarAssigns
            : getTypeArguments(getClosestParentType(cls, toClass), toClass, typeVarAssigns));
      }
   }

   public static Map<TypeVariable<?>, Type> getTypeArguments(ParameterizedType type) {
      return getTypeArguments(type, getRawType(type), null);
   }

   private static Map<TypeVariable<?>, Type> getTypeArguments(
      ParameterizedType parameterizedType, Class<?> toClass, Map<TypeVariable<?>, Type> subtypeVarAssigns
   ) {
      Class<?> cls = getRawType(parameterizedType);
      if (!isAssignable(cls, toClass)) {
         return null;
      } else {
         Type ownerType = parameterizedType.getOwnerType();
         Map<TypeVariable<?>, Type> typeVarAssigns;
         if (ownerType instanceof ParameterizedType) {
            ParameterizedType parameterizedOwnerType = (ParameterizedType)ownerType;
            typeVarAssigns = getTypeArguments(parameterizedOwnerType, getRawType(parameterizedOwnerType), subtypeVarAssigns);
         } else {
            typeVarAssigns = subtypeVarAssigns == null ? new HashMap<>() : new HashMap<>(subtypeVarAssigns);
         }

         Type[] typeArgs = parameterizedType.getActualTypeArguments();
         TypeVariable<?>[] typeParams = cls.getTypeParameters();

         for (int i = 0; i < typeParams.length; i++) {
            Type typeArg = typeArgs[i];
            typeVarAssigns.put(typeParams[i], typeVarAssigns.getOrDefault(typeArg, typeArg));
         }

         return toClass.equals(cls) ? typeVarAssigns : getTypeArguments(getClosestParentType(cls, toClass), toClass, typeVarAssigns);
      }
   }

   public static Map<TypeVariable<?>, Type> getTypeArguments(Type type, Class<?> toClass) {
      return getTypeArguments(type, toClass, null);
   }

   private static Map<TypeVariable<?>, Type> getTypeArguments(Type type, Class<?> toClass, Map<TypeVariable<?>, Type> subtypeVarAssigns) {
      if (type instanceof Class) {
         return getTypeArguments((Class<?>)type, toClass, subtypeVarAssigns);
      } else if (type instanceof ParameterizedType) {
         return getTypeArguments((ParameterizedType)type, toClass, subtypeVarAssigns);
      } else if (type instanceof GenericArrayType) {
         return getTypeArguments(
            ((GenericArrayType)type).getGenericComponentType(), toClass.isArray() ? toClass.getComponentType() : toClass, subtypeVarAssigns
         );
      } else if (type instanceof WildcardType) {
         for (Type bound : getImplicitUpperBounds((WildcardType)type)) {
            if (isAssignable(bound, toClass)) {
               return getTypeArguments(bound, toClass, subtypeVarAssigns);
            }
         }

         return null;
      } else if (type instanceof TypeVariable) {
         for (Type boundx : getImplicitBounds((TypeVariable<?>)type)) {
            if (isAssignable(boundx, toClass)) {
               return getTypeArguments(boundx, toClass, subtypeVarAssigns);
            }
         }

         return null;
      } else {
         throw new IllegalStateException("found an unhandled type: " + type);
      }
   }

   public static boolean isArrayType(Type type) {
      return type instanceof GenericArrayType || type instanceof Class && ((Class)type).isArray();
   }

   private static boolean isAssignable(Type type, Class<?> toClass) {
      if (type == null) {
         return toClass == null || !toClass.isPrimitive();
      } else if (toClass == null) {
         return false;
      } else if (toClass.equals(type)) {
         return true;
      } else if (type instanceof Class) {
         return ClassUtils.isAssignable((Class<?>)type, toClass);
      } else if (type instanceof ParameterizedType) {
         return isAssignable(getRawType((ParameterizedType)type), toClass);
      } else if (type instanceof TypeVariable) {
         for (Type bound : ((TypeVariable)type).getBounds()) {
            if (isAssignable(bound, toClass)) {
               return true;
            }
         }

         return false;
      } else if (!(type instanceof GenericArrayType)) {
         if (type instanceof WildcardType) {
            return false;
         } else {
            throw new IllegalStateException("found an unhandled type: " + type);
         }
      } else {
         return toClass.equals(Object.class)
            || toClass.isArray() && isAssignable(((GenericArrayType)type).getGenericComponentType(), toClass.getComponentType());
      }
   }

   private static boolean isAssignable(Type type, GenericArrayType toGenericArrayType, Map<TypeVariable<?>, Type> typeVarAssigns) {
      if (type == null) {
         return true;
      } else if (toGenericArrayType == null) {
         return false;
      } else if (toGenericArrayType.equals(type)) {
         return true;
      } else {
         Type toComponentType = toGenericArrayType.getGenericComponentType();
         if (!(type instanceof Class)) {
            if (type instanceof GenericArrayType) {
               return isAssignable(((GenericArrayType)type).getGenericComponentType(), toComponentType, typeVarAssigns);
            } else if (type instanceof WildcardType) {
               for (Type bound : getImplicitUpperBounds((WildcardType)type)) {
                  if (isAssignable(bound, toGenericArrayType)) {
                     return true;
                  }
               }

               return false;
            } else if (type instanceof TypeVariable) {
               for (Type boundx : getImplicitBounds((TypeVariable<?>)type)) {
                  if (isAssignable(boundx, toGenericArrayType)) {
                     return true;
                  }
               }

               return false;
            } else if (type instanceof ParameterizedType) {
               return false;
            } else {
               throw new IllegalStateException("found an unhandled type: " + type);
            }
         } else {
            Class<?> cls = (Class<?>)type;
            return cls.isArray() && isAssignable(cls.getComponentType(), toComponentType, typeVarAssigns);
         }
      }
   }

   private static boolean isAssignable(Type type, ParameterizedType toParameterizedType, Map<TypeVariable<?>, Type> typeVarAssigns) {
      if (type == null) {
         return true;
      } else if (toParameterizedType == null) {
         return false;
      } else if (type instanceof GenericArrayType) {
         return false;
      } else if (toParameterizedType.equals(type)) {
         return true;
      } else {
         Class<?> toClass = getRawType(toParameterizedType);
         Map<TypeVariable<?>, Type> fromTypeVarAssigns = getTypeArguments(type, toClass, null);
         if (fromTypeVarAssigns == null) {
            return false;
         } else if (fromTypeVarAssigns.isEmpty()) {
            return true;
         } else {
            Map<TypeVariable<?>, Type> toTypeVarAssigns = getTypeArguments(toParameterizedType, toClass, typeVarAssigns);

            for (TypeVariable<?> var : toTypeVarAssigns.keySet()) {
               Type toTypeArg = unrollVariableAssignments(var, toTypeVarAssigns);
               Type fromTypeArg = unrollVariableAssignments(var, fromTypeVarAssigns);
               if ((toTypeArg != null || !(fromTypeArg instanceof Class))
                  && fromTypeArg != null
                  && toTypeArg != null
                  && !toTypeArg.equals(fromTypeArg)
                  && (!(toTypeArg instanceof WildcardType) || !isAssignable(fromTypeArg, toTypeArg, typeVarAssigns))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public static boolean isAssignable(Type type, Type toType) {
      return isAssignable(type, toType, null);
   }

   private static boolean isAssignable(Type type, Type toType, Map<TypeVariable<?>, Type> typeVarAssigns) {
      if (toType == null || toType instanceof Class) {
         return isAssignable(type, (Class<?>)toType);
      } else if (toType instanceof ParameterizedType) {
         return isAssignable(type, (ParameterizedType)toType, typeVarAssigns);
      } else if (toType instanceof GenericArrayType) {
         return isAssignable(type, (GenericArrayType)toType, typeVarAssigns);
      } else if (toType instanceof WildcardType) {
         return isAssignable(type, (WildcardType)toType, typeVarAssigns);
      } else if (toType instanceof TypeVariable) {
         return isAssignable(type, (TypeVariable<?>)toType, typeVarAssigns);
      } else {
         throw new IllegalStateException("found an unhandled type: " + toType);
      }
   }

   private static boolean isAssignable(Type type, TypeVariable<?> toTypeVariable, Map<TypeVariable<?>, Type> typeVarAssigns) {
      if (type == null) {
         return true;
      } else if (toTypeVariable == null) {
         return false;
      } else if (toTypeVariable.equals(type)) {
         return true;
      } else {
         if (type instanceof TypeVariable) {
            Type[] bounds = getImplicitBounds((TypeVariable<?>)type);

            for (Type bound : bounds) {
               if (isAssignable(bound, toTypeVariable, typeVarAssigns)) {
                  return true;
               }
            }
         }

         if (!(type instanceof Class) && !(type instanceof ParameterizedType) && !(type instanceof GenericArrayType) && !(type instanceof WildcardType)) {
            throw new IllegalStateException("found an unhandled type: " + type);
         } else {
            return false;
         }
      }
   }

   private static boolean isAssignable(Type type, WildcardType toWildcardType, Map<TypeVariable<?>, Type> typeVarAssigns) {
      if (type == null) {
         return true;
      } else if (toWildcardType == null) {
         return false;
      } else if (toWildcardType.equals(type)) {
         return true;
      } else {
         Type[] toUpperBounds = getImplicitUpperBounds(toWildcardType);
         Type[] toLowerBounds = getImplicitLowerBounds(toWildcardType);
         if (!(type instanceof WildcardType)) {
            for (Type toBound : toUpperBounds) {
               if (!isAssignable(type, substituteTypeVariables(toBound, typeVarAssigns), typeVarAssigns)) {
                  return false;
               }
            }

            for (Type toBoundx : toLowerBounds) {
               if (!isAssignable(substituteTypeVariables(toBoundx, typeVarAssigns), type, typeVarAssigns)) {
                  return false;
               }
            }

            return true;
         } else {
            WildcardType wildcardType = (WildcardType)type;
            Type[] upperBounds = getImplicitUpperBounds(wildcardType);
            Type[] lowerBounds = getImplicitLowerBounds(wildcardType);

            for (Type toBoundxx : toUpperBounds) {
               toBoundxx = substituteTypeVariables(toBoundxx, typeVarAssigns);

               for (Type bound : upperBounds) {
                  if (!isAssignable(bound, toBoundxx, typeVarAssigns)) {
                     return false;
                  }
               }
            }

            for (Type toBoundxx : toLowerBounds) {
               toBoundxx = substituteTypeVariables(toBoundxx, typeVarAssigns);

               for (Type boundx : lowerBounds) {
                  if (!isAssignable(toBoundxx, boundx, typeVarAssigns)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public static boolean isInstance(Object value, Type type) {
      if (type == null) {
         return false;
      } else {
         return value == null ? !(type instanceof Class) || !((Class)type).isPrimitive() : isAssignable(value.getClass(), type, null);
      }
   }

   private static <T> void mapTypeVariablesToArguments(Class<T> cls, ParameterizedType parameterizedType, Map<TypeVariable<?>, Type> typeVarAssigns) {
      Type ownerType = parameterizedType.getOwnerType();
      if (ownerType instanceof ParameterizedType) {
         mapTypeVariablesToArguments(cls, (ParameterizedType)ownerType, typeVarAssigns);
      }

      Type[] typeArgs = parameterizedType.getActualTypeArguments();
      TypeVariable<?>[] typeVars = getRawType(parameterizedType).getTypeParameters();
      List<TypeVariable<Class<T>>> typeVarList = Arrays.asList(cls.getTypeParameters());

      for (int i = 0; i < typeArgs.length; i++) {
         TypeVariable<?> typeVar = typeVars[i];
         Type typeArg = typeArgs[i];
         if (typeVarList.contains(typeArg) && typeVarAssigns.containsKey(typeVar)) {
            typeVarAssigns.put((TypeVariable<?>)typeArg, typeVarAssigns.get(typeVar));
         }
      }
   }

   public static Type[] normalizeUpperBounds(Type[] bounds) {
      Validate.notNull(bounds, "bounds");
      if (bounds.length < 2) {
         return bounds;
      } else {
         Set<Type> types = new HashSet<>(bounds.length);

         for (Type type1 : bounds) {
            boolean subtypeFound = false;

            for (Type type2 : bounds) {
               if (type1 != type2 && isAssignable(type2, type1, null)) {
                  subtypeFound = true;
                  break;
               }
            }

            if (!subtypeFound) {
               types.add(type1);
            }
         }

         return types.toArray(ArrayUtils.EMPTY_TYPE_ARRAY);
      }
   }

   public static final ParameterizedType parameterize(Class<?> rawClass, Map<TypeVariable<?>, Type> typeVariableMap) {
      Validate.notNull(rawClass, "rawClass");
      Validate.notNull(typeVariableMap, "typeVariableMap");
      return parameterizeWithOwner(null, rawClass, extractTypeArgumentsFrom(typeVariableMap, rawClass.getTypeParameters()));
   }

   public static final ParameterizedType parameterize(Class<?> rawClass, Type... typeArguments) {
      return parameterizeWithOwner(null, rawClass, typeArguments);
   }

   private static String parameterizedTypeToString(ParameterizedType parameterizedType) {
      StringBuilder builder = new StringBuilder();
      Type useOwner = parameterizedType.getOwnerType();
      Class<?> raw = (Class<?>)parameterizedType.getRawType();
      if (useOwner == null) {
         builder.append(raw.getName());
      } else {
         if (useOwner instanceof Class) {
            builder.append(((Class)useOwner).getName());
         } else {
            builder.append(useOwner.toString());
         }

         builder.append('.').append(raw.getSimpleName());
      }

      int[] recursiveTypeIndexes = findRecursiveTypes(parameterizedType);
      if (recursiveTypeIndexes.length > 0) {
         appendRecursiveTypes(builder, recursiveTypeIndexes, parameterizedType.getActualTypeArguments());
      } else {
         appendAllTo(builder.append('<'), ", ", parameterizedType.getActualTypeArguments()).append('>');
      }

      return builder.toString();
   }

   public static final ParameterizedType parameterizeWithOwner(Type owner, Class<?> rawClass, Map<TypeVariable<?>, Type> typeVariableMap) {
      Validate.notNull(rawClass, "rawClass");
      Validate.notNull(typeVariableMap, "typeVariableMap");
      return parameterizeWithOwner(owner, rawClass, extractTypeArgumentsFrom(typeVariableMap, rawClass.getTypeParameters()));
   }

   public static final ParameterizedType parameterizeWithOwner(Type owner, Class<?> rawClass, Type... typeArguments) {
      Validate.notNull(rawClass, "rawClass");
      Type useOwner;
      if (rawClass.getEnclosingClass() == null) {
         Validate.isTrue(owner == null, "no owner allowed for top-level %s", rawClass);
         useOwner = null;
      } else if (owner == null) {
         useOwner = rawClass.getEnclosingClass();
      } else {
         Validate.isTrue(isAssignable(owner, rawClass.getEnclosingClass()), "%s is invalid owner type for parameterized %s", owner, rawClass);
         useOwner = owner;
      }

      Validate.noNullElements(typeArguments, "null type argument at index %s");
      Validate.isTrue(
         rawClass.getTypeParameters().length == typeArguments.length,
         "invalid number of type parameters specified: expected %d, got %d",
         rawClass.getTypeParameters().length,
         typeArguments.length
      );
      return new TypeUtils.ParameterizedTypeImpl(rawClass, useOwner, typeArguments);
   }

   private static Type substituteTypeVariables(Type type, Map<TypeVariable<?>, Type> typeVarAssigns) {
      if (type instanceof TypeVariable && typeVarAssigns != null) {
         Type replacementType = typeVarAssigns.get(type);
         if (replacementType == null) {
            throw new IllegalArgumentException("missing assignment type for type variable " + type);
         } else {
            return replacementType;
         }
      } else {
         return type;
      }
   }

   public static String toLongString(TypeVariable<?> typeVariable) {
      Validate.notNull(typeVariable, "typeVariable");
      StringBuilder buf = new StringBuilder();
      GenericDeclaration d = typeVariable.getGenericDeclaration();
      if (d instanceof Class) {
         Class<?> c;
         for (c = (Class<?>)d; c.getEnclosingClass() != null; c = c.getEnclosingClass()) {
            buf.insert(0, c.getSimpleName()).insert(0, '.');
         }

         buf.insert(0, c.getName());
      } else if (d instanceof Type) {
         buf.append(toString((Type)d));
      } else {
         buf.append(d);
      }

      return buf.append(':').append(typeVariableToString(typeVariable)).toString();
   }

   private static <T> String toString(T object) {
      return object instanceof Type ? toString((Type)object) : object.toString();
   }

   public static String toString(Type type) {
      Validate.notNull(type);
      if (type instanceof Class) {
         return classToString((Class<?>)type);
      } else if (type instanceof ParameterizedType) {
         return parameterizedTypeToString((ParameterizedType)type);
      } else if (type instanceof WildcardType) {
         return wildcardTypeToString((WildcardType)type);
      } else if (type instanceof TypeVariable) {
         return typeVariableToString((TypeVariable<?>)type);
      } else if (type instanceof GenericArrayType) {
         return genericArrayTypeToString((GenericArrayType)type);
      } else {
         throw new IllegalArgumentException(ObjectUtils.identityToString(type));
      }
   }

   public static boolean typesSatisfyVariables(Map<TypeVariable<?>, Type> typeVariableMap) {
      Validate.notNull(typeVariableMap, "typeVariableMap");

      for (Entry<TypeVariable<?>, Type> entry : typeVariableMap.entrySet()) {
         TypeVariable<?> typeVar = entry.getKey();
         Type type = entry.getValue();

         for (Type bound : getImplicitBounds(typeVar)) {
            if (!isAssignable(type, substituteTypeVariables(bound, typeVariableMap), typeVariableMap)) {
               return false;
            }
         }
      }

      return true;
   }

   private static String typeVariableToString(TypeVariable<?> typeVariable) {
      StringBuilder buf = new StringBuilder(typeVariable.getName());
      Type[] bounds = typeVariable.getBounds();
      if (bounds.length > 0 && (bounds.length != 1 || !Object.class.equals(bounds[0]))) {
         buf.append(" extends ");
         appendAllTo(buf, " & ", typeVariable.getBounds());
      }

      return buf.toString();
   }

   private static Type[] unrollBounds(Map<TypeVariable<?>, Type> typeArguments, Type[] bounds) {
      Type[] result = bounds;

      for (int i = 0; i < result.length; i++) {
         Type unrolled = unrollVariables(typeArguments, result[i]);
         if (unrolled == null) {
            result = ArrayUtils.remove((Type[])result, i--);
         } else {
            result[i] = unrolled;
         }
      }

      return result;
   }

   private static Type unrollVariableAssignments(TypeVariable<?> typeVariable, Map<TypeVariable<?>, Type> typeVarAssigns) {
      while (true) {
         Type result = typeVarAssigns.get(typeVariable);
         if (!(result instanceof TypeVariable) || result.equals(typeVariable)) {
            return result;
         }

         typeVariable = (TypeVariable<?>)result;
      }
   }

   public static Type unrollVariables(Map<TypeVariable<?>, Type> typeArguments, Type type) {
      if (typeArguments == null) {
         typeArguments = Collections.emptyMap();
      }

      if (containsTypeVariables(type)) {
         if (type instanceof TypeVariable) {
            return unrollVariables(typeArguments, typeArguments.get(type));
         }

         if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType)type;
            Map<TypeVariable<?>, Type> parameterizedTypeArguments;
            if (p.getOwnerType() == null) {
               parameterizedTypeArguments = typeArguments;
            } else {
               parameterizedTypeArguments = new HashMap<>(typeArguments);
               parameterizedTypeArguments.putAll(getTypeArguments(p));
            }

            Type[] args = p.getActualTypeArguments();

            for (int i = 0; i < args.length; i++) {
               Type unrolled = unrollVariables(parameterizedTypeArguments, args[i]);
               if (unrolled != null) {
                  args[i] = unrolled;
               }
            }

            return parameterizeWithOwner(p.getOwnerType(), (Class<?>)p.getRawType(), args);
         }

         if (type instanceof WildcardType) {
            WildcardType wild = (WildcardType)type;
            return wildcardType()
               .withUpperBounds(unrollBounds(typeArguments, wild.getUpperBounds()))
               .withLowerBounds(unrollBounds(typeArguments, wild.getLowerBounds()))
               .build();
         }
      }

      return type;
   }

   public static TypeUtils.WildcardTypeBuilder wildcardType() {
      return new TypeUtils.WildcardTypeBuilder();
   }

   private static String wildcardTypeToString(WildcardType wildcardType) {
      StringBuilder buf = new StringBuilder().append('?');
      Type[] lowerBounds = wildcardType.getLowerBounds();
      Type[] upperBounds = wildcardType.getUpperBounds();
      if (lowerBounds.length <= 1 && (lowerBounds.length != 1 || lowerBounds[0] == null)) {
         if (upperBounds.length > 1 || upperBounds.length == 1 && !Object.class.equals(upperBounds[0])) {
            appendAllTo(buf.append(" extends "), " & ", upperBounds);
         }
      } else {
         appendAllTo(buf.append(" super "), " & ", lowerBounds);
      }

      return buf.toString();
   }

   public static <T> Typed<T> wrap(Class<T> type) {
      return wrap(type);
   }

   public static <T> Typed<T> wrap(Type type) {
      return () -> type;
   }

   private static final class GenericArrayTypeImpl implements GenericArrayType {
      private final Type componentType;

      private GenericArrayTypeImpl(Type componentType) {
         this.componentType = componentType;
      }

      @Override
      public boolean equals(Object obj) {
         return obj == this || obj instanceof GenericArrayType && TypeUtils.equals(this, (GenericArrayType)obj);
      }

      @Override
      public Type getGenericComponentType() {
         return this.componentType;
      }

      @Override
      public int hashCode() {
         int result = 1072;
         return result | this.componentType.hashCode();
      }

      @Override
      public String toString() {
         return TypeUtils.toString(this);
      }
   }

   private static final class ParameterizedTypeImpl implements ParameterizedType {
      private final Class<?> raw;
      private final Type useOwner;
      private final Type[] typeArguments;

      private ParameterizedTypeImpl(Class<?> rawClass, Type useOwner, Type[] typeArguments) {
         this.raw = rawClass;
         this.useOwner = useOwner;
         this.typeArguments = Arrays.copyOf(typeArguments, typeArguments.length, Type[].class);
      }

      @Override
      public boolean equals(Object obj) {
         return obj == this || obj instanceof ParameterizedType && TypeUtils.equals(this, (ParameterizedType)obj);
      }

      @Override
      public Type[] getActualTypeArguments() {
         return (Type[])this.typeArguments.clone();
      }

      @Override
      public Type getOwnerType() {
         return this.useOwner;
      }

      @Override
      public Type getRawType() {
         return this.raw;
      }

      @Override
      public int hashCode() {
         int result = 1136;
         result |= this.raw.hashCode();
         result <<= 4;
         result |= Objects.hashCode(this.useOwner);
         result <<= 8;
         return result | Arrays.hashCode((Object[])this.typeArguments);
      }

      @Override
      public String toString() {
         return TypeUtils.toString(this);
      }
   }

   public static class WildcardTypeBuilder implements Builder<WildcardType> {
      private Type[] upperBounds;
      private Type[] lowerBounds;

      private WildcardTypeBuilder() {
      }

      public WildcardType build() {
         return new TypeUtils.WildcardTypeImpl(this.upperBounds, this.lowerBounds);
      }

      public TypeUtils.WildcardTypeBuilder withLowerBounds(Type... bounds) {
         this.lowerBounds = bounds;
         return this;
      }

      public TypeUtils.WildcardTypeBuilder withUpperBounds(Type... bounds) {
         this.upperBounds = bounds;
         return this;
      }
   }

   private static final class WildcardTypeImpl implements WildcardType {
      private final Type[] upperBounds;
      private final Type[] lowerBounds;

      private WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
         this.upperBounds = ObjectUtils.defaultIfNull(upperBounds, ArrayUtils.EMPTY_TYPE_ARRAY);
         this.lowerBounds = ObjectUtils.defaultIfNull(lowerBounds, ArrayUtils.EMPTY_TYPE_ARRAY);
      }

      @Override
      public boolean equals(Object obj) {
         return obj == this || obj instanceof WildcardType && TypeUtils.equals(this, (WildcardType)obj);
      }

      @Override
      public Type[] getLowerBounds() {
         return (Type[])this.lowerBounds.clone();
      }

      @Override
      public Type[] getUpperBounds() {
         return (Type[])this.upperBounds.clone();
      }

      @Override
      public int hashCode() {
         int result = 18688;
         result |= Arrays.hashCode((Object[])this.upperBounds);
         result <<= 8;
         return result | Arrays.hashCode((Object[])this.lowerBounds);
      }

      @Override
      public String toString() {
         return TypeUtils.toString(this);
      }
   }
}
