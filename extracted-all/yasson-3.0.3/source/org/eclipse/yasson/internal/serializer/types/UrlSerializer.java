package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.net.URL;
import org.eclipse.yasson.internal.SerializationContextImpl;

class UrlSerializer extends TypeSerializer<URL> {
   UrlSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   void serializeValue(URL value, JsonGenerator generator, SerializationContextImpl context) {
      generator.write(value.toString());
   }
}
