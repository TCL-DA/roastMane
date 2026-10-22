package org.eclipse.yasson.internal.jsonstructure;

import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser.Event;
import java.util.Iterator;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

abstract class JsonStructureIterator implements Iterator<Event> {
   abstract JsonValue getValue();

   abstract JsonbException createIncompatibleValueError();

   String getString() {
      JsonValue value = this.getValue();
      return value instanceof JsonString ? ((JsonString)value).getString() : value.toString();
   }

   Event getValueEvent(JsonValue value) {
      switch (value.getValueType()) {
         case NUMBER:
            return Event.VALUE_NUMBER;
         case STRING:
         case TRUE:
         case FALSE:
            return Event.VALUE_STRING;
         case OBJECT:
            return Event.START_OBJECT;
         case ARRAY:
            return Event.START_ARRAY;
         case NULL:
            return Event.VALUE_NULL;
         default:
            throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "unknown json value: " + value.getValueType()));
      }
   }
}
