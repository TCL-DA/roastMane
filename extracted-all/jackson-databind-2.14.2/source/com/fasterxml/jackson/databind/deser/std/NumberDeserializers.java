package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;

public class NumberDeserializers {
   private static final HashSet<String> _classNames = new HashSet<>();

   public static JsonDeserializer<?> find(Class<?> rawType, String clsName) {
      if (rawType.isPrimitive()) {
         if (rawType == int.class) {
            return NumberDeserializers.IntegerDeserializer.primitiveInstance;
         }

         if (rawType == boolean.class) {
            return NumberDeserializers.BooleanDeserializer.primitiveInstance;
         }

         if (rawType == long.class) {
            return NumberDeserializers.LongDeserializer.primitiveInstance;
         }

         if (rawType == double.class) {
            return NumberDeserializers.DoubleDeserializer.primitiveInstance;
         }

         if (rawType == char.class) {
            return NumberDeserializers.CharacterDeserializer.primitiveInstance;
         }

         if (rawType == byte.class) {
            return NumberDeserializers.ByteDeserializer.primitiveInstance;
         }

         if (rawType == short.class) {
            return NumberDeserializers.ShortDeserializer.primitiveInstance;
         }

         if (rawType == float.class) {
            return NumberDeserializers.FloatDeserializer.primitiveInstance;
         }

         if (rawType == void.class) {
            return NullifyingDeserializer.instance;
         }
      } else {
         if (!_classNames.contains(clsName)) {
            return null;
         }

         if (rawType == Integer.class) {
            return NumberDeserializers.IntegerDeserializer.wrapperInstance;
         }

         if (rawType == Boolean.class) {
            return NumberDeserializers.BooleanDeserializer.wrapperInstance;
         }

         if (rawType == Long.class) {
            return NumberDeserializers.LongDeserializer.wrapperInstance;
         }

         if (rawType == Double.class) {
            return NumberDeserializers.DoubleDeserializer.wrapperInstance;
         }

         if (rawType == Character.class) {
            return NumberDeserializers.CharacterDeserializer.wrapperInstance;
         }

         if (rawType == Byte.class) {
            return NumberDeserializers.ByteDeserializer.wrapperInstance;
         }

         if (rawType == Short.class) {
            return NumberDeserializers.ShortDeserializer.wrapperInstance;
         }

         if (rawType == Float.class) {
            return NumberDeserializers.FloatDeserializer.wrapperInstance;
         }

         if (rawType == Number.class) {
            return NumberDeserializers.NumberDeserializer.instance;
         }

         if (rawType == BigDecimal.class) {
            return NumberDeserializers.BigDecimalDeserializer.instance;
         }

         if (rawType == BigInteger.class) {
            return NumberDeserializers.BigIntegerDeserializer.instance;
         }
      }

      throw new IllegalArgumentException("Internal error: can't find deserializer for " + rawType.getName());
   }

   static {
      Class<?>[] numberTypes = new Class[]{
         Boolean.class,
         Byte.class,
         Short.class,
         Character.class,
         Integer.class,
         Long.class,
         Float.class,
         Double.class,
         Number.class,
         BigDecimal.class,
         BigInteger.class
      };

      for (Class<?> cls : numberTypes) {
         _classNames.add(cls.getName());
      }
   }

   @JacksonStdImpl
   public static class BigDecimalDeserializer extends StdScalarDeserializer<BigDecimal> {
      public static final NumberDeserializers.BigDecimalDeserializer instance = new NumberDeserializers.BigDecimalDeserializer();

      public BigDecimalDeserializer() {
         super(BigDecimal.class);
      }

      @Override
      public Object getEmptyValue(DeserializationContext ctxt) {
         return BigDecimal.ZERO;
      }

      @Override
      public final LogicalType logicalType() {
         return LogicalType.Float;
      }

      public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         String text;
         switch (p.currentTokenId()) {
            case 1:
               text = ctxt.extractScalarFromObject(p, this, this._valueClass);
               break;
            case 2:
            case 4:
            case 5:
            default:
               return (BigDecimal)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
            case 3:
               return this._deserializeFromArray(p, ctxt);
            case 6:
               text = p.getText();
               break;
            case 7:
               CoercionAction act = this._checkIntToFloatCoercion(p, ctxt, this._valueClass);
               if (act == CoercionAction.AsNull) {
                  return this.getNullValue(ctxt);
               }

               if (act == CoercionAction.AsEmpty) {
                  return (BigDecimal)this.getEmptyValue(ctxt);
               }
            case 8:
               return p.getDecimalValue();
         }

