package org.eclipse.parsson;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParsingException;
import jakarta.json.stream.JsonParser.Event;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.eclipse.parsson.api.BufferPool;

public class JsonParserImpl implements JsonParser {
   private final BufferPool bufferPool;
   private final boolean rejectDuplicateKeys;
   private final Map<String, ?> config;
   private JsonParserImpl.Context currentContext = new JsonParserImpl.NoneContext();
   private Event currentEvent;
   private final JsonParserImpl.Stack stack = new JsonParserImpl.Stack();
   private final JsonTokenizer tokenizer;
   private boolean closed = false;

   public JsonParserImpl(Reader reader, BufferPool bufferPool) {
      this(reader, bufferPool, false, Collections.emptyMap());
   }

   public JsonParserImpl(Reader reader, BufferPool bufferPool, boolean rejectDuplicateKeys, Map<String, ?> config) {
      this.bufferPool = bufferPool;
      this.rejectDuplicateKeys = rejectDuplicateKeys;
      this.config = config;
      this.tokenizer = new JsonTokenizer(reader, bufferPool);
   }

   public JsonParserImpl(InputStream in, BufferPool bufferPool) {
      this(in, bufferPool, false, Collections.emptyMap());
   }

   public JsonParserImpl(InputStream in, BufferPool bufferPool, boolean rejectDuplicateKeys, Map<String, ?> config) {
      this.bufferPool = bufferPool;
      this.rejectDuplicateKeys = rejectDuplicateKeys;
      this.config = config;
      UnicodeDetectingInputStream uin = new UnicodeDetectingInputStream(in);
      this.tokenizer = new JsonTokenizer(new InputStreamReader(uin, uin.getCharset()), bufferPool);
   }

   public JsonParserImpl(InputStream in, Charset encoding, BufferPool bufferPool) {
      this(in, encoding, bufferPool, false, Collections.emptyMap());
   }

   public JsonParserImpl(InputStream in, Charset encoding, BufferPool bufferPool, boolean rejectDuplicateKeys, Map<String, ?> config) {
      this.bufferPool = bufferPool;
      this.rejectDuplicateKeys = rejectDuplicateKeys;
      this.config = config;
      this.tokenizer = new JsonTokenizer(new InputStreamReader(in, encoding), bufferPool);
   }

   public String getString() {
      if (this.currentEvent != Event.KEY_NAME && this.currentEvent != Event.VALUE_STRING && this.currentEvent != Event.VALUE_NUMBER) {
         throw new IllegalStateException(JsonMessages.PARSER_GETSTRING_ERR(this.currentEvent));
      } else {
         return this.tokenizer.getValue();
      }
   }

   public boolean isIntegralNumber() {
      if (this.currentEvent != Event.VALUE_NUMBER) {
         throw new IllegalStateException(JsonMessages.PARSER_ISINTEGRALNUMBER_ERR(this.currentEvent));
      } else {
         return this.tokenizer.isIntegral();
      }
   }

   public int getInt() {
      if (this.currentEvent != Event.VALUE_NUMBER) {
         throw new IllegalStateException(JsonMessages.PARSER_GETINT_ERR(this.currentEvent));
      } else {
         return this.tokenizer.getInt();
      }
   }

   boolean isDefinitelyInt() {
      return this.tokenizer.isDefinitelyInt();
   }

   boolean isDefinitelyLong() {
      return this.tokenizer.isDefinitelyLong();
   }

   public long getLong() {
      if (this.currentEvent != Event.VALUE_NUMBER) {
         throw new IllegalStateException(JsonMessages.PARSER_GETLONG_ERR(this.currentEvent));
      } else {
         return this.tokenizer.getLong();
      }
   }

   public BigDecimal getBigDecimal() {
      if (this.currentEvent != Event.VALUE_NUMBER) {
         throw new IllegalStateException(JsonMessages.PARSER_GETBIGDECIMAL_ERR(this.currentEvent));
      } else {
         return this.tokenizer.getBigDecimal();
      }
   }

   public JsonArray getArray() {
      if (this.currentEvent != Event.START_ARRAY) {
         throw new IllegalStateException(JsonMessages.PARSER_GETARRAY_ERR(this.currentEvent));
      } else {
         return this.getArray(new JsonArrayBuilderImpl(this.bufferPool));
      }
   }

   public JsonObject getObject() {
      if (this.currentEvent != Event.START_OBJECT) {
         throw new IllegalStateException(JsonMessages.PARSER_GETOBJECT_ERR(this.currentEvent));
      } else {
         return this.getObject(new JsonObjectBuilderImpl(this.bufferPool, this.rejectDuplicateKeys, this.config));
      }
   }

