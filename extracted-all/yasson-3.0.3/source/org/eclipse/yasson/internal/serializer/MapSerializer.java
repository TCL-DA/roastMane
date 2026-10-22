package org.eclipse.yasson.internal.serializer;

import jakarta.json.stream.JsonGenerator;
import java.util.Map;
import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.serializer.types.TypeSerializers;

abstract class MapSerializer implements ModelSerializer {
   private final ModelSerializer keySerializer;
   private final ModelSerializer valueSerializer;

   MapSerializer(ModelSerializer keySerializer, ModelSerializer valueSerializer) {
      this.keySerializer = keySerializer;
      this.valueSerializer = valueSerializer;
   }

   ModelSerializer getKeySerializer() {
      return this.keySerializer;
   }

   ModelSerializer getValueSerializer() {
      return this.valueSerializer;
   }

   static MapSerializer create(Class<?> keyClass, ModelSerializer keySerializer, ModelSerializer valueSerializer) {
      if (TypeSerializers.isSupportedMapKey(keyClass)) {
         return new MapSerializer.StringKeyMapSerializer(keySerializer, valueSerializer);
      } else {
         return (MapSerializer)(Object.class.equals(keyClass)
            ? new MapSerializer.DynamicMapSerializer(keySerializer, valueSerializer)
            : new MapSerializer.ObjectKeyMapSerializer(keySerializer, valueSerializer));
      }
   }

   private static final class DynamicMapSerializer extends MapSerializer {
      private final MapSerializer.StringKeyMapSerializer stringMap;
      private final MapSerializer.ObjectKeyMapSerializer objectMap;
      private MapSerializer serializer;

      DynamicMapSerializer(ModelSerializer keySerializer, ModelSerializer valueSerializer) {
         super(keySerializer, valueSerializer);
         this.stringMap = new MapSerializer.StringKeyMapSerializer(keySerializer, valueSerializer);
         this.objectMap = new MapSerializer.ObjectKeyMapSerializer(keySerializer, valueSerializer);
      }

      @Override
      public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
         if (this.serializer == null) {
            Map<Object, Object> map = (Map<Object, Object>)value;
            boolean suitable = true;

            for (Object key : map.keySet()) {
               if (key == null) {
                  if (context.getJsonbContext().getConfigProperties().isForceMapArraySerializerForNullKeys()) {
                     suitable = false;
                     break;
                  }
               } else {
                  Class<?> keyClass = key.getClass();
                  if (!TypeSerializers.isSupportedMapKey(keyClass)) {
                     suitable = false;
                     break;
                  }
               }
            }

            this.serializer = (MapSerializer)(suitable ? this.stringMap : this.objectMap);
         }

         this.serializer.serialize(value, generator, context);
      }
   }

   private static final class ObjectKeyMapSerializer extends MapSerializer {
      ObjectKeyMapSerializer(ModelSerializer keySerializer, ModelSerializer valueSerializer) {
         super(keySerializer, valueSerializer);
      }

      @Override
      public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
         Map<Object, Object> map = (Map<Object, Object>)value;
         generator.writeStartArray();
         map.forEach((key, val) -> {
            generator.writeStartObject();
            generator.writeKey("key");
            if (key == null) {
               generator.writeNull();
            } else {
               this.getKeySerializer().serialize(key, generator, context);
            }

            generator.writeKey("value");
            this.getValueSerializer().serialize(val, generator, context);
            generator.writeEnd();
         });
         generator.writeEnd();
      }
   }

   private static final class StringKeyMapSerializer extends MapSerializer {
      StringKeyMapSerializer(ModelSerializer keySerializer, ModelSerializer valueSerializer) {
         super(keySerializer, valueSerializer);
      }

      @Override
      public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
         Map<Object, Object> map = (Map<Object, Object>)value;
         generator.writeStartObject();
         map.forEach((key, val) -> {
            this.getKeySerializer().serialize(key, generator, context);
            this.getValueSerializer().serialize(val, generator, context);
         });
         generator.writeEnd();
      }
   }
}
