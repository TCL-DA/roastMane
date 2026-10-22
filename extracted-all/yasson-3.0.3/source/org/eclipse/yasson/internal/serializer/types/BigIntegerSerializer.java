package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.math.BigInteger;

class BigIntegerSerializer extends AbstractNumberSerializer<BigInteger> {
   BigIntegerSerializer(TypeSerializerBuilder builder) {
      super(builder);
   }

   void writeValue(BigInteger value, JsonGenerator generator) {
      generator.write(value);
   }
}
