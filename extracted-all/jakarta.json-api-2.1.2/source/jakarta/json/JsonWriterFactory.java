package jakarta.json;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

public interface JsonWriterFactory {
   JsonWriter createWriter(Writer var1);

   JsonWriter createWriter(OutputStream var1);

   JsonWriter createWriter(OutputStream var1, Charset var2);

   Map<String, ?> getConfigInUse();
}
