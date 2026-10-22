package org.eclipse.parsson;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.json.JsonValue.ValueType;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.eclipse.parsson.api.BufferPool;

class JsonArrayBuilderImpl implements JsonArrayBuilder {
   private ArrayList<JsonValue> valueList;
   private final BufferPool bufferPool;

   JsonArrayBuilderImpl(BufferPool bufferPool) {
      this.bufferPool = bufferPool;
   }

   JsonArrayBuilderImpl(JsonArray array, BufferPool bufferPool) {
      this.bufferPool = bufferPool;
      this.valueList = new ArrayList<>();
      this.valueList.addAll(array);
   }

   JsonArrayBuilderImpl(Collection<?> collection, BufferPool bufferPool) {
      this.bufferPool = bufferPool;
      this.valueList = new ArrayList<>();
      this.populate(collection);
   }

   public JsonArrayBuilder add(JsonValue value) {
      this.validateValue(value);
      this.addValueList(value);
      return this;
   }

   public JsonArrayBuilder add(String value) {
      this.validateValue(value);
      this.addValueList(new JsonStringImpl(value));
      return this;
   }

   public JsonArrayBuilder add(BigDecimal value) {
      this.validateValue(value);
      this.addValueList(JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonArrayBuilder add(BigInteger value) {
      this.validateValue(value);
      this.addValueList(JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonArrayBuilder add(int value) {
      this.addValueList(JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonArrayBuilder add(long value) {
      this.addValueList(JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonArrayBuilder add(double value) {
      this.addValueList(JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonArrayBuilder add(boolean value) {
      this.addValueList(value ? JsonValue.TRUE : JsonValue.FALSE);
      return this;
   }

   public JsonArrayBuilder addNull() {
      this.addValueList(JsonValue.NULL);
      return this;
   }

   public JsonArrayBuilder add(JsonObjectBuilder builder) {
      if (builder == null) {
         throw new NullPointerException(JsonMessages.ARRBUILDER_OBJECT_BUILDER_NULL());
      } else {
         this.addValueList(builder.build());
         return this;
      }
   }

   public JsonArrayBuilder add(JsonArrayBuilder builder) {
      if (builder == null) {
         throw new NullPointerException(JsonMessages.ARRBUILDER_ARRAY_BUILDER_NULL());
      } else {
         this.addValueList(builder.build());
         return this;
      }
   }

   public JsonArrayBuilder addAll(JsonArrayBuilder builder) {
      if (builder == null) {
         throw new NullPointerException(JsonMessages.ARRBUILDER_ARRAY_BUILDER_NULL());
      } else {
         if (this.valueList == null) {
            this.valueList = new ArrayList<>();
         }

         this.valueList.addAll(builder.build());
         return this;
      }
   }

   public JsonArrayBuilder add(int index, JsonValue value) {
      this.validateValue(value);
      this.addValueList(index, value);
      return this;
   }

   public JsonArrayBuilder add(int index, String value) {
      this.validateValue(value);
      this.addValueList(index, new JsonStringImpl(value));
      return this;
   }

   public JsonArrayBuilder add(int index, BigDecimal value) {
      this.validateValue(value);
      this.addValueList(index, JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonArrayBuilder add(int index, BigInteger value) {
      this.validateValue(value);
      this.addValueList(index, JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonArrayBuilder add(int index, int value) {
      this.addValueList(index, JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonArrayBuilder add(int index, long value) {
      this.addValueList(index, JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonArrayBuilder add(int index, double value) {
      this.addValueList(index, JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonArrayBuilder add(int index, boolean value) {
      this.addValueList(index, value ? JsonValue.TRUE : JsonValue.FALSE);
      return this;
   }

   public JsonArrayBuilder addNull(int index) {
      this.addValueList(index, JsonValue.NULL);
      return this;
   }

   public JsonArrayBuilder add(int index, JsonObjectBuilder builder) {
      if (builder == null) {
         throw new NullPointerException(JsonMessages.ARRBUILDER_OBJECT_BUILDER_NULL());
      } else {
         this.addValueList(index, builder.build());
         return this;
      }
   }

   public JsonArrayBuilder add(int index, JsonArrayBuilder builder) {
      if (builder == null) {
         throw new NullPointerException(JsonMessages.ARRBUILDER_OBJECT_BUILDER_NULL());
      } else {
         this.addValueList(index, builder.build());
         return this;
      }
   }

   public JsonArrayBuilder set(int index, JsonValue value) {
      this.validateValue(value);
      this.setValueList(index, value);
      return this;
   }

   public JsonArrayBuilder set(int index, String value) {
      this.validateValue(value);
      this.setValueList(index, new JsonStringImpl(value));
      return this;
   }

   public JsonArrayBuilder set(int index, BigDecimal value) {
      this.validateValue(value);
      this.setValueList(index, JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonArrayBuilder set(int index, BigInteger value) {
      this.validateValue(value);
      this.setValueList(index, JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonArrayBuilder set(int index, int value) {
      this.setValueList(index, JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonArrayBuilder set(int index, long value) {
      this.setValueList(index, JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonArrayBuilder set(int index, double value) {
      this.setValueList(index, JsonNumberImpl.getJsonNumber(value));
      return this;
   }

   public JsonArrayBuilder set(int index, boolean value) {
      this.setValueList(index, value ? JsonValue.TRUE : JsonValue.FALSE);
      return this;
   }

   public JsonArrayBuilder setNull(int index) {
      this.setValueList(index, JsonValue.NULL);
      return this;
   }

   public JsonArrayBuilder set(int index, JsonObjectBuilder builder) {
      if (builder == null) {
         throw new NullPointerException(JsonMessages.ARRBUILDER_OBJECT_BUILDER_NULL());
      } else {
         this.setValueList(index, builder.build());
         return this;
      }
   }

   public JsonArrayBuilder set(int index, JsonArrayBuilder builder) {
      if (builder == null) {
         throw new NullPointerException(JsonMessages.ARRBUILDER_OBJECT_BUILDER_NULL());
      } else {
         this.setValueList(index, builder.build());
         return this;
      }
   }

   public JsonArrayBuilder remove(int index) {
      if (this.valueList == null) {
         throw new IndexOutOfBoundsException(JsonMessages.ARRBUILDER_VALUELIST_NULL(index, 0));
      } else {
         this.valueList.remove(index);
         return this;
      }
   }

   public JsonArray build() {
      List<JsonValue> snapshot;
      if (this.valueList == null) {
         snapshot = Collections.emptyList();
      } else {
         snapshot = Collections.unmodifiableList(this.valueList);
      }

      this.valueList = null;
      return new JsonArrayBuilderImpl.JsonArrayImpl(snapshot, this.bufferPool);
   }

   private void populate(Collection<?> collection) {
      for (Object value : collection) {
         if (value != null && value instanceof Optional) {
            ((Optional)value).ifPresent(v -> this.valueList.add(MapUtil.handle(v, this.bufferPool)));
         } else {
            this.valueList.add(MapUtil.handle(value, this.bufferPool));
         }
      }
   }

   private void addValueList(JsonValue value) {
      if (this.valueList == null) {
         this.valueList = new ArrayList<>();
      }

      this.valueList.add(value);
   }

   private void addValueList(int index, JsonValue value) {
      if (this.valueList == null) {
         this.valueList = new ArrayList<>();
      }

      this.valueList.add(index, value);
   }

   private void setValueList(int index, JsonValue value) {
      if (this.valueList == null) {
         throw new IndexOutOfBoundsException(JsonMessages.ARRBUILDER_VALUELIST_NULL(index, 0));
      } else {
         this.valueList.set(index, value);
      }
   }

   private void validateValue(Object value) {
      if (value == null) {
         throw new NullPointerException(JsonMessages.ARRBUILDER_VALUE_NULL());
      }
   }

   private static final class JsonArrayImpl extends AbstractList<JsonValue> implements JsonArray {
      private final List<JsonValue> valueList;
      private final BufferPool bufferPool;
      private int hashCode;

      JsonArrayImpl(List<JsonValue> valueList, BufferPool bufferPool) {
         this.valueList = valueList;
         this.bufferPool = bufferPool;
      }

      @Override
      public int size() {
         return this.valueList.size();
      }

      public JsonObject getJsonObject(int index) {
         return (JsonObject)this.valueList.get(index);
      }

      public JsonArray getJsonArray(int index) {
         return (JsonArray)this.valueList.get(index);
      }

      public JsonNumber getJsonNumber(int index) {
         return (JsonNumber)this.valueList.get(index);
      }

      public JsonString getJsonString(int index) {
         return (JsonString)this.valueList.get(index);
      }

      public <T extends JsonValue> List<T> getValuesAs(Class<T> clazz) {
         return (List<T>)this.valueList;
      }

      public String getString(int index) {
         return this.getJsonString(index).getString();
      }

      public String getString(int index, String defaultValue) {
         try {
            return this.getString(index);
         } catch (Exception var4) {
            return defaultValue;
         }
      }

      public int getInt(int index) {
         return this.getJsonNumber(index).intValue();
      }

      public int getInt(int index, int defaultValue) {
         try {
            return this.getInt(index);
         } catch (Exception var4) {
            return defaultValue;
         }
      }

      public boolean getBoolean(int index) {
         JsonValue jsonValue = this.get(index);
         if (jsonValue == JsonValue.TRUE) {
            return true;
         } else if (jsonValue == JsonValue.FALSE) {
            return false;
         } else {
            throw new ClassCastException();
         }
      }

      public boolean getBoolean(int index, boolean defaultValue) {
         try {
            return this.getBoolean(index);
         } catch (Exception var4) {
            return defaultValue;
         }
      }

      public boolean isNull(int index) {
         return this.valueList.get(index).equals(JsonValue.NULL);
      }

      public ValueType getValueType() {
         return ValueType.ARRAY;
      }

      public JsonValue get(int index) {
         return this.valueList.get(index);
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

      public JsonArray asJsonArray() {
         return this;
      }
   }
}
