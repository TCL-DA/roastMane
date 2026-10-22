package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.time.ZoneId;
import org.eclipse.yasson.internal.SerializationContextImpl;

class ZoneIdSerializer extends TypeSerializer<ZoneId> {
   ZoneIdSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   void serializeValue(ZoneId value, JsonGenerator generator, SerializationContextImpl context) {
      generator.write(value.getId());
   }
}
