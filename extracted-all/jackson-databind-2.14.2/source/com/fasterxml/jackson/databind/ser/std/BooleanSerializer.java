package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.NumberType;
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

@JacksonStdImpl
public final class BooleanSerializer extends StdScalarSerializer<Object> implements ContextualSerializer {
   private static final long serialVersionUID = 1L;
   protected final boolean _forPrimitive;

   public BooleanSerializer(boolean forPrimitive) {
      super(forPrimitive ? boolean.class : Boolean.class, false);
      this._forPrimitive = forPrimitive;
   }

   @Override
   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
      Value format = this.findFormatOverrides(serializers, property, this.handledType());
      if (format != null) {
         Shape shape = format.getShape();
         if (shape.isNumeric()) {
            return new BooleanSerializer.AsNumber(this._forPrimitive);
         }

         if (shape == Shape.STRING) {
            return new ToStringSerializer(this._handledType);
         }
      }

      return this;
   }

   @Override
   public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
      g.writeBoolean(Boolean.TRUE.equals(value));
   }

   @Override
   public final void serializeWithType(Object value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      g.writeBoolean(Boolean.TRUE.equals(value));
   }

   @Override
   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
      return this.createSchemaNode("boolean", !this._forPrimitive);
   }

   @Override
   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      visitor.expectBooleanFormat(typeHint);
   }

   static final class AsNumber extends StdScalarSerializer<Object> implements ContextualSerializer {
      private static final long serialVersionUID = 1L;
      protected final boolean _forPrimitive;

      public AsNumber(boolean forPrimitive) {
         super(forPrimitive ? boolean.class : Boolean.class, false);
         this._forPrimitive = forPrimitive;
      }

      @Override
      public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
         g.writeNumber(Boolean.FALSE.equals(value) ? 0 : 1);
      }

      @Override
      public final void serializeWithType(Object value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
         g.writeBoolean(Boolean.TRUE.equals(value));
      }

      @Override
      public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
         this.visitIntFormat(visitor, typeHint, NumberType.INT);
      }

      @Override
      public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
         Value format = this.findFormatOverrides(serializers, property, Boolean.class);
         if (format != null) {
            Shape shape = format.getShape();
            if (!shape.isNumeric()) {
               return new BooleanSerializer(this._forPrimitive);
            }
         }

         return this;
      }
   }
}
