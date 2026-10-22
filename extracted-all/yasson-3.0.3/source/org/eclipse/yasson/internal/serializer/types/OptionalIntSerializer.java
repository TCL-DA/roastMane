package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.util.OptionalInt;
import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.serializer.ModelSerializer;

class OptionalIntSerializer implements ModelSerializer {
   private final ModelSerializer typeSerializer;

   OptionalIntSerializer(ModelSerializer typeSerializer) {
      this.typeSerializer = typeSerializer;
   }

   @Override
   public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
      OptionalInt optionalInt = (OptionalInt)value;
      if (optionalInt != null && optionalInt.isPresent()) {
         this.typeSerializer.serialize(optionalInt.getAsInt(), generator, context);
      } else {
         this.typeSerializer.serialize(null, generator, context);
      }
   }
}
