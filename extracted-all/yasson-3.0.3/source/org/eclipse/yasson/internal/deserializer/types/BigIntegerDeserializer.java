package org.eclipse.yasson.internal.deserializer.types;

import java.math.BigInteger;

class BigIntegerDeserializer extends AbstractNumberDeserializer<BigInteger> {
   BigIntegerDeserializer(TypeDeserializerBuilder builder) {
      super(builder, true);
   }

   BigInteger parseNumberValue(String value) {
      return new BigInteger(value);
   }
}
