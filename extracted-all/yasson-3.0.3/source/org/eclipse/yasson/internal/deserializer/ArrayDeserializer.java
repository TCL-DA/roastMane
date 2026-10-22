package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.yasson.internal.DeserializationContextImpl;

class ArrayDeserializer implements ModelDeserializer<JsonParser> {
   private final ModelDeserializer<JsonParser> delegate;

   ArrayDeserializer(ModelDeserializer<JsonParser> delegate) {
      this.delegate = delegate;
   }

   public Object deserialize(JsonParser parser, DeserializationContextImpl context) {
      Collection<Object> collection = new ArrayList<>();

      while (parser.hasNext()) {
         Event next = parser.next();
         context.setLastValueEvent(next);
         switch (next) {
            case START_OBJECT:
            case START_ARRAY:
            case VALUE_STRING:
            case VALUE_TRUE:
            case VALUE_FALSE:
            case VALUE_NUMBER:
            case VALUE_NULL:
               DeserializationContextImpl newContext = new DeserializationContextImpl(context);
               collection.add(this.delegate.deserialize(parser, newContext));
               break;
            case END_ARRAY:
               return collection;
            default:
               throw new JsonbException("Unexpected state: " + next);
         }
      }

      return collection;
   }
}
