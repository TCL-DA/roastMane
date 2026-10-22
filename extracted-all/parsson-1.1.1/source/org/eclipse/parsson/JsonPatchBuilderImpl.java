package org.eclipse.parsson;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonPatch;
import jakarta.json.JsonPatchBuilder;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonPatch.Operation;

public final class JsonPatchBuilderImpl implements JsonPatchBuilder {
   private final JsonArrayBuilder builder;

   public JsonPatchBuilderImpl(JsonArray patch) {
      this.builder = new JsonArrayBuilderImpl(patch, JsonUtil.getInternalBufferPool());
   }

   public JsonPatchBuilderImpl() {
      this.builder = new JsonArrayBuilderImpl(JsonUtil.getInternalBufferPool());
   }

   public <T extends JsonStructure> T apply(T target) {
      return (T)this.build().apply(target);
   }

   public JsonPatchBuilder add(String path, JsonValue value) {
      this.builder
         .add(new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool()).add("op", Operation.ADD.operationName()).add("path", path).add("value", value));
      return this;
   }

   public JsonPatchBuilder add(String path, String value) {
      this.builder
         .add(new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool()).add("op", Operation.ADD.operationName()).add("path", path).add("value", value));
      return this;
   }

   public JsonPatchBuilder add(String path, int value) {
      this.builder
         .add(new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool()).add("op", Operation.ADD.operationName()).add("path", path).add("value", value));
      return this;
   }

   public JsonPatchBuilder add(String path, boolean value) {
      this.builder
         .add(new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool()).add("op", Operation.ADD.operationName()).add("path", path).add("value", value));
      return this;
   }

   public JsonPatchBuilder remove(String path) {
      this.builder.add(new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool()).add("op", Operation.REMOVE.operationName()).add("path", path));
      return this;
   }

   public JsonPatchBuilder replace(String path, JsonValue value) {
      this.builder
         .add(new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool()).add("op", Operation.REPLACE.operationName()).add("path", path).add("value", value));
      return this;
   }

   public JsonPatchBuilder replace(String path, String value) {
      this.builder
         .add(new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool()).add("op", Operation.REPLACE.operationName()).add("path", path).add("value", value));
      return this;
   }

   public JsonPatchBuilder replace(String path, int value) {
      this.builder
         .add(new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool()).add("op", Operation.REPLACE.operationName()).add("path", path).add("value", value));
      return this;
   }

   public JsonPatchBuilder replace(String path, boolean value) {
      this.builder
         .add(new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool()).add("op", Operation.REPLACE.operationName()).add("path", path).add("value", value));
      return this;
   }

   public JsonPatchBuilder move(String path, String from) {
      this.builder
         .add(new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool()).add("op", Operation.MOVE.operationName()).add("path", path).add("from", from));
      return this;
   }

   public JsonPatchBuilder copy(String path, String from) {
      this.builder
         .add(new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool()).add("op", Operation.COPY.operationName()).add("path", path).add("from", from));
      return this;
   }

   public JsonPatchBuilder test(String path, JsonValue value) {
      this.builder
         .add(new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool()).add("op", Operation.TEST.operationName()).add("path", path).add("value", value));
      return this;
   }

   public JsonPatchBuilder test(String path, String value) {
      this.builder
         .add(new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool()).add("op", Operation.TEST.operationName()).add("path", path).add("value", value));
      return this;
   }

   public JsonPatchBuilder test(String path, int value) {
      this.builder
         .add(new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool()).add("op", Operation.TEST.operationName()).add("path", path).add("value", value));
      return this;
   }

   public JsonPatchBuilder test(String path, boolean value) {
      this.builder
         .add(new JsonObjectBuilderImpl(JsonUtil.getInternalBufferPool()).add("op", Operation.TEST.operationName()).add("path", path).add("value", value));
      return this;
   }

   public JsonArray buildAsJsonArray() {
      return this.builder.build();
   }

   public JsonPatch build() {
      return new JsonPatchImpl(this.buildAsJsonArray());
   }
}