   public JsonValue getValue() {
      switch (this.currentEvent) {
         case START_ARRAY:
            return this.getArray(new JsonArrayBuilderImpl(this.bufferPool));
         case START_OBJECT:
            return this.getObject(new JsonObjectBuilderImpl(this.bufferPool, this.rejectDuplicateKeys, this.config));
         case KEY_NAME:
         case VALUE_STRING:
            return new JsonStringImpl(this.getCharSequence());
         case VALUE_NUMBER:
            if (this.isDefinitelyInt()) {
               return JsonNumberImpl.getJsonNumber(this.getInt());
            } else {
               if (this.isDefinitelyLong()) {
                  return JsonNumberImpl.getJsonNumber(this.getLong());
               }

               return JsonNumberImpl.getJsonNumber(this.getBigDecimal());
            }
         case VALUE_TRUE:
            return JsonValue.TRUE;
         case VALUE_FALSE:
            return JsonValue.FALSE;
         case VALUE_NULL:
            return JsonValue.NULL;
         case END_ARRAY:
         case END_OBJECT:
         default:
            throw new IllegalStateException(JsonMessages.PARSER_GETVALUE_ERR(this.currentEvent));
      }
   }

   public Stream<JsonValue> getArrayStream() {
      if (this.currentEvent != Event.START_ARRAY) {
         throw new IllegalStateException(JsonMessages.PARSER_GETARRAY_ERR(this.currentEvent));
      } else {
         Spliterator<JsonValue> spliterator = new AbstractSpliterator<JsonValue>(Long.MAX_VALUE, 16) {
            @Override
            public Spliterator<JsonValue> trySplit() {
               return null;
            }

            @Override
            public boolean tryAdvance(Consumer<? super JsonValue> action) {
               if (action == null) {
                  throw new NullPointerException();
               } else if (!JsonParserImpl.this.hasNext()) {
                  return false;
               } else if (JsonParserImpl.this.next() == Event.END_ARRAY) {
                  return false;
               } else {
                  action.accept(JsonParserImpl.this.getValue());
                  return true;
               }
            }
         };
         return StreamSupport.stream(spliterator, false);
      }
   }

   public Stream<Entry<String, JsonValue>> getObjectStream() {
      if (this.currentEvent != Event.START_OBJECT) {
         throw new IllegalStateException(JsonMessages.PARSER_GETOBJECT_ERR(this.currentEvent));
      } else {
         Spliterator<Entry<String, JsonValue>> spliterator = new AbstractSpliterator<Entry<String, JsonValue>>(Long.MAX_VALUE, 16) {
            @Override
            public Spliterator<Entry<String, JsonValue>> trySplit() {
               return null;
            }

            @Override
            public boolean tryAdvance(Consumer<? super Entry<String, JsonValue>> action) {
               if (action == null) {
                  throw new NullPointerException();
               } else if (!JsonParserImpl.this.hasNext()) {
                  return false;
               } else {
                  Event e = JsonParserImpl.this.next();
                  if (e == Event.END_OBJECT) {
                     return false;
                  } else if (e != Event.KEY_NAME) {
                     throw new JsonException(JsonMessages.INTERNAL_ERROR());
                  } else {
                     String key = JsonParserImpl.this.getString();
                     if (!JsonParserImpl.this.hasNext()) {
                        throw new JsonException(JsonMessages.INTERNAL_ERROR());
                     } else {
                        JsonParserImpl.this.next();
                        JsonValue value = JsonParserImpl.this.getValue();
                        action.accept(new SimpleImmutableEntry<>(key, value));
                        return true;
                     }
                  }
               }
            }
         };
         return StreamSupport.stream(spliterator, false);
      }
   }

   public Stream<JsonValue> getValueStream() {
      if (!(this.currentContext instanceof JsonParserImpl.NoneContext)) {
         throw new IllegalStateException(JsonMessages.PARSER_GETVALUESTREAM_ERR());
      } else {
         Spliterator<JsonValue> spliterator = new AbstractSpliterator<JsonValue>(Long.MAX_VALUE, 16) {
            @Override
            public Spliterator<JsonValue> trySplit() {
               return null;
            }

            @Override
            public boolean tryAdvance(Consumer<? super JsonValue> action) {
               if (action == null) {
                  throw new NullPointerException();
               } else if (!JsonParserImpl.this.hasNext()) {
                  return false;
               } else {
                  JsonParserImpl.this.next();
                  action.accept(JsonParserImpl.this.getValue());
                  return true;
               }
            }
         };
         return StreamSupport.stream(spliterator, false);
      }
   }

   public void skipArray() {
      if (this.currentEvent == Event.START_ARRAY) {
         this.currentContext.skip();
         this.currentContext = this.stack.pop();
         this.currentEvent = Event.END_ARRAY;
      }
   }

