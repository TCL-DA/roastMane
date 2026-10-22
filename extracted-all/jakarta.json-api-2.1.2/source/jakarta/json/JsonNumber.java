package jakarta.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface JsonNumber extends JsonValue {
   boolean isIntegral();

   int intValue();

   int intValueExact();

   long longValue();

   long longValueExact();

   BigInteger bigIntegerValue();

   BigInteger bigIntegerValueExact();

   double doubleValue();

   BigDecimal bigDecimalValue();

   default Number numberValue() {
      throw new UnsupportedOperationException();
   }

   @Override
   String toString();

   @Override
   boolean equals(Object var1);

   @Override
   int hashCode();
}
