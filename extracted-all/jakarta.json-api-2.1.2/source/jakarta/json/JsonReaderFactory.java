package jakarta.json;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

public interface JsonReaderFactory {
   JsonReader createReader(Reader var1);

   JsonReader createReader(InputStream var1);

   JsonReader createReader(InputStream var1, Charset var2);

   Map<String, ?> getConfigInUse();
}
