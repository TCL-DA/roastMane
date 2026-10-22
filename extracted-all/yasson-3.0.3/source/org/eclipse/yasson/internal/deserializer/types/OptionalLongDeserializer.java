package org.eclipse.yasson.internal.deserializer.types;

import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.util.OptionalLong;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.deserializer.ModelDeserializer;

class OptionalLongDeserializer implements ModelDeserializer<JsonParser> {
   private final ModelDeserializer<JsonParser> extractor;
   private final ModelDeserializer<Object> nullValueDelegate;

   OptionalLongDeserializer(ModelDeserializer<JsonParser> extractor, ModelDeserializer<Object> nullValueDelegate) {
      this.extractor = extractor;
      this.nullValueDelegate = nullValueDelegate;
   }

   public Object deserialize(JsonParser value, DeserializationContextImpl context) {
      if (context.getLastValueEvent() == Event.VALUE_NULL) {
         return this.nullValueDelegate.deserialize(OptionalLong.empty(), context);
      } else {
         OptionalLong optional = OptionalLong.of((Long)this.extractor.deserialize(value, context));
         return this.nullValueDelegate.deserialize(optional, context);
      }
   }
}
