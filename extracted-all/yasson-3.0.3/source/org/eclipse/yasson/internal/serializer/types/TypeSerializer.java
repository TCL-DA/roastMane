package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.serializer.ModelSerializer;

abstract class TypeSerializer<T> implements ModelSerializer {
   private final ModelSerializer serializer;

   TypeSerializer(TypeSerializerBuilder serializerBuilder) {
      if (serializerBuilder.isKey()) {
         this.serializer = new TypeSerializer.KeySerializer();
      } else {
         this.serializer = new TypeSerializer.ValueSerializer();
      }
   }

   @Override
   public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
      this.serializer.serialize(value, generator, context);
   }

   abstract void serializeValue(T var1, JsonGenerator var2, SerializationContextImpl var3);

   void serializeKey(T key, JsonGenerator generator, SerializationContextImpl context) {
      generator.writeKey(String.valueOf(key));
   }

   private final class KeySerializer implements ModelSerializer {
      @Override
      public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
         TypeSerializer.this.serializeKey((T)value, generator, context);
      }
   }

   private final class ValueSerializer implements ModelSerializer {
      @Override
      public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
         TypeSerializer.this.serializeValue((T)value, generator, context);
      }
   }
}
