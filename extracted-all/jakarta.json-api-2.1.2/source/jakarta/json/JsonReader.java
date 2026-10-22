package jakarta.json;

import java.io.Closeable;

public interface JsonReader extends Closeable {
   JsonStructure read();

   JsonObject readObject();

   JsonArray readArray();

   default JsonValue readValue() {
      throw new UnsupportedOperationException();
   }

   @Override
   void close();
}
