package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

@JacksonStdImpl
public class JsonValueSerializer extends StdSerializer<Object> implements ContextualSerializer, JsonFormatVisitable, SchemaAware {
   protected final AnnotatedMember _accessor;
   protected final TypeSerializer _valueTypeSerializer;
   protected final JsonSerializer<Object> _valueSerializer;
   protected final BeanProperty _property;
   protected final JavaType _valueType;
   protected final boolean _forceTypeInformation;
   protected transient PropertySerializerMap _dynamicSerializers;

   public JsonValueSerializer(AnnotatedMember accessor, TypeSerializer vts, JsonSerializer<?> ser) {
      super(accessor.getType());
      this._accessor = accessor;
      this._valueType = accessor.getType();
      this._valueTypeSerializer = vts;
      this._valueSerializer = (JsonSerializer<Object>)ser;
      this._property = null;
      this._forceTypeInformation = true;
      this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
   }

   @Deprecated
   public JsonValueSerializer(AnnotatedMember accessor, JsonSerializer<?> ser) {
      this(accessor, null, ser);
   }

   public JsonValueSerializer(JsonValueSerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> ser, boolean forceTypeInfo) {
      super(_notNullClass(src.handledType()));
      this._accessor = src._accessor;
      this._valueType = src._valueType;
      this._valueTypeSerializer = vts;
      this._valueSerializer = (JsonSerializer<Object>)ser;
      this._property = property;
      this._forceTypeInformation = forceTypeInfo;
      this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
   }

   private static final Class<Object> _notNullClass(Class<?> cls) {
      return cls == null ? Object.class : cls;
   }

