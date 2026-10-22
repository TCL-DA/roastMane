package org.eclipse.parsson;

import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

class JsonStructureParser implements JsonParser {
   private JsonStructureParser.Scope current;
   private Event state;
   private final Deque<JsonStructureParser.Scope> scopeStack = new ArrayDeque<>();

   JsonStructureParser(JsonArray array) {
      this.current = new JsonStructureParser.ArrayScope(array);
   }

   JsonStructureParser(JsonObject object) {
      this.current = new JsonStructureParser.ObjectScope(object);
   }

   public String getString() {
      switch (this.state) {
         case KEY_NAME:
            return ((JsonStructureParser.ObjectScope)this.current).key;
         case VALUE_STRING:
            return ((JsonString)this.current.getJsonValue()).getString();
         case VALUE_NUMBER:
            return ((JsonNumber)this.current.getJsonValue()).toString();
         default:
            throw new IllegalStateException(JsonMessages.PARSER_GETSTRING_ERR(this.state));
      }
   }

   public boolean isIntegralNumber() {
      if (this.state == Event.VALUE_NUMBER) {
         return ((JsonNumber)this.current.getJsonValue()).isIntegral();
      } else {
         throw new IllegalStateException(JsonMessages.PARSER_ISINTEGRALNUMBER_ERR(this.state));
      }
   }

   public int getInt() {
      if (this.state == Event.VALUE_NUMBER) {
         return ((JsonNumber)this.current.getJsonValue()).intValue();
      } else {
         throw new IllegalStateException(JsonMessages.PARSER_GETINT_ERR(this.state));
      }
   }

   public long getLong() {
      if (this.state == Event.VALUE_NUMBER) {
         return ((JsonNumber)this.current.getJsonValue()).longValue();
      } else {
         throw new IllegalStateException(JsonMessages.PARSER_GETLONG_ERR(this.state));
      }
   }

   public BigDecimal getBigDecimal() {
      if (this.state == Event.VALUE_NUMBER) {
         return ((JsonNumber)this.current.getJsonValue()).bigDecimalValue();
      } else {
         throw new IllegalStateException(JsonMessages.PARSER_GETBIGDECIMAL_ERR(this.state));
      }
   }

   public JsonLocation getLocation() {
      return JsonLocationImpl.UNKNOWN;
   }

   public boolean hasNext() {
      return this.state != Event.END_OBJECT && this.state != Event.END_ARRAY || !this.scopeStack.isEmpty();
   }

   public Event next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         this.transition();
         return this.state;
      }
   }

   private void transition() {
      if (this.state == null) {
         this.state = this.current instanceof JsonStructureParser.ArrayScope ? Event.START_ARRAY : Event.START_OBJECT;
      } else {
         if (this.state == Event.END_OBJECT || this.state == Event.END_ARRAY) {
            this.current = this.scopeStack.pop();
         }

         if (this.current instanceof JsonStructureParser.ArrayScope) {
            if (this.current.hasNext()) {
               this.current.next();
               this.state = getState(this.current.getJsonValue());
               if (this.state == Event.START_ARRAY || this.state == Event.START_OBJECT) {
                  this.scopeStack.push(this.current);
                  this.current = JsonStructureParser.Scope.createScope(this.current.getJsonValue());
               }
            } else {
               this.state = Event.END_ARRAY;
            }
         } else if (this.state == Event.KEY_NAME) {
            this.state = getState(this.current.getJsonValue());
            if (this.state == Event.START_ARRAY || this.state == Event.START_OBJECT) {
               this.scopeStack.push(this.current);
               this.current = JsonStructureParser.Scope.createScope(this.current.getJsonValue());
            }
         } else if (this.current.hasNext()) {
            this.current.next();
            this.state = Event.KEY_NAME;
         } else {
            this.state = Event.END_OBJECT;
         }
      }
   }

   public void close() {
   }

   public void skipObject() {
      if (this.current instanceof JsonStructureParser.ObjectScope) {
         int depth = 1;

         do {
            if (this.state == Event.KEY_NAME) {
               this.state = getState(this.current.getJsonValue());
               switch (this.state) {
                  case START_OBJECT:
                     depth++;
                     break;
                  case END_OBJECT:
                     depth--;
               }
            } else if (this.current.hasNext()) {
               this.current.next();
               this.state = Event.KEY_NAME;
            } else {
               this.state = Event.END_OBJECT;
               depth--;
            }
         } while (this.state != Event.END_OBJECT && depth > 0);
      }
   }

   public void skipArray() {
      if (this.current instanceof JsonStructureParser.ArrayScope) {
         int depth = 1;

         do {
            if (this.current.hasNext()) {
               this.current.next();
               this.state = getState(this.current.getJsonValue());
               switch (this.state) {
                  case START_ARRAY:
                     depth++;
                     break;
                  case END_ARRAY:
                     depth--;
               }
            } else {
               this.state = Event.END_ARRAY;
               depth--;
            }
         } while (this.state != Event.END_ARRAY || depth != 0);
      }
   }

   private static Event getState(JsonValue value) {
      switch (value.getValueType()) {
         case ARRAY:
            return Event.START_ARRAY;
         case OBJECT:
            return Event.START_OBJECT;
         case STRING:
            return Event.VALUE_STRING;
         case NUMBER:
            return Event.VALUE_NUMBER;
         case TRUE:
            return Event.VALUE_TRUE;
         case FALSE:
            return Event.VALUE_FALSE;
         case NULL:
            return Event.VALUE_NULL;
         default:
            throw new JsonException(JsonMessages.PARSER_STATE_ERR(value.getValueType()));
      }
   }

   private static class ArrayScope extends JsonStructureParser.Scope {
      private final Iterator<JsonValue> it;
      private JsonValue value;

      ArrayScope(JsonArray array) {
         this.it = array.iterator();
      }

      @Override
      public boolean hasNext() {
         return this.it.hasNext();
      }

      public JsonValue next() {
         this.value = this.it.next();
         return this.value;
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }

      @Override
      JsonValue getJsonValue() {
         return this.value;
      }
   }

   private static class ObjectScope extends JsonStructureParser.Scope {
      private final Iterator<Entry<String, JsonValue>> it;
      private JsonValue value;
      private String key;

      ObjectScope(JsonObject object) {
         this.it = object.entrySet().iterator();
      }

      @Override
      public boolean hasNext() {
         return this.it.hasNext();
      }

      public Entry<String, JsonValue> next() {
         Entry<String, JsonValue> next = this.it.next();
         this.key = next.getKey();
         this.value = next.getValue();
         return next;
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }

      @Override
      JsonValue getJsonValue() {
         return this.value;
      }
   }

   private abstract static class Scope implements Iterator {
      private Scope() {
      }

      abstract JsonValue getJsonValue();

      static JsonStructureParser.Scope createScope(JsonValue value) {
         if (value instanceof JsonArray) {
            return new JsonStructureParser.ArrayScope((JsonArray)value);
         } else if (value instanceof JsonObject) {
            return new JsonStructureParser.ObjectScope((JsonObject)value);
         } else {
            throw new JsonException(JsonMessages.PARSER_SCOPE_ERR(value));
         }
      }
   }
}
