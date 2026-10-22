package com.google.gson.internal.bind;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.ReflectionAccessFilterHelper;
import com.google.gson.internal.reflect.ReflectionHelper;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {
   private final ConstructorConstructor constructorConstructor;
   private final FieldNamingStrategy fieldNamingPolicy;
   private final Excluder excluder;
   private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
   private final List<ReflectionAccessFilter> reflectionFilters;

   public ReflectiveTypeAdapterFactory(
      ConstructorConstructor constructorConstructor,
      FieldNamingStrategy fieldNamingPolicy,
      Excluder excluder,
      JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory,
      List<ReflectionAccessFilter> reflectionFilters
   ) {
      this.constructorConstructor = constructorConstructor;
      this.fieldNamingPolicy = fieldNamingPolicy;
      this.excluder = excluder;
      this.jsonAdapterFactory = jsonAdapterFactory;
      this.reflectionFilters = reflectionFilters;
   }

   private boolean includeField(Field f, boolean serialize) {
      return !this.excluder.excludeClass(f.getType(), serialize) && !this.excluder.excludeField(f, serialize);
   }

   private List<String> getFieldNames(Field f) {
      SerializedName annotation = f.getAnnotation(SerializedName.class);
      if (annotation == null) {
         String name = this.fieldNamingPolicy.translateName(f);
         return Collections.singletonList(name);
      } else {
         String serializedName = annotation.value();
         String[] alternates = annotation.alternate();
         if (alternates.length == 0) {
            return Collections.singletonList(serializedName);
         } else {
            List<String> fieldNames = new ArrayList<>(alternates.length + 1);
            fieldNames.add(serializedName);

            for (String alternate : alternates) {
               fieldNames.add(alternate);
            }

            return fieldNames;
         }
      }
   }

   @Override
   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      Class<? super T> raw = type.getRawType();
      if (!Object.class.isAssignableFrom(raw)) {
         return null;
      } else {
         ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, raw);
         if (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_ALL) {
            throw new JsonIOException(
               "ReflectionAccessFilter does not permit using reflection for " + raw + ". Register a TypeAdapter for this type or adjust the access filter."
            );
         } else {
            boolean blockInaccessible = filterResult == ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE;
            ObjectConstructor<T> constructor = this.constructorConstructor.get(type);
            return new ReflectiveTypeAdapterFactory.Adapter<>(constructor, this.getBoundFields(gson, type, raw, blockInaccessible));
         }
      }
   }

   private static void checkAccessible(Object object, Field field) {
      if (!ReflectionAccessFilterHelper.canAccess(field, Modifier.isStatic(field.getModifiers()) ? null : object)) {
         throw new JsonIOException(
            "Field '"
               + field.getDeclaringClass().getName()
               + "#"
               + field.getName()
               + "' is not accessible and ReflectionAccessFilter does not permit making it accessible. Register a TypeAdapter for the declaring type or adjust the access filter."
         );
      }
   }

   private ReflectiveTypeAdapterFactory.BoundField createBoundField(
      final Gson context, final Field field, String name, final TypeToken<?> fieldType, boolean serialize, boolean deserialize, final boolean blockInaccessible
   ) {
      final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
      JsonAdapter annotation = field.getAnnotation(JsonAdapter.class);
      TypeAdapter<?> mapped = null;
      if (annotation != null) {
         mapped = this.jsonAdapterFactory.getTypeAdapter(this.constructorConstructor, context, fieldType, annotation);
      }

      final boolean jsonAdapterPresent = mapped != null;
      if (mapped == null) {
         mapped = context.getAdapter(fieldType);
      }

      final TypeAdapter<?> typeAdapter = mapped;
      return new ReflectiveTypeAdapterFactory.BoundField(name, serialize, deserialize) {
         @Override
         void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException {
            if (this.serialized) {
               if (blockInaccessible) {
                  ReflectiveTypeAdapterFactory.checkAccessible(value, field);
               }

               Object fieldValue = field.get(value);
               if (fieldValue != value) {
                  writer.name(this.name);
                  TypeAdapter t = (TypeAdapter)(jsonAdapterPresent
                     ? typeAdapter
                     : new TypeAdapterRuntimeTypeWrapper<>(context, typeAdapter, fieldType.getType()));
                  t.write(writer, fieldValue);
               }
            }
         }

         @Override
         void read(JsonReader reader, Object value) throws IOException, IllegalAccessException {
            Object fieldValue = typeAdapter.read(reader);
            if (fieldValue != null || !isPrimitive) {
               if (blockInaccessible) {
                  ReflectiveTypeAdapterFactory.checkAccessible(value, field);
               }

               field.set(value, fieldValue);
            }
         }
      };
   }

   private Map<String, ReflectiveTypeAdapterFactory.BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw, boolean blockInaccessible) {
      Map<String, ReflectiveTypeAdapterFactory.BoundField> result = new LinkedHashMap<>();
      if (raw.isInterface()) {
         return result;
      } else {
         Type declaredType = type.getType();

         for (Class<?> originalRaw = raw; raw != Object.class; raw = type.getRawType()) {
            Field[] fields = raw.getDeclaredFields();
            if (raw != originalRaw && fields.length > 0) {
               ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, raw);
               if (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_ALL) {
                  throw new JsonIOException(
                     "ReflectionAccessFilter does not permit using reflection for "
                        + raw
                        + " (supertype of "
                        + originalRaw
                        + "). Register a TypeAdapter for this type or adjust the access filter."
                  );
               }

               blockInaccessible = filterResult == ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE;
            }

            for (Field field : fields) {
               boolean serialize = this.includeField(field, true);
               boolean deserialize = this.includeField(field, false);
               if (serialize || deserialize) {
                  if (!blockInaccessible) {
                     ReflectionHelper.makeAccessible(field);
                  }

                  Type fieldType = $Gson$Types.resolve(type.getType(), raw, field.getGenericType());
                  List<String> fieldNames = this.getFieldNames(field);
                  ReflectiveTypeAdapterFactory.BoundField previous = null;
                  int i = 0;

                  for (int size = fieldNames.size(); i < size; i++) {
                     String name = fieldNames.get(i);
                     if (i != 0) {
                        serialize = false;
                     }

                     ReflectiveTypeAdapterFactory.BoundField boundField = this.createBoundField(
                        context, field, name, TypeToken.get(fieldType), serialize, deserialize, blockInaccessible
                     );
                     ReflectiveTypeAdapterFactory.BoundField replaced = result.put(name, boundField);
                     if (previous == null) {
                        previous = replaced;
                     }
                  }

                  if (previous != null) {
                     throw new IllegalArgumentException(declaredType + " declares multiple JSON fields named " + previous.name);
                  }
               }
            }

            type = TypeToken.get($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
         }

         return result;
      }
   }

   public static final class Adapter<T> extends TypeAdapter<T> {
      private final ObjectConstructor<T> constructor;
      private final Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields;

      Adapter(ObjectConstructor<T> constructor, Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields) {
         this.constructor = constructor;
         this.boundFields = boundFields;
      }

      @Override
      public T read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            T instance = this.constructor.construct();

            try {
               in.beginObject();

               while (in.hasNext()) {
                  String name = in.nextName();
                  ReflectiveTypeAdapterFactory.BoundField field = this.boundFields.get(name);
                  if (field != null && field.deserialized) {
                     field.read(in, instance);
                  } else {
                     in.skipValue();
                  }
               }
            } catch (IllegalStateException var5) {
               throw new JsonSyntaxException(var5);
            } catch (IllegalAccessException var6) {
               throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(var6);
            }

            in.endObject();
            return instance;
         }
      }

      @Override
      public void write(JsonWriter out, T value) throws IOException {
         if (value == null) {
            out.nullValue();
         } else {
            out.beginObject();

            try {
               for (ReflectiveTypeAdapterFactory.BoundField boundField : this.boundFields.values()) {
                  boundField.write(out, value);
               }
            } catch (IllegalAccessException var5) {
               throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(var5);
            }

            out.endObject();
         }
      }
   }

   abstract static class BoundField {
      final String name;
      final boolean serialized;
      final boolean deserialized;

      protected BoundField(String name, boolean serialized, boolean deserialized) {
         this.name = name;
         this.serialized = serialized;
         this.deserialized = deserialized;
      }

      abstract void write(JsonWriter var1, Object var2) throws IOException, IllegalAccessException;

      abstract void read(JsonReader var1, Object var2) throws IOException, IllegalAccessException;
   }
}
