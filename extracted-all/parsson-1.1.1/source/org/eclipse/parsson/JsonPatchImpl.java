package org.eclipse.parsson;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonPatch;
import jakarta.json.JsonPatchBuilder;
import jakarta.json.JsonPointer;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonPatch.Operation;
import jakarta.json.JsonValue.ValueType;

public class JsonPatchImpl implements JsonPatch {
   private final JsonArray patch;

   public JsonPatchImpl(JsonArray patch) {
      this.patch = patch;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj != null && obj.getClass() == JsonPatchImpl.class ? this.patch.equals(((JsonPatchImpl)obj).patch) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.patch.hashCode();
   }

   @Override
   public String toString() {
      return this.patch.toString();
   }

   public JsonStructure apply(JsonStructure target) {
      JsonStructure result = target;

      for (JsonValue operation : this.patch) {
         if (operation.getValueType() != ValueType.OBJECT) {
            throw new JsonException(JsonMessages.PATCH_MUST_BE_ARRAY());
         }

         result = this.apply(result, (JsonObject)operation);
      }

      return result;
   }

   public JsonArray toJsonArray() {
      return this.patch;
   }

   public static JsonArray diff(JsonStructure source, JsonStructure target) {
      return new JsonPatchImpl.DiffGenerator().diff(source, target);
   }

   private JsonStructure apply(JsonStructure target, JsonObject operation) {
      JsonPointer pointer = this.getPointer(operation, "path");
      JsonString op = operation.getJsonString("op");
      if (op == null) {
         throw new JsonException(JsonMessages.PATCH_OPERATION_MISSING());
      } else {
         switch (Operation.fromOperationName(op.getString())) {
            case ADD:
               return pointer.add(target, this.getValue(operation));
            case REPLACE:
               return pointer.replace(target, this.getValue(operation));
            case REMOVE:
               return pointer.remove(target);
            case COPY:
               JsonPointer from = this.getPointer(operation, "from");
               return pointer.add(target, from.getValue(target));
            case MOVE:
               String dest = operation.getString("path");
               String src = operation.getString("from");
               if (dest.startsWith(src) && src.length() < dest.length()) {
                  throw new JsonException(JsonMessages.PATCH_MOVE_PROPER_PREFIX(src, dest));
               } else {
                  JsonPointer fromx = this.getPointer(operation, "from");
                  if (!fromx.containsValue(target)) {
                     throw new JsonException(JsonMessages.PATCH_MOVE_TARGET_NULL(src));
                  } else {
                     if (pointer.equals(fromx)) {
                        return target;
                     }

                     return pointer.add(fromx.remove(target), fromx.getValue(target));
                  }
               }
            case TEST:
               if (!this.getValue(operation).equals(pointer.getValue(target))) {
                  throw new JsonException(JsonMessages.PATCH_TEST_FAILED(operation.getString("path"), this.getValue(operation).toString()));
               }

               return target;
            default:
               throw new JsonException(JsonMessages.PATCH_ILLEGAL_OPERATION(operation.getString("op")));
         }
      }
   }

   private JsonPointer getPointer(JsonObject operation, String member) {
      JsonString pointerString = operation.getJsonString(member);
      if (pointerString == null) {
         this.missingMember(operation.getString("op"), member);
      }

      return new JsonPointerImpl(pointerString.getString());
   }

   private JsonValue getValue(JsonObject operation) {
      JsonValue value = (JsonValue)operation.get("value");
      if (value == null) {
         this.missingMember(operation.getString("op"), "value");
      }

      return value;
   }

   private void missingMember(String op, String member) {
      throw new JsonException(JsonMessages.PATCH_MEMBER_MISSING(op, member));
   }

   static class DiffGenerator {
      private JsonPatchBuilder builder;

      JsonArray diff(JsonStructure source, JsonStructure target) {
         this.builder = new JsonPatchBuilderImpl();
         this.diff("", source, target);
         return this.builder.build().toJsonArray();
      }

      private void diff(String path, JsonValue source, JsonValue target) {
         if (!source.equals(target)) {
            ValueType s = source.getValueType();
            ValueType t = target.getValueType();
            if (s == ValueType.OBJECT && t == ValueType.OBJECT) {
               this.diffObject(path, (JsonObject)source, (JsonObject)target);
            } else if (s == ValueType.ARRAY && t == ValueType.ARRAY) {
               this.diffArray(path, (JsonArray)source, (JsonArray)target);
            } else {
               this.builder.replace(path, target);
            }
         }
      }

      private void diffObject(String path, JsonObject source, JsonObject target) {
         source.forEach((key, value) -> {
            if (target.containsKey(key)) {
               this.diff(path + '/' + Json.encodePointer(key), value, (JsonValue)target.get(key));
            } else {
               this.builder.remove(path + '/' + Json.encodePointer(key));
            }
         });
         target.forEach((key, value) -> {
            if (!source.containsKey(key)) {
               this.builder.add(path + '/' + Json.encodePointer(key), value);
            }
         });
      }

      private void diffArray(String path, JsonArray source, JsonArray target) {
         int m = source.size();
         int n = target.size();
         int[][] c = new int[m + 1][n + 1];

         for (int i = 0; i < m + 1; i++) {
            c[i][0] = 0;
         }

         for (int i = 0; i < n + 1; i++) {
            c[0][i] = 0;
         }

         for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
               if (((JsonValue)source.get(i)).equals(target.get(j))) {
                  c[i + 1][j + 1] = (c[i][j] & -2) + 3;
               } else {
                  c[i + 1][j + 1] = Math.max(c[i + 1][j], c[i][j + 1]) & -2;
               }
            }
         }

         this.emit(path, source, target, c, m, n);
      }

      private void emit(String path, JsonArray source, JsonArray target, int[][] c, int i, int j) {
         if (i == 0) {
            if (j > 0) {
               this.emit(path, source, target, c, i, j - 1);
               this.builder.add(path + '/' + (j - 1), (JsonValue)target.get(j - 1));
            }
         } else if (j == 0) {
            if (i > 0) {
               this.builder.remove(path + '/' + (i - 1));
               this.emit(path, source, target, c, i - 1, j);
            }
         } else if ((c[i][j] & 1) == 1) {
            this.emit(path, source, target, c, i - 1, j - 1);
         } else {
            int f = c[i][j - 1] >> 1;
            int g = c[i - 1][j] >> 1;
            if (f > g) {
               this.emit(path, source, target, c, i, j - 1);
               this.builder.add(path + '/' + (j - 1), (JsonValue)target.get(j - 1));
            } else if (f < g) {
               this.builder.remove(path + '/' + (i - 1));
               this.emit(path, source, target, c, i - 1, j);
            } else {
               this.diff(path + '/' + (i - 1), (JsonValue)source.get(i - 1), (JsonValue)target.get(j - 1));
               this.emit(path, source, target, c, i - 1, j - 1);
            }
         }
      }
   }
}
