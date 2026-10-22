package jakarta.json;

import java.util.Map;

public interface JsonObject extends JsonStructure, Map<String, JsonValue> {
   JsonArray getJsonArray(String var1);

   JsonObject getJsonObject(String var1);

   JsonNumber getJsonNumber(String var1);

   JsonString getJsonString(String var1);

   String getString(String var1);

   String getString(String var1, String var2);

   int getInt(String var1);

   int getInt(String var1, int var2);

   boolean getBoolean(String var1);

   boolean getBoolean(String var1, boolean var2);

   boolean isNull(String var1);
}
