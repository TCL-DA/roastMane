package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.util.Collection;
import org.eclipse.yasson.internal.DeserializationContextImpl;

class CollectionDeserializer implements ModelDeserializer<JsonParser> {
   private final ModelDeserializer<JsonParser> delegate;

   CollectionDeserializer(ModelDeserializer<JsonParser> delegate) {
      this.delegate = delegate;
   }

   public Object deserialize(JsonParser parser, DeserializationContextImpl context) {
      Collection<Object> collection = (Collection<Object>)context.getInstance();

      while (parser.hasNext()) {
         Event next = parser.next();
         context.setLastValueEvent(next);
         switch (next) {
            case VALUE_NULL:
            case START_OBJECT:
            case START_ARRAY:
            case VALUE_STRING:
            case VALUE_TRUE:
            case VALUE_FALSE:
            case VALUE_NUMBER:
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
