package org.eclipse.yasson.internal.deserializer.types;

import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.util.OptionalDouble;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.deserializer.ModelDeserializer;

class OptionalDoubleDeserializer implements ModelDeserializer<JsonParser> {
   private final ModelDeserializer<JsonParser> extractor;
   private final ModelDeserializer<Object> nullValueDelegate;

   OptionalDoubleDeserializer(ModelDeserializer<JsonParser> extractor, ModelDeserializer<Object> nullValueDelegate) {
      this.extractor = extractor;
      this.nullValueDelegate = nullValueDelegate;
   }

   public Object deserialize(JsonParser value, DeserializationContextImpl context) {
      if (context.getLastValueEvent() == Event.VALUE_NULL) {
         return this.nullValueDelegate.deserialize(OptionalDouble.empty(), context);
      } else {
         OptionalDouble optional = OptionalDouble.of((Double)this.extractor.deserialize(value, context));
         return this.nullValueDelegate.deserialize(optional, context);
      }
   }
}
