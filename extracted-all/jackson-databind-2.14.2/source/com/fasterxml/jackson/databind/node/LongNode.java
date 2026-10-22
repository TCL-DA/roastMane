package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.io.NumberOutput;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class LongNode extends NumericNode {
   protected final long _value;

   public LongNode(long v) {
      this._value = v;
   }

   public static LongNode valueOf(long l) {
      return new LongNode(l);
   }

   @Override
   public JsonToken asToken() {
      return JsonToken.VALUE_NUMBER_INT;
   }

   @Override
   public NumberType numberType() {
      return NumberType.LONG;
   }

   @Override
   public boolean isIntegralNumber() {
      return true;
   }

   @Override
   public boolean isLong() {
      return true;
   }

   @Override
   public boolean canConvertToInt() {
      return this._value >= -2147483648L && this._value <= 2147483647L;
   }

   @Override
   public boolean canConvertToLong() {
      return true;
   }

   @Override
   public Number numberValue() {
      return this._value;
   }

   @Override
   public short shortValue() {
      return (short)((int)this._value);
   }

   @Override
   public int intValue() {
      return (int)this._value;
   }

   @Override
   public long longValue() {
      return this._value;
   }

   @Override
   public float floatValue() {
      return (float)this._value;
   }

   @Override
   public double doubleValue() {
      return (double)this._value;
   }

   @Override
   public BigDecimal decimalValue() {
      return BigDecimal.valueOf(this._value);
   }

   @Override
   public BigInteger bigIntegerValue() {
      return BigInteger.valueOf(this._value);
   }

   @Override
   public String asText() {
      return NumberOutput.toString(this._value);
   }

   @Override
   public boolean asBoolean(boolean defaultValue) {
      return this._value != 0L;
   }

   @Override
   public final void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
      g.writeNumber(this._value);
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else {
         return o instanceof LongNode ? ((LongNode)o)._value == this._value : false;
      }
   }

   @Override
   public int hashCode() {
      return (int)this._value ^ (int)(this._value >> 32);
   }
}
