package jakarta.json;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface JsonArray extends JsonStructure, List<JsonValue> {
   JsonObject getJsonObject(int var1);

   JsonArray getJsonArray(int var1);

   JsonNumber getJsonNumber(int var1);

   JsonString getJsonString(int var1);

   <T extends JsonValue> List<T> getValuesAs(Class<T> var1);

   default <T, K extends JsonValue> List<T> getValuesAs(Function<K, T> func) {
      Stream<K> stream = (Stream<K>)this.stream();
      return stream.map(func).collect(Collectors.toList());
   }

   String getString(int var1);

   String getString(int var1, String var2);

   int getInt(int var1);

   int getInt(int var1, int var2);

   boolean getBoolean(int var1);

   boolean getBoolean(int var1, boolean var2);

   boolean isNull(int var1);
}
