package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;

class IntegerSerializer extends AbstractNumberSerializer<Integer> {
   IntegerSerializer(TypeSerializerBuilder builder) {
      super(builder);
   }

   void writeValue(Integer value, JsonGenerator generator) {
      generator.write(value);
   }
}
