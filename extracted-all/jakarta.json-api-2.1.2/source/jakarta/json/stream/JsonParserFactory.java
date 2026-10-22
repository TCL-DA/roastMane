package jakarta.json.stream;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

public interface JsonParserFactory {
   JsonParser createParser(Reader var1);

   JsonParser createParser(InputStream var1);

   JsonParser createParser(InputStream var1, Charset var2);

   JsonParser createParser(JsonObject var1);

   JsonParser createParser(JsonArray var1);

   Map<String, ?> getConfigInUse();
}
