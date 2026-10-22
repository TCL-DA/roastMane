package org.glassfish.hk2.api;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public abstract class TypeLiteral<T> {
   private transient Type type;
   private transient Class<T> rawType;

   protected TypeLiteral() {
   }

   public final Type getType() {
      if (this.type == null) {
         Class<?> typeLiteralSubclass = getTypeLiteralSubclass(this.getClass());
         if (typeLiteralSubclass == null) {
            throw new RuntimeException(this.getClass() + " is not a subclass of TypeLiteral<T>");
         }

         this.type = getTypeParameter(typeLiteralSubclass);
         if (this.type == null) {
            throw new RuntimeException(this.getClass() + " does not specify the type parameter T of TypeLiteral<T>");
         }
      }

      return this.type;
   }

   public final Type[] getParameterTypes() {
      this.type = this.getType();
      return this.type instanceof ParameterizedType ? ((ParameterizedType)this.type).getActualTypeArguments() : new Type[0];
   }

   public final Class<T> getRawType() {
      if (this.rawType == null) {
         Type t = this.getType();
         return (Class<T>)getRawType(t);
      } else {
         return this.rawType;
      }
   }

   public static Class<?> getRawType(Type type) {
      if (type instanceof Class) {
         return (Class<?>)type;
      } else if (type instanceof ParameterizedType) {
         ParameterizedType parameterizedType = (ParameterizedType)type;
         return (Class<?>)parameterizedType.getRawType();
      } else if (type instanceof GenericArrayType) {
         return Object[].class;
      } else if (type instanceof WildcardType) {
         return null;
      } else {
         throw new RuntimeException("Illegal type");
      }
   }

   private static Class<?> getTypeLiteralSubclass(Class<?> clazz) {
      Class<?> superClass = clazz.getSuperclass();
      if (superClass.equals(TypeLiteral.class)) {
         return clazz;
      } else {
         return superClass.equals(Object.class) ? null : getTypeLiteralSubclass(superClass);
      }
   }

   private static Type getTypeParameter(Class<?> typeLiteralSubclass) {
      Type type = typeLiteralSubclass.getGenericSuperclass();
      if (type instanceof ParameterizedType) {
         ParameterizedType parameterizedType = (ParameterizedType)type;
         if (parameterizedType.getActualTypeArguments().length == 1) {
            return parameterizedType.getActualTypeArguments()[0];
         }
      }

      return null;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof TypeLiteral) {
         TypeLiteral<?> that = (TypeLiteral<?>)obj;
         return this.getType().equals(that.getType());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.getType().hashCode();
   }

   @Override
   public String toString() {
      return this.getType().toString();
   }
}
