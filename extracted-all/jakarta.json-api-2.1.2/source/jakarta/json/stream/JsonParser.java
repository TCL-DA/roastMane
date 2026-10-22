package jakarta.json.stream;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.Closeable;
import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.stream.Stream;

public interface JsonParser extends Closeable {
   boolean hasNext();

   JsonParser.Event next();

   default JsonParser.Event currentEvent() {
      throw new UnsupportedOperationException();
   }

   String getString();

   boolean isIntegralNumber();

   int getInt();

   long getLong();

   BigDecimal getBigDecimal();

   JsonLocation getLocation();

   default JsonObject getObject() {
      throw new UnsupportedOperationException();
   }

   default JsonValue getValue() {
      throw new UnsupportedOperationException();
   }

   default JsonArray getArray() {
      throw new UnsupportedOperationException();
   }

   default Stream<JsonValue> getArrayStream() {
      throw new UnsupportedOperationException();
   }

   default Stream<Entry<String, JsonValue>> getObjectStream() {
      throw new UnsupportedOperationException();
   }

   default Stream<JsonValue> getValueStream() {
      throw new UnsupportedOperationException();
   }

   default void skipArray() {
      throw new UnsupportedOperationException();
   }

   default void skipObject() {
      throw new UnsupportedOperationException();
   }

   @Override
   void close();

   public static enum Event {
      START_ARRAY,
      START_OBJECT,
      KEY_NAME,
      VALUE_STRING,
      VALUE_NUMBER,
      VALUE_TRUE,
      VALUE_FALSE,
      VALUE_NULL,
      END_OBJECT,
      END_ARRAY;
   }
}
