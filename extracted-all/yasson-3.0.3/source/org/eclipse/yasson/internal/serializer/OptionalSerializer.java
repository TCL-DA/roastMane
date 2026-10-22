package org.eclipse.yasson.internal.serializer;

import jakarta.json.stream.JsonGenerator;
import java.util.Optional;
import org.eclipse.yasson.internal.SerializationContextImpl;

class OptionalSerializer implements ModelSerializer {
   private final ModelSerializer delegate;

   OptionalSerializer(ModelSerializer delegate) {
      this.delegate = delegate;
   }

   @Override
   public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
      Optional<Object> optional = (Optional<Object>)value;
      this.delegate.serialize(optional == null ? null : optional.orElse(null), generator, context);
   }
}
