package org.eclipse.yasson.internal.deserializer.types;

import java.math.BigDecimal;

class BigDecimalDeserializer extends AbstractNumberDeserializer<BigDecimal> {
   BigDecimalDeserializer(TypeDeserializerBuilder builder) {
      super(builder, false);
   }

   BigDecimal parseNumberValue(String value) {
      return new BigDecimal(value);
   }
}
