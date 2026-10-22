package org.eclipse.yasson.internal.deserializer.types;

import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.deserializer.ModelDeserializer;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class JsonValueDeserializer implements ModelDeserializer<JsonParser> {
   private final ModelDeserializer<Object> delegate;
   private final JsonValue nullValue;

   JsonValueDeserializer(TypeDeserializerBuilder builder, JsonValue nullValue) {
      this.delegate = builder.getDelegate();
      this.nullValue = nullValue;
   }

   public Object deserialize(JsonParser value, DeserializationContextImpl context) {
      Event last = context.getLastValueEvent();
      return this.delegate.deserialize(this.deserializeValue(last, value), context);
   }

   private JsonValue deserializeValue(Event last, JsonParser parser) {
      switch (last) {
         case VALUE_TRUE:
            return JsonValue.TRUE;
         case VALUE_FALSE:
            return JsonValue.FALSE;
         case VALUE_NULL:
            return this.nullValue;
         case VALUE_STRING:
         case VALUE_NUMBER:
            return parser.getValue();
         case START_OBJECT:
            return parser.getObject();
         case START_ARRAY:
            return parser.getArray();
         default:
            throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Unknown JSON value: " + last));
      }
   }
}
