package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.math.BigDecimal;

class FloatSerializer extends AbstractNumberSerializer<Float> {
   FloatSerializer(TypeSerializerBuilder builder) {
      super(builder);
   }

   void writeValue(Float value, JsonGenerator generator) {
      generator.write(new BigDecimal(String.valueOf(value)));
   }
}
