package org.glassfish.hk2.utilities.reflection;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class TypeChecker {
   public static boolean isRawTypeSafe(Type requiredType, Type beanType) {
      Class<?> requiredClass = ReflectionHelper.getRawClass(requiredType);
      if (requiredClass == null) {
         return false;
      } else {
         requiredClass = ReflectionHelper.translatePrimitiveType(requiredClass);
         Class<?> beanClass = ReflectionHelper.getRawClass(beanType);
         if (beanClass == null) {
            return false;
         } else {
            beanClass = ReflectionHelper.translatePrimitiveType(beanClass);
            if (!requiredClass.isAssignableFrom(beanClass)) {
               return false;
            } else if (requiredType instanceof Class || requiredType instanceof GenericArrayType) {
               return true;
            } else if (!(requiredType instanceof ParameterizedType)) {
               throw new IllegalArgumentException("requiredType " + requiredType + " is of unknown type");
            } else {
               ParameterizedType requiredPT = (ParameterizedType)requiredType;
               Type[] requiredTypeVariables = requiredPT.getActualTypeArguments();
               Type[] beanTypeVariables;
               if (beanType instanceof Class) {
                  beanTypeVariables = ((Class)beanType).getTypeParameters();
               } else {
                  if (!(beanType instanceof ParameterizedType)) {
                     throw new IllegalArgumentException("Uknown beanType " + beanType);
                  }

                  beanTypeVariables = ((ParameterizedType)beanType).getActualTypeArguments();
               }

               if (requiredTypeVariables.length != beanTypeVariables.length) {
                  return false;
               } else {
                  for (int lcv = 0; lcv < requiredTypeVariables.length; lcv++) {
                     Type requiredTypeVariable = requiredTypeVariables[lcv];
                     Type beanTypeVariable = beanTypeVariables[lcv];
                     if (isActualType(requiredTypeVariable) && isActualType(beanTypeVariable)) {
                        if (!isRawTypeSafe(requiredTypeVariable, beanTypeVariable)) {
                           return false;
                        }
                     } else if (isArrayType(requiredTypeVariable) && isArrayType(beanTypeVariable)) {
                        Type requiredArrayType = getArrayType(requiredTypeVariable);
                        Type beanArrayType = getArrayType(beanTypeVariable);
                        if (!isRawTypeSafe(requiredArrayType, beanArrayType)) {
                           return false;
                        }
                     } else if (isWildcard(requiredTypeVariable) && isActualType(beanTypeVariable)) {
                        WildcardType wt = getWildcard(requiredTypeVariable);
                        Class<?> beanActualType = ReflectionHelper.getRawClass(beanTypeVariable);
                        if (!isWildcardActualSafe(wt, beanActualType)) {
                           return false;
                        }
                     } else if (isWildcard(requiredTypeVariable) && isTypeVariable(beanTypeVariable)) {
                        WildcardType wt = getWildcard(requiredTypeVariable);
                        TypeVariable<?> tv = getTypeVariable(beanTypeVariable);
                        if (!isWildcardTypeVariableSafe(wt, tv)) {
                           return false;
                        }
                     } else if (isActualType(requiredTypeVariable) && isTypeVariable(beanTypeVariable)) {
                        Class<?> requiredActual = ReflectionHelper.getRawClass(requiredTypeVariable);
                        TypeVariable<?> tv = getTypeVariable(beanTypeVariable);
                        if (!isActualTypeVariableSafe(requiredActual, tv)) {
                           return false;
                        }
                     } else {
                        if (!isTypeVariable(requiredTypeVariable) || !isTypeVariable(beanTypeVariable)) {
                           return false;
                        }

                        TypeVariable<?> rtv = getTypeVariable(requiredTypeVariable);
                        TypeVariable<?> btv = getTypeVariable(beanTypeVariable);
                        if (!isTypeVariableTypeVariableSafe(rtv, btv)) {
                           return false;
                        }
                     }
                  }

                  return true;
               }
            }
         }
      }
   }

   private static boolean isTypeVariableTypeVariableSafe(TypeVariable<?> rtv, TypeVariable<?> btv) {
      Class<?> rtvBound = getBound(rtv.getBounds());
      if (rtvBound == null) {
         return false;
      } else {
         Class<?> btvBound = getBound(btv.getBounds());
         return btvBound == null ? false : btvBound.isAssignableFrom(rtvBound);
      }
   }

   private static boolean isActualTypeVariableSafe(Class<?> actual, TypeVariable<?> tv) {
      Class<?> tvBound = getBound(tv.getBounds());
      return tvBound == null ? false : actual.isAssignableFrom(tvBound);
   }

   private static boolean isWildcardTypeVariableSafe(WildcardType wildcard, TypeVariable<?> tv) {
      Class<?> tvBound = getBound(tv.getBounds());
      if (tvBound == null) {
         return false;
      } else {
         Class<?> upperBound = getBound(wildcard.getUpperBounds());
         if (upperBound == null) {
            return false;
         } else if (!upperBound.isAssignableFrom(tvBound)) {
            return false;
         } else {
            Class<?> lowerBound = getBound(wildcard.getLowerBounds());
            return lowerBound == null ? true : tvBound.isAssignableFrom(lowerBound);
         }
      }
   }

   private static Class<?> getBound(Type[] bounds) {
      if (bounds == null) {
         return null;
      } else if (bounds.length < 1) {
         return null;
      } else if (bounds.length > 1) {
         throw new AssertionError("Do not understand multiple bounds");
      } else {
         return ReflectionHelper.getRawClass(bounds[0]);
      }
   }

   private static boolean isWildcardActualSafe(WildcardType wildcard, Class<?> actual) {
      Class<?> upperBound = getBound(wildcard.getUpperBounds());
      if (upperBound == null) {
         return false;
      } else if (!upperBound.isAssignableFrom(actual)) {
         return false;
      } else {
         Class<?> lowerBound = getBound(wildcard.getLowerBounds());
         return lowerBound == null ? true : actual.isAssignableFrom(lowerBound);
      }
   }

   private static WildcardType getWildcard(Type type) {
      if (type == null) {
         return null;
      } else {
         return type instanceof WildcardType ? (WildcardType)type : null;
      }
   }

   private static TypeVariable<?> getTypeVariable(Type type) {
      if (type == null) {
         return null;
      } else {
         return type instanceof TypeVariable ? (TypeVariable)type : null;
      }
   }

   private static boolean isWildcard(Type type) {
      return type == null ? false : type instanceof WildcardType;
   }

   private static boolean isTypeVariable(Type type) {
      return type == null ? false : type instanceof TypeVariable;
   }

   private static boolean isActualType(Type type) {
      return type == null ? false : type instanceof Class || type instanceof ParameterizedType;
   }

   private static boolean isArrayType(Type type) {
      if (type == null) {
         return false;
      } else if (type instanceof Class) {
         Class<?> clazz = (Class<?>)type;
         return clazz.isArray();
      } else {
         return type instanceof GenericArrayType;
      }
   }

   private static Type getArrayType(Type type) {
      if (type == null) {
         return null;
      } else if (type instanceof Class) {
         Class<?> clazz = (Class<?>)type;
         return clazz.getComponentType();
      } else if (type instanceof GenericArrayType) {
         GenericArrayType gat = (GenericArrayType)type;
         return gat.getGenericComponentType();
      } else {
         return null;
      }
   }
}
