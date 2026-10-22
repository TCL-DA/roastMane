package org.eclipse.parsson;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;

abstract class NodeReference {
   public abstract boolean contains();

   public abstract JsonValue get();

   public abstract JsonStructure add(JsonValue var1);

   public abstract JsonStructure remove();

   public abstract JsonStructure replace(JsonValue var1);

   public static NodeReference of(JsonStructure structure) {
      return new NodeReference.RootReference(structure);
   }

   public static NodeReference of(JsonObject object, String name) {
      return new NodeReference.ObjectReference(object, name);
   }

   public static NodeReference of(JsonArray array, int index) {
      return new NodeReference.ArrayReference(array, index);
   }

   static class ArrayReference extends NodeReference {
      private final JsonArray array;
      private final int index;

      ArrayReference(JsonArray array, int index) {
         this.array = array;
         this.index = index;
      }

      @Override
      public boolean contains() {
         return this.array != null && this.index > -1 && this.index < this.array.size();
      }

      @Override
      public JsonValue get() {
         if (!this.contains()) {
            throw new JsonException(JsonMessages.NODEREF_ARRAY_INDEX_ERR(this.index, this.array.size()));
         } else {
            return (JsonValue)this.array.get(this.index);
         }
      }

      public JsonArray add(JsonValue value) {
         JsonArrayBuilder builder = new JsonArrayBuilderImpl(this.array, JsonUtil.getInternalBufferPool());
         if (this.index != -1 && this.index != this.array.size()) {
            if (this.index >= this.array.size()) {
               throw new JsonException(JsonMessages.NODEREF_ARRAY_INDEX_ERR(this.index, this.array.size()));
            }

            builder.add(this.index, value);
         } else {
            builder.add(value);
         }

         return builder.build();
      }

      public JsonArray remove() {
         if (!this.contains()) {
            throw new JsonException(JsonMessages.NODEREF_ARRAY_INDEX_ERR(this.index, this.array.size()));
         } else {
            JsonArrayBuilder builder = new JsonArrayBuilderImpl(this.array, JsonUtil.getInternalBufferPool());
            return builder.remove(this.index).build();
         }
      }

      public JsonArray replace(JsonValue value) {
         if (!this.contains()) {
            throw new JsonException(JsonMessages.NODEREF_ARRAY_INDEX_ERR(this.index, this.array.size()));
         } else {
            JsonArrayBuilder builder = new JsonArrayBuilderImpl(this.array, JsonUtil.getInternalBufferPool());
            return builder.set(this.index, value).build();
         }
      }
   }

   static class ObjectReference extends NodeReference {
      private final JsonObject object;
      private final String key;

      ObjectReference(JsonObject object, String key) {
         this.object = object;
         this.key = key;
      }

      @Override
      public boolean contains() {
         return this.object != null && this.object.containsKey(this.key);
      }

      @Override
      public JsonValue get() {
         if (!this.contains()) {
            throw new JsonException(JsonMessages.NODEREF_OBJECT_MISSING(this.key));
         } else {
            return (JsonValue)this.object.get(this.key);
         }
      }

      public JsonObject add(JsonValue value) {
         return new JsonObjectBuilderImpl(this.object, JsonUtil.getInternalBufferPool()).add(this.key, value).build();
      }

      public JsonObject remove() {
         if (!this.contains()) {
            throw new JsonException(JsonMessages.NODEREF_OBJECT_MISSING(this.key));
         } else {
            return new JsonObjectBuilderImpl(this.object, JsonUtil.getInternalBufferPool()).remove(this.key).build();
         }
      }

      public JsonObject replace(JsonValue value) {
         if (!this.contains()) {
            throw new JsonException(JsonMessages.NODEREF_OBJECT_MISSING(this.key));
         } else {
            return this.add(value);
         }
      }
   }

   static class RootReference extends NodeReference {
      private JsonStructure root;

      RootReference(JsonStructure root) {
         this.root = root;
      }

      @Override
      public boolean contains() {
         return this.root != null;
      }

      @Override
      public JsonValue get() {
         return this.root;
      }

      @Override
      public JsonStructure add(JsonValue value) {
         switch (value.getValueType()) {
            case OBJECT:
            case ARRAY:
               this.root = (JsonStructure)value;
               return this.root;
            default:
               throw new JsonException(JsonMessages.NODEREF_VALUE_ADD_ERR());
         }
      }

      @Override
      public JsonStructure remove() {
         throw new JsonException(JsonMessages.NODEREF_VALUE_CANNOT_REMOVE());
      }

      @Override
      public JsonStructure replace(JsonValue value) {
         return this.add(value);
      }
   }
}
