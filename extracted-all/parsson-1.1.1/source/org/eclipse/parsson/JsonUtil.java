package org.eclipse.parsson;

import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import java.io.StringReader;
import org.eclipse.parsson.api.BufferPool;

public final class JsonUtil {
   private static BufferPool internalPool;

   private JsonUtil() {
   }

   static BufferPool getInternalBufferPool() {
      if (internalPool == null) {
         internalPool = new BufferPoolImpl();
      }

      return internalPool;
   }

   public static JsonValue toJson(String jsonString) {
      StringBuilder builder = new StringBuilder();
      boolean single_context = false;

      for (int i = 0; i < jsonString.length(); i++) {
         char ch = jsonString.charAt(i);
         if (ch == '\\') {
            if (++i < jsonString.length()) {
               ch = jsonString.charAt(i);
               if (!single_context || ch != '\'') {
                  builder.append('\\');
               }
            }
         } else if (ch == '\'') {
            ch = '"';
            single_context = !single_context;
         }

         builder.append(ch);
      }

      JsonReader reader = new JsonReaderImpl(new StringReader(builder.toString()), getInternalBufferPool());
      JsonValue value = reader.readValue();
      reader.close();
      return value;
   }
}
