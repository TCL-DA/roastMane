package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.util.UUID;
import org.eclipse.yasson.internal.SerializationContextImpl;

class UuidSerializer extends TypeSerializer<UUID> {
   UuidSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   void serializeValue(UUID value, JsonGenerator generator, SerializationContextImpl context) {
      generator.write(value.toString());
   }
}
