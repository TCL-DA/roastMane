package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;

class DoubleSerializer extends AbstractNumberSerializer<Double> {
   DoubleSerializer(TypeSerializerBuilder builder) {
      super(builder);
   }

   void writeValue(Double value, JsonGenerator generator) {
      generator.write(value);
   }
}
