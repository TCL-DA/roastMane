package org.eclipse.yasson.internal.jsonstructure;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser.Event;
import java.util.Iterator;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

public class JsonObjectIterator extends JsonStructureIterator {
   private final JsonObject jsonObject;
   private final Iterator<String> keyIterator;
   private String currentKey;
   private JsonObjectIterator.State state = JsonObjectIterator.State.START;

   JsonObjectIterator(JsonObject jsonObject) {
      this.jsonObject = jsonObject;
      this.keyIterator = jsonObject.keySet().iterator();
   }

   private void nextKey() {
      if (!this.keyIterator.hasNext()) {
         throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Object is empty"));
      } else {
         this.currentKey = this.keyIterator.next();
      }
   }

   public Event next() {
      switch (this.state) {
         case START:
            if (this.keyIterator.hasNext()) {
               this.nextKey();
               this.setState(JsonObjectIterator.State.KEY);
               return Event.KEY_NAME;
            }

            this.setState(JsonObjectIterator.State.END);
            return Event.END_OBJECT;
         case KEY:
            this.setState(JsonObjectIterator.State.VALUE);
            JsonValue value = this.getValue();
            return this.getValueEvent(value);
         case VALUE:
            if (this.keyIterator.hasNext()) {
               this.nextKey();
               this.setState(JsonObjectIterator.State.KEY);
               return Event.KEY_NAME;
            }

            this.setState(JsonObjectIterator.State.END);
            return Event.END_OBJECT;
         default:
            throw new JsonbException("Illegal state");
      }
   }

   @Override
   public boolean hasNext() {
      return this.state != JsonObjectIterator.State.END;
   }

   @Override
   public JsonValue getValue() {
      return (JsonValue)(this.state == JsonObjectIterator.State.START && this.currentKey == null
         ? this.jsonObject
         : (JsonValue)this.jsonObject.get(this.currentKey));
   }

   @Override
   String getString() {
      return this.state == JsonObjectIterator.State.KEY ? this.currentKey : super.getString();
   }

   @Override
   JsonbException createIncompatibleValueError() {
      return new JsonbException(Messages.getMessage(MessageKeys.NUMBER_INCOMPATIBLE_VALUE_TYPE_OBJECT, this.getValue().getValueType(), this.currentKey));
   }

   private void setState(JsonObjectIterator.State state) {
      this.state = state;
   }

   public String getKey() {
      return this.currentKey;
   }

   public static enum State {
      START,
      KEY,
      VALUE,
      END;
   }
}
