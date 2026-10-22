package org.eclipse.yasson.internal.deserializer.types;

import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;
import org.eclipse.yasson.internal.DeserializationContextImpl;

class LongDeserializer extends AbstractNumberDeserializer<Long> {
   LongDeserializer(TypeDeserializerBuilder builder) {
      super(builder, true);
   }

   Long parseNumberValue(String value) {
      return Long.parseLong(value);
   }

   @Override
   Object deserializeNumberValue(JsonParser value, DeserializationContextImpl context, Type rType) {
      return value.getLong();
   }
}
