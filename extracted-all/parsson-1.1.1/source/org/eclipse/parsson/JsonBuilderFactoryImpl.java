package org.eclipse.parsson;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.eclipse.parsson.api.BufferPool;

class JsonBuilderFactoryImpl implements JsonBuilderFactory {
   private final Map<String, ?> config = Collections.emptyMap();
   private final BufferPool bufferPool;
   private final boolean rejectDuplicateKeys;

   JsonBuilderFactoryImpl(BufferPool bufferPool, boolean rejectDuplicateKeys) {
      this.bufferPool = bufferPool;
      this.rejectDuplicateKeys = rejectDuplicateKeys;
   }

   public JsonObjectBuilder createObjectBuilder() {
      return new JsonObjectBuilderImpl(this.bufferPool, this.rejectDuplicateKeys, Collections.emptyMap());
   }

   public JsonObjectBuilder createObjectBuilder(JsonObject object) {
      return new JsonObjectBuilderImpl(object, this.bufferPool, this.rejectDuplicateKeys, Collections.emptyMap());
   }

   public JsonObjectBuilder createObjectBuilder(Map<String, Object> object) {
      return new JsonObjectBuilderImpl(object, this.bufferPool, this.rejectDuplicateKeys, Collections.emptyMap());
   }

   public JsonArrayBuilder createArrayBuilder() {
      return new JsonArrayBuilderImpl(this.bufferPool);
   }

   public JsonArrayBuilder createArrayBuilder(JsonArray array) {
      return new JsonArrayBuilderImpl(array, this.bufferPool);
   }

   public JsonArrayBuilder createArrayBuilder(Collection<?> collection) {
      return new JsonArrayBuilderImpl(collection, this.bufferPool);
   }

   public Map<String, ?> getConfigInUse() {
      return this.config;
   }
}
