package jakarta.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface JsonObjectBuilder {
   JsonObjectBuilder add(String var1, JsonValue var2);

   JsonObjectBuilder add(String var1, String var2);

   JsonObjectBuilder add(String var1, BigInteger var2);

   JsonObjectBuilder add(String var1, BigDecimal var2);

   JsonObjectBuilder add(String var1, int var2);

   JsonObjectBuilder add(String var1, long var2);

   JsonObjectBuilder add(String var1, double var2);

   JsonObjectBuilder add(String var1, boolean var2);

   JsonObjectBuilder addNull(String var1);

   JsonObjectBuilder add(String var1, JsonObjectBuilder var2);

   JsonObjectBuilder add(String var1, JsonArrayBuilder var2);

   default JsonObjectBuilder addAll(JsonObjectBuilder builder) {
      throw new UnsupportedOperationException();
   }

   default JsonObjectBuilder remove(String name) {
      throw new UnsupportedOperationException();
   }

   JsonObject build();
}