   public void skipObject() {
      if (this.currentEvent == Event.START_OBJECT) {
         this.currentContext.skip();
         this.currentContext = this.stack.pop();
         this.currentEvent = Event.END_OBJECT;
      }
   }

   private JsonArray getArray(JsonArrayBuilder builder) {
      while (this.hasNext()) {
         Event e = this.next();
         if (e == Event.END_ARRAY) {
            return builder.build();
         }

         builder.add(this.getValue());
      }

      throw this.parsingException(JsonTokenizer.JsonToken.EOF, "[CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL, SQUARECLOSE]");
   }

   private CharSequence getCharSequence() {
      if (this.currentEvent != Event.KEY_NAME && this.currentEvent != Event.VALUE_STRING && this.currentEvent != Event.VALUE_NUMBER) {
         throw new IllegalStateException(JsonMessages.PARSER_GETSTRING_ERR(this.currentEvent));
      } else {
         return this.tokenizer.getCharSequence();
      }
   }

   private JsonObject getObject(JsonObjectBuilder builder) {
      while (this.hasNext()) {
         Event e = this.next();
         if (e == Event.END_OBJECT) {
            return builder.build();
         }

         String key = this.getString();
         this.next();
         builder.add(key, this.getValue());
      }

      throw this.parsingException(JsonTokenizer.JsonToken.EOF, "[STRING, CURLYCLOSE]");
   }

   public JsonLocation getLocation() {
      return this.tokenizer.getLocation();
   }

   public JsonLocation getLastCharLocation() {
      return this.tokenizer.getLastCharLocation();
   }

   public boolean hasNext() {
      if (this.stack.isEmpty() && this.currentEvent != null && this.currentEvent.compareTo(Event.KEY_NAME) > 0) {
         JsonTokenizer.JsonToken token = this.tokenizer.nextToken();
         if (token != JsonTokenizer.JsonToken.EOF) {
            throw new JsonParsingException(JsonMessages.PARSER_EXPECTED_EOF(token), this.getLastCharLocation());
         } else {
            return false;
         }
      } else if (!this.stack.isEmpty() && !this.tokenizer.hasNextToken()) {
         this.currentEvent = this.currentContext.getNextEvent();
         return false;
      } else {
         return true;
      }
   }

