package jakarta.json;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

final class EmptyArray extends AbstractList<JsonValue> implements JsonArray, Serializable, RandomAccess {
   private static final long serialVersionUID = 7295439472061642859L;

   public JsonValue get(int index) {
      throw new IndexOutOfBoundsException("Index: " + index);
   }

   @Override
   public int size() {
      return 0;
   }

   @Override
   public JsonObject getJsonObject(int index) {
      return (JsonObject)this.get(index);
   }

   @Override
   public JsonArray getJsonArray(int index) {
      return (JsonArray)this.get(index);
   }

   @Override
   public JsonNumber getJsonNumber(int index) {
      return (JsonNumber)this.get(index);
   }

   @Override
   public JsonString getJsonString(int index) {
      return (JsonString)this.get(index);
   }

   @Override
   public <T extends JsonValue> List<T> getValuesAs(Class<T> clazz) {
      return Collections.emptyList();
   }

   @Override
   public String getString(int index) {
      return this.getJsonString(index).getString();
   }

   @Override
   public String getString(int index, String defaultValue) {
      return defaultValue;
   }

   @Override
   public int getInt(int index) {
      return this.getJsonNumber(index).intValue();
   }

   @Override
   public int getInt(int index, int defaultValue) {
      return defaultValue;
   }

   @Override
   public boolean getBoolean(int index) {
      return this.get(index) == JsonValue.TRUE;
   }

   @Override
   public boolean getBoolean(int index, boolean defaultValue) {
      return defaultValue;
   }

   @Override
   public boolean isNull(int index) {
      return this.get(index) == JsonValue.NULL;
   }

   @Override
   public JsonValue.ValueType getValueType() {
      return JsonValue.ValueType.ARRAY;
   }

   private Object readResolve() {
      return JsonValue.EMPTY_JSON_ARRAY;
   }
}
