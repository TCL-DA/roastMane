package jakarta.json.stream;

import jakarta.json.JsonValue;
import java.io.Closeable;
import java.io.Flushable;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface JsonGenerator extends Flushable, Closeable {
   String PRETTY_PRINTING = "jakarta.json.stream.JsonGenerator.prettyPrinting";

   JsonGenerator writeStartObject();

   JsonGenerator writeStartObject(String var1);

   JsonGenerator writeKey(String var1);

   JsonGenerator writeStartArray();

   JsonGenerator writeStartArray(String var1);

   JsonGenerator write(String var1, JsonValue var2);

   JsonGenerator write(String var1, String var2);

   JsonGenerator write(String var1, BigInteger var2);

   JsonGenerator write(String var1, BigDecimal var2);

   JsonGenerator write(String var1, int var2);

   JsonGenerator write(String var1, long var2);

   JsonGenerator write(String var1, double var2);

   JsonGenerator write(String var1, boolean var2);

   JsonGenerator writeNull(String var1);

   JsonGenerator writeEnd();

   JsonGenerator write(JsonValue var1);

   JsonGenerator write(String var1);

   JsonGenerator write(BigDecimal var1);

   JsonGenerator write(BigInteger var1);

   JsonGenerator write(int var1);

   JsonGenerator write(long var1);

   JsonGenerator write(double var1);

   JsonGenerator write(boolean var1);

   JsonGenerator writeNull();

   @Override
   void close();

   @Override
   void flush();
}
