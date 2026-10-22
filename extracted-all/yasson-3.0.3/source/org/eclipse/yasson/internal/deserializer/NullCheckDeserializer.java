package org.eclipse.yasson.internal.deserializer;

import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import org.eclipse.yasson.internal.DeserializationContextImpl;

public class NullCheckDeserializer implements ModelDeserializer<JsonParser> {
   private final ModelDeserializer<JsonParser> nonNullDeserializer;
   private final ModelDeserializer<Object> nullDeserializer;

   public NullCheckDeserializer(ModelDeserializer<JsonParser> nonNullDeserializer, ModelDeserializer<Object> nullDeserializer) {
      this.nonNullDeserializer = nonNullDeserializer;
      this.nullDeserializer = nullDeserializer;
   }

   public Object deserialize(JsonParser value, DeserializationContextImpl context) {
      return context.getLastValueEvent() != Event.VALUE_NULL
         ? this.nonNullDeserializer.deserialize(value, context)
         : this.nullDeserializer.deserialize(null, context);
   }

   @Override
   public String toString() {
      return "Null value check";
   }
}
