package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.time.Duration;
import org.eclipse.yasson.internal.SerializationContextImpl;

class DurationSerializer extends TypeSerializer<Duration> {
   DurationSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   void serializeValue(Duration value, JsonGenerator generator, SerializationContextImpl context) {
      generator.write(value.toString());
   }
}
