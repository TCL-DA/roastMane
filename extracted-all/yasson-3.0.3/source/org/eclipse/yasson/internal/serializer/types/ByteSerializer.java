package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;

class ByteSerializer extends AbstractNumberSerializer<Byte> {
   ByteSerializer(TypeSerializerBuilder builder) {
      super(builder);
   }

   void writeValue(Byte value, JsonGenerator generator) {
      generator.write(value);
   }
}
