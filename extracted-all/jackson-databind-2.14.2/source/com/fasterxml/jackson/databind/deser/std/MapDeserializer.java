package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JacksonStdImpl
public class MapDeserializer extends ContainerDeserializerBase<Map<Object, Object>> implements ContextualDeserializer, ResolvableDeserializer {
   private static final long serialVersionUID = 1L;
   protected final KeyDeserializer _keyDeserializer;
   protected boolean _standardStringKey;
   protected final JsonDeserializer<Object> _valueDeserializer;
   protected final TypeDeserializer _valueTypeDeserializer;
   protected final ValueInstantiator _valueInstantiator;
   protected JsonDeserializer<Object> _delegateDeserializer;
   protected PropertyBasedCreator _propertyBasedCreator;
   protected final boolean _hasDefaultCreator;
   protected Set<String> _ignorableProperties;
   protected Set<String> _includableProperties;
   protected IgnorePropertiesUtil.Checker _inclusionChecker;
   protected boolean _checkDupSquash;

   public MapDeserializer(
      JavaType mapType, ValueInstantiator valueInstantiator, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser
   ) {
      super(mapType, null, null);
      this._keyDeserializer = keyDeser;
      this._valueDeserializer = valueDeser;
      this._valueTypeDeserializer = valueTypeDeser;
      this._valueInstantiator = valueInstantiator;
      this._hasDefaultCreator = valueInstantiator.canCreateUsingDefault();
      this._delegateDeserializer = null;
      this._propertyBasedCreator = null;
      this._standardStringKey = this._isStdKeyDeser(mapType, keyDeser);
      this._inclusionChecker = null;
      this._checkDupSquash = mapType.getContentType().hasRawClass(Object.class);
   }

   protected MapDeserializer(MapDeserializer src) {
      super(src);
      this._keyDeserializer = src._keyDeserializer;
      this._valueDeserializer = src._valueDeserializer;
      this._valueTypeDeserializer = src._valueTypeDeserializer;
      this._valueInstantiator = src._valueInstantiator;
      this._propertyBasedCreator = src._propertyBasedCreator;
      this._delegateDeserializer = src._delegateDeserializer;
      this._hasDefaultCreator = src._hasDefaultCreator;
      this._ignorableProperties = src._ignorableProperties;
      this._includableProperties = src._includableProperties;
      this._inclusionChecker = src._inclusionChecker;
      this._standardStringKey = src._standardStringKey;
      this._checkDupSquash = src._checkDupSquash;
   }

   protected MapDeserializer(
      MapDeserializer src,
      KeyDeserializer keyDeser,
      JsonDeserializer<Object> valueDeser,
      TypeDeserializer valueTypeDeser,
      NullValueProvider nuller,
      Set<String> ignorable
   ) {
      this(src, keyDeser, valueDeser, valueTypeDeser, nuller, ignorable, null);
   }

   protected MapDeserializer(
      MapDeserializer src,
      KeyDeserializer keyDeser,
      JsonDeserializer<Object> valueDeser,
      TypeDeserializer valueTypeDeser,
      NullValueProvider nuller,
      Set<String> ignorable,
      Set<String> includable
   ) {
      super(src, nuller, src._unwrapSingle);
      this._keyDeserializer = keyDeser;
      this._valueDeserializer = valueDeser;
      this._valueTypeDeserializer = valueTypeDeser;
      this._valueInstantiator = src._valueInstantiator;
      this._propertyBasedCreator = src._propertyBasedCreator;
      this._delegateDeserializer = src._delegateDeserializer;
      this._hasDefaultCreator = src._hasDefaultCreator;
      this._ignorableProperties = ignorable;
      this._includableProperties = includable;
      this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(ignorable, includable);
      this._standardStringKey = this._isStdKeyDeser(this._containerType, keyDeser);
      this._checkDupSquash = src._checkDupSquash;
   }

   protected MapDeserializer withResolved(
      KeyDeserializer keyDeser, TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser, NullValueProvider nuller, Set<String> ignorable
   ) {
      return this.withResolved(keyDeser, valueTypeDeser, valueDeser, nuller, ignorable, this._includableProperties);
   }

