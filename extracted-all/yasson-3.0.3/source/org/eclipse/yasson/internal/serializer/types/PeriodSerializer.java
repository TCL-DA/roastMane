package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.time.Period;
import org.eclipse.yasson.internal.SerializationContextImpl;

class PeriodSerializer extends TypeSerializer<Period> {
   PeriodSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   void serializeValue(Period value, JsonGenerator generator, SerializationContextImpl context) {
      generator.write(value.toString());
   }
}
