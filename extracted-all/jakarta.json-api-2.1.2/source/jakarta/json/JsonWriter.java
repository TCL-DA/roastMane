package jakarta.json;

import java.io.Closeable;

public interface JsonWriter extends Closeable {
   void writeArray(JsonArray var1);

   void writeObject(JsonObject var1);

   void write(JsonStructure var1);

   default void write(JsonValue value) {
      throw new UnsupportedOperationException();
   }

   @Override
   void close();
}
