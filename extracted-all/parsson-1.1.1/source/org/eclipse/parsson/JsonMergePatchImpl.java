package org.eclipse.parsson;

import jakarta.json.JsonMergePatch;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import java.util.Objects;

public final class JsonMergePatchImpl implements JsonMergePatch {
   private JsonValue patch;

   public JsonMergePatchImpl(JsonValue patch) {
      this.patch = patch;
   }

   public JsonValue apply(JsonValue target) {
      return mergePatch(target, this.patch);
   }

   public JsonValue toJsonValue() {
      return this.patch;
   }

   private static JsonValue mergePatch(JsonValue target, JsonValue patch) {
      if (patch.getValueType() != ValueType.OBJECT) {
         return patch;
      } else {
         if (target.getValueType() != ValueType.OBJECT) {
            target = JsonValue.EMPTY_JSON_OBJECT;
         }

         JsonObject targetJsonObject = target.asJsonObject();
         JsonObjectBuilder builder = new JsonObjectBuilderImpl(targetJsonObject, JsonUtil.getInternalBufferPool());
         patch.asJsonObject().forEach((key, value) -> {
            if (value == JsonValue.NULL) {
               if (targetJsonObject.containsKey(key)) {
                  builder.remove(key);
               }
            } else if (targetJsonObject.containsKey(key)) {
               builder.add(key, mergePatch((JsonValue)targetJsonObject.get(key), value));
            } else {
               builder.add(key, mergePatch(JsonValue.EMPTY_JSON_OBJECT, value));
            }
         });
         return builder.build();
      }
   }

   static JsonValue diff(JsonValue source, JsonValue target) {
      if (source.getValueType() == ValueType.OBJECT && target.getValueType() == ValueType.OBJECT) {
         JsonObject s = (JsonObject)source;
         JsonObject t = (JsonObject)target;
         JsonObjectBuilder builder = new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool());
         s.forEach((key, value) -> {
            if (t.containsKey(key)) {
               if (!value.equals(t.get(key))) {
                  builder.add(key, diff(value, (JsonValue)t.get(key)));
               }
            } else {
               builder.addNull(key);
            }
         });
         t.forEach((key, value) -> {
            if (!s.containsKey(key)) {
               builder.add(key, value);
            }
         });
         return builder.build();
      } else {
         return target;
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != JsonMergePatchImpl.class) {
         return false;
      } else {
         JsonMergePatchImpl other = (JsonMergePatchImpl)obj;
         return Objects.equals(this.patch, other.patch);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.patch);
   }

   @Override
   public String toString() {
      return this.patch.toString();
   }
}
