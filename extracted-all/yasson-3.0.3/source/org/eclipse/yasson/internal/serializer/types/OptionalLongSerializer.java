package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.util.OptionalLong;
import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.serializer.ModelSerializer;

class OptionalLongSerializer implements ModelSerializer {
   private final ModelSerializer typeSerializer;

   OptionalLongSerializer(ModelSerializer typeSerializer) {
      this.typeSerializer = typeSerializer;
   }

   @Override
   public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
      OptionalLong optionalLong = (OptionalLong)value;
      if (optionalLong != null && optionalLong.isPresent()) {
         this.typeSerializer.serialize(optionalLong.getAsLong(), generator, context);
      } else {
         this.typeSerializer.serialize(null, generator, context);
      }
   }
}
