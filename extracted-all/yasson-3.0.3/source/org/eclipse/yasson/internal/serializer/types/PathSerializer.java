package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.nio.file.Path;
import org.eclipse.yasson.internal.SerializationContextImpl;

class PathSerializer extends TypeSerializer<Path> {
   PathSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   void serializeValue(Path value, JsonGenerator generator, SerializationContextImpl context) {
      generator.write(value.toString());
   }
}
