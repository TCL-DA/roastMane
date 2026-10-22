package org.cropster.roastingintelligence.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.cropster.roastingintelligence.measurement.SourceParameter;

public class SourceParameterSerializer extends StdSerializer<Map<String, Set<SourceParameter>>> {
   public SourceParameterSerializer() {
      super(Set.class, false);
   }

   public void serialize(Map<String, Set<SourceParameter>> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeStartObject();

      for (Entry<String, Set<SourceParameter>> sourceEntry : value.entrySet()) {
         if (sourceEntry.getValue() != null && !sourceEntry.getValue().isEmpty()) {
            gen.writeFieldName(sourceEntry.getKey());
            gen.writeStartObject();

            for (SourceParameter s : sourceEntry.getValue()) {
               gen.writeStringField(s.getName(), s.getValue());
            }

            gen.writeEndObject();
         }
      }

      gen.writeEndObject();
   }
}
