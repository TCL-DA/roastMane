package org.cropster.roastingintelligence.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class MapAsNameValuesSerializer extends StdSerializer<Map<String, ?>> {
   private static final long serialVersionUID = 1259445170897609007L;

   public MapAsNameValuesSerializer() {
      super(Map.class, false);
   }

   public void serialize(Map<String, ?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
      jgen.writeStartArray();

      for (Entry<String, ?> entry : value.entrySet()) {
         jgen.writeStartObject();
         jgen.writeFieldName("name");
         jgen.writeString(entry.getKey());
         if (!(entry.getValue() instanceof Map) && !(entry.getValue() instanceof Collection)) {
            jgen.writeFieldName("value");
         } else {
            jgen.writeFieldName("values");
         }

         jgen.writeObject(entry.getValue());
         jgen.writeEndObject();
      }

      jgen.writeEndArray();
   }
}
