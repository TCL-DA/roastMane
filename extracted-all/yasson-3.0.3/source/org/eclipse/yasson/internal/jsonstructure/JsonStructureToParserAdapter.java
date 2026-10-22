package org.eclipse.yasson.internal.jsonstructure;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

public class JsonStructureToParserAdapter implements JsonParser {
   private Deque<JsonStructureIterator> iterators = new ArrayDeque<>();
   private final JsonStructure rootStructure;

   public JsonStructureToParserAdapter(JsonStructure structure) {
      this.rootStructure = structure;
   }

   public boolean hasNext() {
      return this.iterators.peek().hasNext();
   }

   public Event next() {
      if (this.iterators.isEmpty()) {
         if (this.rootStructure instanceof JsonObject) {
            this.iterators.push(new JsonObjectIterator((JsonObject)this.rootStructure));
            return Event.START_OBJECT;
         }

         if (this.rootStructure instanceof JsonArray) {
            this.iterators.push(new JsonArrayIterator((JsonArray)this.rootStructure));
            return Event.START_ARRAY;
         }
      }

      JsonStructureIterator current = this.iterators.peek();
      Event next = current.next();
      if (next == Event.START_OBJECT) {
         this.iterators.push(new JsonObjectIterator((JsonObject)current.getValue()));
      } else if (next == Event.START_ARRAY) {
         this.iterators.push(new JsonArrayIterator((JsonArray)current.getValue()));
      } else if (next == Event.END_OBJECT || next == Event.END_ARRAY) {
         this.iterators.pop();
      }

      return next;
   }

   public String getString() {
      return this.iterators.peek().getString();
   }

   public boolean isIntegralNumber() {
      return this.getJsonNumberValue().isIntegral();
   }

   public int getInt() {
      return this.getJsonNumberValue().intValueExact();
   }

   public long getLong() {
      return this.getJsonNumberValue().longValueExact();
   }

   public BigDecimal getBigDecimal() {
      return this.getJsonNumberValue().bigDecimalValue();
   }

   public JsonObject getObject() {
      JsonStructureIterator current = this.iterators.peek();
      if (current instanceof JsonObjectIterator) {
         this.iterators.pop();
         return current.getValue().asJsonObject();
      } else {
         throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Outside of object context"));
      }
   }

   private JsonNumber getJsonNumberValue() {
      JsonStructureIterator iterator = this.iterators.peek();
      JsonValue value = iterator.getValue();
      if (value.getValueType() != ValueType.NUMBER) {
         throw iterator.createIncompatibleValueError();
      } else {
         return (JsonNumber)value;
      }
   }

   public JsonLocation getLocation() {
      throw new JsonbException("Operation not supported");
   }

   public void skipArray() {
      if (!this.iterators.isEmpty()) {
         JsonStructureIterator current = this.iterators.peek();
         if (current instanceof JsonArrayIterator) {
            this.iterators.pop();
         }
      }
   }

   public void skipObject() {
      if (!this.iterators.isEmpty()) {
         JsonStructureIterator current = this.iterators.peek();
         if (current instanceof JsonObjectIterator) {
            this.iterators.pop();
         }
      }
   }

   public void close() {
   }
}
