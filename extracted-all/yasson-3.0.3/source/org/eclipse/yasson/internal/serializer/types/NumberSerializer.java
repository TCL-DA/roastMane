package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.math.BigDecimal;

class NumberSerializer extends AbstractNumberSerializer<Number> {
   NumberSerializer(TypeSerializerBuilder builder) {
      super(builder);
   }

   void writeValue(Number value, JsonGenerator generator) {
      generator.write(new BigDecimal(String.valueOf(value)));
   }
}
