package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.util.TimeZone;
import org.eclipse.yasson.internal.SerializationContextImpl;

class TimeZoneSerializer extends TypeSerializer<TimeZone> {
   TimeZoneSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   void serializeValue(TimeZone value, JsonGenerator generator, SerializationContextImpl context) {
      generator.write(value.getID());
   }
}