   protected JsonValueSerializer withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> ser, boolean forceTypeInfo) {
      return this._property == property && this._valueTypeSerializer == vts && this._valueSerializer == ser && forceTypeInfo == this._forceTypeInformation
         ? this
         : new JsonValueSerializer(this, property, vts, ser, forceTypeInfo);
   }

   @Override
   public boolean isEmpty(SerializerProvider ctxt, Object bean) {
      Object referenced = this._accessor.getValue(bean);
      if (referenced == null) {
         return true;
      } else {
         JsonSerializer<Object> ser = this._valueSerializer;
         if (ser == null) {
            try {
               ser = this._findDynamicSerializer(ctxt, referenced.getClass());
            } catch (JsonMappingException var6) {
               throw new RuntimeJsonMappingException(var6);
            }
         }

         return ser.isEmpty(ctxt, referenced);
      }
   }

   @Override
   public JsonSerializer<?> createContextual(SerializerProvider ctxt, BeanProperty property) throws JsonMappingException {
      TypeSerializer typeSer = this._valueTypeSerializer;
      if (typeSer != null) {
         typeSer = typeSer.forProperty(property);
      }

      JsonSerializer<?> ser = this._valueSerializer;
      if (ser == null) {
         if (ctxt.isEnabled(MapperFeature.USE_STATIC_TYPING) || this._valueType.isFinal()) {
            ser = ctxt.findPrimaryPropertySerializer(this._valueType, property);
            boolean forceTypeInformation = this.isNaturalTypeWithStdHandling(this._valueType.getRawClass(), ser);
            return this.withResolved(property, typeSer, ser, forceTypeInformation);
         } else {
            return property != this._property ? this.withResolved(property, typeSer, ser, this._forceTypeInformation) : this;
         }
      } else {
         ser = ctxt.handlePrimaryContextualization(ser, property);
         return this.withResolved(property, typeSer, ser, this._forceTypeInformation);
      }
   }

   @Override
   public void serialize(Object bean, JsonGenerator gen, SerializerProvider ctxt) throws IOException {
      Object value;
      try {
         value = this._accessor.getValue(bean);
      } catch (Exception var6) {
         value = null;
         this.wrapAndThrow(ctxt, var6, bean, this._accessor.getName() + "()");
      }

      if (value == null) {
         ctxt.defaultSerializeNull(gen);
      } else {
         JsonSerializer<Object> ser = this._valueSerializer;
         if (ser == null) {
            ser = this._findDynamicSerializer(ctxt, value.getClass());
         }

         if (this._valueTypeSerializer != null) {
            ser.serializeWithType(value, gen, ctxt, this._valueTypeSerializer);
         } else {
            ser.serialize(value, gen, ctxt);
         }
      }
   }

   @Override
   public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider ctxt, TypeSerializer typeSer0) throws IOException {
      Object value;
      try {
         value = this._accessor.getValue(bean);
      } catch (Exception var8) {
         value = null;
         this.wrapAndThrow(ctxt, var8, bean, this._accessor.getName() + "()");
      }

      if (value == null) {
         ctxt.defaultSerializeNull(gen);
      } else {
         JsonSerializer<Object> ser = this._valueSerializer;
         if (ser == null) {
            ser = this._findDynamicSerializer(ctxt, value.getClass());
         } else if (this._forceTypeInformation) {
            WritableTypeId typeIdDef = typeSer0.writeTypePrefix(gen, typeSer0.typeId(bean, JsonToken.VALUE_STRING));
            ser.serialize(value, gen, ctxt);
            typeSer0.writeTypeSuffix(gen, typeIdDef);
            return;
         }

         JsonValueSerializer.TypeSerializerRerouter rr = new JsonValueSerializer.TypeSerializerRerouter(typeSer0, bean);
         ser.serializeWithType(value, gen, ctxt, rr);
      }
   }

   @Override
   public JsonNode getSchema(SerializerProvider ctxt, Type typeHint) throws JsonMappingException {
      return this._valueSerializer instanceof SchemaAware ? ((SchemaAware)this._valueSerializer).getSchema(ctxt, null) : JsonSchema.getDefaultSchemaNode();
   }

   @Override
   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      Class<?> declaring = this._accessor.getDeclaringClass();
      if (declaring == null || !ClassUtil.isEnumType(declaring) || !this._acceptJsonFormatVisitorForEnum(visitor, typeHint, declaring)) {
         JsonSerializer<Object> ser = this._valueSerializer;
         if (ser == null) {
            ser = visitor.getProvider().findTypedValueSerializer(this._valueType, false, this._property);
            if (ser == null) {
               visitor.expectAnyFormat(typeHint);
               return;
            }
         }

         ser.acceptJsonFormatVisitor(visitor, this._valueType);
      }
   }

   protected boolean _acceptJsonFormatVisitorForEnum(JsonFormatVisitorWrapper visitor, JavaType typeHint, Class<?> enumType) throws JsonMappingException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.gen.VarType.isGeneric()" because "newRet" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.getInferredExprType(InvocationExprent.java:634)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.DoStatement.toJava(DoStatement.java:146)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:241)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeMethod(ClassWriter.java:1283)
      //
      // Bytecode:
      // 00: aload 1
      // 01: aload 2
      // 02: invokeinterface com/fasterxml/jackson/databind/jsonFormatVisitors/JsonFormatVisitorWrapper.expectStringFormat (Lcom/fasterxml/jackson/databind/JavaType;)Lcom/fasterxml/jackson/databind/jsonFormatVisitors/JsonStringFormatVisitor; 2
      // 07: astore 4
      // 09: aload 4
      // 0b: ifnull a0
      // 0e: new java/util/LinkedHashSet
      // 11: dup
      // 12: invokespecial java/util/LinkedHashSet.<init> ()V
      // 15: astore 5
      // 17: aload 3
      // 18: invokevirtual java/lang/Class.getEnumConstants ()[Ljava/lang/Object;
      // 1b: astore 6
      // 1d: aload 6
      // 1f: arraylength
      // 20: istore 7
      // 22: bipush 0
      // 23: istore 8
      // 25: iload 8
      // 27: iload 7
      // 29: if_icmpge 97
      // 2c: aload 6
      // 2e: iload 8
      // 30: aaload
      // 31: astore 9
      // 33: aload 5
      // 35: aload 0
      // 36: getfield com/fasterxml/jackson/databind/ser/std/JsonValueSerializer._accessor Lcom/fasterxml/jackson/databind/introspect/AnnotatedMember;
      // 39: aload 9
      // 3b: invokevirtual com/fasterxml/jackson/databind/introspect/AnnotatedMember.getValue (Ljava/lang/Object;)Ljava/lang/Object;
      // 3e: invokestatic java/lang/String.valueOf (Ljava/lang/Object;)Ljava/lang/String;
      // 41: invokeinterface java/util/Set.add (Ljava/lang/Object;)Z 2
      // 46: pop
      // 47: goto 91
      // 4a: astore 10
      // 4c: aload 10
      // 4e: astore 11
      // 50: aload 11
      // 52: instanceof java/lang/reflect/InvocationTargetException
      // 55: ifeq 6a
      // 58: aload 11
      // 5a: invokevirtual java/lang/Throwable.getCause ()Ljava/lang/Throwable;
      // 5d: ifnull 6a
      // 60: aload 11
      // 62: invokevirtual java/lang/Throwable.getCause ()Ljava/lang/Throwable;
      // 65: astore 11
      // 67: goto 50
      // 6a: aload 11
      // 6c: invokestatic com/fasterxml/jackson/databind/util/ClassUtil.throwIfError (Ljava/lang/Throwable;)Ljava/lang/Throwable;
      // 6f: pop
      // 70: aload 11
      // 72: aload 9
      // 74: new java/lang/StringBuilder
      // 77: dup
      // 78: invokespecial java/lang/StringBuilder.<init> ()V
      // 7b: aload 0
      // 7c: getfield com/fasterxml/jackson/databind/ser/std/JsonValueSerializer._accessor Lcom/fasterxml/jackson/databind/introspect/AnnotatedMember;
      // 7f: invokevirtual com/fasterxml/jackson/databind/introspect/AnnotatedMember.getName ()Ljava/lang/String;
      // 82: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 85: ldc "()"
      // 87: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 8a: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // 8d: invokestatic com/fasterxml/jackson/databind/JsonMappingException.wrapWithPath (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;)Lcom/fasterxml/jackson/databind/JsonMappingException;
      // 90: athrow
      // 91: iinc 8 1
      // 94: goto 25
      // 97: aload 4
      // 99: aload 5
      // 9b: invokeinterface com/fasterxml/jackson/databind/jsonFormatVisitors/JsonStringFormatVisitor.enumTypes (Ljava/util/Set;)V 2
      // a0: bipush 1
      // a1: ireturn
   }

   protected boolean isNaturalTypeWithStdHandling(Class<?> rawType, JsonSerializer<?> ser) {
      if (rawType.isPrimitive()) {
         if (rawType != int.class && rawType != boolean.class && rawType != double.class) {
            return false;
         }
      } else if (rawType != String.class && rawType != Integer.class && rawType != Boolean.class && rawType != Double.class) {
         return false;
      }

      return this.isDefaultSerializer(ser);
   }

   protected JsonSerializer<Object> _findDynamicSerializer(SerializerProvider ctxt, Class<?> valueClass) throws JsonMappingException {
      JsonSerializer<Object> serializer = this._dynamicSerializers.serializerFor(valueClass);
      if (serializer == null) {
         if (this._valueType.hasGenericTypes()) {
            JavaType fullType = ctxt.constructSpecializedType(this._valueType, valueClass);
            serializer = ctxt.findPrimaryPropertySerializer(fullType, this._property);
            PropertySerializerMap.SerializerAndMapResult result = this._dynamicSerializers.addSerializer(fullType, serializer);
            this._dynamicSerializers = result.map;
         } else {
            serializer = ctxt.findPrimaryPropertySerializer(valueClass, this._property);
            PropertySerializerMap.SerializerAndMapResult result = this._dynamicSerializers.addSerializer(valueClass, serializer);
            this._dynamicSerializers = result.map;
         }
      }

      return serializer;
   }

   @Override
   public String toString() {
      return "(@JsonValue serializer for method " + this._accessor.getDeclaringClass() + "#" + this._accessor.getName() + ")";
   }

   static class TypeSerializerRerouter extends TypeSerializer {
      protected final TypeSerializer _typeSerializer;
      protected final Object _forObject;

      public TypeSerializerRerouter(TypeSerializer ts, Object ob) {
         this._typeSerializer = ts;
         this._forObject = ob;
      }

      @Override
      public TypeSerializer forProperty(BeanProperty prop) {
         throw new UnsupportedOperationException();
      }

      @Override
      public As getTypeInclusion() {
         return this._typeSerializer.getTypeInclusion();
      }

      @Override
      public String getPropertyName() {
         return this._typeSerializer.getPropertyName();
      }

      @Override
      public TypeIdResolver getTypeIdResolver() {
         return this._typeSerializer.getTypeIdResolver();
      }

      @Override
      public WritableTypeId writeTypePrefix(JsonGenerator g, WritableTypeId typeId) throws IOException {
         typeId.forValue = this._forObject;
         return this._typeSerializer.writeTypePrefix(g, typeId);
      }

      @Override
      public WritableTypeId writeTypeSuffix(JsonGenerator g, WritableTypeId typeId) throws IOException {
         return this._typeSerializer.writeTypeSuffix(g, typeId);
      }

      @Deprecated
      @Override
      public void writeTypePrefixForScalar(Object value, JsonGenerator gen) throws IOException {
         this._typeSerializer.writeTypePrefixForScalar(this._forObject, gen);
      }

      @Deprecated
      @Override
      public void writeTypePrefixForObject(Object value, JsonGenerator gen) throws IOException {
         this._typeSerializer.writeTypePrefixForObject(this._forObject, gen);
      }

      @Deprecated
      @Override
      public void writeTypePrefixForArray(Object value, JsonGenerator gen) throws IOException {
         this._typeSerializer.writeTypePrefixForArray(this._forObject, gen);
      }

      @Deprecated
      @Override
      public void writeTypeSuffixForScalar(Object value, JsonGenerator gen) throws IOException {
         this._typeSerializer.writeTypeSuffixForScalar(this._forObject, gen);
      }

      @Deprecated
      @Override
      public void writeTypeSuffixForObject(Object value, JsonGenerator gen) throws IOException {
         this._typeSerializer.writeTypeSuffixForObject(this._forObject, gen);
      }

      @Deprecated
      @Override
      public void writeTypeSuffixForArray(Object value, JsonGenerator gen) throws IOException {
         this._typeSerializer.writeTypeSuffixForArray(this._forObject, gen);
      }

      @Deprecated
      @Override
      public void writeTypePrefixForScalar(Object value, JsonGenerator gen, Class<?> type) throws IOException {
         this._typeSerializer.writeTypePrefixForScalar(this._forObject, gen, type);
      }

      @Deprecated
      @Override
      public void writeTypePrefixForObject(Object value, JsonGenerator gen, Class<?> type) throws IOException {
         this._typeSerializer.writeTypePrefixForObject(this._forObject, gen, type);
      }

      @Deprecated
      @Override
      public void writeTypePrefixForArray(Object value, JsonGenerator gen, Class<?> type) throws IOException {
         this._typeSerializer.writeTypePrefixForArray(this._forObject, gen, type);
      }

      @Deprecated
      @Override
      public void writeCustomTypePrefixForScalar(Object value, JsonGenerator gen, String typeId) throws IOException {
         this._typeSerializer.writeCustomTypePrefixForScalar(this._forObject, gen, typeId);
      }

      @Deprecated
      @Override
      public void writeCustomTypePrefixForObject(Object value, JsonGenerator gen, String typeId) throws IOException {
         this._typeSerializer.writeCustomTypePrefixForObject(this._forObject, gen, typeId);
      }

      @Deprecated
      @Override
      public void writeCustomTypePrefixForArray(Object value, JsonGenerator gen, String typeId) throws IOException {
         this._typeSerializer.writeCustomTypePrefixForArray(this._forObject, gen, typeId);
      }

      @Deprecated
      @Override
      public void writeCustomTypeSuffixForScalar(Object value, JsonGenerator gen, String typeId) throws IOException {
         this._typeSerializer.writeCustomTypeSuffixForScalar(this._forObject, gen, typeId);
      }

      @Deprecated
      @Override
      public void writeCustomTypeSuffixForObject(Object value, JsonGenerator gen, String typeId) throws IOException {
         this._typeSerializer.writeCustomTypeSuffixForObject(this._forObject, gen, typeId);
      }

      @Deprecated
      @Override
      public void writeCustomTypeSuffixForArray(Object value, JsonGenerator gen, String typeId) throws IOException {
         this._typeSerializer.writeCustomTypeSuffixForArray(this._forObject, gen, typeId);
      }
   }
}
