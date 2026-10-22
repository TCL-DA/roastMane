package nonapi.io.github.classgraph.json;

import io.github.classgraph.ScanResult;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;

class ClassFields {
   final List<FieldTypeInfo> fieldOrder = new ArrayList<>();
   final Map<String, FieldTypeInfo> fieldNameToFieldTypeInfo = new HashMap<>();
   Field idField;
   private static final Comparator<Field> FIELD_NAME_ORDER_COMPARATOR = new Comparator<Field>() {
      public int compare(Field a, Field b) {
         return a.getName().compareTo(b.getName());
      }
   };
   private static final Comparator<Field> SERIALIZATION_FORMAT_FIELD_NAME_ORDER_COMPARATOR = new Comparator<Field>() {
      public int compare(Field a, Field b) {
         return a.getName().equals("format") ? -1 : (b.getName().equals("format") ? 1 : a.getName().compareTo(b.getName()));
      }
   };
   private static final String SERIALIZATION_FORMAT_CLASS_NAME = ScanResult.class.getName() + "$SerializationFormat";

   public ClassFields(Class<?> cls, boolean resolveTypes, boolean onlySerializePublicFields, ClassFieldCache classFieldCache, ReflectionUtils reflectionUtils) {
      Set<String> visibleFieldNames = new HashSet<>();
      List<List<FieldTypeInfo>> fieldSuperclassReversedOrder = new ArrayList<>();
      TypeResolutions currTypeResolutions = null;
      Type currType = cls;

      while (currType != Object.class && currType != null) {
         Class<?> currRawType;
         if (currType instanceof ParameterizedType) {
            ParameterizedType currParameterizedType = (ParameterizedType)currType;
            currRawType = (Class<?>)currParameterizedType.getRawType();
         } else {
            if (!(currType instanceof Class)) {
               throw new IllegalArgumentException("Illegal class type: " + currType);
            }

            currRawType = (Class<?>)currType;
         }

         Field[] fields = currRawType.getDeclaredFields();
         Arrays.sort(
            fields, cls.getName().equals(SERIALIZATION_FORMAT_CLASS_NAME) ? SERIALIZATION_FORMAT_FIELD_NAME_ORDER_COMPARATOR : FIELD_NAME_ORDER_COMPARATOR
         );
         List<FieldTypeInfo> fieldOrderWithinClass = new ArrayList<>();

         for (Field field : fields) {
            if (visibleFieldNames.add(field.getName())) {
               boolean isIdField = field.isAnnotationPresent(Id.class);
               if (isIdField) {
                  if (this.idField != null) {
                     throw new IllegalArgumentException(
                        "More than one @Id annotation: "
                           + this.idField.getDeclaringClass()
                           + "."
                           + this.idField
                           + " ; "
                           + currRawType.getName()
                           + "."
                           + field.getName()
                     );
                  }

                  this.idField = field;
               }

               if (JSONUtils.fieldIsSerializable(field, onlySerializePublicFields, reflectionUtils)) {
                  Type fieldGenericType = field.getGenericType();
                  Type fieldTypePartiallyResolved = currTypeResolutions != null && resolveTypes
                     ? currTypeResolutions.resolveTypeVariables(fieldGenericType)
                     : fieldGenericType;
                  FieldTypeInfo fieldTypeInfo = new FieldTypeInfo(field, fieldTypePartiallyResolved, classFieldCache);
                  this.fieldNameToFieldTypeInfo.put(field.getName(), fieldTypeInfo);
                  fieldOrderWithinClass.add(fieldTypeInfo);
               } else if (isIdField) {
                  throw new IllegalArgumentException(
                     "@Id annotation field must be accessible, final, and non-transient: " + currRawType.getName() + "." + field.getName()
                  );
               }
            }
         }

         fieldSuperclassReversedOrder.add(fieldOrderWithinClass);
         Type genericSuperType = currRawType.getGenericSuperclass();
         if (resolveTypes) {
            if (genericSuperType instanceof ParameterizedType) {
               Type resolvedSupertype = currTypeResolutions == null ? genericSuperType : currTypeResolutions.resolveTypeVariables(genericSuperType);
               currTypeResolutions = resolvedSupertype instanceof ParameterizedType ? new TypeResolutions((ParameterizedType)resolvedSupertype) : null;
               currType = resolvedSupertype;
            } else {
               if (!(genericSuperType instanceof Class)) {
                  throw new IllegalArgumentException("Got unexpected supertype " + genericSuperType);
               }

               currType = genericSuperType;
               currTypeResolutions = null;
            }
         } else {
            currType = genericSuperType;
         }
      }

      for (int i = fieldSuperclassReversedOrder.size() - 1; i >= 0; i--) {
         List<FieldTypeInfo> fieldGroupingForClass = fieldSuperclassReversedOrder.get(i);
         this.fieldOrder.addAll(fieldGroupingForClass);
      }
   }
}