         CoercionAction actx = this._checkFromStringCoercion(ctxt, text);
         if (actx == CoercionAction.AsNull) {
            return this.getNullValue(ctxt);
         } else if (actx == CoercionAction.AsEmpty) {
            return (BigDecimal)this.getEmptyValue(ctxt);
         } else {
            text = text.trim();
            if (this._hasTextualNull(text)) {
               return this.getNullValue(ctxt);
            } else {
               try {
                  return NumberInput.parseBigDecimal(text);
               } catch (IllegalArgumentException var6) {
                  return (BigDecimal)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation");
               }
            }
         }
      }
   }

   @JacksonStdImpl
   public static class BigIntegerDeserializer extends StdScalarDeserializer<BigInteger> {
      public static final NumberDeserializers.BigIntegerDeserializer instance = new NumberDeserializers.BigIntegerDeserializer();

      public BigIntegerDeserializer() {
         super(BigInteger.class);
      }

      @Override
      public Object getEmptyValue(DeserializationContext ctxt) {
         return BigInteger.ZERO;
      }

      @Override
      public final LogicalType logicalType() {
         return LogicalType.Integer;
      }

      public BigInteger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (p.isExpectedNumberIntToken()) {
            return p.getBigIntegerValue();
         } else {
            String text;
            switch (p.currentTokenId()) {
               case 1:
                  text = ctxt.extractScalarFromObject(p, this, this._valueClass);
                  break;
               case 2:
               case 4:
               case 5:
               case 7:
               default:
                  return (BigInteger)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
               case 3:
                  return this._deserializeFromArray(p, ctxt);
               case 6:
                  text = p.getText();
                  break;
               case 8:
                  CoercionAction act = this._checkFloatToIntCoercion(p, ctxt, this._valueClass);
                  if (act == CoercionAction.AsNull) {
                     return this.getNullValue(ctxt);
                  }

                  if (act == CoercionAction.AsEmpty) {
                     return (BigInteger)this.getEmptyValue(ctxt);
                  }

                  return p.getDecimalValue().toBigInteger();
            }

            CoercionAction actx = this._checkFromStringCoercion(ctxt, text);
            if (actx == CoercionAction.AsNull) {
               return this.getNullValue(ctxt);
            } else if (actx == CoercionAction.AsEmpty) {
               return (BigInteger)this.getEmptyValue(ctxt);
            } else {
               text = text.trim();
               if (this._hasTextualNull(text)) {
                  return this.getNullValue(ctxt);
               } else {
                  try {
                     return NumberInput.parseBigInteger(text);
                  } catch (IllegalArgumentException var6) {
                     return (BigInteger)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation");
                  }
               }
            }
         }
      }
   }

   @JacksonStdImpl
   public static final class BooleanDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Boolean> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.BooleanDeserializer primitiveInstance = new NumberDeserializers.BooleanDeserializer(boolean.class, Boolean.FALSE);
      static final NumberDeserializers.BooleanDeserializer wrapperInstance = new NumberDeserializers.BooleanDeserializer(Boolean.class, null);

      public BooleanDeserializer(Class<Boolean> cls, Boolean nvl) {
         super(cls, LogicalType.Boolean, nvl, Boolean.FALSE);
      }

      public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         JsonToken t = p.currentToken();
         if (t == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE;
         } else if (t == JsonToken.VALUE_FALSE) {
            return Boolean.FALSE;
         } else {
            return this._primitive ? this._parseBooleanPrimitive(p, ctxt) : this._parseBoolean(p, ctxt, this._valueClass);
         }
      }

      public Boolean deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
         JsonToken t = p.currentToken();
         if (t == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE;
         } else if (t == JsonToken.VALUE_FALSE) {
            return Boolean.FALSE;
         } else {
            return this._primitive ? this._parseBooleanPrimitive(p, ctxt) : this._parseBoolean(p, ctxt, this._valueClass);
         }
      }
   }

   @JacksonStdImpl
   public static class ByteDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Byte> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.ByteDeserializer primitiveInstance = new NumberDeserializers.ByteDeserializer(byte.class, (byte)0);
      static final NumberDeserializers.ByteDeserializer wrapperInstance = new NumberDeserializers.ByteDeserializer(Byte.class, null);

      public ByteDeserializer(Class<Byte> cls, Byte nvl) {
         super(cls, LogicalType.Integer, nvl, (byte)0);
      }

      public Byte deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (p.isExpectedNumberIntToken()) {
            return p.getByteValue();
         } else {
            return this._primitive ? this._parseBytePrimitive(p, ctxt) : this._parseByte(p, ctxt);
         }
      }

      protected Byte _parseByte(JsonParser p, DeserializationContext ctxt) throws IOException {
         String text;
         switch (p.currentTokenId()) {
            case 1:
               text = ctxt.extractScalarFromObject(p, this, this._valueClass);
               break;
            case 2:
            case 4:
            case 5:
            case 9:
            case 10:
            default:
               return (Byte)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
            case 3:
               return this._deserializeFromArray(p, ctxt);
            case 6:
               text = p.getText();
               break;
            case 7:
               return p.getByteValue();
            case 8:
               CoercionAction act = this._checkFloatToIntCoercion(p, ctxt, this._valueClass);
               if (act == CoercionAction.AsNull) {
                  return this.getNullValue(ctxt);
               }

               if (act == CoercionAction.AsEmpty) {
                  return (Byte)this.getEmptyValue(ctxt);
               }

               return p.getByteValue();
            case 11:
               return this.getNullValue(ctxt);
         }

         CoercionAction actx = this._checkFromStringCoercion(ctxt, text);
         if (actx == CoercionAction.AsNull) {
            return this.getNullValue(ctxt);
         } else if (actx == CoercionAction.AsEmpty) {
            return (Byte)this.getEmptyValue(ctxt);
         } else {
            text = text.trim();
            if (this._checkTextualNull(ctxt, text)) {
               return this.getNullValue(ctxt);
            } else {
               int value;
               try {
                  value = NumberInput.parseInt(text);
               } catch (IllegalArgumentException var7) {
                  return (Byte)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Byte value");
               }

               return this._byteOverflow(value)
                  ? (Byte)ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value cannot be represented as 8-bit value")
                  : (byte)value;
            }
         }
      }
   }

   @JacksonStdImpl
   public static class CharacterDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Character> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.CharacterDeserializer primitiveInstance = new NumberDeserializers.CharacterDeserializer(char.class, '\u0000');
      static final NumberDeserializers.CharacterDeserializer wrapperInstance = new NumberDeserializers.CharacterDeserializer(Character.class, null);

      public CharacterDeserializer(Class<Character> cls, Character nvl) {
         super(cls, LogicalType.Integer, nvl, '\u0000');
      }

      public Character deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         String text;
         switch (p.currentTokenId()) {
            case 1:
               text = ctxt.extractScalarFromObject(p, this, this._valueClass);
               break;
            case 2:
            case 4:
            case 5:
            case 8:
            case 9:
            case 10:
            default:
               return (Character)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
            case 3:
               return this._deserializeFromArray(p, ctxt);
            case 6:
               text = p.getText();
               break;
            case 7:
               CoercionAction act = ctxt.findCoercionAction(this.logicalType(), this._valueClass, CoercionInputShape.Integer);
               switch (act) {
                  case Fail:
                     this._checkCoercionFail(ctxt, act, this._valueClass, p.getNumberValue(), "Integer value (" + p.getText() + ")");
                  case AsNull:
                     return this.getNullValue(ctxt);
                  case AsEmpty:
                     return (Character)this.getEmptyValue(ctxt);
                  default:
                     int value = p.getIntValue();
                     if (value >= 0 && value <= 65535) {
                        return (char)value;
                     }

                     return (Character)ctxt.handleWeirdNumberValue(this.handledType(), value, "value outside valid Character range (0x0000 - 0xFFFF)");
               }
            case 11:
               if (this._primitive) {
                  this._verifyNullForPrimitive(ctxt);
               }

               return this.getNullValue(ctxt);
         }

         if (text.length() == 1) {
            return text.charAt(0);
         } else {
            CoercionAction act = this._checkFromStringCoercion(ctxt, text);
            if (act == CoercionAction.AsNull) {
               return this.getNullValue(ctxt);
            } else if (act == CoercionAction.AsEmpty) {
               return (Character)this.getEmptyValue(ctxt);
            } else {
               text = text.trim();
               return this._checkTextualNull(ctxt, text)
                  ? this.getNullValue(ctxt)
                  : (Character)ctxt.handleWeirdStringValue(this.handledType(), text, "Expected either Integer value code or 1-character String");
            }
         }
      }
   }

   @JacksonStdImpl
   public static class DoubleDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Double> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.DoubleDeserializer primitiveInstance = new NumberDeserializers.DoubleDeserializer(double.class, 0.0);
      static final NumberDeserializers.DoubleDeserializer wrapperInstance = new NumberDeserializers.DoubleDeserializer(Double.class, null);

      public DoubleDeserializer(Class<Double> cls, Double nvl) {
         super(cls, LogicalType.Float, nvl, 0.0);
      }

      public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            return p.getDoubleValue();
         } else {
            return this._primitive ? this._parseDoublePrimitive(p, ctxt) : this._parseDouble(p, ctxt);
         }
      }

      public Double deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
         if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            return p.getDoubleValue();
         } else {
            return this._primitive ? this._parseDoublePrimitive(p, ctxt) : this._parseDouble(p, ctxt);
         }
      }

      protected final Double _parseDouble(JsonParser p, DeserializationContext ctxt) throws IOException {
         String text;
         switch (p.currentTokenId()) {
            case 1:
               text = ctxt.extractScalarFromObject(p, this, this._valueClass);
               break;
            case 2:
            case 4:
            case 5:
            case 9:
            case 10:
            default:
               return (Double)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
            case 3:
               return this._deserializeFromArray(p, ctxt);
            case 6:
               text = p.getText();
               break;
            case 7:
               CoercionAction act = this._checkIntToFloatCoercion(p, ctxt, this._valueClass);
               if (act == CoercionAction.AsNull) {
                  return this.getNullValue(ctxt);
               }

               if (act == CoercionAction.AsEmpty) {
                  return (Double)this.getEmptyValue(ctxt);
               }
            case 8:
               return p.getDoubleValue();
            case 11:
               return this.getNullValue(ctxt);
         }

         Double nan = this._checkDoubleSpecialValue(text);
         if (nan != null) {
            return nan;
         } else {
            CoercionAction actx = this._checkFromStringCoercion(ctxt, text);
            if (actx == CoercionAction.AsNull) {
               return this.getNullValue(ctxt);
            } else if (actx == CoercionAction.AsEmpty) {
               return (Double)this.getEmptyValue(ctxt);
            } else {
               text = text.trim();
               if (this._checkTextualNull(ctxt, text)) {
                  return this.getNullValue(ctxt);
               } else {
                  try {
                     return _parseDouble(text, p.isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));
                  } catch (IllegalArgumentException var6) {
                     return (Double)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid `Double` value");
                  }
               }
            }
         }
      }
   }

   @JacksonStdImpl
   public static class FloatDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Float> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.FloatDeserializer primitiveInstance = new NumberDeserializers.FloatDeserializer(float.class, 0.0F);
      static final NumberDeserializers.FloatDeserializer wrapperInstance = new NumberDeserializers.FloatDeserializer(Float.class, null);

      public FloatDeserializer(Class<Float> cls, Float nvl) {
         super(cls, LogicalType.Float, nvl, 0.0F);
      }

      public Float deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            return p.getFloatValue();
         } else {
            return this._primitive ? this._parseFloatPrimitive(p, ctxt) : this._parseFloat(p, ctxt);
         }
      }

      protected final Float _parseFloat(JsonParser p, DeserializationContext ctxt) throws IOException {
         String text;
         switch (p.currentTokenId()) {
            case 1:
               text = ctxt.extractScalarFromObject(p, this, this._valueClass);
               break;
            case 2:
            case 4:
            case 5:
            case 9:
            case 10:
            default:
               return (Float)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
            case 3:
               return this._deserializeFromArray(p, ctxt);
            case 6:
               text = p.getText();
               break;
            case 7:
               CoercionAction act = this._checkIntToFloatCoercion(p, ctxt, this._valueClass);
               if (act == CoercionAction.AsNull) {
                  return this.getNullValue(ctxt);
               }

               if (act == CoercionAction.AsEmpty) {
                  return (Float)this.getEmptyValue(ctxt);
               }
            case 8:
               return p.getFloatValue();
            case 11:
               return this.getNullValue(ctxt);
         }

         Float nan = this._checkFloatSpecialValue(text);
         if (nan != null) {
            return nan;
         } else {
            CoercionAction actx = this._checkFromStringCoercion(ctxt, text);
            if (actx == CoercionAction.AsNull) {
               return this.getNullValue(ctxt);
            } else if (actx == CoercionAction.AsEmpty) {
               return (Float)this.getEmptyValue(ctxt);
            } else {
               text = text.trim();
               if (this._checkTextualNull(ctxt, text)) {
                  return this.getNullValue(ctxt);
               } else {
                  try {
                     return NumberInput.parseFloat(text, p.isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));
                  } catch (IllegalArgumentException var6) {
                     return (Float)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid `Float` value");
                  }
               }
            }
         }
      }
   }

   @JacksonStdImpl
   public static final class IntegerDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Integer> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.IntegerDeserializer primitiveInstance = new NumberDeserializers.IntegerDeserializer(int.class, 0);
      static final NumberDeserializers.IntegerDeserializer wrapperInstance = new NumberDeserializers.IntegerDeserializer(Integer.class, null);

      public IntegerDeserializer(Class<Integer> cls, Integer nvl) {
         super(cls, LogicalType.Integer, nvl, 0);
      }

      @Override
      public boolean isCachable() {
         return true;
      }

      public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (p.isExpectedNumberIntToken()) {
            return p.getIntValue();
         } else {
            return this._primitive ? this._parseIntPrimitive(p, ctxt) : this._parseInteger(p, ctxt, Integer.class);
         }
      }

      public Integer deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
         if (p.isExpectedNumberIntToken()) {
            return p.getIntValue();
         } else {
            return this._primitive ? this._parseIntPrimitive(p, ctxt) : this._parseInteger(p, ctxt, Integer.class);
         }
      }
   }

   @JacksonStdImpl
   public static final class LongDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Long> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.LongDeserializer primitiveInstance = new NumberDeserializers.LongDeserializer(long.class, 0L);
      static final NumberDeserializers.LongDeserializer wrapperInstance = new NumberDeserializers.LongDeserializer(Long.class, null);

      public LongDeserializer(Class<Long> cls, Long nvl) {
         super(cls, LogicalType.Integer, nvl, 0L);
      }

      @Override
      public boolean isCachable() {
         return true;
      }

      public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (p.isExpectedNumberIntToken()) {
            return p.getLongValue();
         } else {
            return this._primitive ? this._parseLongPrimitive(p, ctxt) : this._parseLong(p, ctxt, Long.class);
         }
      }
   }

   @JacksonStdImpl
   public static class NumberDeserializer extends StdScalarDeserializer<Object> {
      public static final NumberDeserializers.NumberDeserializer instance = new NumberDeserializers.NumberDeserializer();

      public NumberDeserializer() {
         super(Number.class);
      }

      @Override
      public final LogicalType logicalType() {
         return LogicalType.Integer;
      }

      @Override
      public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         String text;
         switch (p.currentTokenId()) {
            case 1:
               text = ctxt.extractScalarFromObject(p, this, this._valueClass);
               break;
            case 2:
            case 4:
            case 5:
            default:
               return ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
            case 3:
               return this._deserializeFromArray(p, ctxt);
            case 6:
               text = p.getText();
               break;
            case 7:
               if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
                  return this._coerceIntegral(p, ctxt);
               }

               return p.getNumberValue();
            case 8:
               if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS) && !p.isNaN()) {
                  return p.getDecimalValue();
               }

               return p.getNumberValue();
         }

         CoercionAction act = this._checkFromStringCoercion(ctxt, text);
         if (act == CoercionAction.AsNull) {
            return this.getNullValue(ctxt);
         } else if (act == CoercionAction.AsEmpty) {
            return this.getEmptyValue(ctxt);
         } else {
            text = text.trim();
            if (this._hasTextualNull(text)) {
               return this.getNullValue(ctxt);
            } else if (this._isPosInf(text)) {
               return Double.POSITIVE_INFINITY;
            } else if (this._isNegInf(text)) {
               return Double.NEGATIVE_INFINITY;
            } else if (this._isNaN(text)) {
               return Double.NaN;
            } else {
               try {
                  if (!this._isIntNumber(text)) {
                     return ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                        ? NumberInput.parseBigDecimal(text)
                        : NumberInput.parseDouble(text, p.isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));
                  } else if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                     return NumberInput.parseBigInteger(text);
                  } else {
                     long value = NumberInput.parseLong(text);
                     return !ctxt.isEnabled(DeserializationFeature.USE_LONG_FOR_INTS) && value <= 2147483647L && value >= -2147483648L ? (int)value : value;
                  }
               } catch (IllegalArgumentException var7) {
                  return ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid number");
               }
            }
         }
      }

      @Override
      public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
         switch (p.currentTokenId()) {
            case 6:
            case 7:
            case 8:
               return this.deserialize(p, ctxt);
            default:
               return typeDeserializer.deserializeTypedFromScalar(p, ctxt);
         }
      }
   }

   protected abstract static class PrimitiveOrWrapperDeserializer<T> extends StdScalarDeserializer<T> {
      private static final long serialVersionUID = 1L;
      protected final LogicalType _logicalType;
      protected final T _nullValue;
      protected final T _emptyValue;
      protected final boolean _primitive;

      protected PrimitiveOrWrapperDeserializer(Class<T> vc, LogicalType logicalType, T nvl, T empty) {
         super(vc);
         this._logicalType = logicalType;
         this._nullValue = nvl;
         this._emptyValue = empty;
         this._primitive = vc.isPrimitive();
      }

      @Deprecated
      protected PrimitiveOrWrapperDeserializer(Class<T> vc, T nvl, T empty) {
         this(vc, LogicalType.OtherScalar, nvl, empty);
      }

      @Override
      public AccessPattern getNullAccessPattern() {
         if (this._primitive) {
            return AccessPattern.DYNAMIC;
         } else {
            return this._nullValue == null ? AccessPattern.ALWAYS_NULL : AccessPattern.CONSTANT;
         }
      }

      @Override
      public final T getNullValue(DeserializationContext ctxt) throws JsonMappingException {
         if (this._primitive && ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            ctxt.reportInputMismatch(
               this,
               "Cannot map `null` into type %s (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)",
               ClassUtil.classNameOf(this.handledType())
            );
         }

         return this._nullValue;
      }

      @Override
      public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
         return this._emptyValue;
      }

      @Override
      public final LogicalType logicalType() {
         return this._logicalType;
      }
   }

   @JacksonStdImpl
   public static class ShortDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Short> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.ShortDeserializer primitiveInstance = new NumberDeserializers.ShortDeserializer(short.class, (short)0);
      static final NumberDeserializers.ShortDeserializer wrapperInstance = new NumberDeserializers.ShortDeserializer(Short.class, null);

      public ShortDeserializer(Class<Short> cls, Short nvl) {
         super(cls, LogicalType.Integer, nvl, (short)0);
      }

      public Short deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (p.isExpectedNumberIntToken()) {
            return p.getShortValue();
         } else {
            return this._primitive ? this._parseShortPrimitive(p, ctxt) : this._parseShort(p, ctxt);
         }
      }

      protected Short _parseShort(JsonParser p, DeserializationContext ctxt) throws IOException {
         String text;
         switch (p.currentTokenId()) {
            case 1:
               text = ctxt.extractScalarFromObject(p, this, this._valueClass);
               break;
            case 2:
            case 4:
            case 5:
            case 9:
            case 10:
            default:
               return (Short)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
            case 3:
               return this._deserializeFromArray(p, ctxt);
            case 6:
               text = p.getText();
               break;
            case 7:
               return p.getShortValue();
            case 8:
               CoercionAction act = this._checkFloatToIntCoercion(p, ctxt, this._valueClass);
               if (act == CoercionAction.AsNull) {
                  return this.getNullValue(ctxt);
               }

               if (act == CoercionAction.AsEmpty) {
                  return (Short)this.getEmptyValue(ctxt);
               }

               return p.getShortValue();
            case 11:
               return this.getNullValue(ctxt);
         }

         CoercionAction actx = this._checkFromStringCoercion(ctxt, text);
         if (actx == CoercionAction.AsNull) {
            return this.getNullValue(ctxt);
         } else if (actx == CoercionAction.AsEmpty) {
            return (Short)this.getEmptyValue(ctxt);
         } else {
            text = text.trim();
            if (this._checkTextualNull(ctxt, text)) {
               return this.getNullValue(ctxt);
            } else {
               int value;
               try {
                  value = NumberInput.parseInt(text);
               } catch (IllegalArgumentException var7) {
                  return (Short)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Short value");
               }

               return this._shortOverflow(value)
                  ? (Short)ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value cannot be represented as 16-bit value")
                  : (short)value;
            }
         }
      }
   }
}
