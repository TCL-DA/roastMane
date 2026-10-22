package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.deserializer.types.TypeDeserializer;

public class ValueExtractor implements ModelDeserializer<JsonParser> {
   private final TypeDeserializer delegate;

   public ValueExtractor(TypeDeserializer delegate) {
      this.delegate = delegate;
   }

   public Object deserialize(JsonParser value, DeserializationContextImpl context) {
      Event last = context.getLastValueEvent();
      switch (last) {
         case VALUE_TRUE:
            return this.delegate.deserialize(Boolean.TRUE.booleanValue(), context);
         case VALUE_FALSE:
            return this.delegate.deserialize(Boolean.FALSE.booleanValue(), context);
         case KEY_NAME:
         case VALUE_STRING:
            return this.delegate.deserialize(value.getString(), context);
         case VALUE_NUMBER:
            return this.delegate.deserialize(value, context);
         default:
            throw new JsonbException("Could not extract data. Received event: " + last);
      }
   }
}
