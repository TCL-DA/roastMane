package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

@JacksonStdImpl
public final class StringCollectionDeserializer extends ContainerDeserializerBase<Collection<String>> implements ContextualDeserializer {
   private static final long serialVersionUID = 1L;
   protected final JsonDeserializer<String> _valueDeserializer;
   protected final ValueInstantiator _valueInstantiator;
   protected final JsonDeserializer<Object> _delegateDeserializer;

   public StringCollectionDeserializer(JavaType collectionType, JsonDeserializer<?> valueDeser, ValueInstantiator valueInstantiator) {
      this(collectionType, valueInstantiator, null, valueDeser, valueDeser, null);
   }

   protected StringCollectionDeserializer(
      JavaType collectionType,
      ValueInstantiator valueInstantiator,
      JsonDeserializer<?> delegateDeser,
      JsonDeserializer<?> valueDeser,
      NullValueProvider nuller,
      Boolean unwrapSingle
   ) {
      super(collectionType, nuller, unwrapSingle);
      this._valueDeserializer = (JsonDeserializer<String>)valueDeser;
      this._valueInstantiator = valueInstantiator;
      this._delegateDeserializer = (JsonDeserializer<Object>)delegateDeser;
   }

   protected StringCollectionDeserializer withResolved(
      JsonDeserializer<?> delegateDeser, JsonDeserializer<?> valueDeser, NullValueProvider nuller, Boolean unwrapSingle
   ) {
      return Objects.equals(this._unwrapSingle, unwrapSingle)
            && this._nullProvider == nuller
            && this._valueDeserializer == valueDeser
            && this._delegateDeserializer == delegateDeser
         ? this
         : new StringCollectionDeserializer(this._containerType, this._valueInstantiator, delegateDeser, valueDeser, nuller, unwrapSingle);
   }

   @Override
   public boolean isCachable() {
      return this._valueDeserializer == null && this._delegateDeserializer == null;
   }

   @Override
   public LogicalType logicalType() {
      return LogicalType.Collection;
   }

   @Override
   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      JsonDeserializer<Object> delegate = null;
      if (this._valueInstantiator != null) {
         AnnotatedWithParams delegateCreator = this._valueInstantiator.getArrayDelegateCreator();
         if (delegateCreator != null) {
            JavaType delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
            delegate = this.findDeserializer(ctxt, delegateType, property);
         } else if (this._valueInstantiator.getDelegateCreator() != null) {
            JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
            delegate = this.findDeserializer(ctxt, delegateType, property);
         }
      }

      JsonDeserializer<?> valueDeser = this._valueDeserializer;
      JavaType valueType = this._containerType.getContentType();
      if (valueDeser == null) {
         valueDeser = this.findConvertingContentDeserializer(ctxt, property, valueDeser);
         if (valueDeser == null) {
            valueDeser = ctxt.findContextualValueDeserializer(valueType, property);
         }
      } else {
         valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, valueType);
      }

      Boolean unwrapSingle = this.findFormatFeature(ctxt, property, Collection.class, Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      NullValueProvider nuller = this.findContentNullProvider(ctxt, property, valueDeser);
      if (this.isDefaultDeserializer(valueDeser)) {
         valueDeser = null;
      }

      return this.withResolved(delegate, valueDeser, nuller, unwrapSingle);
   }

   @Override
   public JsonDeserializer<Object> getContentDeserializer() {
      return this._valueDeserializer;
   }

   @Override
   public ValueInstantiator getValueInstantiator() {
      return this._valueInstantiator;
   }

   public Collection<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._delegateDeserializer != null) {
         return (Collection<String>)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
      } else {
         Collection<String> result = (Collection<String>)this._valueInstantiator.createUsingDefault(ctxt);
         return this.deserialize(p, ctxt, result);
      }
   }

   public Collection<String> deserialize(JsonParser p, DeserializationContext ctxt, Collection<String> result) throws IOException {
      if (!p.isExpectedStartArrayToken()) {
         return this.handleNonArray(p, ctxt, result);
      } else if (this._valueDeserializer != null) {
         return this.deserializeUsingCustom(p, ctxt, result, this._valueDeserializer);
      } else {
         try {
            while (true) {
               String value = p.nextTextValue();
               if (value != null) {
                  result.add(value);
               } else {
                  JsonToken t = p.currentToken();
                  if (t == JsonToken.END_ARRAY) {
                     return result;
                  }

                  if (t == JsonToken.VALUE_NULL) {
                     if (this._skipNullValues) {
                        continue;
                     }

                     value = (String)this._nullProvider.getNullValue(ctxt);
                  } else {
                     value = this._parseString(p, ctxt, this._nullProvider);
                  }

                  result.add(value);
               }
            }
         } catch (Exception var6) {
            throw JsonMappingException.wrapWithPath(var6, result, result.size());
         }
      }
   }

   private Collection<String> deserializeUsingCustom(JsonParser p, DeserializationContext ctxt, Collection<String> result, JsonDeserializer<String> deser) throws IOException {
      try {
         while (true) {
            String value;
            if (p.nextTextValue() == null) {
               JsonToken t = p.currentToken();
               if (t == JsonToken.END_ARRAY) {
                  return result;
               }

               if (t == JsonToken.VALUE_NULL) {
                  if (this._skipNullValues) {
                     continue;
                  }

                  value = (String)this._nullProvider.getNullValue(ctxt);
               } else {
                  value = deser.deserialize(p, ctxt);
               }
            } else {
               value = deser.deserialize(p, ctxt);
            }

            result.add(value);
         }
      } catch (Exception var7) {
         throw JsonMappingException.wrapWithPath(var7, result, result.size());
      }
   }

   @Override
   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromArray(p, ctxt);
   }

   private final Collection<String> handleNonArray(JsonParser p, DeserializationContext ctxt, Collection<String> result) throws IOException {
      boolean canWrap = this._unwrapSingle == Boolean.TRUE || this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      if (!canWrap) {
         return p.hasToken(JsonToken.VALUE_STRING) ? this._deserializeFromString(p, ctxt) : (Collection)ctxt.handleUnexpectedToken(this._containerType, p);
      } else {
         JsonDeserializer<String> valueDes = this._valueDeserializer;
         JsonToken t = p.currentToken();
         String value;
         if (t == JsonToken.VALUE_NULL) {
            if (this._skipNullValues) {
               return result;
            }

            value = (String)this._nullProvider.getNullValue(ctxt);
         } else {
            if (p.hasToken(JsonToken.VALUE_STRING)) {
               String textValue = p.getText();
               if (textValue.isEmpty()) {
                  CoercionAction act = ctxt.findCoercionAction(this.logicalType(), this.handledType(), CoercionInputShape.EmptyString);
                  if (act != CoercionAction.Fail) {
                     return (Collection<String>)this._deserializeFromEmptyString(p, ctxt, act, this.handledType(), "empty String (\"\")");
                  }
               } else if (_isBlank(textValue)) {
                  CoercionAction act = ctxt.findCoercionFromBlankString(this.logicalType(), this.handledType(), CoercionAction.Fail);
                  if (act != CoercionAction.Fail) {
                     return (Collection<String>)this._deserializeFromEmptyString(p, ctxt, act, this.handledType(), "blank String (all whitespace)");
                  }
               }
            }

            try {
               value = valueDes == null ? this._parseString(p, ctxt, this._nullProvider) : valueDes.deserialize(p, ctxt);
            } catch (Exception var10) {
               throw JsonMappingException.wrapWithPath(var10, result, result.size());
            }
         }

         result.add(value);
         return result;
      }
   }
}
