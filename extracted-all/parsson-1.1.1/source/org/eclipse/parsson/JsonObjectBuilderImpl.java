package org.eclipse.parsson;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.json.JsonConfig.KeyStrategy;
import jakarta.json.JsonValue.ValueType;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.parsson.api.BufferPool;

class JsonObjectBuilderImpl implements JsonObjectBuilder {
   protected Map<String, JsonValue> valueMap;
   private final BufferPool bufferPool;
   private final JsonObjectBuilderImpl.DuplicateStrategy duplicateStrategy;

   JsonObjectBuilderImpl(BufferPool bufferPool) {
      this(bufferPool, false, Collections.emptyMap());
   }

   JsonObjectBuilderImpl(BufferPool bufferPool, boolean rejectDuplicateKeys, Map<String, ?> config) {
      this.bufferPool = bufferPool;
      this.duplicateStrategy = JsonObjectBuilderImpl.DuplicateStrategy.strategyFromProperty(
         config.get("jakarta.json.JsonConfig.keyStrategy"), rejectDuplicateKeys
      );
   }

   JsonObjectBuilderImpl(JsonObject object, BufferPool bufferPool) {
      this(object, bufferPool, false, Collections.emptyMap());
   }

   JsonObjectBuilderImpl(JsonObject object, BufferPool bufferPool, boolean rejectDuplicateKeys, Map<String, ?> config) {
      this.bufferPool = bufferPool;
      this.valueMap = new LinkedHashMap<>();
      this.valueMap.putAll(object);
      this.duplicateStrategy = JsonObjectBuilderImpl.DuplicateStrategy.strategyFromProperty(
         config.get("jakarta.json.JsonConfig.keyStrategy"), rejectDuplicateKeys
      );
   }

   JsonObjectBuilderImpl(Map<String, ?> map, BufferPool bufferPool) {
      this(map, bufferPool, false, Collections.emptyMap());
   }

   JsonObjectBuilderImpl(Map<String, ?> map, BufferPool bufferPool, boolean rejectDuplicateKeys, Map<String, ?> config) {
      this.bufferPool = bufferPool;
      this.valueMap = new LinkedHashMap<>();
      this.populate(map);
      this.duplicateStrategy = JsonObjectBuilderImpl.DuplicateStrategy.strategyFromProperty(
         config.get("jakarta.json.JsonConfig.keyStrategy"), rejectDuplicateKeys
      );
   }

   public JsonObjectBuilder add(String name, JsonValue value) {
      this.validateName(name);
      this.validateValue(value);
      this.putValueMap(name, value);
      return this;
   }

   public JsonObjectBuilder add(String name, String value) {
      this.validateName(name);
      this.validateValue(value);
      this.putValueMap(name, new JsonStringImpl(value));
      return this;
   }

