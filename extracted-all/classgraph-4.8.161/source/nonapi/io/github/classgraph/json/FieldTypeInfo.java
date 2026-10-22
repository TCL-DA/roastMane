package nonapi.io.github.classgraph.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;

class FieldTypeInfo {
   final Field field;
   private final Type fieldTypePartiallyResolved;
   private final boolean hasUnresolvedTypeVariables;
   private final boolean isTypeVariable;
   private final FieldTypeInfo.PrimitiveType primitiveType;
   private Constructor<?> constructorForFieldTypeWithSizeHint;
   private Constructor<?> defaultConstructorForFieldType;

   private static boolean hasTypeVariables(Type type) {
      if (!(type instanceof TypeVariable) && !(type instanceof GenericArrayType)) {
         if (type instanceof ParameterizedType) {
            for (Type arg : ((ParameterizedType)type).getActualTypeArguments()) {
               if (hasTypeVariables(arg)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public FieldTypeInfo(Field field, Type fieldTypePartiallyResolved, ClassFieldCache classFieldCache) {
      this.field = field;
      this.fieldTypePartiallyResolved = fieldTypePartiallyResolved;
      this.isTypeVariable = fieldTypePartiallyResolved instanceof TypeVariable;
      this.hasUnresolvedTypeVariables = this.isTypeVariable || hasTypeVariables(fieldTypePartiallyResolved);
      boolean isArray = fieldTypePartiallyResolved instanceof GenericArrayType
         || fieldTypePartiallyResolved instanceof Class && ((Class)fieldTypePartiallyResolved).isArray();
      if (!isArray && !this.isTypeVariable) {
         Class<?> fieldRawType = JSONUtils.getRawType(fieldTypePartiallyResolved);
         if (fieldRawType == int.class) {
            this.primitiveType = FieldTypeInfo.PrimitiveType.INTEGER;
         } else if (fieldRawType == long.class) {
            this.primitiveType = FieldTypeInfo.PrimitiveType.LONG;
         } else if (fieldRawType == short.class) {
            this.primitiveType = FieldTypeInfo.PrimitiveType.SHORT;
         } else if (fieldRawType == double.class) {
            this.primitiveType = FieldTypeInfo.PrimitiveType.DOUBLE;
         } else if (fieldRawType == float.class) {
            this.primitiveType = FieldTypeInfo.PrimitiveType.FLOAT;
         } else if (fieldRawType == boolean.class) {
            this.primitiveType = FieldTypeInfo.PrimitiveType.BOOLEAN;
         } else if (fieldRawType == byte.class) {
            this.primitiveType = FieldTypeInfo.PrimitiveType.BYTE;
         } else if (fieldRawType == char.class) {
            this.primitiveType = FieldTypeInfo.PrimitiveType.CHARACTER;
         } else if (fieldRawType == Class.class) {
            this.primitiveType = FieldTypeInfo.PrimitiveType.CLASS_REF;
         } else {
            this.primitiveType = FieldTypeInfo.PrimitiveType.NON_PRIMITIVE;
         }

         if (!JSONUtils.isBasicValueType(fieldRawType)) {
            if (Collection.class.isAssignableFrom(fieldRawType) || Map.class.isAssignableFrom(fieldRawType)) {
               this.constructorForFieldTypeWithSizeHint = classFieldCache.getConstructorWithSizeHintForConcreteTypeOf(fieldRawType);
            }

            if (this.constructorForFieldTypeWithSizeHint == null) {
               this.defaultConstructorForFieldType = classFieldCache.getDefaultConstructorForConcreteTypeOf(fieldRawType);
            }
         }
      } else {
         this.primitiveType = FieldTypeInfo.PrimitiveType.NON_PRIMITIVE;
      }
   }

   public Constructor<?> getConstructorForFieldTypeWithSizeHint(Type fieldTypeFullyResolved, ClassFieldCache classFieldCache) {
      if (!this.isTypeVariable) {
         return this.constructorForFieldTypeWithSizeHint;
      } else {
         Class<?> fieldRawTypeFullyResolved = JSONUtils.getRawType(fieldTypeFullyResolved);
         return !Collection.class.isAssignableFrom(fieldRawTypeFullyResolved) && !Map.class.isAssignableFrom(fieldRawTypeFullyResolved)
            ? null
            : classFieldCache.getConstructorWithSizeHintForConcreteTypeOf(fieldRawTypeFullyResolved);
      }
   }

   public Constructor<?> getDefaultConstructorForFieldType(Type fieldTypeFullyResolved, ClassFieldCache classFieldCache) {
      if (!this.isTypeVariable) {
         return this.defaultConstructorForFieldType;
      } else {
         Class<?> fieldRawTypeFullyResolved = JSONUtils.getRawType(fieldTypeFullyResolved);
         return classFieldCache.getDefaultConstructorForConcreteTypeOf(fieldRawTypeFullyResolved);
      }
   }

   public Type getFullyResolvedFieldType(TypeResolutions typeResolutions) {
      return !this.hasUnresolvedTypeVariables ? this.fieldTypePartiallyResolved : typeResolutions.resolveTypeVariables(this.fieldTypePartiallyResolved);
   }

   void setFieldValue(Object containingObj, Object value) {
      try {
         if (value == null) {
            if (this.primitiveType != FieldTypeInfo.PrimitiveType.NON_PRIMITIVE) {
               throw new IllegalArgumentException(
                  "Tried to set primitive-typed field " + this.field.getDeclaringClass().getName() + "." + this.field.getName() + " to null value"
               );
            } else {
               this.field.set(containingObj, null);
            }
         } else {
            switch (this.primitiveType) {
               case NON_PRIMITIVE:
                  this.field.set(containingObj, value);
                  break;
               case CLASS_REF:
                  if (!(value instanceof Class)) {
                     throw new IllegalArgumentException("Expected value of type Class<?>; got " + value.getClass().getName());
                  }

                  this.field.set(containingObj, value);
                  break;
               case INTEGER:
                  if (!(value instanceof Integer)) {
                     throw new IllegalArgumentException("Expected value of type Integer; got " + value.getClass().getName());
                  }

                  this.field.setInt(containingObj, (Integer)value);
                  break;
               case LONG:
                  if (!(value instanceof Long)) {
                     throw new IllegalArgumentException("Expected value of type Long; got " + value.getClass().getName());
                  }

                  this.field.setLong(containingObj, (Long)value);
                  break;
               case SHORT:
                  if (!(value instanceof Short)) {
                     throw new IllegalArgumentException("Expected value of type Short; got " + value.getClass().getName());
                  }

                  this.field.setShort(containingObj, (Short)value);
                  break;
               case DOUBLE:
                  if (!(value instanceof Double)) {
                     throw new IllegalArgumentException("Expected value of type Double; got " + value.getClass().getName());
                  }

                  this.field.setDouble(containingObj, (Double)value);
                  break;
               case FLOAT:
                  if (!(value instanceof Float)) {
                     throw new IllegalArgumentException("Expected value of type Float; got " + value.getClass().getName());
                  }

                  this.field.setFloat(containingObj, (Float)value);
                  break;
               case BOOLEAN:
                  if (!(value instanceof Boolean)) {
                     throw new IllegalArgumentException("Expected value of type Boolean; got " + value.getClass().getName());
                  }

                  this.field.setBoolean(containingObj, (Boolean)value);
                  break;
               case BYTE:
                  if (!(value instanceof Byte)) {
                     throw new IllegalArgumentException("Expected value of type Byte; got " + value.getClass().getName());
                  }

                  this.field.setByte(containingObj, (Byte)value);
                  break;
               case CHARACTER:
                  if (!(value instanceof Character)) {
                     throw new IllegalArgumentException("Expected value of type Character; got " + value.getClass().getName());
                  }

                  this.field.setChar(containingObj, (Character)value);
                  break;
               default:
                  throw new IllegalArgumentException();
            }
         }
      } catch (IllegalAccessException | IllegalArgumentException var4) {
         throw new IllegalArgumentException("Could not set field " + this.field.getDeclaringClass().getName() + "." + this.field.getName(), var4);
      }
   }

   @Override
   public String toString() {
      return this.fieldTypePartiallyResolved + " " + this.field.getDeclaringClass().getName() + "." + this.field.getDeclaringClass().getName();
   }

   private static enum PrimitiveType {
      NON_PRIMITIVE,
      INTEGER,
      LONG,
      SHORT,
      DOUBLE,
      FLOAT,
      BOOLEAN,
      BYTE,
      CHARACTER,
      CLASS_REF;
   }
}
