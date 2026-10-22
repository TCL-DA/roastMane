package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;

class LongSerializer extends AbstractNumberSerializer<Long> {
   LongSerializer(TypeSerializerBuilder builder) {
      super(builder);
   }

   void writeValue(Long value, JsonGenerator generator) {
      generator.write(value);
   }
}
