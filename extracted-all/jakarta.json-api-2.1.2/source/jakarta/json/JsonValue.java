package jakarta.json;

public interface JsonValue {
   JsonObject EMPTY_JSON_OBJECT = new EmptyObject();
   JsonArray EMPTY_JSON_ARRAY = new EmptyArray();
   JsonValue NULL = new JsonValueImpl(JsonValue.ValueType.NULL);
   JsonValue TRUE = new JsonValueImpl(JsonValue.ValueType.TRUE);
   JsonValue FALSE = new JsonValueImpl(JsonValue.ValueType.FALSE);

   JsonValue.ValueType getValueType();

   default JsonObject asJsonObject() {
      return JsonObject.class.cast(this);
   }

   default JsonArray asJsonArray() {
      return JsonArray.class.cast(this);
   }

   @Override
   String toString();

   public static enum ValueType {
      ARRAY,
      OBJECT,
      STRING,
      NUMBER,
      TRUE,
      FALSE,
      NULL;
   }
}
