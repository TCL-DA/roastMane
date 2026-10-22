package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import org.eclipse.yasson.internal.SerializationContextImpl;

class CharSerializer extends TypeSerializer<Character> {
   CharSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   void serializeValue(Character value, JsonGenerator generator, SerializationContextImpl context) {
      generator.write(String.valueOf(value));
   }
}
