package jakarta.json;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Set;
import java.util.Map.Entry;

final class EmptyObject extends AbstractMap<String, JsonValue> implements JsonObject, Serializable {
   private static final long serialVersionUID = -1461653546889072583L;

   @Override
   public Set<Entry<String, JsonValue>> entrySet() {
      return Collections.emptySet();
   }

   @Override
   public JsonArray getJsonArray(String name) {
      return (JsonArray)this.get(name);
   }

   @Override
   public JsonObject getJsonObject(String name) {
      return (JsonObject)this.get(name);
   }

   @Override
   public JsonNumber getJsonNumber(String name) {
      return (JsonNumber)this.get(name);
   }

   @Override
   public JsonString getJsonString(String name) {
      return (JsonString)this.get(name);
   }

   @Override
   public String getString(String name) {
      return this.getJsonString(name).getString();
   }

   @Override
   public String getString(String name, String defaultValue) {
      return defaultValue;
   }

   @Override
   public int getInt(String name) {
      return this.getJsonNumber(name).intValue();
   }

   @Override
   public int getInt(String name, int defaultValue) {
      return defaultValue;
   }

   @Override
   public boolean getBoolean(String name) {
      throw new NullPointerException();
   }

   @Override
   public boolean getBoolean(String name, boolean defaultValue) {
      return defaultValue;
   }

   @Override
   public boolean isNull(String name) {
      throw new NullPointerException();
   }

   @Override
   public JsonValue.ValueType getValueType() {
      return JsonValue.ValueType.OBJECT;
   }

   private Object readResolve() {
      return JsonValue.EMPTY_JSON_OBJECT;
   }
}
