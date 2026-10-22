package com.google.gson;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.LazilyParsedNumber;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonPrimitive extends JsonElement {
   private final Object value;

   public JsonPrimitive(Boolean bool) {
      this.value = $Gson$Preconditions.checkNotNull(bool);
   }

   public JsonPrimitive(Number number) {
      this.value = $Gson$Preconditions.checkNotNull(number);
   }

   public JsonPrimitive(String string) {
      this.value = $Gson$Preconditions.checkNotNull(string);
   }

   public JsonPrimitive(Character c) {
      this.value = $Gson$Preconditions.checkNotNull(c).toString();
   }

   public JsonPrimitive deepCopy() {
      return this;
   }

   public boolean isBoolean() {
      return this.value instanceof Boolean;
   }

   @Override
   public boolean getAsBoolean() {
      return this.isBoolean() ? (Boolean)this.value : Boolean.parseBoolean(this.getAsString());
   }

   public boolean isNumber() {
      return this.value instanceof Number;
   }

   @Override
   public Number getAsNumber() {
      return (Number)(this.value instanceof String ? new LazilyParsedNumber((String)this.value) : (Number)this.value);
   }

   public boolean isString() {
      return this.value instanceof String;
   }

   @Override
   public String getAsString() {
      if (this.isNumber()) {
         return this.getAsNumber().toString();
      } else {
         return this.isBoolean() ? ((Boolean)this.value).toString() : (String)this.value;
      }
   }

   @Override
   public double getAsDouble() {
      return this.isNumber() ? this.getAsNumber().doubleValue() : Double.parseDouble(this.getAsString());
   }

   @Override
   public BigDecimal getAsBigDecimal() {
      return this.value instanceof BigDecimal ? (BigDecimal)this.value : new BigDecimal(this.value.toString());
   }

   @Override
   public BigInteger getAsBigInteger() {
      return this.value instanceof BigInteger ? (BigInteger)this.value : new BigInteger(this.value.toString());
   }

   @Override
   public float getAsFloat() {
      return this.isNumber() ? this.getAsNumber().floatValue() : Float.parseFloat(this.getAsString());
   }

   @Override
   public long getAsLong() {
      return this.isNumber() ? this.getAsNumber().longValue() : Long.parseLong(this.getAsString());
   }

   @Override
   public short getAsShort() {
      return this.isNumber() ? this.getAsNumber().shortValue() : Short.parseShort(this.getAsString());
   }

   @Override
   public int getAsInt() {
      return this.isNumber() ? this.getAsNumber().intValue() : Integer.parseInt(this.getAsString());
   }

   @Override
   public byte getAsByte() {
      return this.isNumber() ? this.getAsNumber().byteValue() : Byte.parseByte(this.getAsString());
   }

   @Override
   public char getAsCharacter() {
      return this.getAsString().charAt(0);
   }

   @Override
   public int hashCode() {
      if (this.value == null) {
         return 31;
      } else if (isIntegral(this)) {
         long value = this.getAsNumber().longValue();
         return (int)(value ^ value >>> 32);
      } else if (this.value instanceof Number) {
         long value = Double.doubleToLongBits(this.getAsNumber().doubleValue());
         return (int)(value ^ value >>> 32);
      } else {
         return this.value.hashCode();
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         JsonPrimitive other = (JsonPrimitive)obj;
         if (this.value == null) {
            return other.value == null;
         } else if (isIntegral(this) && isIntegral(other)) {
            return this.getAsNumber().longValue() == other.getAsNumber().longValue();
         } else if (this.value instanceof Number && other.value instanceof Number) {
            double a = this.getAsNumber().doubleValue();
            double b = other.getAsNumber().doubleValue();
            return a == b || Double.isNaN(a) && Double.isNaN(b);
         } else {
            return this.value.equals(other.value);
         }
      } else {
         return false;
      }
   }

   private static boolean isIntegral(JsonPrimitive primitive) {
      if (!(primitive.value instanceof Number)) {
         return false;
      } else {
         Number number = (Number)primitive.value;
         return number instanceof BigInteger || number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte;
      }
   }
}
