package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.io.NumberOutput;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Map;

public class NumberSerializers {
   protected NumberSerializers() {
   }

   public static void addAll(Map<String, JsonSerializer<?>> allDeserializers) {
      allDeserializers.put(Integer.class.getName(), new NumberSerializers.IntegerSerializer(Integer.class));
      allDeserializers.put(int.class.getName(), new NumberSerializers.IntegerSerializer(int.class));
      allDeserializers.put(Long.class.getName(), new NumberSerializers.LongSerializer(Long.class));
      allDeserializers.put(long.class.getName(), new NumberSerializers.LongSerializer(long.class));
      allDeserializers.put(Byte.class.getName(), NumberSerializers.IntLikeSerializer.instance);
      allDeserializers.put(byte.class.getName(), NumberSerializers.IntLikeSerializer.instance);
      allDeserializers.put(Short.class.getName(), NumberSerializers.ShortSerializer.instance);
      allDeserializers.put(short.class.getName(), NumberSerializers.ShortSerializer.instance);
      allDeserializers.put(Double.class.getName(), new NumberSerializers.DoubleSerializer(Double.class));
      allDeserializers.put(double.class.getName(), new NumberSerializers.DoubleSerializer(double.class));
      allDeserializers.put(Float.class.getName(), NumberSerializers.FloatSerializer.instance);
      allDeserializers.put(float.class.getName(), NumberSerializers.FloatSerializer.instance);
   }

   public abstract static class Base<T> extends StdScalarSerializer<T> implements ContextualSerializer {
      protected final NumberType _numberType;
      protected final String _schemaType;
      protected final boolean _isInt;

      protected Base(Class<?> cls, NumberType numberType, String schemaType) {
         super(cls, false);
         this._numberType = numberType;
         this._schemaType = schemaType;
         this._isInt = numberType == NumberType.INT || numberType == NumberType.LONG || numberType == NumberType.BIG_INTEGER;
      }

      @Override
      public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
         return this.createSchemaNode(this._schemaType, true);
      }

      @Override
      public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
         if (this._isInt) {
            this.visitIntFormat(visitor, typeHint, this._numberType);
         } else {
            this.visitFloatFormat(visitor, typeHint, this._numberType);
         }
      }

      @Override
      public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
         Value format = this.findFormatOverrides(prov, property, this.handledType());
         if (format != null) {
            switch (format.getShape()) {
               case STRING:
                  if (this.handledType() == BigDecimal.class) {
                     return NumberSerializer.bigDecimalAsStringSerializer();
                  }

                  return ToStringSerializer.instance;
            }
         }

         return this;
      }
   }

   @JacksonStdImpl
   public static class DoubleSerializer extends NumberSerializers.Base<Object> {
      public DoubleSerializer(Class<?> cls) {
         super(cls, NumberType.DOUBLE, "number");
      }

      @Override
      public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
         gen.writeNumber((Double)value);
      }

      @Override
      public void serializeWithType(Object value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
         Double d = (Double)value;
         if (NumberOutput.notFinite(d)) {
            WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.VALUE_NUMBER_FLOAT));
            g.writeNumber(d);
            typeSer.writeTypeSuffix(g, typeIdDef);
         } else {
            g.writeNumber(d);
         }
      }

      @Deprecated
      public static boolean notFinite(double value) {
         return NumberOutput.notFinite(value);
      }
   }

   @JacksonStdImpl
   public static class FloatSerializer extends NumberSerializers.Base<Object> {
      static final NumberSerializers.FloatSerializer instance = new NumberSerializers.FloatSerializer();

      public FloatSerializer() {
         super(Float.class, NumberType.FLOAT, "number");
      }

      @Override
      public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
         gen.writeNumber((Float)value);
      }
   }

   @JacksonStdImpl
   public static class IntLikeSerializer extends NumberSerializers.Base<Object> {
      static final NumberSerializers.IntLikeSerializer instance = new NumberSerializers.IntLikeSerializer();

      public IntLikeSerializer() {
         super(Number.class, NumberType.INT, "integer");
      }

      @Override
      public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
         gen.writeNumber(((Number)value).intValue());
      }
   }

   @JacksonStdImpl
   public static class IntegerSerializer extends NumberSerializers.Base<Object> {
      public IntegerSerializer(Class<?> type) {
         super(type, NumberType.INT, "integer");
      }

      @Override
      public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
         gen.writeNumber((Integer)value);
      }

      @Override
      public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
         this.serialize(value, gen, provider);
      }
   }

   @JacksonStdImpl
   public static class LongSerializer extends NumberSerializers.Base<Object> {
      public LongSerializer(Class<?> cls) {
         super(cls, NumberType.LONG, "number");
      }

      @Override
      public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
         gen.writeNumber((Long)value);
      }
   }

   @JacksonStdImpl
   public static class ShortSerializer extends NumberSerializers.Base<Object> {
      static final NumberSerializers.ShortSerializer instance = new NumberSerializers.ShortSerializer();

      public ShortSerializer() {
         super(Short.class, NumberType.INT, "number");
      }

      @Override
      public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
         gen.writeNumber((Short)value);
      }
   }
}
