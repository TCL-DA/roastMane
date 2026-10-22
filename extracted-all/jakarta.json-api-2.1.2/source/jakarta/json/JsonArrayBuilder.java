package jakarta.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface JsonArrayBuilder {
   JsonArrayBuilder add(JsonValue var1);

   JsonArrayBuilder add(String var1);

   JsonArrayBuilder add(BigDecimal var1);

   JsonArrayBuilder add(BigInteger var1);

   JsonArrayBuilder add(int var1);

   JsonArrayBuilder add(long var1);

   JsonArrayBuilder add(double var1);

   JsonArrayBuilder add(boolean var1);

   JsonArrayBuilder addNull();

   JsonArrayBuilder add(JsonObjectBuilder var1);

   JsonArrayBuilder add(JsonArrayBuilder var1);

   default JsonArrayBuilder addAll(JsonArrayBuilder builder) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder add(int index, JsonValue value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder add(int index, String value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder add(int index, BigDecimal value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder add(int index, BigInteger value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder add(int index, int value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder add(int index, long value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder add(int index, double value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder add(int index, boolean value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder addNull(int index) {
      return this.add(index, JsonValue.NULL);
   }

   default JsonArrayBuilder add(int index, JsonObjectBuilder builder) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder add(int index, JsonArrayBuilder builder) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder set(int index, JsonValue value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder set(int index, String value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder set(int index, BigDecimal value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder set(int index, BigInteger value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder set(int index, int value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder set(int index, long value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder set(int index, double value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder set(int index, boolean value) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder setNull(int index) {
      return this.set(index, JsonValue.NULL);
   }

   default JsonArrayBuilder set(int index, JsonObjectBuilder builder) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder set(int index, JsonArrayBuilder builder) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder remove(int index) {
      throw new UnsupportedOperationException();
   }

   JsonArray build();
}
