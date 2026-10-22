package org.eclipse.yasson.internal.deserializer.types;

import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;
import org.eclipse.yasson.internal.DeserializationContextImpl;

class IntegerDeserializer extends AbstractNumberDeserializer<Integer> {
   IntegerDeserializer(TypeDeserializerBuilder builder) {
      super(builder, true);
   }

   Integer parseNumberValue(String value) {
      return Integer.parseInt(value);
   }

   @Override
   Object deserializeNumberValue(JsonParser value, DeserializationContextImpl context, Type rType) {
      return value.getInt();
   }
}
