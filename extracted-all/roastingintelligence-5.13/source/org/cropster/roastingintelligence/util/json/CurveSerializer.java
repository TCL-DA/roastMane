package org.cropster.roastingintelligence.util.json;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import org.cropster.roastingintelligence.model.Curve;

public class CurveSerializer extends StdSerializer<Map<String, Curve>> {
   public CurveSerializer() {
      super(Map.class, false);
   }

   public void serialize(Map<String, Curve> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
      jgen.writeStartArray();

      for (Entry<String, Curve> entry : value.entrySet()) {
         jgen.writeStartObject();
         jgen.writeStringField("name", entry.getKey());
         jgen.writeStringField("unit", entry.getValue().getUnit().name());
         jgen.writeObjectField("values", entry.getValue());
         jgen.writeEndObject();
      }

      jgen.writeEndArray();
   }
}
