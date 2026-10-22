package org.eclipse.yasson.internal.jsonstructure;

import jakarta.json.JsonArray;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser.Event;
import java.util.Iterator;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

public class JsonArrayIterator extends JsonStructureIterator {
   private final Iterator<JsonValue> valueIterator;
   private JsonValue currentValue;

   public JsonArrayIterator(JsonArray jsonArray) {
      this.valueIterator = jsonArray.iterator();
   }

   @Override
   public boolean hasNext() {
      return true;
   }

   public Event next() {
      if (this.valueIterator.hasNext()) {
         this.currentValue = this.valueIterator.next();
         return this.getValueEvent(this.currentValue);
      } else {
         return Event.END_ARRAY;
      }
   }

   @Override
   JsonValue getValue() {
      return this.currentValue;
   }

   @Override
   JsonbException createIncompatibleValueError() {
      return new JsonbException(Messages.getMessage(MessageKeys.NUMBER_INCOMPATIBLE_VALUE_TYPE_ARRAY, this.getValue().getValueType()));
   }

   @Override
   String getString() {
      return this.currentValue instanceof JsonString ? ((JsonString)this.currentValue).getString() : this.currentValue.toString();
   }
}
