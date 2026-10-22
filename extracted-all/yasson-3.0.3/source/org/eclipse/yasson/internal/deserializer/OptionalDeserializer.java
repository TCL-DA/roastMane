package org.eclipse.yasson.internal.deserializer;

import jakarta.json.stream.JsonParser;
import java.util.Optional;
import org.eclipse.yasson.internal.DeserializationContextImpl;

class OptionalDeserializer implements ModelDeserializer<JsonParser> {
   private final ModelDeserializer<JsonParser> typeDeserializer;
   private final ModelDeserializer<Object> delegate;

   OptionalDeserializer(ModelDeserializer<JsonParser> typeDeserializer, ModelDeserializer<Object> delegate) {
      this.typeDeserializer = typeDeserializer;
      this.delegate = delegate;
   }

   public Object deserialize(JsonParser value, DeserializationContextImpl context) {
      Optional<Object> val = Optional.ofNullable(this.typeDeserializer.deserialize(value, context));
      return this.delegate.deserialize(val, context);
   }
}
