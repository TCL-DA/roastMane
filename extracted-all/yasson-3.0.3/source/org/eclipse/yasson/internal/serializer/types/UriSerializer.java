package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.net.URI;
import org.eclipse.yasson.internal.SerializationContextImpl;

class UriSerializer extends TypeSerializer<URI> {
   UriSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   void serializeValue(URI value, JsonGenerator generator, SerializationContextImpl context) {
      generator.write(value.toString());
   }
}
