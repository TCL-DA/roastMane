package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.util.OptionalDouble;
import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.serializer.ModelSerializer;

class OptionalDoubleSerializer implements ModelSerializer {
   private final ModelSerializer typeSerializer;

   OptionalDoubleSerializer(ModelSerializer typeSerializer) {
      this.typeSerializer = typeSerializer;
   }

   @Override
   public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
      OptionalDouble optionalDouble = (OptionalDouble)value;
      if (optionalDouble != null && optionalDouble.isPresent()) {
         this.typeSerializer.serialize(optionalDouble.getAsDouble(), generator, context);
      } else {
         this.typeSerializer.serialize(null, generator, context);
      }
   }
}
