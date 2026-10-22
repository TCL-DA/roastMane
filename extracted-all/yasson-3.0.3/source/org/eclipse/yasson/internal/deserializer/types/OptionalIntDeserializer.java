package org.eclipse.yasson.internal.deserializer.types;

import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.util.OptionalInt;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.deserializer.ModelDeserializer;

class OptionalIntDeserializer implements ModelDeserializer<JsonParser> {
   private final ModelDeserializer<JsonParser> extractor;
   private final ModelDeserializer<Object> delegate;

   OptionalIntDeserializer(ModelDeserializer<JsonParser> extractor, ModelDeserializer<Object> delegate) {
      this.extractor = extractor;
      this.delegate = delegate;
   }

   public Object deserialize(JsonParser value, DeserializationContextImpl context) {
      if (context.getLastValueEvent() == Event.VALUE_NULL) {
         return this.delegate.deserialize(OptionalInt.empty(), context);
      } else {
         OptionalInt optional = OptionalInt.of((Integer)this.extractor.deserialize(value, context));
         return this.delegate.deserialize(optional, context);
      }
   }
}