   public JsonObjectBuilder add(String name, BigInteger value) {
      this.validateName(name);
      this.validateValue(value);
      this.putValueMap(name, JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonObjectBuilder add(String name, BigDecimal value) {
      this.validateName(name);
      this.validateValue(value);
      this.putValueMap(name, JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonObjectBuilder add(String name, int value) {
      this.validateName(name);
      this.putValueMap(name, JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonObjectBuilder add(String name, long value) {
      this.validateName(name);
      this.putValueMap(name, JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonObjectBuilder add(String name, double value) {
      this.validateName(name);
      this.putValueMap(name, JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonObjectBuilder add(String name, boolean value) {
      this.validateName(name);
      this.putValueMap(name, value ? JsonValue.TRUE : JsonValue.FALSE);
      return this;
   }

   public JsonObjectBuilder addNull(String name) {
      this.validateName(name);
      this.putValueMap(name, JsonValue.NULL);
      return this;
   }

   public JsonObjectBuilder add(String name, JsonObjectBuilder builder) {
      this.validateName(name);
      if (builder == null) {
         throw new NullPointerException(JsonMessages.OBJBUILDER_OBJECT_BUILDER_NULL());
      } else {
         this.putValueMap(name, builder.build());
         return this;
      }
   }

   public JsonObjectBuilder add(String name, JsonArrayBuilder builder) {
      this.validateName(name);
      if (builder == null) {
         throw new NullPointerException(JsonMessages.OBJBUILDER_ARRAY_BUILDER_NULL());
      } else {
         this.putValueMap(name, builder.build());
         return this;
      }
   }

   public JsonObjectBuilder addAll(JsonObjectBuilder builder) {
      if (builder == null) {
         throw new NullPointerException(JsonMessages.OBJBUILDER_OBJECT_BUILDER_NULL());
      } else {
         if (this.valueMap == null) {
            this.valueMap = new LinkedHashMap<>();
         }

         this.valueMap.putAll(builder.build());
         return this;
      }
   }

   public JsonObjectBuilder remove(String name) {
      this.validateName(name);
      this.valueMap.remove(name);
      return this;
   }

   public JsonObject build() {
      Map<String, JsonValue> snapshot = this.valueMap == null ? Collections.emptyMap() : Collections.unmodifiableMap(this.valueMap);
      this.valueMap = null;
      return new JsonObjectBuilderImpl.JsonObjectImpl(snapshot, this.bufferPool);
   }

   private void populate(Map<String, ?> map) {
      for (String field : map.keySet()) {
         Object value = map.get(field);
         if (value != null && value instanceof Optional) {
            ((Optional)value).ifPresent(v -> this.valueMap.put(field, MapUtil.handle(v, this.bufferPool)));
         } else {
            this.valueMap.put(field, MapUtil.handle(value, this.bufferPool));
         }
      }
   }

   private void putValueMap(String name, JsonValue value) {
      if (this.valueMap == null) {
         this.valueMap = new LinkedHashMap<>();
      }

      JsonValue previousValue = this.valueMap.get(name);
      this.valueMap.put(name, this.duplicateStrategy.getValue(name, value, previousValue));
   }

   private void validateName(String name) {
      if (name == null) {
         throw new NullPointerException(JsonMessages.OBJBUILDER_NAME_NULL());
      }
   }

   private void validateValue(Object value) {
      if (value == null) {
         throw new NullPointerException(JsonMessages.OBJBUILDER_VALUE_NULL());
      }
   }

   private static enum DuplicateStrategy {
      NONE(KeyStrategy.NONE) {
         @Override
         protected JsonValue getValue(String name, JsonValue value, JsonValue previous) {
            if (previous != null) {
               throw new IllegalStateException(JsonMessages.DUPLICATE_KEY(name));
            } else {
               return value;
            }
         }
      },
      FIRST(KeyStrategy.FIRST) {
         @Override
         protected JsonValue getValue(String name, JsonValue value, JsonValue previous) {
            return previous != null ? previous : value;
         }
      },
      LAST(KeyStrategy.LAST) {
         @Override
         protected JsonValue getValue(String name, JsonValue value, JsonValue previous) {
            return value;
         }
      };

      private final KeyStrategy property;

      private DuplicateStrategy(KeyStrategy property) {
         this.property = property;
      }

      private static JsonObjectBuilderImpl.DuplicateStrategy strategyFromProperty(Object value, boolean rejectDuplicateKeys) {
         if (value != null) {
            for (JsonObjectBuilderImpl.DuplicateStrategy strategy : values()) {
               if (strategy.property.equals(value)) {
                  return strategy;
               }
            }
         }

         return rejectDuplicateKeys ? NONE : LAST;
      }

      protected abstract JsonValue getValue(String var1, JsonValue var2, JsonValue var3);
   }

   private static final class JsonObjectImpl extends AbstractMap<String, JsonValue> implements JsonObject {
      private final Map<String, JsonValue> valueMap;
      private final BufferPool bufferPool;
      private int hashCode;

      JsonObjectImpl(Map<String, JsonValue> valueMap, BufferPool bufferPool) {
         this.valueMap = valueMap;
         this.bufferPool = bufferPool;
      }

      public JsonArray getJsonArray(String name) {
         return (JsonArray)this.get(name);
      }

      public JsonObject getJsonObject(String name) {
         return (JsonObject)this.get(name);
      }

      public JsonNumber getJsonNumber(String name) {
         return (JsonNumber)this.get(name);
      }

      public JsonString getJsonString(String name) {
         return (JsonString)this.get(name);
      }

      public String getString(String name) {
         return this.getJsonString(name).getString();
      }

      public String getString(String name, String defaultValue) {
         JsonValue value = this.get(name);
         return value instanceof JsonString ? ((JsonString)value).getString() : defaultValue;
      }

      public int getInt(String name) {
         return this.getJsonNumber(name).intValue();
      }

      public int getInt(String name, int defaultValue) {
         JsonValue value = this.get(name);
         return value instanceof JsonNumber ? ((JsonNumber)value).intValue() : defaultValue;
      }

      public boolean getBoolean(String name) {
         JsonValue value = this.get(name);
         if (value == null) {
            throw new NullPointerException();
         } else if (value == JsonValue.TRUE) {
            return true;
         } else if (value == JsonValue.FALSE) {
            return false;
         } else {
            throw new ClassCastException();
         }
      }

      public boolean getBoolean(String name, boolean defaultValue) {
         JsonValue value = this.get(name);
         if (value == JsonValue.TRUE) {
            return true;
         } else {
            return value == JsonValue.FALSE ? false : defaultValue;
         }
      }

      public boolean isNull(String name) {
         return this.get(name).equals(JsonValue.NULL);
      }

      public ValueType getValueType() {
         return ValueType.OBJECT;
      }

      @Override
      public Set<Entry<String, JsonValue>> entrySet() {
         return this.valueMap.entrySet();
      }

      @Override
      public int hashCode() {
         if (this.hashCode == 0) {
            this.hashCode = super.hashCode();
         }

         return this.hashCode;
      }

      @Override
      public String toString() {
         StringWriter sw = new StringWriter();
         JsonWriter jw = new JsonWriterImpl(sw, this.bufferPool);

         try {
            jw.write(this);
         } catch (Throwable var6) {
            try {
               jw.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         jw.close();
         return sw.toString();
      }

      public JsonObject asJsonObject() {
         return this;
      }

      @Override
      public int size() {
         return this.valueMap.size();
      }

      public JsonValue get(Object key) {
         return this.valueMap.get(key);
      }

      @Override
      public boolean containsKey(Object key) {
         return this.valueMap.containsKey(key);
      }
   }
}