   protected MapDeserializer withResolved(
      KeyDeserializer keyDeser,
      TypeDeserializer valueTypeDeser,
      JsonDeserializer<?> valueDeser,
      NullValueProvider nuller,
      Set<String> ignorable,
      Set<String> includable
   ) {
      return this._keyDeserializer == keyDeser
            && this._valueDeserializer == valueDeser
            && this._valueTypeDeserializer == valueTypeDeser
            && this._nullProvider == nuller
            && this._ignorableProperties == ignorable
            && this._includableProperties == includable
         ? this
         : new MapDeserializer(this, keyDeser, (JsonDeserializer<Object>)valueDeser, valueTypeDeser, nuller, ignorable, includable);
   }

   protected final boolean _isStdKeyDeser(JavaType mapType, KeyDeserializer keyDeser) {
      if (keyDeser == null) {
         return true;
      } else {
         JavaType keyType = mapType.getKeyType();
         if (keyType == null) {
            return true;
         } else {
            Class<?> rawKeyType = keyType.getRawClass();
            return (rawKeyType == String.class || rawKeyType == Object.class) && this.isDefaultKeyDeserializer(keyDeser);
         }
      }
   }

   @Deprecated
   public void setIgnorableProperties(String[] ignorable) {
      this._ignorableProperties = ignorable != null && ignorable.length != 0 ? ArrayBuilders.arrayToSet(ignorable) : null;
      this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignorableProperties, this._includableProperties);
   }

   public void setIgnorableProperties(Set<String> ignorable) {
      this._ignorableProperties = ignorable != null && !ignorable.isEmpty() ? ignorable : null;
      this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignorableProperties, this._includableProperties);
   }

   public void setIncludableProperties(Set<String> includable) {
      this._includableProperties = includable;
      this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignorableProperties, this._includableProperties);
   }

   @Override
   public void resolve(DeserializationContext ctxt) throws JsonMappingException {
      if (this._valueInstantiator.canCreateUsingDelegate()) {
         JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
         if (delegateType == null) {
            ctxt.reportBadDefinition(
               this._containerType,
               String.format(
                  "Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'",
                  this._containerType,
                  this._valueInstantiator.getClass().getName()
               )
            );
         }

         this._delegateDeserializer = this.findDeserializer(ctxt, delegateType, null);
      } else if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
         JavaType delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
         if (delegateType == null) {
            ctxt.reportBadDefinition(
               this._containerType,
               String.format(
                  "Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'",
                  this._containerType,
                  this._valueInstantiator.getClass().getName()
               )
            );
         }

         this._delegateDeserializer = this.findDeserializer(ctxt, delegateType, null);
      }

      if (this._valueInstantiator.canCreateFromObjectWith()) {
         SettableBeanProperty[] creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
         this._propertyBasedCreator = PropertyBasedCreator.construct(
            ctxt, this._valueInstantiator, creatorProps, ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
         );
      }

      this._standardStringKey = this._isStdKeyDeser(this._containerType, this._keyDeserializer);
   }

   @Override
   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      KeyDeserializer keyDeser = this._keyDeserializer;
      if (keyDeser == null) {
         keyDeser = ctxt.findKeyDeserializer(this._containerType.getKeyType(), property);
      } else if (keyDeser instanceof ContextualKeyDeserializer) {
         keyDeser = ((ContextualKeyDeserializer)keyDeser).createContextual(ctxt, property);
      }

      JsonDeserializer<?> valueDeser = this._valueDeserializer;
      if (property != null) {
         valueDeser = this.findConvertingContentDeserializer(ctxt, property, valueDeser);
      }

      JavaType vt = this._containerType.getContentType();
      if (valueDeser == null) {
         valueDeser = ctxt.findContextualValueDeserializer(vt, property);
      } else {
         valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
      }

      TypeDeserializer vtd = this._valueTypeDeserializer;
      if (vtd != null) {
         vtd = vtd.forProperty(property);
      }

      Set<String> ignored = this._ignorableProperties;
      Set<String> included = this._includableProperties;
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (_neitherNull(intr, property)) {
         AnnotatedMember member = property.getMember();
         if (member != null) {
            DeserializationConfig config = ctxt.getConfig();
            Value ignorals = intr.findPropertyIgnoralByName(config, member);
            if (ignorals != null) {
               Set<String> ignoresToAdd = ignorals.findIgnoredForDeserialization();
               if (!ignoresToAdd.isEmpty()) {
                  ignored = ignored == null ? new HashSet<>() : new HashSet<>(ignored);

                  for (String str : ignoresToAdd) {
                     ignored.add(str);
                  }
               }
            }

            com.fasterxml.jackson.annotation.JsonIncludeProperties.Value inclusions = intr.findPropertyInclusionByName(config, member);
            if (inclusions != null) {
               Set<String> includedToAdd = inclusions.getIncluded();
               if (includedToAdd != null) {
                  Set<String> newIncluded = new HashSet<>();
                  if (included == null) {
                     newIncluded = new HashSet<>(includedToAdd);
                  } else {
                     for (String str : includedToAdd) {
                        if (included.contains(str)) {
                           newIncluded.add(str);
                        }
                     }
                  }

                  included = newIncluded;
               }
            }
         }
      }

      return this.withResolved(keyDeser, vtd, valueDeser, this.findContentNullProvider(ctxt, property, valueDeser), ignored, included);
   }

   @Override
   public JsonDeserializer<Object> getContentDeserializer() {
      return this._valueDeserializer;
   }

   @Override
   public ValueInstantiator getValueInstantiator() {
      return this._valueInstantiator;
   }

   @Override
   public boolean isCachable() {
      return this._valueDeserializer == null
         && this._keyDeserializer == null
         && this._valueTypeDeserializer == null
         && this._ignorableProperties == null
         && this._includableProperties == null;
   }

   @Override
   public LogicalType logicalType() {
      return LogicalType.Map;
   }

   public Map<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._propertyBasedCreator != null) {
         return this._deserializeUsingCreator(p, ctxt);
      } else if (this._delegateDeserializer != null) {
         return (Map<Object, Object>)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
      } else if (!this._hasDefaultCreator) {
         return (Map<Object, Object>)ctxt.handleMissingInstantiator(this.getMapClass(), this.getValueInstantiator(), p, "no default constructor found");
      } else {
         switch (p.currentTokenId()) {
            case 1:
            case 2:
            case 5:
               Map<Object, Object> result = (Map<Object, Object>)this._valueInstantiator.createUsingDefault(ctxt);
               if (this._standardStringKey) {
                  return this._readAndBindStringKeyMap(p, ctxt, result);
               }

               return this._readAndBind(p, ctxt, result);
            case 3:
               return this._deserializeFromArray(p, ctxt);
            case 4:
            default:
               return (Map<Object, Object>)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
            case 6:
               return this._deserializeFromString(p, ctxt);
         }
      }
   }

   public Map<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
      p.setCurrentValue(result);
      JsonToken t = p.currentToken();
      if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME) {
         return (Map<Object, Object>)ctxt.handleUnexpectedToken(this.getMapClass(), p);
      } else if (this._standardStringKey) {
         this._readAndUpdateStringKeyMap(p, ctxt, result);
         return result;
      } else {
         this._readAndUpdate(p, ctxt, result);
         return result;
      }
   }

   @Override
   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromObject(p, ctxt);
   }

   public final Class<?> getMapClass() {
      return this._containerType.getRawClass();
   }

   @Override
   public JavaType getValueType() {
      return this._containerType;
   }

   protected final Map<Object, Object> _readAndBind(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
      KeyDeserializer keyDes = this._keyDeserializer;
      JsonDeserializer<Object> valueDes = this._valueDeserializer;
      TypeDeserializer typeDeser = this._valueTypeDeserializer;
      MapDeserializer.MapReferringAccumulator referringAccumulator = null;
      boolean useObjectId = valueDes.getObjectIdReader() != null;
      if (useObjectId) {
         referringAccumulator = new MapDeserializer.MapReferringAccumulator(this._containerType.getContentType().getRawClass(), result);
      }

      String keyStr;
      if (p.isExpectedStartObjectToken()) {
         keyStr = p.nextFieldName();
      } else {
         JsonToken t = p.currentToken();
         if (t != JsonToken.FIELD_NAME) {
            if (t == JsonToken.END_OBJECT) {
               return result;
            }

            ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null);
         }

         keyStr = p.currentName();
      }

      for (; keyStr != null; keyStr = p.nextFieldName()) {
         Object key = keyDes.deserializeKey(keyStr, ctxt);
         JsonToken t = p.nextToken();
         if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyStr)) {
            p.skipChildren();
         } else {
            try {
               Object value;
               if (t == JsonToken.VALUE_NULL) {
                  if (this._skipNullValues) {
                     continue;
                  }

                  value = this._nullProvider.getNullValue(ctxt);
               } else if (typeDeser == null) {
                  value = valueDes.deserialize(p, ctxt);
               } else {
                  value = valueDes.deserializeWithType(p, ctxt, typeDeser);
               }

               if (useObjectId) {
                  referringAccumulator.put(key, value);
               } else {
                  Object oldValue = result.put(key, value);
                  if (oldValue != null) {
                     this._squashDups(ctxt, result, key, oldValue, value);
                  }
               }
            } catch (UnresolvedForwardReference var14) {
               this.handleUnresolvedReference(ctxt, referringAccumulator, key, var14);
            } catch (Exception var15) {
               this.wrapAndThrow(ctxt, var15, result, keyStr);
            }
         }
      }

      return result;
   }

   protected final Map<Object, Object> _readAndBindStringKeyMap(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
      JsonDeserializer<Object> valueDes = this._valueDeserializer;
      TypeDeserializer typeDeser = this._valueTypeDeserializer;
      MapDeserializer.MapReferringAccumulator referringAccumulator = null;
      boolean useObjectId = valueDes.getObjectIdReader() != null;
      if (useObjectId) {
         referringAccumulator = new MapDeserializer.MapReferringAccumulator(this._containerType.getContentType().getRawClass(), result);
      }

      String key;
      if (p.isExpectedStartObjectToken()) {
         key = p.nextFieldName();
      } else {
         JsonToken t = p.currentToken();
         if (t == JsonToken.END_OBJECT) {
            return result;
         }

         if (t != JsonToken.FIELD_NAME) {
            ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null);
         }

         key = p.currentName();
      }

      for (; key != null; key = p.nextFieldName()) {
         JsonToken tx = p.nextToken();
         if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(key)) {
            p.skipChildren();
         } else {
            try {
               Object value;
               if (tx == JsonToken.VALUE_NULL) {
                  if (this._skipNullValues) {
                     continue;
                  }

                  value = this._nullProvider.getNullValue(ctxt);
               } else if (typeDeser == null) {
                  value = valueDes.deserialize(p, ctxt);
               } else {
                  value = valueDes.deserializeWithType(p, ctxt, typeDeser);
               }

               if (useObjectId) {
                  referringAccumulator.put(key, value);
               } else {
                  Object oldValue = result.put(key, value);
                  if (oldValue != null) {
                     this._squashDups(ctxt, result, key, oldValue, value);
                  }
               }
            } catch (UnresolvedForwardReference var12) {
               this.handleUnresolvedReference(ctxt, referringAccumulator, key, var12);
            } catch (Exception var13) {
               this.wrapAndThrow(ctxt, var13, result, key);
            }
         }
      }

      return result;
   }

   public Map<Object, Object> _deserializeUsingCreator(JsonParser p, DeserializationContext ctxt) throws IOException {
      PropertyBasedCreator creator = this._propertyBasedCreator;
      PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, null);
      JsonDeserializer<Object> valueDes = this._valueDeserializer;
      TypeDeserializer typeDeser = this._valueTypeDeserializer;
      String key;
      if (p.isExpectedStartObjectToken()) {
         key = p.nextFieldName();
      } else if (p.hasToken(JsonToken.FIELD_NAME)) {
         key = p.currentName();
      } else {
         key = null;
      }

      for (; key != null; key = p.nextFieldName()) {
         JsonToken t = p.nextToken();
         if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(key)) {
            p.skipChildren();
         } else {
            SettableBeanProperty prop = creator.findCreatorProperty(key);
            if (prop != null) {
               if (buffer.assignParameter(prop, prop.deserialize(p, ctxt))) {
                  p.nextToken();

                  Map<Object, Object> result;
                  try {
                     result = (Map<Object, Object>)creator.build(ctxt, buffer);
                  } catch (Exception var13) {
                     return this.wrapAndThrow(ctxt, var13, this._containerType.getRawClass(), key);
                  }

                  return this._readAndBind(p, ctxt, result);
               }
            } else {
               Object actualKey = this._keyDeserializer.deserializeKey(key, ctxt);

               Object value;
               try {
                  if (t == JsonToken.VALUE_NULL) {
                     if (this._skipNullValues) {
                        continue;
                     }

                     value = this._nullProvider.getNullValue(ctxt);
                  } else if (typeDeser == null) {
                     value = valueDes.deserialize(p, ctxt);
                  } else {
                     value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                  }
               } catch (Exception var15) {
                  this.wrapAndThrow(ctxt, var15, this._containerType.getRawClass(), key);
                  return null;
               }

               buffer.bufferMapProperty(actualKey, value);
            }
         }
      }

      try {
         return (Map<Object, Object>)creator.build(ctxt, buffer);
      } catch (Exception var14) {
         this.wrapAndThrow(ctxt, var14, this._containerType.getRawClass(), key);
         return null;
      }
   }

   protected final void _readAndUpdate(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
      KeyDeserializer keyDes = this._keyDeserializer;
      JsonDeserializer<Object> valueDes = this._valueDeserializer;
      TypeDeserializer typeDeser = this._valueTypeDeserializer;
      String keyStr;
      if (p.isExpectedStartObjectToken()) {
         keyStr = p.nextFieldName();
      } else {
         JsonToken t = p.currentToken();
         if (t == JsonToken.END_OBJECT) {
            return;
         }

         if (t != JsonToken.FIELD_NAME) {
            ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null);
         }

         keyStr = p.currentName();
      }

      for (; keyStr != null; keyStr = p.nextFieldName()) {
         Object key = keyDes.deserializeKey(keyStr, ctxt);
         JsonToken tx = p.nextToken();
         if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyStr)) {
            p.skipChildren();
         } else {
            try {
               if (tx == JsonToken.VALUE_NULL) {
                  if (!this._skipNullValues) {
                     result.put(key, this._nullProvider.getNullValue(ctxt));
                  }
               } else {
                  Object old = result.get(key);
                  Object value;
                  if (old != null) {
                     if (typeDeser == null) {
                        value = valueDes.deserialize(p, ctxt, old);
                     } else {
                        value = valueDes.deserializeWithType(p, ctxt, typeDeser, old);
                     }
                  } else if (typeDeser == null) {
                     value = valueDes.deserialize(p, ctxt);
                  } else {
                     value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                  }

                  if (value != old) {
                     result.put(key, value);
                  }
               }
            } catch (Exception var12) {
               this.wrapAndThrow(ctxt, var12, result, keyStr);
            }
         }
      }
   }

   protected final void _readAndUpdateStringKeyMap(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
      JsonDeserializer<Object> valueDes = this._valueDeserializer;
      TypeDeserializer typeDeser = this._valueTypeDeserializer;
      String key;
      if (p.isExpectedStartObjectToken()) {
         key = p.nextFieldName();
      } else {
         JsonToken t = p.currentToken();
         if (t == JsonToken.END_OBJECT) {
            return;
         }

         if (t != JsonToken.FIELD_NAME) {
            ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null);
         }

         key = p.currentName();
      }

      for (; key != null; key = p.nextFieldName()) {
         JsonToken tx = p.nextToken();
         if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(key)) {
            p.skipChildren();
         } else {
            try {
               if (tx == JsonToken.VALUE_NULL) {
                  if (!this._skipNullValues) {
                     result.put(key, this._nullProvider.getNullValue(ctxt));
                  }
               } else {
                  Object old = result.get(key);
                  Object value;
                  if (old != null) {
                     if (typeDeser == null) {
                        value = valueDes.deserialize(p, ctxt, old);
                     } else {
                        value = valueDes.deserializeWithType(p, ctxt, typeDeser, old);
                     }
                  } else if (typeDeser == null) {
                     value = valueDes.deserialize(p, ctxt);
                  } else {
                     value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                  }

                  if (value != old) {
                     result.put(key, value);
                  }
               }
            } catch (Exception var10) {
               this.wrapAndThrow(ctxt, var10, result, key);
            }
         }
      }
   }

   protected void _squashDups(DeserializationContext ctxt, Map<Object, Object> result, Object key, Object oldValue, Object newValue) {
      if (this._checkDupSquash && ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES)) {
         if (oldValue instanceof List) {
            ((List)oldValue).add(newValue);
            result.put(key, oldValue);
         } else {
            ArrayList<Object> l = new ArrayList<>();
            l.add(oldValue);
            l.add(newValue);
            result.put(key, l);
         }
      }
   }

   private void handleUnresolvedReference(
      DeserializationContext ctxt, MapDeserializer.MapReferringAccumulator accumulator, Object key, UnresolvedForwardReference reference
   ) throws JsonMappingException {
      if (accumulator == null) {
         ctxt.reportInputMismatch(this, "Unresolved forward reference but no identity info: " + reference);
      }

      ReadableObjectId.Referring referring = accumulator.handleUnresolvedReference(reference, key);
      reference.getRoid().appendReferring(referring);
   }

   static class MapReferring extends ReadableObjectId.Referring {
      private final MapDeserializer.MapReferringAccumulator _parent;
      public final Map<Object, Object> next = new LinkedHashMap<>();
      public final Object key;

      MapReferring(MapDeserializer.MapReferringAccumulator parent, UnresolvedForwardReference ref, Class<?> valueType, Object key) {
         super(ref, valueType);
         this._parent = parent;
         this.key = key;
      }

      @Override
      public void handleResolvedForwardReference(Object id, Object value) throws IOException {
         this._parent.resolveForwardReference(id, value);
      }
   }

   private static final class MapReferringAccumulator {
      private final Class<?> _valueType;
      private Map<Object, Object> _result;
      private List<MapDeserializer.MapReferring> _accumulator = new ArrayList<>();

      public MapReferringAccumulator(Class<?> valueType, Map<Object, Object> result) {
         this._valueType = valueType;
         this._result = result;
      }

      public void put(Object key, Object value) {
         if (this._accumulator.isEmpty()) {
            this._result.put(key, value);
         } else {
            MapDeserializer.MapReferring ref = this._accumulator.get(this._accumulator.size() - 1);
            ref.next.put(key, value);
         }
      }

      public ReadableObjectId.Referring handleUnresolvedReference(UnresolvedForwardReference reference, Object key) {
         MapDeserializer.MapReferring id = new MapDeserializer.MapReferring(this, reference, this._valueType, key);
         this._accumulator.add(id);
         return id;
      }

      public void resolveForwardReference(Object id, Object value) throws IOException {
         Iterator<MapDeserializer.MapReferring> iterator = this._accumulator.iterator();
         Map<Object, Object> previous = this._result;

         while (iterator.hasNext()) {
            MapDeserializer.MapReferring ref = iterator.next();
            if (ref.hasId(id)) {
               iterator.remove();
               previous.put(ref.key, value);
               previous.putAll(ref.next);
               return;
            }

            previous = ref.next;
         }

         throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
      }
   }
}
