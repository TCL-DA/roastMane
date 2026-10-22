package org.eclipse.parsson;

import jakarta.json.JsonNumber;
import jakarta.json.JsonValue.ValueType;
import java.math.BigDecimal;
import java.math.BigInteger;

abstract class JsonNumberImpl implements JsonNumber {
   private int hashCode;

   static JsonNumber getJsonNumber(int num) {
      return new JsonNumberImpl.JsonIntNumber(num);
   }

   static JsonNumber getJsonNumber(long num) {
      return new JsonNumberImpl.JsonLongNumber(num);
   }

   static JsonNumber getJsonNumber(BigInteger value) {
      if (value == null) {
         throw new NullPointerException("Value is null");
      } else {
         return new JsonNumberImpl.JsonBigDecimalNumber(new BigDecimal(value));
      }
   }

   static JsonNumber getJsonNumber(double value) {
      return new JsonNumberImpl.JsonBigDecimalNumber(BigDecimal.valueOf(value));
   }

   static JsonNumber getJsonNumber(BigDecimal value) {
      if (value == null) {
         throw new NullPointerException("Value is null");
      } else {
         return new JsonNumberImpl.JsonBigDecimalNumber(value);
      }
   }

   static JsonNumber getJsonNumber(Number value) {
      if (value == null) {
         throw new NullPointerException("Value is null");
      } else if (value instanceof Integer) {
         return getJsonNumber(value.intValue());
      } else if (value instanceof Long) {
         return getJsonNumber(value.longValue());
      } else if (value instanceof Double) {
         return getJsonNumber(value.doubleValue());
      } else if (value instanceof BigInteger) {
         return getJsonNumber((BigInteger)value);
      } else {
         return (JsonNumber)(value instanceof BigDecimal ? getJsonNumber((BigDecimal)value) : new JsonNumberImpl.JsonNumberNumber(value));
      }
   }

   public boolean isIntegral() {
      return this.bigDecimalValue().scale() == 0;
   }

   public int intValue() {
      return this.bigDecimalValue().intValue();
   }

   public int intValueExact() {
      return this.bigDecimalValue().intValueExact();
   }

   public long longValue() {
      return this.bigDecimalValue().longValue();
   }

   public long longValueExact() {
      return this.bigDecimalValue().longValueExact();
   }

   public double doubleValue() {
      return this.bigDecimalValue().doubleValue();
   }

   public BigInteger bigIntegerValue() {
      return this.bigDecimalValue().toBigInteger();
   }

   public BigInteger bigIntegerValueExact() {
      return this.bigDecimalValue().toBigIntegerExact();
   }

   public ValueType getValueType() {
      return ValueType.NUMBER;
   }

   @Override
   public int hashCode() {
      if (this.hashCode == 0) {
         this.hashCode = this.bigDecimalValue().hashCode();
      }

      return this.hashCode;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof JsonNumber)) {
         return false;
      } else {
         JsonNumber other = (JsonNumber)obj;
         return this.bigDecimalValue().equals(other.bigDecimalValue());
      }
   }

   @Override
   public String toString() {
      return this.bigDecimalValue().toString();
   }

   private static final class JsonBigDecimalNumber extends JsonNumberImpl {
      private final BigDecimal bigDecimal;

      JsonBigDecimalNumber(BigDecimal value) {
         this.bigDecimal = value;
      }

      public BigDecimal bigDecimalValue() {
         return this.bigDecimal;
      }

      public Number numberValue() {
         return this.bigDecimalValue();
      }
   }

   private static final class JsonIntNumber extends JsonNumberImpl {
      private final int num;
      private BigDecimal bigDecimal;

      JsonIntNumber(int num) {
         this.num = num;
      }

      @Override
      public boolean isIntegral() {
         return true;
      }

      @Override
      public int intValue() {
         return this.num;
      }

      @Override
      public int intValueExact() {
         return this.num;
      }

      @Override
      public long longValue() {
         return (long)this.num;
      }

      @Override
      public long longValueExact() {
         return (long)this.num;
      }

      @Override
      public double doubleValue() {
         return (double)this.num;
      }

      public BigDecimal bigDecimalValue() {
         BigDecimal bd = this.bigDecimal;
         if (bd == null) {
            this.bigDecimal = bd = new BigDecimal(this.num);
         }

         return bd;
      }

      public Number numberValue() {
         return this.num;
      }

      @Override
      public String toString() {
         return Integer.toString(this.num);
      }
   }

   private static final class JsonLongNumber extends JsonNumberImpl {
      private final long num;
      private BigDecimal bigDecimal;

      JsonLongNumber(long num) {
         this.num = num;
      }

      @Override
      public boolean isIntegral() {
         return true;
      }

      @Override
      public int intValue() {
         return (int)this.num;
      }

      @Override
      public int intValueExact() {
         return Math.toIntExact(this.num);
      }

      @Override
      public long longValue() {
         return this.num;
      }

      @Override
      public long longValueExact() {
         return this.num;
      }

      @Override
      public double doubleValue() {
         return (double)this.num;
      }

      public BigDecimal bigDecimalValue() {
         BigDecimal bd = this.bigDecimal;
         if (bd == null) {
            this.bigDecimal = bd = new BigDecimal(this.num);
         }

         return bd;
      }

      public Number numberValue() {
         return this.num;
      }

      @Override
      public String toString() {
         return Long.toString(this.num);
      }
   }

   private static final class JsonNumberNumber extends JsonNumberImpl {
      private final Number num;
      private BigDecimal bigDecimal;

      JsonNumberNumber(Number num) {
         this.num = num;
      }

      public Number numberValue() {
         return this.num;
      }

      public BigDecimal bigDecimalValue() {
         BigDecimal bd = this.bigDecimal;
         if (bd == null) {
            this.bigDecimal = bd = new BigDecimal(this.num.toString());
         }

         return bd;
      }
   }
}
