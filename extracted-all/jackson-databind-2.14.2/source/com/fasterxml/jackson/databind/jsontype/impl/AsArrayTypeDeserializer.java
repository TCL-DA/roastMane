package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.JsonParserSequence;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.io.Serializable;

public class AsArrayTypeDeserializer extends TypeDeserializerBase implements Serializable {
   private static final long serialVersionUID = 1L;

   public AsArrayTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl) {
      super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
   }

   public AsArrayTypeDeserializer(AsArrayTypeDeserializer src, BeanProperty property) {
      super(src, property);
   }

   @Override
   public TypeDeserializer forProperty(BeanProperty prop) {
      return prop == this._property ? this : new AsArrayTypeDeserializer(this, prop);
   }

   @Override
   public As getTypeInclusion() {
      return As.WRAPPER_ARRAY;
   }

   @Override
   public Object deserializeTypedFromArray(JsonParser jp, DeserializationContext ctxt) throws IOException {
      return this._deserialize(jp, ctxt);
   }

   @Override
   public Object deserializeTypedFromObject(JsonParser jp, DeserializationContext ctxt) throws IOException {
      return this._deserialize(jp, ctxt);
   }

   @Override
   public Object deserializeTypedFromScalar(JsonParser jp, DeserializationContext ctxt) throws IOException {
      return this._deserialize(jp, ctxt);
   }

   @Override
   public Object deserializeTypedFromAny(JsonParser jp, DeserializationContext ctxt) throws IOException {
      return this._deserialize(jp, ctxt);
   }

   protected Object _deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.canReadTypeId()) {
         Object typeId = p.getTypeId();
         if (typeId != null) {
            return this._deserializeWithNativeTypeId(p, ctxt, typeId);
         }
      }

      boolean hadStartArray = p.isExpectedStartArrayToken();
      String typeId = this._locateTypeId(p, ctxt);
      JsonDeserializer<Object> deser = this._findDeserializer(ctxt, typeId);
      if (this._typeIdVisible && !this._usesExternalId() && p.hasToken(JsonToken.START_OBJECT)) {
         TokenBuffer tb = ctxt.bufferForInputBuffering(p);
         tb.writeStartObject();
         tb.writeFieldName(this._typePropertyName);
         tb.writeString(typeId);
         p.clearCurrentToken();
         p = JsonParserSequence.createFlattened(false, tb.asParser(p), p);
         p.nextToken();
      }

      if (hadStartArray && p.currentToken() == JsonToken.END_ARRAY) {
         return deser.getNullValue(ctxt);
      } else {
         Object value = deser.deserialize(p, ctxt);
         if (hadStartArray && p.nextToken() != JsonToken.END_ARRAY) {
            ctxt.reportWrongTokenException(this.baseType(), JsonToken.END_ARRAY, "expected closing END_ARRAY after type information and deserialized value");
         }

         return value;
      }
   }

   protected String _locateTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (!p.isExpectedStartArrayToken()) {
         if (this._defaultImpl != null) {
            return this._idResolver.idFromBaseType();
         } else {
            ctxt.reportWrongTokenException(
               this.baseType(), JsonToken.START_ARRAY, "need JSON Array to contain As.WRAPPER_ARRAY type information for class " + this.baseTypeName()
            );
            return null;
         }
      } else {
         JsonToken t = p.nextToken();
         if (t != JsonToken.VALUE_STRING && (t == null || !t.isScalarValue())) {
            ctxt.reportWrongTokenException(
               this.baseType(), JsonToken.VALUE_STRING, "need JSON String, Number of Boolean that contains type id (for subtype of %s)", this.baseTypeName()
            );
            return null;
         } else {
            String result = p.getText();
            p.nextToken();
            return result;
         }
      }
   }

   protected boolean _usesExternalId() {
      return false;
   }
}
