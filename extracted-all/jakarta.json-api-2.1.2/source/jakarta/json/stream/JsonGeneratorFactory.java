package jakarta.json.stream;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

public interface JsonGeneratorFactory {
   JsonGenerator createGenerator(Writer var1);

   JsonGenerator createGenerator(OutputStream var1);

   JsonGenerator createGenerator(OutputStream var1, Charset var2);

   Map<String, ?> getConfigInUse();
}
