package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class StdJdkSerializers {
   public static Collection<Entry<Class<?>, Object>> all() {
      HashMap<Class<?>, Object> sers = new HashMap<>();
      sers.put(URL.class, new ToStringSerializer(URL.class));
      sers.put(URI.class, new ToStringSerializer(URI.class));
      sers.put(Currency.class, new ToStringSerializer(Currency.class));
      sers.put(UUID.class, new UUIDSerializer());
      sers.put(Pattern.class, new ToStringSerializer(Pattern.class));
      sers.put(Locale.class, new ToStringSerializer(Locale.class));
      sers.put(AtomicBoolean.class, StdJdkSerializers.AtomicBooleanSerializer.class);
      sers.put(AtomicInteger.class, StdJdkSerializers.AtomicIntegerSerializer.class);
      sers.put(AtomicLong.class, StdJdkSerializers.AtomicLongSerializer.class);
      sers.put(File.class, FileSerializer.class);
      sers.put(Class.class, ClassSerializer.class);
      sers.put(Void.class, NullSerializer.instance);
      sers.put(void.class, NullSerializer.instance);
      return sers.entrySet();
   }

   public static class AtomicBooleanSerializer extends StdScalarSerializer<AtomicBoolean> {
      public AtomicBooleanSerializer() {
         super(AtomicBoolean.class, false);
      }

      public void serialize(AtomicBoolean value, JsonGenerator gen, SerializerProvider provider) throws IOException {
         gen.writeBoolean(value.get());
      }

      @Override
      public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
         return this.createSchemaNode("boolean", true);
      }

      @Override
      public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
         visitor.expectBooleanFormat(typeHint);
      }
   }

   public static class AtomicIntegerSerializer extends StdScalarSerializer<AtomicInteger> {
      public AtomicIntegerSerializer() {
         super(AtomicInteger.class, false);
      }

      public void serialize(AtomicInteger value, JsonGenerator gen, SerializerProvider provider) throws IOException {
         gen.writeNumber(value.get());
      }

      @Override
      public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
         return this.createSchemaNode("integer", true);
      }

      @Override
      public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
         this.visitIntFormat(visitor, typeHint, NumberType.INT);
      }
   }

   public static class AtomicLongSerializer extends StdScalarSerializer<AtomicLong> {
      public AtomicLongSerializer() {
         super(AtomicLong.class, false);
      }

      public void serialize(AtomicLong value, JsonGenerator gen, SerializerProvider provider) throws IOException {
         gen.writeNumber(value.get());
      }

      @Override
      public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
         return this.createSchemaNode("integer", true);
      }

      @Override
      public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
         this.visitIntFormat(visitor, typeHint, NumberType.LONG);
      }
   }
}