   public Event next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.currentEvent = this.currentContext.getNextEvent();
      }
   }

   public Event currentEvent() {
      return this.currentEvent;
   }

   public void close() {
      if (!this.closed) {
         try {
            this.tokenizer.close();
            this.closed = true;
         } catch (IOException var2) {
            throw new JsonException(JsonMessages.PARSER_TOKENIZER_CLOSE_IO(), var2);
         }
      }
   }

   private JsonParsingException parsingException(JsonTokenizer.JsonToken token, String expectedTokens) {
      JsonLocation location = this.getLastCharLocation();
      return new JsonParsingException(JsonMessages.PARSER_INVALID_TOKEN(token, location, expectedTokens), location);
   }

   private final class ArrayContext extends JsonParserImpl.Context {
      private boolean firstValue = true;

      private ArrayContext() {
      }

      @Override
      public Event getNextEvent() {
         JsonTokenizer.JsonToken token = JsonParserImpl.this.tokenizer.nextToken();
         if (token == JsonTokenizer.JsonToken.EOF) {
            switch (JsonParserImpl.this.currentEvent) {
               case START_ARRAY:
                  throw JsonParserImpl.this.parsingException(token, "[CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]");
               default:
                  throw JsonParserImpl.this.parsingException(token, "[COMMA, CURLYCLOSE]");
            }
         } else if (token == JsonTokenizer.JsonToken.SQUARECLOSE) {
            JsonParserImpl.this.currentContext = JsonParserImpl.this.stack.pop();
            return Event.END_ARRAY;
         } else {
            if (this.firstValue) {
               this.firstValue = false;
            } else {
               if (token != JsonTokenizer.JsonToken.COMMA) {
                  throw JsonParserImpl.this.parsingException(token, "[COMMA]");
               }

               token = JsonParserImpl.this.tokenizer.nextToken();
            }

            if (token.isValue()) {
               return token.getEvent();
            } else if (token == JsonTokenizer.JsonToken.CURLYOPEN) {
               JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
               JsonParserImpl.this.currentContext = JsonParserImpl.this.new ObjectContext();
               return Event.START_OBJECT;
            } else if (token == JsonTokenizer.JsonToken.SQUAREOPEN) {
               JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
               JsonParserImpl.this.currentContext = JsonParserImpl.this.new ArrayContext();
               return Event.START_ARRAY;
            } else {
               throw JsonParserImpl.this.parsingException(token, "[CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]");
            }
         }
      }

      @Override
      void skip() {
         int depth = 1;

         JsonTokenizer.JsonToken token;
         do {
            token = JsonParserImpl.this.tokenizer.nextToken();
            switch (token) {
               case SQUARECLOSE:
                  depth--;
                  break;
               case SQUAREOPEN:
                  depth++;
            }
         } while (token != JsonTokenizer.JsonToken.SQUARECLOSE || depth != 0);
      }
   }

   private abstract class Context {
      JsonParserImpl.Context next;

      private Context() {
      }

      abstract Event getNextEvent();

      abstract void skip();
   }

   private final class NoneContext extends JsonParserImpl.Context {
      private NoneContext() {
      }

      @Override
      public Event getNextEvent() {
         JsonTokenizer.JsonToken token = JsonParserImpl.this.tokenizer.nextToken();
         if (token == JsonTokenizer.JsonToken.CURLYOPEN) {
            JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
            JsonParserImpl.this.currentContext = JsonParserImpl.this.new ObjectContext();
            return Event.START_OBJECT;
         } else if (token == JsonTokenizer.JsonToken.SQUAREOPEN) {
            JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
            JsonParserImpl.this.currentContext = JsonParserImpl.this.new ArrayContext();
            return Event.START_ARRAY;
         } else if (token.isValue()) {
            return token.getEvent();
         } else {
            throw JsonParserImpl.this.parsingException(token, "[CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]");
         }
      }

      @Override
      void skip() {
      }
   }

   private final class ObjectContext extends JsonParserImpl.Context {
      private boolean firstValue = true;

      private ObjectContext() {
      }

      @Override
      public Event getNextEvent() {
         JsonTokenizer.JsonToken token = JsonParserImpl.this.tokenizer.nextToken();
         if (token == JsonTokenizer.JsonToken.EOF) {
            switch (JsonParserImpl.this.currentEvent) {
               case START_OBJECT:
                  throw JsonParserImpl.this.parsingException(token, "[STRING, CURLYCLOSE]");
               case KEY_NAME:
                  throw JsonParserImpl.this.parsingException(token, "[COLON]");
               default:
                  throw JsonParserImpl.this.parsingException(token, "[COMMA, CURLYCLOSE]");
            }
         } else if (JsonParserImpl.this.currentEvent == Event.KEY_NAME) {
            if (token != JsonTokenizer.JsonToken.COLON) {
               throw JsonParserImpl.this.parsingException(token, "[COLON]");
            } else {
               token = JsonParserImpl.this.tokenizer.nextToken();
               if (token.isValue()) {
                  return token.getEvent();
               } else if (token == JsonTokenizer.JsonToken.CURLYOPEN) {
                  JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
                  JsonParserImpl.this.currentContext = JsonParserImpl.this.new ObjectContext();
                  return Event.START_OBJECT;
               } else if (token == JsonTokenizer.JsonToken.SQUAREOPEN) {
                  JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
                  JsonParserImpl.this.currentContext = JsonParserImpl.this.new ArrayContext();
                  return Event.START_ARRAY;
               } else {
                  throw JsonParserImpl.this.parsingException(token, "[CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]");
               }
            }
         } else if (token == JsonTokenizer.JsonToken.CURLYCLOSE) {
            JsonParserImpl.this.currentContext = JsonParserImpl.this.stack.pop();
            return Event.END_OBJECT;
         } else {
            if (this.firstValue) {
               this.firstValue = false;
            } else {
               if (token != JsonTokenizer.JsonToken.COMMA) {
                  throw JsonParserImpl.this.parsingException(token, "[COMMA]");
               }

               token = JsonParserImpl.this.tokenizer.nextToken();
            }

            if (token == JsonTokenizer.JsonToken.STRING) {
               return Event.KEY_NAME;
            } else {
               throw JsonParserImpl.this.parsingException(token, "[STRING]");
            }
         }
      }

      @Override
      void skip() {
         int depth = 1;

         JsonTokenizer.JsonToken token;
         do {
            token = JsonParserImpl.this.tokenizer.nextToken();
            switch (token) {
               case CURLYCLOSE:
                  depth--;
                  break;
               case CURLYOPEN:
                  depth++;
            }
         } while (token != JsonTokenizer.JsonToken.CURLYCLOSE || depth != 0);
      }
   }

   private static final class Stack {
      private JsonParserImpl.Context head;

      private Stack() {
      }

      private void push(JsonParserImpl.Context context) {
         context.next = this.head;
         this.head = context;
      }

      private JsonParserImpl.Context pop() {
         if (this.head == null) {
            throw new NoSuchElementException();
         } else {
            JsonParserImpl.Context temp = this.head;
            this.head = this.head.next;
            return temp;
         }
      }

      private JsonParserImpl.Context peek() {
         return this.head;
      }

      private boolean isEmpty() {
         return this.head == null;
      }
   }
}
