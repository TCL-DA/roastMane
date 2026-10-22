package org.eclipse.parsson;

import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonPointer;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import java.io.Serializable;
import java.util.function.BiFunction;

public final class JsonPointerImpl implements JsonPointer, Serializable {
   private static final long serialVersionUID = -8123110179640843141L;
   private final String[] tokens;
   private final String jsonPointer;

   public JsonPointerImpl(String jsonPointer) {
      this.jsonPointer = jsonPointer;
      this.tokens = jsonPointer.split("/", -1);
      if (!"".equals(this.tokens[0])) {
         throw new JsonException(JsonMessages.POINTER_FORMAT_INVALID());
      } else {
         for (int i = 1; i < this.tokens.length; i++) {
            String token = this.tokens[i];
            StringBuilder reftoken = new StringBuilder();

            for (int j = 0; j < token.length(); j++) {
               char ch = token.charAt(j);
               if (ch == '~' && j < token.length() - 1) {
                  char ch1 = token.charAt(j + 1);
                  if (ch1 == '0') {
                     ch = '~';
                     j++;
                  } else if (ch1 == '1') {
                     ch = '/';
                     j++;
                  }
               }

               reftoken.append(ch);
            }

            this.tokens[i] = reftoken.toString();
         }
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj != null && obj.getClass() == JsonPointerImpl.class ? this.jsonPointer.equals(((JsonPointerImpl)obj).jsonPointer) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.jsonPointer.hashCode();
   }

   public boolean containsValue(JsonStructure target) {
      NodeReference[] refs = this.getReferences(target);
      return refs[0].contains();
   }

   public JsonValue getValue(JsonStructure target) {
      NodeReference[] refs = this.getReferences(target);
      return refs[0].get();
   }

   public JsonStructure add(JsonStructure target, JsonValue value) {
      return this.execute(NodeReference::add, target, value);
   }

   public JsonStructure replace(JsonStructure target, JsonValue value) {
      return this.execute(NodeReference::replace, target, value);
   }

   public JsonStructure remove(JsonStructure target) {
      return this.execute((r, v) -> r.remove(), target, null);
   }

   @Override
   public String toString() {
      return this.jsonPointer;
   }

   private JsonStructure execute(BiFunction<NodeReference, JsonValue, JsonStructure> op, JsonStructure target, JsonValue value) {
      NodeReference[] refs = this.getReferences(target);
      JsonStructure result = op.apply(refs[0], value);

      for (int i = 1; i < refs.length; i++) {
         result = refs[i].replace(result);
      }

      return result;
   }

   private NodeReference[] getReferences(JsonStructure target) {
      if (this.tokens.length == 1) {
         return new NodeReference[]{NodeReference.of(target)};
      } else {
         NodeReference[] references = new NodeReference[this.tokens.length - 1];
         JsonValue value = target;
         int s = this.tokens.length;

         for (int i = 1; i < s; i++) {
            switch (value.getValueType()) {
               case OBJECT:
                  JsonObject object = (JsonObject)value;
                  references[s - i - 1] = NodeReference.of(object, this.tokens[i]);
                  if (i < s - 1) {
                     value = (JsonValue)object.get(this.tokens[i]);
                     if (value == null) {
                        throw new JsonException(JsonMessages.POINTER_MAPPING_MISSING(object, this.tokens[i]));
                     }
                  }
                  break;
               case ARRAY:
                  int index = getIndex(this.tokens[i]);
                  JsonArray array = (JsonArray)value;
                  references[s - i - 1] = NodeReference.of(array, index);
                  if (i < s - 1 && index != -1) {
                     if (index >= array.size()) {
                        throw new JsonException(JsonMessages.NODEREF_ARRAY_INDEX_ERR(index, array.size()));
                     }

                     value = (JsonValue)array.get(index);
                  }
                  break;
               default:
                  throw new JsonException(JsonMessages.POINTER_REFERENCE_INVALID(value.getValueType()));
            }
         }

         return references;
      }
   }

   private static int getIndex(String token) {
      if (token == null || token.length() == 0) {
         throw new JsonException(JsonMessages.POINTER_ARRAY_INDEX_ERR(token));
      } else if (token.equals("-")) {
         return -1;
      } else if (token.equals("0")) {
         return 0;
      } else if (token.charAt(0) != '+' && token.charAt(0) != '-') {
         try {
            return Integer.parseInt(token);
         } catch (NumberFormatException var2) {
            throw new JsonException(JsonMessages.POINTER_ARRAY_INDEX_ILLEGAL(token), var2);
         }
      } else {
         throw new JsonException(JsonMessages.POINTER_ARRAY_INDEX_ERR(token));
      }
   }
}
