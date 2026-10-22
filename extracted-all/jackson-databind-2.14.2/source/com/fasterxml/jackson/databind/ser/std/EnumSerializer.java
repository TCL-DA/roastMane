package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.util.EnumValues;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@JacksonStdImpl
public class EnumSerializer extends StdScalarSerializer<Enum<?>> implements ContextualSerializer {
   private static final long serialVersionUID = 1L;
   protected final EnumValues _values;
   protected final Boolean _serializeAsIndex;

   public EnumSerializer(EnumValues v, Boolean serializeAsIndex) {
      super(v.getEnumClass(), false);
      this._values = v;
      this._serializeAsIndex = serializeAsIndex;
   }

   public static EnumSerializer construct(Class<?> enumClass, SerializationConfig config, BeanDescription beanDesc, Value format) {
      EnumValues v = EnumValues.constructFromName(config, (Class<Enum<?>>)enumClass);
      Boolean serializeAsIndex = _isShapeWrittenUsingIndex(enumClass, format, true, null);
      return new EnumSerializer(v, serializeAsIndex);
   }

   @Override
   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
      Value format = this.findFormatOverrides(serializers, property, this.handledType());
      if (format != null) {
         Class<?> type = this.handledType();
         Boolean serializeAsIndex = _isShapeWrittenUsingIndex(type, format, false, this._serializeAsIndex);
         if (!Objects.equals(serializeAsIndex, this._serializeAsIndex)) {
            return new EnumSerializer(this._values, serializeAsIndex);
         }
      }

      return this;
   }

   public EnumValues getEnumValues() {
      return this._values;
   }

   public final void serialize(Enum<?> en, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (this._serializeAsIndex(serializers)) {
         gen.writeNumber(en.ordinal());
      } else if (serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
         gen.writeString(en.toString());
      } else {
         gen.writeString(this._values.serializedValueFor(en));
      }
   }

   @Override
   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
      if (this._serializeAsIndex(provider)) {
         return this.createSchemaNode("integer", true);
      } else {
         ObjectNode objectNode = this.createSchemaNode("string", true);
         if (typeHint != null) {
            JavaType type = provider.constructType(typeHint);
            if (type.isEnumType()) {
               ArrayNode enumNode = objectNode.putArray("enum");

               for (SerializableString value : this._values.values()) {
                  enumNode.add(value.getValue());
               }
            }
         }

         return objectNode;
      }
   }

   @Override
   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      SerializerProvider serializers = visitor.getProvider();
      if (this._serializeAsIndex(serializers)) {
         this.visitIntFormat(visitor, typeHint, NumberType.INT);
      } else {
         JsonStringFormatVisitor stringVisitor = visitor.expectStringFormat(typeHint);
         if (stringVisitor != null) {
            Set<String> enums = new LinkedHashSet<>();
            if (serializers != null && serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
               for (Enum<?> e : this._values.enums()) {
                  enums.add(e.toString());
               }
            } else {
               for (SerializableString value : this._values.values()) {
                  enums.add(value.getValue());
               }
            }

            stringVisitor.enumTypes(enums);
         }
      }
   }

   protected final boolean _serializeAsIndex(SerializerProvider serializers) {
      return this._serializeAsIndex != null ? this._serializeAsIndex : serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_INDEX);
   }

   protected static Boolean _isShapeWrittenUsingIndex(Class<?> enumClass, Value format, boolean fromClass, Boolean defaultValue) {
      Shape shape = format == null ? null : format.getShape();
      if (shape == null) {
         return defaultValue;
      } else if (shape == Shape.ANY || shape == Shape.SCALAR) {
         return defaultValue;
      } else if (shape == Shape.STRING || shape == Shape.NATURAL) {
         return Boolean.FALSE;
      } else if (!shape.isNumeric() && shape != Shape.ARRAY) {
         throw new IllegalArgumentException(
            String.format(
               "Unsupported serialization shape (%s) for Enum %s, not supported as %s annotation", shape, enumClass.getName(), fromClass ? "class" : "property"
            )
         );
      } else {
         return Boolean.TRUE;
      }
   }
}
