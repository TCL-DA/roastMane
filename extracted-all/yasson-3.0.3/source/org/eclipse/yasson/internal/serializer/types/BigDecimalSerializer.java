package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.math.BigDecimal;

class BigDecimalSerializer extends AbstractNumberSerializer<BigDecimal> {
   BigDecimalSerializer(TypeSerializerBuilder builder) {
      super(builder);
   }

   void writeValue(BigDecimal value, JsonGenerator generator) {
      generator.write(value);
   }
}
