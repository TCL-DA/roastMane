package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.time.ZoneOffset;
import org.eclipse.yasson.internal.SerializationContextImpl;

class ZoneOffsetSerializer extends TypeSerializer<ZoneOffset> {
   ZoneOffsetSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   void serializeValue(ZoneOffset value, JsonGenerator generator, SerializationContextImpl context) {
      generator.write(value.getId());
   }
}
