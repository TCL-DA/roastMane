package org.eclipse.yasson.internal.deserializer;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.stream.Stream;
import org.eclipse.yasson.internal.DeserializationContextImpl;

class YassonParser implements JsonParser {
   private final JsonParser delegate;
   private final DeserializationContextImpl context;
   private int level;

   YassonParser(JsonParser delegate, Event firstEvent, DeserializationContextImpl context) {
      this.delegate = delegate;
      this.context = context;
      this.level = this.determineLevelValue(firstEvent);
   }

   private int determineLevelValue(Event firstEvent) {
      switch (firstEvent) {
         case START_ARRAY:
         case START_OBJECT:
            return 1;
         default:
            return 0;
      }
   }

   void skipRemaining() {
      while (this.hasNext()) {
         this.next();
      }
   }

   public boolean hasNext() {
      return this.level < 1 ? false : this.delegate.hasNext();
   }

   public Event next() {
      this.validate();
      Event next = this.delegate.next();
      this.context.setLastValueEvent(next);
      switch (next) {
         case START_ARRAY:
         case START_OBJECT:
            this.level++;
            break;
         case END_OBJECT:
         case END_ARRAY:
            this.level--;
      }

      return next;
   }

   public String getString() {
      return this.delegate.getString();
   }

   public boolean isIntegralNumber() {
      return this.delegate.isIntegralNumber();
   }

   public int getInt() {
      return this.delegate.getInt();
   }

   public long getLong() {
      return this.delegate.getLong();
   }

   public BigDecimal getBigDecimal() {
      return this.delegate.getBigDecimal();
   }

   public JsonLocation getLocation() {
      return this.delegate.getLocation();
   }

   public JsonObject getObject() {
      this.validate();
      this.level--;
      JsonObject jsonObject = this.delegate.getObject();
      this.context.setLastValueEvent(Event.END_OBJECT);
      return jsonObject;
   }

   public JsonValue getValue() {
      Event currentLevel = this.context.getLastValueEvent();
      switch (currentLevel) {
         case START_ARRAY:
            return this.getArray();
         case START_OBJECT:
            return this.getObject();
         default:
            return this.delegate.getValue();
      }
   }

   public JsonArray getArray() {
      this.validate();
      this.level--;
      JsonArray array = this.delegate.getArray();
      this.context.setLastValueEvent(Event.END_ARRAY);
      return array;
   }

   public Stream<JsonValue> getArrayStream() {
      this.validate();
      this.level--;
      return this.delegate.getArrayStream();
   }

   public Stream<Entry<String, JsonValue>> getObjectStream() {
      this.validate();
      this.level--;
      return this.delegate.getObjectStream();
   }

   public Stream<JsonValue> getValueStream() {
      this.validate();
      this.level--;
      return this.delegate.getValueStream();
   }

   public void skipArray() {
      this.validate();
      this.level--;
      this.delegate.skipArray();
   }

   public void skipObject() {
      this.validate();
      this.level--;
      this.delegate.skipObject();
   }

   public void close() {
      throw new UnsupportedOperationException();
   }

   private void validate() {
      if (this.level < 1) {
         throw new NoSuchElementException("There are no more elements available!");
      }
   }
}
