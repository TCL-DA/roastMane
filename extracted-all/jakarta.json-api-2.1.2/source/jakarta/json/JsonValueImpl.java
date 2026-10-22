package jakarta.json;

import java.io.Serializable;

final class JsonValueImpl implements JsonValue, Serializable {
   private static final long serialVersionUID = 83723433120886104L;
   private final JsonValue.ValueType valueType;

   JsonValueImpl(JsonValue.ValueType valueType) {
      this.valueType = valueType;
   }

   @Override
   public JsonValue.ValueType getValueType() {
      return this.valueType;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof JsonValue ? this.getValueType().equals(((JsonValue)obj).getValueType()) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.valueType.hashCode();
   }

   @Override
   public String toString() {
      return this.valueType.name().toLowerCase();
   }
}
